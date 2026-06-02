package openfind.ai.data.repository

import openfind.ai.data.local.dao.SavedDao
import openfind.ai.domain.model.DomainResult
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class DomainRepository(
    private val savedDao: SavedDao
) {
    suspend fun checkDomain(domain: String, doAudit: Boolean = false): DomainResult {
        val clean = domain.lowercase()
            .replace("https://", "")
            .replace("http://", "")
            .replace("www.", "")
            .split("/")[0]

        val ip: String? = try {
            InetAddress.getByName(clean).hostAddress
        } catch (_: Exception) { null }

        if (ip != null) {
            var result = DomainResult(
                domain = clean,
                status = "taken",
                detail = "Registered (Active via DNS)",
                method = "DNS Resolution",
                ip = ip
            )
            if (doAudit) {
                val audit = auditServer(clean, ip, null)
                result = result.copy(
                    sslActive = audit.sslActive,
                    sslIssuer = audit.sslIssuer,
                    cloudflare = audit.cloudflare,
                    nsServers = audit.nsServers
                )
            }
            return result
        }

        val tld = clean.substringAfterLast(".").lowercase()
        val server = whoisServers[tld] ?: "whois.iana.org"
        val rawWhois = queryWhoisServer(clean, server)

        if (rawWhois.startsWith("ERROR:")) {
            return DomainResult(
                domain = clean,
                status = "unknown",
                detail = "Network error: ${rawWhois.removePrefix("ERROR:")}",
                method = "Limit / WHOIS Network"
            )
        }

        val resLower = rawWhois.lowercase()
        var referServer: String? = null
        if (resLower.contains("refer:")) {
            for (line in rawWhois.split("\n")) {
                if (line.trim().lowercase().startsWith("refer:")) {
                    referServer = line.substringAfter(":").trim()
                    break
                }
            }
        }

        var finalWhois = rawWhois
        if (!referServer.isNullOrEmpty()) {
            val redirected = queryWhoisServer(clean, referServer)
            if (!redirected.startsWith("ERROR:")) finalWhois = redirected
        }

        val finalLower = finalWhois.lowercase()
        val isAvailable = availablePatterns.any { finalLower.contains(it) }

        if (isAvailable) {
            return DomainResult(
                domain = clean,
                status = "available",
                detail = "Available for registration!",
                method = "WHOIS Socket 43 Query"
            )
        }

        val isTaken = takenPatterns.any { finalLower.contains(it) } || finalWhois.length > 250

        var registrar: String? = null
        var creationDate: String? = null
        for (line in finalWhois.split("\n")) {
            val l = line.trim().lowercase()
            if (l.contains("creation date") || l.contains("created:") || l.contains("fecha de creacion")) {
                val part = line.trim().substringAfter(":").trim()
                if (part.isNotEmpty()) creationDate = part
            }
            if (l.contains("registrar:") || l.contains("registrador:")) {
                val part = line.trim().substringAfter(":").trim()
                if (part.isNotEmpty()) registrar = part
            }
        }

        if (isTaken) {
            var result = DomainResult(
                domain = clean,
                status = "taken",
                detail = "Registered (Confirmed by WHOIS)",
                method = "WHOIS Socket 43 Query",
                registrar = registrar,
                creationDate = creationDate
            )
            if (doAudit) {
                val audit = auditServer(clean, null, finalWhois)
                result = result.copy(
                    sslActive = audit.sslActive,
                    sslIssuer = audit.sslIssuer,
                    cloudflare = audit.cloudflare,
                    nsServers = audit.nsServers
                )
            }
            return result
        }

        return DomainResult(
            domain = clean,
            status = "unknown",
            detail = "Could not be determined with certainty",
            method = "WHOIS Socket 43 Query"
        )
    }

    suspend fun checkBulk(
        domains: List<String>,
        doAudit: Boolean = false,
        onProgress: (Int, Int, DomainResult) -> Unit
    ) {
        for ((index, domain) in domains.withIndex()) {
            val result = checkDomain(domain, doAudit)
            onProgress(index + 1, domains.size, result)
            kotlinx.coroutines.delay(300)
        }
    }

    fun generateNames(keyword: String, tlds: List<String>, maxResults: Int = 12): List<String> {
        val suffixes = listOf("ai", "hub", "flow", "tech", "ify", "ly", "base", "smart", "labs", "sys", "zone", "net")
        val prefixes = listOf("get", "try", "go", "my", "the", "we", "neo", "mega", "alpha")
        val results = mutableListOf<String>()

        for (tld in tlds) {
            results.add("$keyword.$tld")
            for (p in prefixes.shuffled().take(2)) {
                results.add("$p$keyword.$tld")
            }
            for (s in suffixes.shuffled().take(2)) {
                results.add("$keyword$s.$tld")
            }
        }
        return results.distinct().take(maxResults)
    }

    fun evaluateBrandHeuristic(domain: String): Pair<Float, String> {
        val nameOnly = domain.substringBefore(".").lowercase()
        val tld = domain.substringAfter(".", "").lowercase()
        val length = nameOnly.length

        var pronScore = 7.5
        var memScore = 7.0
        var lenScore = 8.0
        var tldScore = 7.0

        lenScore = when {
            length <= 4 -> 10.0
            length <= 6 -> 9.5
            length <= 8 -> 8.5
            length <= 10 -> 7.0
            length <= 12 -> 5.5
            else -> 3.5
        }

        val vowelsCount = nameOnly.count { it in "aeiou" }
        if (length > 0) {
            val ratio = vowelsCount.toDouble() / length
            pronScore = when {
                ratio in 0.38..0.52 -> 9.8
                ratio in 0.25..0.65 -> 8.5
                else -> 5.0
            }

            var syllableCount = 0
            var lastWasVowel = false
            for (char in nameOnly) {
                val isVowel = char in "aeiou"
                if (isVowel && !lastWasVowel) syllableCount++
                lastWasVowel = isVowel
            }
            if (syllableCount in 2..3) pronScore += 0.5
            else if (syllableCount == 1 && length <= 4) pronScore += 0.3
        }

        if (Regex("[bcdfghjklmnpqrstvwxyz]{3,}").containsMatchIn(nameOnly)) pronScore -= 3.0
        if (Regex("[qxzwj][qxzwyj]").containsMatchIn(nameOnly)) pronScore -= 2.0
        if (nameOnly.contains("ee") || nameOnly.contains("oo") || nameOnly.contains("aa")) pronScore += 0.5

        pronScore = pronScore.coerceIn(1.0, 10.0)
        val cognitiveWords = listOf("ai", "bot", "neo", "intel", "mind", "galaxy", "yi", "tech", "dev", "labs", "hub", "nexus", "core", "app", "flow", "think", "deep", "learn", "cloud", "net", "open", "find", "cyber", "data", "meta", "smart", "zen", "pixel", "layer", "matrix", "vertex", "vector", "crypt")
        for (word in cognitiveWords) {
            if (nameOnly.contains(word)) { memScore += 1.5; break }
        }
        if (nameOnly.contains("-") || nameOnly.contains("_")) memScore -= 2.0
        if (nameOnly.any { it.isDigit() }) memScore -= 1.5
        memScore = memScore.coerceIn(1.0, 10.0)

        tldScore = when (tld) {
            "ai" -> { val aiWords = listOf("mind", "brain", "bot", "intel", "neural", "deep", "learn", "think", "chat", "net", "open", "find", "labs")
                if (aiWords.any { nameOnly.contains(it) }) 10.0 else 9.0 }
            "com" -> 9.8
            "io" -> { val techWords = listOf("tech", "dev", "labs", "code", "cyber", "flow", "hub", "sys")
                if (techWords.any { nameOnly.contains(it) }) 10.0 else 9.0 }
            "co" -> 8.5
            "net" -> 7.8
            else -> 6.5
        }

        var finalScore = (pronScore * 0.25) + (memScore * 0.35) + (lenScore * 0.15) + (tldScore * 0.25)
        finalScore = (finalScore * 10.0).coerceIn(1.0, 10.0)

        val feedback = when {
            finalScore >= 9.0 -> "Highly recommend this brand. Excellent phonetic flow (S-tier)."
            finalScore >= 7.5 -> "Very good branding option. The agent estimates a brand viability above 85%."
            finalScore >= 5.5 -> "Commercially acceptable but I suggest searching for punchier alternatives."
            else -> "Complex name for branding. I suggest using the generator for better options."
        }

        return Pair(finalScore.toFloat(), feedback)
    }

    private data class AuditResult(
        val sslActive: Boolean = false,
        val sslIssuer: String? = null,
        val cloudflare: String = "none",
        val nsServers: List<String> = emptyList()
    )

    private fun auditServer(domain: String, ip: String?, rawWhois: String?): AuditResult {
        val nsList = mutableListOf<String>()

        if (!rawWhois.isNullOrEmpty()) {
            for (line in rawWhois.split("\n")) {
                val l = line.trim().lowercase()
                if (l.startsWith("nserver:") || l.startsWith("name server:") || l.startsWith("nameserver:")) {
                    val parts = l.split(":", limit = 2)
                    if (parts.size > 1) {
                        var nsVal = parts[1].trim()
                        if (nsVal.endsWith(".")) nsVal = nsVal.dropLast(1)
                        if (nsVal.isNotEmpty() && nsVal !in nsList) nsList.add(nsVal)
                    }
                }
            }
        }

        var sslActive = false
        var sslIssuer: String? = null
        var isOrange = false
        var isGray = false

        val hostIp = ip ?: try {
            InetAddress.getByName(domain).hostAddress
        } catch (_: Exception) { null }

        if (hostIp != null) {
            try {
                val socket = Socket()
                socket.connect(java.net.InetSocketAddress(domain, 80), 1500)
                socket.soTimeout = 1500
                val out = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                out.print("HEAD / HTTP/1.1\r\nHost: $domain\r\nUser-Agent: Mozilla/5.0\r\nConnection: close\r\n\r\n")
                out.flush()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line!!.lowercase().startsWith("server:")) {
                        if (line!!.substringAfter(":").trim().lowercase().contains("cloudflare")) isOrange = true
                        break
                    }
                }
                socket.close()
            } catch (_: Exception) {}

            try {
                val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
                val sslSocket = factory.createSocket() as SSLSocket
                sslSocket.connect(java.net.InetSocketAddress(domain, 443), 1500)
                sslSocket.soTimeout = 1500
                sslSocket.startHandshake()
                sslActive = true
                val certs = sslSocket.session.peerCertificates
                if (certs.isNotEmpty()) {
                    val cert = certs[0] as java.security.cert.X509Certificate
                    var cn = "Unknown Issuer"
                    for (part in cert.issuerDN.name.split(",")) {
                        if (part.trim().startsWith("CN=")) { cn = part.substringAfter("CN=").trim(); break }
                    }
                    sslIssuer = cn
                    if (cn.lowercase().contains("cloudflare")) isOrange = true
                }
                sslSocket.close()
            } catch (_: Exception) {}
        }

        val hasCfNs = nsList.any { it.contains("cloudflare") }
        if (hasCfNs && !isOrange) isGray = true

        val cloudflareStatus = when {
            isOrange -> "orange"
            isGray -> "gray"
            else -> "none"
        }

        return AuditResult(sslActive, sslIssuer, cloudflareStatus, nsList)
    }

    private fun queryWhoisServer(domain: String, server: String): String {
        return try {
            val socket = Socket()
            socket.connect(java.net.InetSocketAddress(server, 43), 6000)
            socket.soTimeout = 6000
            val out = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))
            val query = when (server) {
                "whois.verisign-grs.com" -> "domain $domain\r\n"
                "whois.denic.de" -> "-T dn $domain\r\n"
                else -> "$domain\r\n"
            }
            out.print(query)
            out.flush()
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) response.append(line).append("\n")
            socket.close()
            response.toString()
        } catch (e: Exception) { "ERROR: ${e.message}" }
    }

    companion object {
        val whoisServers = mapOf(
            "com" to "whois.verisign-grs.com", "net" to "whois.verisign-grs.com",
            "org" to "whois.pir.org", "info" to "whois.afilias.net", "biz" to "whois.nic.biz",
            "io" to "whois.nic.io", "co" to "whois.nic.co", "me" to "whois.nic.me",
            "es" to "whois.nic.es", "mx" to "whois.nic.mx", "cl" to "whois.nic.cl",
            "ar" to "whois.nic.ar", "pe" to "kero.yachay.pe", "us" to "whois.nic.us",
            "la" to "whois.nic.la", "tv" to "whois.nic.tv", "cc" to "whois.nic.cc",
            "br" to "whois.registro.br", "ru" to "whois.tcinet.ru", "uk" to "whois.nic.uk",
            "fr" to "whois.nic.fr", "de" to "whois.denic.de", "it" to "whois.nic.it",
            "nl" to "whois.domain-registry.nl", "cn" to "whois.cnnic.cn", "in" to "whois.registry.in",
            "to" to "whois.tonic.to"
        )

        private val availablePatterns = listOf(
            "no match", "not found", "available", "no entries found",
            "no data found", "free", "status: free", "incorrect domain name",
            "is free", "no registered", "not registered", "no object found",
            "domain not found", "is available", "no match for", "no se encuentra",
            "el dominio no existe", "no matching record", "no domain found", "object_not_found"
        )

        private val takenPatterns = listOf(
            "registrar:", "creation date:", "domain name:", "registry domain id:",
            "status: registered", "registered:", "expir", "changed:", "owner:"
        )
    }
}
