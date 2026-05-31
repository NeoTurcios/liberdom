package com.neoturcios.liberdom

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var btnLangToggle: Button
    private lateinit var txtHeroTitle: TextView
    private lateinit var txtHeroSubtitle: TextView
    private lateinit var edtDomainInput: EditText
    private lateinit var btnCheck: Button
    
    private lateinit var progressBar: ProgressBar
    private lateinit var txtLoading: TextView
    
    private lateinit var cardResult: LinearLayout
    private lateinit var txtResultDomain: TextView
    private lateinit var txtResultBadge: TextView
    private lateinit var txtResultDesc: TextView
    
    private lateinit var lblTechnicalStatus: TextView
    private lateinit var txtTechnicalStatus: TextView
    private lateinit var lblServerIp: TextView
    private lateinit var txtServerIp: TextView
    private lateinit var lblRegistrar: TextView
    private lateinit var txtRegistrar: TextView
    private lateinit var lblCreationDate: TextView
    private lateinit var txtCreationDate: TextView
    private lateinit var lblMethod: TextView
    private lateinit var txtMethod: TextView
    private lateinit var txtFooter: TextView

    private lateinit var rowIp: LinearLayout
    private lateinit var rowRegistrar: LinearLayout
    private lateinit var rowCreated: LinearLayout

    private var currentLang = "es"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        btnLangToggle = findViewById(R.id.btnLangToggle)
        txtHeroTitle = findViewById(R.id.txtHeroTitle)
        txtHeroSubtitle = findViewById(R.id.txtHeroSubtitle)
        edtDomainInput = findViewById(R.id.edtDomainInput)
        btnCheck = findViewById(R.id.btnCheck)
        
        progressBar = findViewById(R.id.progressBar)
        txtLoading = findViewById(R.id.txtLoading)
        
        cardResult = findViewById(R.id.cardResult)
        txtResultDomain = findViewById(R.id.txtResultDomain)
        txtResultBadge = findViewById(R.id.txtResultBadge)
        txtResultDesc = findViewById(R.id.txtResultDesc)
        
        lblTechnicalStatus = findViewById(R.id.lblTechnicalStatus)
        txtTechnicalStatus = findViewById(R.id.txtTechnicalStatus)
        lblServerIp = findViewById(R.id.lblServerIp)
        txtServerIp = findViewById(R.id.txtServerIp)
        lblRegistrar = findViewById(R.id.lblRegistrar)
        txtRegistrar = findViewById(R.id.txtRegistrar)
        lblCreationDate = findViewById(R.id.lblCreationDate)
        txtCreationDate = findViewById(R.id.txtCreationDate)
        lblMethod = findViewById(R.id.lblMethod)
        txtMethod = findViewById(R.id.txtMethod)
        txtFooter = findViewById(R.id.txtFooter)

        rowIp = findViewById(R.id.rowIp)
        rowRegistrar = findViewById(R.id.rowRegistrar)
        rowCreated = findViewById(R.id.rowCreated)

        // Configurar idioma inicial (detectar del dispositivo)
        val defaultLocale = Locale.getDefault().language
        currentLang = if (defaultLocale == "en") "en" else "es"
        actualizarIdiomaInterfaz()

        // Eventos
        btnLangToggle.setOnClickListener {
            currentLang = if (currentLang == "es") "en" else "es"
            actualizarIdiomaInterfaz()
        }

        btnCheck.setOnClickListener {
            ejecutarChequeo()
        }

        edtDomainInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ejecutarChequeo()
                true
            } else {
                false
            }
        }
    }

    private fun actualizarIdiomaInterfaz() {
        if (currentLang == "es") {
            btnLangToggle.text = "🇺🇸 EN"
            txtHeroTitle.text = "Encuentra tu próximo dominio"
            txtHeroSubtitle.text = "Búsqueda directa e híbrida DNS/WHOIS de bajo nivel sin intermediarios ni cobros."
            edtDomainInput.hint = "Introduce tu dominio... (ej: miweb.com)"
            btnCheck.text = "Comprobar disponibilidad"
            lblTechnicalStatus.text = "Estado Técnico:"
            lblServerIp.text = "IP del Servidor:"
            lblRegistrar.text = "Registrador:"
            lblCreationDate.text = "Fecha de Creación:"
            lblMethod.text = "Método de Detección:"
            txtFooter.text = "Diseñado con amor y código abierto por NeoTurcios\nLicencia No Comercial © 2026"
        } else {
            btnLangToggle.text = "🇪🇸 ES"
            txtHeroTitle.text = "Find your next domain"
            txtHeroSubtitle.text = "Low-level direct and hybrid DNS/WHOIS search without intermediaries or premium fees."
            edtDomainInput.hint = "Enter your domain... (e.g. myweb.com)"
            btnCheck.text = "Check availability"
            lblTechnicalStatus.text = "Technical Status:"
            lblServerIp.text = "Server IP:"
            lblRegistrar.text = "Registrar:"
            lblCreationDate.text = "Creation Date:"
            lblMethod.text = "Detection Method:"
            txtFooter.text = "Designed with love and open source by NeoTurcios\nNon-Commercial License © 2026"
        }

        // Si la tarjeta de resultados está abierta, actualizar sus etiquetas estáticas
        if (cardResult.visibility == View.VISIBLE) {
            val badgeText = txtResultBadge.text.toString().lowercase()
            if (badgeText.contains("disponible") || badgeText.contains("available") || badgeText.contains("libre") || badgeText.contains("free")) {
                txtResultBadge.text = if (currentLang == "es") "Libre" else "Free"
                txtResultDesc.text = if (currentLang == "es") "¡Felicidades! Este dominio está libre. Puedes registrarlo ahora mismo en tu proveedor favorito." else "Congratulations! This domain is free. You can register it right now with your favorite provider."
            } else if (badgeText.contains("registrado") || badgeText.contains("taken") || badgeText.contains("comprado")) {
                txtResultBadge.text = if (currentLang == "es") "Registrado" else "Taken"
                txtResultDesc.text = if (currentLang == "es") "Dominio ya ocupado. Está comprado y registrado en internet." else "Domain already taken. It is purchased and registered on the internet."
            } else {
                txtResultBadge.text = if (currentLang == "es") "Dudoso" else "Unknown"
                txtResultDesc.text = if (currentLang == "es") "No se pudo determinar con certeza (Límite de peticiones WHOIS o TLD no soportado)." else "Could not be determined with certainty (WHOIS rate limit or unsupported TLD)."
            }
        }
    }

    private fun ejecutarChequeo() {
        val rawInput = edtDomainInput.text.toString().trim()
        if (rawInput.isEmpty()) {
            val alert = if (currentLang == "es") "Por favor, introduce un dominio" else "Please enter a domain"
            Toast.makeText(this, alert, Toast.LENGTH_SHORT).show()
            return
        }

        // Limpiar entrada
        var domain = rawInput.lowercase(Locale.getDefault())
            .replace("https://", "")
            .replace("http://", "")
            .replace("www.", "")
        domain = domain.split("/")[0]

        if (!domain.contains(".") || domain.length < 4) {
            val alert = if (currentLang == "es") "Formato de dominio inválido" else "Invalid domain format"
            Toast.makeText(this, alert, Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar Loader
        progressBar.visibility = View.VISIBLE
        txtLoading.visibility = View.VISIBLE
        txtLoading.text = if (currentLang == "es") "Consultando bases de datos para $domain..." else "Querying global databases for $domain..."
        cardResult.visibility = View.GONE

        // Ejecutar en hilo de fondo para evitar NetworkOnMainThreadException
        Thread {
            try {
                // 1. Fase DNS
                var ip: String? = null
                try {
                    val address = InetAddress.getByName(domain)
                    ip = address.hostAddress
                } catch (e: Exception) {
                    // Falló DNS, pasamos a WHOIS
                }

                if (ip != null) {
                    // Está comprado por DNS
                    runOnUiThread {
                        mostrarResultado("comprado", if (currentLang == "es") "Registrado (Activo por DNS)" else "Registered (Active via DNS)", ip, null, null, if (currentLang == "es") "Resolución DNS" else "DNS Resolution", domain)
                    }
                    return@Thread
                }

                // 2. Fase WHOIS
                val tld = domain.substringAfterLast(".").lowercase(Locale.getDefault())
                val tldServers = mapOf(
                    "com" to "whois.verisign-grs.com",
                    "net" to "whois.verisign-grs.com",
                    "org" to "whois.pir.org",
                    "info" to "whois.afilias.net",
                    "biz" to "whois.nic.biz",
                    "io" to "whois.nic.io",
                    "co" to "whois.nic.co",
                    "me" to "whois.nic.me",
                    "es" to "whois.nic.es",
                    "mx" to "whois.mx",
                    "cl" to "whois.nic.cl",
                    "ar" to "whois.nic.ar",
                    "pe" to "kero.yachay.pe",
                    "us" to "whois.nic.us",
                    "la" to "whois.nic.la",
                    "tv" to "whois.nic.tv",
                    "cc" to "whois.nic.cc",
                    "br" to "whois.registro.br",
                    "ru" to "whois.tcinet.ru",
                    "uk" to "whois.nic.uk",
                    "fr" to "whois.nic.fr",
                    "de" to "whois.denic.de",
                    "it" to "whois.nic.it",
                    "nl" to "whois.domain-registry.nl",
                    "cn" to "whois.cnnic.cn",
                    "in" to "whois.registry.in",
                    "to" to "whois.tonic.to"
                )

                val server = tldServers[tld] ?: "whois.iana.org"
                val rawWhois = queryWhoisServer(domain, server)

                if (rawWhois.startsWith("ERROR:")) {
                    runOnUiThread {
                        ocultarLoader()
                        val alert = if (currentLang == "es") "Error de conexión de red" else "Network connection error"
                        Toast.makeText(this@MainActivity, "$alert: ${rawWhois.substring(6)}", Toast.LENGTH_LONG).show()
                    }
                    return@Thread
                }

                // Analizar respuesta WHOIS
                val resLower = rawWhois.lowercase(Locale.getDefault())
                var referServer: String? = null
                if (resLower.contains("refer:")) {
                    val lines = rawWhois.split("\n")
                    for (line in lines) {
                        if (line.trim().lowercase(Locale.getDefault()).startsWith("refer:")) {
                            referServer = line.substringAfter(":").trim()
                            break
                        }
                    }
                }

                var finalWhois = rawWhois
                if (!referServer.isNullOrEmpty()) {
                    val redirectedWhois = queryWhoisServer(domain, referServer)
                    if (!redirectedWhois.startsWith("ERROR:")) {
                        finalWhois = redirectedWhois
                    }
                }

                val finalLower = finalWhois.lowercase(Locale.getDefault())

                val patronesDisponible = listOf(
                    "no match", "not found", "available", "no entries found",
                    "no data found", "free", "status: free", "incorrect domain name",
                    "is free", "no registered", "not registered", "no object found",
                    "domain not found", "is available", "no match for", "no se encuentra",
                    "el dominio no existe", "no matching record", "no domain found",
                    "object_not_found"
                )

                var estaDisponible = false
                for (pat in patronesDisponible) {
                    if (finalLower.contains(pat)) {
                        estaDisponible = true
                        break
                    }
                }

                val metodo = if (currentLang == "es") "Consulta WHOIS Socket 43" else "WHOIS Socket 43 Query"

                if (estaDisponible) {
                    runOnUiThread {
                        mostrarResultado("disponible", if (currentLang == "es") "¡Disponible para registro!" else "Available for registration!", null, null, null, metodo, domain)
                    }
                    return@Thread
                }

                val patronesComprado = listOf(
                    "registrar:", "creation date:", "domain name:", "registry domain id:",
                    "status: registered", "registered:", "expir", "changed:", "owner:"
                )

                var estaComprado = false
                for (pat in patronesComprado) {
                    if (finalLower.contains(pat)) {
                        estaComprado = true
                        break
                    }
                }

                // Intentar extraer registrador y fecha
                var registrador: String? = null
                var fechaCreacion: String? = null

                val lines = finalWhois.split("\n")
                for (line in lines) {
                    val lineClean = line.trim()
                    val lineLower = lineClean.lowercase(Locale.getDefault())
                    if (lineLower.contains("creation date") || lineLower.contains("created:") || lineLower.contains("fecha de creacion")) {
                        val part = lineClean.substringAfter(":").trim()
                        if (part.isNotEmpty()) fechaCreacion = part
                    }
                    if (lineLower.contains("registrar:") || lineLower.contains("registrador:")) {
                        val part = lineClean.substringAfter(":").trim()
                        if (part.isNotEmpty()) registrador = part
                    }
                }

                if (estaComprado || finalWhois.length > 250) {
                    runOnUiThread {
                        mostrarResultado("comprado", if (currentLang == "es") "Registrado (Confirmado por WHOIS)" else "Registered (Confirmed by WHOIS)", null, registrador, fechaCreacion, metodo, domain)
                    }
                } else {
                    runOnUiThread {
                        mostrarResultado("desconocido", if (currentLang == "es") "No se pudo determinar (Límite / TLD no soportado)" else "Could not determine (Limit / TLD not supported)", null, null, null, metodo, domain)
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    ocultarLoader()
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun queryWhoisServer(domain: String, server: String): String {
        return try {
            val socket = Socket(server, 43)
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
            while (reader.readLine().also { line = it } != null) {
                response.append(line).append("\n")
            }
            socket.close()
            response.toString()
        } catch (e: Exception) {
            "ERROR: ${e.message}"
        }
    }

    private fun mostrarResultado(estado: String, detalle: String, ip: String?, registrador: String?, fechaCreacion: String?, metodo: String, domain: String) {
        ocultarLoader()
        cardResult.visibility = View.VISIBLE
        txtResultDomain.text = domain.uppercase(Locale.getDefault())
        txtTechnicalStatus.text = detalle
        txtMethod.text = metodo

        // Ajustar visual de tarjeta
        when (estado) {
            "disponible" -> {
                cardResult.setBackgroundResource(R.drawable.card_background_available)
                txtResultBadge.text = if (currentLang == "es") "Libre" else "Free"
                txtResultBadge.setTextColor(0xff00e676.toInt())
                txtResultBadge.setBackgroundColor(0x1a00e676)
                txtResultDesc.text = if (currentLang == "es") "¡Felicidades! Este dominio está libre. Puedes registrarlo ahora mismo en tu proveedor favorito." else "Congratulations! This domain is free. You can register it right now with your favorite provider."
                txtResultDesc.setTextColor(0xff00e676.toInt())
            }
            "comprado" -> {
                cardResult.setBackgroundResource(R.drawable.card_background_taken)
                txtResultBadge.text = if (currentLang == "es") "Registrado" else "Taken"
                txtResultBadge.setTextColor(0xffef4444.toInt())
                txtResultBadge.setBackgroundColor(0x1aef4444)
                txtResultDesc.text = if (currentLang == "es") "Dominio ya ocupado. Está comprado y registrado en internet." else "Domain already taken. It is purchased and registered on the internet."
                txtResultDesc.setTextColor(0xffef4444.toInt())
            }
            else -> {
                cardResult.setBackgroundResource(R.drawable.card_background_unknown)
                txtResultBadge.text = if (currentLang == "es") "Dudoso" else "Unknown"
                txtResultBadge.setTextColor(0xffeab308.toInt())
                txtResultBadge.setBackgroundColor(0x1aeab308)
                txtResultDesc.text = if (currentLang == "es") "No se pudo determinar con certeza (Límite de peticiones WHOIS o TLD no soportado)." else "Could not be determined with certainty (WHOIS rate limit or unsupported TLD)."
                txtResultDesc.setTextColor(0xffeab308.toInt())
            }
        }

        // Mostrar / Ocultar campos de IP, Registrador y Creación
        if (ip != null) {
            rowIp.visibility = View.VISIBLE
            txtServerIp.text = ip
        } else {
            rowIp.visibility = View.GONE
        }

        if (registrador != null) {
            rowRegistrar.visibility = View.VISIBLE
            txtRegistrar.text = registrador
        } else {
            rowRegistrar.visibility = View.GONE
        }

        if (fechaCreacion != null) {
            rowCreated.visibility = View.VISIBLE
            txtCreationDate.text = fechaCreacion
        } else {
            rowCreated.visibility = View.GONE
        }
    }

    private fun ocultarLoader() {
        progressBar.visibility = View.GONE
        txtLoading.visibility = View.GONE
    }
}
