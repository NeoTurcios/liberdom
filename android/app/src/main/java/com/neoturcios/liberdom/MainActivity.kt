package com.neoturcios.liberdom

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Dynamic Island Header
    private lateinit var dynamicIsland: View
    private lateinit var dynamicIslandContent: ViewGroup
    private lateinit var imgIslandLogo: ImageView
    private lateinit var txtIslandTitle: TextView
    private lateinit var islandStatusContainer: View
    private lateinit var txtIslandStatus: TextView
    private lateinit var btnLangToggle: View
    private lateinit var imgLangFlag: ImageView
    private lateinit var txtLangCode: TextView

    // Bottom Glass Dock Navigation
    private lateinit var navTabSearch: View
    private lateinit var navTabBulk: View
    private lateinit var navTabGenerator: View
    private lateinit var navTabSaved: View
    
    private lateinit var imgTabSearch: ImageView
    private lateinit var imgTabBulk: ImageView
    private lateinit var imgTabGenerator: ImageView
    private lateinit var imgTabSaved: ImageView
    
    private lateinit var txtTabSearch: TextView
    private lateinit var txtTabBulk: TextView
    private lateinit var txtTabGenerator: TextView
    private lateinit var txtTabSaved: TextView

    // Content Panels
    private lateinit var panelSearch: View
    private lateinit var panelBulk: View
    private lateinit var panelGenerator: View
    private lateinit var panelSaved: View

    // Panel 1: Search View components
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

    // Search Result Action Toolbar
    private lateinit var btnActionSave: ImageButton
    private lateinit var btnActionPdf: ImageButton
    private lateinit var btnActionShare: ImageButton
    private lateinit var btnActionCopy: ImageButton

    // Registrar Suggested Links
    private lateinit var lblRegisterLinks: TextView
    private lateinit var btnLinkNamecheap: Button
    private lateinit var btnLinkCloudflare: Button
    private lateinit var btnLinkGodaddy: Button

    // Panel 2: Bulk Check components
    private lateinit var txtBulkTitle: TextView
    private lateinit var txtBulkSubtitle: TextView
    private lateinit var edtBulkInput: EditText
    private lateinit var btnBulkCheck: Button
    private lateinit var containerBulkStats: View
    private lateinit var txtBulkStatChecked: TextView
    private lateinit var txtBulkStatFree: TextView
    private lateinit var txtBulkStatTaken: TextView
    private lateinit var bulkProgressBar: ProgressBar
    private lateinit var containerBulkResults: LinearLayout

    // Panel 3: Name Generator components
    private lateinit var txtGeneratorTitle: TextView
    private lateinit var txtGeneratorSubtitle: TextView
    private lateinit var edtGeneratorKeyword: EditText
    private lateinit var lblSelectTld: TextView
    private lateinit var chkCom: CheckBox
    private lateinit var chkNet: CheckBox
    private lateinit var chkOrg: CheckBox
    private lateinit var chkIo: CheckBox
    private lateinit var chkApp: CheckBox
    private lateinit var btnGenerateNames: Button
    private lateinit var containerGeneratorResults: LinearLayout

    // Panel 4: Library / Saved components
    private lateinit var txtSavedTitle: TextView
    private lateinit var btnTabSaved: Button
    private lateinit var btnTabHistory: Button
    private lateinit var lblListCount: TextView
    private lateinit var btnClearList: Button
    private lateinit var containerSavedResults: LinearLayout

    // State Variables
    private var currentLang = "es"
    private var libraryTabMode = "saved" // "saved" or "history"
    private var lastCheckedResult: DomainResult? = null

    // Domain data class
    data class DomainResult(
        val domain: String,
        val status: String, // "disponible", "comprado", "desconocido"
        val detail: String,
        val ip: String?,
        val registrar: String?,
        val creationDate: String?,
        val method: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Header & Dynamic Island
        dynamicIsland = findViewById(R.id.dynamicIsland)
        dynamicIslandContent = findViewById(R.id.dynamicIslandContent)
        imgIslandLogo = findViewById(R.id.imgIslandLogo)
        txtIslandTitle = findViewById(R.id.txtIslandTitle)
        islandStatusContainer = findViewById(R.id.islandStatusContainer)
        txtIslandStatus = findViewById(R.id.txtIslandStatus)
        btnLangToggle = findViewById(R.id.btnLangToggle)
        imgLangFlag = findViewById(R.id.imgLangFlag)
        txtLangCode = findViewById(R.id.txtLangCode)

        // Initialize Navigation Tabs
        navTabSearch = findViewById(R.id.navTabSearch)
        navTabBulk = findViewById(R.id.navTabBulk)
        navTabGenerator = findViewById(R.id.navTabGenerator)
        navTabSaved = findViewById(R.id.navTabSaved)
        
        imgTabSearch = findViewById(R.id.imgTabSearch)
        imgTabBulk = findViewById(R.id.imgTabBulk)
        imgTabGenerator = findViewById(R.id.imgTabGenerator)
        imgTabSaved = findViewById(R.id.imgTabSaved)
        
        txtTabSearch = findViewById(R.id.txtTabSearch)
        txtTabBulk = findViewById(R.id.txtTabBulk)
        txtTabGenerator = findViewById(R.id.txtTabGenerator)
        txtTabSaved = findViewById(R.id.txtTabSaved)

        // Initialize Content Panels
        panelSearch = findViewById(R.id.panelSearch)
        panelBulk = findViewById(R.id.panelBulk)
        panelGenerator = findViewById(R.id.panelGenerator)
        panelSaved = findViewById(R.id.panelSaved)

        // Initialize Panel 1 Views
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

        // Search Actions & Links
        btnActionSave = findViewById(R.id.btnActionSave)
        btnActionPdf = findViewById(R.id.btnActionPdf)
        btnActionShare = findViewById(R.id.btnActionShare)
        btnActionCopy = findViewById(R.id.btnActionCopy)

        lblRegisterLinks = findViewById(R.id.lblRegisterLinks)
        btnLinkNamecheap = findViewById(R.id.btnLinkNamecheap)
        btnLinkCloudflare = findViewById(R.id.btnLinkCloudflare)
        btnLinkGodaddy = findViewById(R.id.btnLinkGodaddy)

        // Initialize Panel 2 Views (Bulk Search)
        txtBulkTitle = findViewById(R.id.txtBulkTitle)
        txtBulkSubtitle = findViewById(R.id.txtBulkSubtitle)
        edtBulkInput = findViewById(R.id.edtBulkInput)
        btnBulkCheck = findViewById(R.id.btnBulkCheck)
        containerBulkStats = findViewById(R.id.containerBulkStats)
        txtBulkStatChecked = findViewById(R.id.txtBulkStatChecked)
        txtBulkStatFree = findViewById(R.id.txtBulkStatFree)
        txtBulkStatTaken = findViewById(R.id.txtBulkStatTaken)
        bulkProgressBar = findViewById(R.id.bulkProgressBar)
        containerBulkResults = findViewById(R.id.containerBulkResults)

        // Initialize Panel 3 Views (Generator)
        txtGeneratorTitle = findViewById(R.id.txtGeneratorTitle)
        txtGeneratorSubtitle = findViewById(R.id.txtGeneratorSubtitle)
        edtGeneratorKeyword = findViewById(R.id.edtGeneratorKeyword)
        lblSelectTld = findViewById(R.id.lblSelectTld)
        chkCom = findViewById(R.id.chkCom)
        chkNet = findViewById(R.id.chkNet)
        chkOrg = findViewById(R.id.chkOrg)
        chkIo = findViewById(R.id.chkIo)
        chkApp = findViewById(R.id.chkApp)
        btnGenerateNames = findViewById(R.id.btnGenerateNames)
        containerGeneratorResults = findViewById(R.id.containerGeneratorResults)

        // Initialize Panel 4 Views (Library / Saved)
        txtSavedTitle = findViewById(R.id.txtSavedTitle)
        btnTabSaved = findViewById(R.id.btnTabSaved)
        btnTabHistory = findViewById(R.id.btnTabHistory)
        lblListCount = findViewById(R.id.lblListCount)
        btnClearList = findViewById(R.id.btnClearList)
        containerSavedResults = findViewById(R.id.containerSavedResults)

        // Configure initial language (detect device language)
        val defaultLocale = Locale.getDefault().language
        currentLang = if (defaultLocale == "en") "en" else "es"
        actualizarIdiomaInterfaz()

        // Tab Navigation click handlers
        navTabSearch.setOnClickListener { switchPanel("search") }
        navTabBulk.setOnClickListener { switchPanel("bulk") }
        navTabGenerator.setOnClickListener { switchPanel("generator") }
        navTabSaved.setOnClickListener { switchPanel("saved") }

        // Language toggle click handler
        btnLangToggle.setOnClickListener {
            currentLang = if (currentLang == "es") "en" else "es"
            actualizarIdiomaInterfaz()
        }

        // Panel 1: Single Check button and action triggers
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

        txtFooter.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/NeoTurcios/liberdom"))
                startActivity(intent)
            } catch (e: Exception) {
                // Ignore
            }
        }

        // Action Toolbar click handlers
        btnActionSave.setOnClickListener {
            lastCheckedResult?.let { res ->
                val saved = toggleSavedDomain(res)
                btnActionSave.setImageResource(if (saved) R.drawable.ic_star else R.drawable.ic_star_border)
                Toast.makeText(
                    this,
                    if (currentLang == "es") {
                        if (saved) "Guardado en Favoritos" else "Eliminado de Favoritos"
                    } else {
                        if (saved) "Saved to Bookmarks" else "Removed from Bookmarks"
                    },
                    Toast.LENGTH_SHORT
                ).show()
                if (panelSaved.visibility == View.VISIBLE) refreshLibraryPanel()
            }
        }

        btnActionPdf.setOnClickListener {
            lastCheckedResult?.let { res ->
                exportResultToPdf(res)
            }
        }

        btnActionShare.setOnClickListener {
            lastCheckedResult?.let { res ->
                shareDomainDetails(res)
            }
        }

        btnActionCopy.setOnClickListener {
            lastCheckedResult?.let { res ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("LiberDom", res.domain.lowercase(Locale.getDefault()))
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    this,
                    if (currentLang == "es") "¡Copiado al portapapeles!" else "Copied to clipboard!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Registrar suggestion click handlers
        btnLinkNamecheap.setOnClickListener {
            lastCheckedResult?.let {
                openRegistrarLink("https://www.namecheap.com/domains/registration/results/?domain=${it.domain.lowercase(Locale.getDefault())}")
            }
        }

        btnLinkCloudflare.setOnClickListener {
            lastCheckedResult?.let {
                openRegistrarLink("https://dash.cloudflare.com/domains/register?query=${it.domain.lowercase(Locale.getDefault())}")
            }
        }

        btnLinkGodaddy.setOnClickListener {
            lastCheckedResult?.let {
                openRegistrarLink("https://www.godaddy.com/domainfind/search?domainToCheck=${it.domain.lowercase(Locale.getDefault())}")
            }
        }

        // Panel 2: Bulk search check
        btnBulkCheck.setOnClickListener {
            ejecutarChequeoLote()
        }

        // Panel 3: Name generator check
        btnGenerateNames.setOnClickListener {
            ejecutarGeneracionNombres()
        }

        // Panel 4: Library sub-tabs & cleaning
        btnTabSaved.setOnClickListener {
            libraryTabMode = "saved"
            btnTabSaved.setBackgroundColor(Color.parseColor("#1C2436"))
            btnTabSaved.setTextColor(Color.WHITE)
            btnTabHistory.setBackgroundColor(Color.TRANSPARENT)
            btnTabHistory.setTextColor(Color.GRAY)
            refreshLibraryPanel()
        }

        btnTabHistory.setOnClickListener {
            libraryTabMode = "history"
            btnTabHistory.setBackgroundColor(Color.parseColor("#1C2436"))
            btnTabHistory.setTextColor(Color.WHITE)
            btnTabSaved.setBackgroundColor(Color.TRANSPARENT)
            btnTabSaved.setTextColor(Color.GRAY)
            refreshLibraryPanel()
        }

        btnClearList.setOnClickListener {
            val title = if (currentLang == "es") "Confirmar eliminación" else "Confirm deletion"
            val message = if (currentLang == "es") {
                if (libraryTabMode == "saved") "¿Seguro que quieres borrar todos tus dominios guardados?" else "¿Seguro que quieres limpiar todo el historial?"
            } else {
                if (libraryTabMode == "saved") "Are you sure you want to clear all saved domains?" else "Are you sure you want to clear search history?"
            }
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(if (currentLang == "es") "Borrar todo" else "Clear All") { _, _ ->
                    val prefKey = if (libraryTabMode == "saved") "liberdom_saved" else "liberdom_history"
                    val sp = getSharedPreferences("liberdom_prefs", Context.MODE_PRIVATE)
                    sp.edit().putString(prefKey, "[]").apply()
                    refreshLibraryPanel()
                }
                .setNegativeButton(if (currentLang == "es") "Cancelar" else "Cancel", null)
                .show()
        }
    }

    private fun switchPanel(panel: String) {
        // Hide soft keyboard
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // Beautiful glass bottom bar active states
        
        val colorActive = Color.parseColor("#00e676")
        val colorInactive = Color.parseColor("#6B7280")

        imgTabSearch.setColorFilter(if (panel == "search") colorActive else colorInactive)
        txtTabSearch.setTextColor(if (panel == "search") colorActive else colorInactive)
        txtTabSearch.setTypeface(null, if (panel == "search") Typeface.BOLD else Typeface.NORMAL)

        imgTabBulk.setColorFilter(if (panel == "bulk") colorActive else colorInactive)
        txtTabBulk.setTextColor(if (panel == "bulk") colorActive else colorInactive)
        txtTabBulk.setTypeface(null, if (panel == "bulk") Typeface.BOLD else Typeface.NORMAL)

        imgTabGenerator.setColorFilter(if (panel == "generator") colorActive else colorInactive)
        txtTabGenerator.setTextColor(if (panel == "generator") colorActive else colorInactive)
        txtTabGenerator.setTypeface(null, if (panel == "generator") Typeface.BOLD else Typeface.NORMAL)

        imgTabSaved.setColorFilter(if (panel == "saved") colorActive else colorInactive)
        txtTabSaved.setTextColor(if (panel == "saved") colorActive else colorInactive)
        txtTabSaved.setTypeface(null, if (panel == "saved") Typeface.BOLD else Typeface.NORMAL)

        // Slide/Fade layouts transition manager
        TransitionManager.beginDelayedTransition(findViewById(R.id.contentContainer))
        
        panelSearch.visibility = if (panel == "search") View.VISIBLE else View.GONE
        panelBulk.visibility = if (panel == "bulk") View.VISIBLE else View.GONE
        panelGenerator.visibility = if (panel == "generator") View.VISIBLE else View.GONE
        panelSaved.visibility = if (panel == "saved") View.VISIBLE else View.GONE

        if (panel == "saved") {
            refreshLibraryPanel()
        }
    }

    private fun actualizarIdiomaInterfaz() {
        if (currentLang == "es") {
            imgLangFlag.setImageResource(R.drawable.flag_es)
            txtLangCode.text = "ES"

            txtHeroTitle.text = "Encuentra tu próximo dominio"
            txtHeroSubtitle.text = "Búsqueda directa e híbrida DNS/WHOIS de bajo nivel sin intermediarios ni cobros."
            edtDomainInput.hint = "Introduce tu dominio... (ej: miweb.com)"
            btnCheck.text = "Comprobar disponibilidad"
            lblTechnicalStatus.text = "Estado Técnico:"
            lblServerIp.text = "IP del Servidor:"
            lblRegistrar.text = "Registrador:"
            lblCreationDate.text = "Fecha de Creación:"
            lblMethod.text = "Método de Detección:"
            txtFooter.text = "Diseñado con amor y código abierto por NeoTurcios\nGitHub: github.com/NeoTurcios/liberdom\nLicencia No Comercial © 2026"

            // Panel Bulk
            txtBulkTitle.text = "Búsqueda por Lote"
            txtBulkSubtitle.text = "Comprueba varios dominios escribiendo uno por cada línea."
            edtBulkInput.hint = "midominio.com\nejemplo.net\nmiempresa.app"
            btnBulkCheck.text = "Escanear Lote"

            // Panel Generator
            txtGeneratorTitle.text = "Generador Creativo"
            txtGeneratorSubtitle.text = "Escribe una palabra clave básica para sugerirte marcas únicas y disponibles."
            edtGeneratorKeyword.hint = "Ejemplo: cloud, tech, smart, aero"
            lblSelectTld.text = "Extensiones a comprobar:"
            btnGenerateNames.text = "Generar nombres"

            // Panel Saved
            txtSavedTitle.text = "Mi Biblioteca"
            btnTabSaved.text = "Guardados"
            btnTabHistory.text = "Historial"
            btnClearList.text = "Limpiar"
            
            lblRegisterLinks.text = "Registrar en proveedores sugeridos:"
        } else {
            imgLangFlag.setImageResource(R.drawable.flag_us)
            txtLangCode.text = "EN"

            txtHeroTitle.text = "Find your next domain"
            txtHeroSubtitle.text = "Low-level direct and hybrid DNS/WHOIS search without intermediaries or premium fees."
            edtDomainInput.hint = "Enter your domain... (e.g. myweb.com)"
            btnCheck.text = "Check availability"
            lblTechnicalStatus.text = "Technical Status:"
            lblServerIp.text = "Server IP:"
            lblRegistrar.text = "Registrar:"
            lblCreationDate.text = "Creation Date:"
            lblMethod.text = "Detection Method:"
            txtFooter.text = "Designed with love and open source by NeoTurcios\nGitHub: github.com/NeoTurcios/liberdom\nNon-Commercial License © 2026"

            // Panel Bulk
            txtBulkTitle.text = "Bulk Domain Check"
            txtBulkSubtitle.text = "Check multiple domains simultaneously, writing one domain per line."
            edtBulkInput.hint = "mydomain.com\nexample.net\nmycompany.app"
            btnBulkCheck.text = "Scan Bulk Domains"

            // Panel Generator
            txtGeneratorTitle.text = "Creative Generator"
            txtGeneratorSubtitle.text = "Enter a basic keyword to generate unique brand concepts."
            edtGeneratorKeyword.hint = "e.g. cloud, tech, smart, aero"
            lblSelectTld.text = "Extensions to check:"
            btnGenerateNames.text = "Generate names"

            // Panel Saved
            txtSavedTitle.text = "My Library"
            btnTabSaved.text = "Saved"
            btnTabHistory.text = "History"
            btnClearList.text = "Clear"
            
            lblRegisterLinks.text = "Register with suggested registrars:"
        }

        // Sync list labels
        if (panelSaved.visibility == View.VISIBLE) {
            refreshLibraryPanel()
        }

        // Sync single result card language
        lastCheckedResult?.let {
            mostrarResultado(it)
        }
    }

    private fun showDynamicIslandLoader(show: Boolean, domainName: String = "") {
        TransitionManager.beginDelayedTransition(dynamicIslandContent)
        if (show) {
            islandStatusContainer.visibility = View.VISIBLE
            txtIslandStatus.text = if (currentLang == "es") "Escanear: $domainName..." else "Scanning: $domainName..."
            btnLangToggle.visibility = View.GONE
        } else {
            islandStatusContainer.visibility = View.GONE
            btnLangToggle.visibility = View.VISIBLE
        }
    }

    private fun ejecutarChequeo() {
        val rawInput = edtDomainInput.text.toString().trim()
        if (rawInput.isEmpty()) {
            val alert = if (currentLang == "es") "Por favor, introduce un dominio" else "Please enter a domain"
            Toast.makeText(this, alert, Toast.LENGTH_SHORT).show()
            return
        }

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

        // Show Loader & Dynamic Island Animation
        progressBar.visibility = View.VISIBLE
        txtLoading.visibility = View.VISIBLE
        txtLoading.text = if (currentLang == "es") "Consultando bases de datos para $domain..." else "Querying global databases for $domain..."
        cardResult.visibility = View.GONE
        
        showDynamicIslandLoader(true, domain)

        Thread {
            try {
                val result = performSingleLookup(domain)
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    txtLoading.visibility = View.GONE
                    showDynamicIslandLoader(false)
                    
                    lastCheckedResult = result
                    mostrarResultado(result)
                    
                    // Add to local history list
                    addToLocalStore("history", result)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    txtLoading.visibility = View.GONE
                    showDynamicIslandLoader(false)
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun performSingleLookup(domain: String): DomainResult {
        // 1. DNS Phase
        var ip: String? = null
        try {
            val address = InetAddress.getByName(domain)
            ip = address.hostAddress
        } catch (e: Exception) {
            // DNS failed, continue to WHOIS
        }

        if (ip != null) {
            return DomainResult(
                domain = domain,
                status = "comprado",
                detail = if (currentLang == "es") "Registrado (Activo por DNS)" else "Registered (Active via DNS)",
                ip = ip,
                registrar = null,
                creationDate = null,
                method = if (currentLang == "es") "Resolución DNS" else "DNS Resolution"
            )
        }

        // 2. WHOIS Phase
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
            "mx" to "whois.nic.mx",
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
            return DomainResult(
                domain = domain,
                status = "desconocido",
                detail = if (currentLang == "es") "Error de red: ${rawWhois.substring(6)}" else "Network error: ${rawWhois.substring(6)}",
                ip = null,
                registrar = null,
                creationDate = null,
                method = if (currentLang == "es") "Límite / Red WHOIS" else "Limit / Red WHOIS"
            )
        }

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
            return DomainResult(
                domain = domain,
                status = "disponible",
                detail = if (currentLang == "es") "¡Disponible para registro!" else "Available for registration!",
                ip = null,
                registrar = null,
                creationDate = null,
                method = metodo
            )
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

        // Try extracting registrar and dates
        var registrar: String? = null
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
                if (part.isNotEmpty()) registrar = part
            }
        }

        if (estaComprado || finalWhois.length > 250) {
            return DomainResult(
                domain = domain,
                status = "comprado",
                detail = if (currentLang == "es") "Registrado (Confirmado por WHOIS)" else "Registered (Confirmed by WHOIS)",
                ip = null,
                registrar = registrar,
                creationDate = fechaCreacion,
                method = metodo
            )
        } else {
            return DomainResult(
                domain = domain,
                status = "desconocido",
                detail = if (currentLang == "es") "No se pudo determinar con certeza" else "Could not be determined with certainty",
                ip = null,
                registrar = null,
                creationDate = null,
                method = metodo
            )
        }
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

            val response = java.lang.StringBuilder()
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

    private fun mostrarResultado(result: DomainResult) {
        cardResult.visibility = View.VISIBLE
        txtResultDomain.text = result.domain.uppercase(Locale.getDefault())
        txtTechnicalStatus.text = result.detail
        txtMethod.text = result.method

        // bookmark active state update
        val bookmarked = isDomainSaved(result.domain)
        btnActionSave.setImageResource(if (bookmarked) R.drawable.ic_star else R.drawable.ic_star_border)

        when (result.status) {
            "disponible" -> {
                cardResult.setBackgroundResource(R.drawable.card_background_available)
                txtResultBadge.text = if (currentLang == "es") "Libre" else "Free"
                txtResultBadge.setTextColor(Color.parseColor("#00e676"))
                txtResultBadge.setBackgroundColor(Color.parseColor("#1a00e676"))
                
                txtResultDesc.text = if (currentLang == "es") "¡Felicidades! Este dominio está libre. Puedes registrarlo ahora mismo en tu proveedor favorito." else "Congratulations! This domain is free. You can register it right now with your favorite provider."
                txtResultDesc.setTextColor(Color.parseColor("#00e676"))
            }
            "comprado" -> {
                cardResult.setBackgroundResource(R.drawable.card_background_taken)
                txtResultBadge.text = if (currentLang == "es") "Registrado" else "Taken"
                txtResultBadge.setTextColor(Color.parseColor("#ef4444"))
                txtResultBadge.setBackgroundColor(Color.parseColor("#1aef4444"))
                
                txtResultDesc.text = if (currentLang == "es") "Dominio ya ocupado. Está comprado y registrado en internet." else "Domain already taken. It is purchased and registered on the internet."
                txtResultDesc.setTextColor(Color.parseColor("#ef4444"))
            }
            else -> {
                cardResult.setBackgroundResource(R.drawable.card_background_unknown)
                txtResultBadge.text = if (currentLang == "es") "Dudoso" else "Unknown"
                txtResultBadge.setTextColor(Color.parseColor("#eab308"))
                txtResultBadge.setBackgroundColor(Color.parseColor("#1aeab308"))
                
                txtResultDesc.text = if (currentLang == "es") "No se pudo determinar con certeza (Límite de peticiones WHOIS o TLD no soportado)." else "Could not be determined with certainty (WHOIS rate limit or unsupported TLD)."
                txtResultDesc.setTextColor(Color.parseColor("#eab308"))
            }
        }

        // Show/Hide rows
        if (result.ip != null) {
            rowIp.visibility = View.VISIBLE
            txtServerIp.text = result.ip
        } else {
            rowIp.visibility = View.GONE
        }

        if (result.registrar != null) {
            rowRegistrar.visibility = View.VISIBLE
            txtRegistrar.text = result.registrar
        } else {
            rowRegistrar.visibility = View.GONE
        }

        if (result.creationDate != null) {
            rowCreated.visibility = View.VISIBLE
            txtCreationDate.text = result.creationDate
        } else {
            rowCreated.visibility = View.GONE
        }
    }

    private fun openRegistrarLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No browser found", Toast.LENGTH_SHORT).show()
        }
    }

    // ==========================================
    // NATIVE PDF EXPORTER REPORT
    // ==========================================
    private fun exportResultToPdf(res: DomainResult) {
        try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Format
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paintTitle = Paint().apply {
                color = Color.parseColor("#070b13")
                textSize = 28f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val paintSub = Paint().apply {
                color = Color.parseColor("#4B5563")
                textSize = 12f
            }

            val paintSection = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val paintBody = Paint().apply {
                color = Color.parseColor("#111827")
                textSize = 12f
            }

            val paintBold = Paint().apply {
                color = Color.parseColor("#111827")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val paintDivider = Paint().apply {
                color = Color.parseColor("#E5E7EB")
                strokeWidth = 2f
            }

            // 1. Draw header background decoration
            val headerBg = Paint().apply { color = Color.parseColor("#CC111827") }
            canvas.drawRect(0f, 0f, 595f, 100f, headerBg)

            // Draw header text
            paintTitle.color = Color.WHITE
            canvas.drawText("LiberDom Domain Analysis Report", 30f, 55f, paintTitle)
            
            paintSub.color = Color.parseColor("#9CA3AF")
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $dateStr", 30f, 80f, paintSub)

            // 2. Draw Domain Big Summary
            paintSection.color = Color.parseColor("#111827")
            canvas.drawText("1. Domain Summary", 40f, 140f, paintSection)
            canvas.drawLine(40f, 150f, 555f, 150f, paintDivider)

            canvas.drawText("Analyzed Domain:", 40f, 180f, paintBody)
            val paintDomainText = Paint().apply {
                color = Color.parseColor("#8b5cf6")
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(res.domain.uppercase(Locale.getDefault()), 180f, 182f, paintDomainText)

            canvas.drawText("Availability Status:", 40f, 215f, paintBody)
            
            // Availability badge drawing inside PDF
            val badgeColor = when (res.status) {
                "disponible" -> Color.parseColor("#00e676")
                "comprado" -> Color.parseColor("#ef4444")
                else -> Color.parseColor("#eab308")
            }
            val paintBadge = Paint().apply { color = badgeColor }
            canvas.drawRoundRect(180f, 200f, 320f, 225f, 6f, 6f, paintBadge)
            
            val paintBadgeText = Paint().apply {
                color = if (res.status == "disponible") Color.BLACK else Color.WHITE
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                if (res.status == "disponible") "AVAILABLE (FREE)" else if (res.status == "comprado") "TAKEN (REGISTERED)" else "UNKNOWN",
                250f, 217f, paintBadgeText
            )

            // Description message inside PDF
            val desc = if (res.status == "disponible") {
                if (currentLang == "es") "¡Felicidades! Este dominio está libre. Puedes registrarlo en tu proveedor favorito."
                else "Congratulations! This domain is free. You can register it with your favorite provider."
            } else {
                if (currentLang == "es") "Dominio ya ocupado. Está comprado y registrado en internet."
                else "Domain already taken. It is purchased and registered on the internet."
            }
            canvas.drawText(desc, 40f, 255f, paintBody)

            // 3. Technical Specifications Table
            canvas.drawText("2. Technical Specifications", 40f, 310f, paintSection)
            canvas.drawLine(40f, 320f, 555f, 320f, paintDivider)

            var y = 350f
            val drawTableRow = { label: String, valText: String ->
                canvas.drawText(label, 50f, y, paintBold)
                canvas.drawText(valText, 220f, y, paintBody)
                canvas.drawLine(40f, y + 8f, 555f, y + 8f, Paint().apply { color = Color.parseColor("#F3F4F6"); strokeWidth = 1f })
                y += 30f
            }

            drawTableRow("Technical Status Code:", res.status.uppercase(Locale.getDefault()))
            drawTableRow("Detection Method:", res.method)
            drawTableRow("IP Address:", res.ip ?: "N/A (No active DNS resolution)")
            drawTableRow("Registrar:", res.registrar ?: "N/A (Not returned or available)")
            drawTableRow("Creation Date:", res.creationDate ?: "N/A (Not returned or available)")

            // 4. suggested provider section
            canvas.drawText("3. Suggested Registrars for Registration", 40f, y + 20f, paintSection)
            canvas.drawLine(40f, y + 30f, 555f, y + 30f, paintDivider)
            y += 55f
            
            canvas.drawText("This domain can be checked or bought directly (no premium fees) on:", 40f, y, paintBody)
            y += 25f
            canvas.drawText("- Namecheap (https://www.namecheap.com)", 60f, y, paintBody)
            y += 20f
            canvas.drawText("- Cloudflare Registrar (https://dash.cloudflare.com)", 60f, y, paintBody)
            y += 20f
            canvas.drawText("- GoDaddy Domain Search (https://www.godaddy.com)", 60f, y, paintBody)

            // 5. Drawing footer
            val footerBg = Paint().apply { color = Color.parseColor("#F9FAFB") }
            canvas.drawRect(0f, 790f, 595f, 842f, footerBg)
            
            val paintFooterText = Paint().apply {
                color = Color.parseColor("#9CA3AF")
                textSize = 10f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Generated natively by LiberDom Android Client. Open Source Security Check.", 297f, 810f, paintFooterText)
            canvas.drawText("Non-Commercial Evaluation License © 2026", 297f, 825f, paintFooterText)

            pdfDoc.finishPage(page)

            // Write PDF to app Cache so we can easily share it securely via FileProvider
            val cacheFile = File(this.cacheDir, "LiberDom_${res.domain.replace(".", "_")}_report.pdf")
            val outputStream = FileOutputStream(cacheFile)
            pdfDoc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDoc.close()

            // Open share intent
            val fileUri = FileProvider.getUriForFile(this, "com.neoturcios.liberdom.fileprovider", cacheFile)
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "LiberDom Domain Report: ${res.domain}")
                putExtra(Intent.EXTRA_TEXT, "LiberDom Domain Availability Analysis Report for ${res.domain}")
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, if (currentLang == "es") "Exportar Reporte PDF" else "Export PDF Report"))

        } catch (e: Exception) {
            Toast.makeText(this, "PDF Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareDomainDetails(res: DomainResult) {
        val shareText = if (currentLang == "es") {
            "🔍 Análisis de dominio LiberDom:\n\n" +
            "Dominio: ${res.domain.uppercase(Locale.getDefault())}\n" +
            "Estado: ${if (res.status == "disponible") "¡DISPONIBLE (LIBRE)!" else "Registrado/Ocupado"}\n" +
            "IP: ${res.ip ?: "Ninguna"}\n" +
            "Registrador: ${res.registrar ?: "N/A"}\n" +
            "Fecha Creación: ${res.creationDate ?: "N/A"}\n" +
            "Detección: ${res.method}\n\n" +
            "¡Busca dominios gratis con la app LiberDom!"
        } else {
            "🔍 LiberDom Domain Analysis:\n\n" +
            "Domain: ${res.domain.uppercase(Locale.getDefault())}\n" +
            "Status: ${if (res.status == "disponible") "AVAILABLE (FREE)!" else "Taken/Registered"}\n" +
            "IP: ${res.ip ?: "None"}\n" +
            "Registrar: ${res.registrar ?: "N/A"}\n" +
            "Created: ${res.creationDate ?: "N/A"}\n" +
            "Method: ${res.method}\n\n" +
            "Check domains for free with LiberDom Android!"
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "LiberDom Check: ${res.domain}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, if (currentLang == "es") "Compartir dominio" else "Share domain"))
    }


    // ==========================================
    // LOCAL STORAGE (PREFERENCES SHARER)
    // ==========================================
    private fun getLocalStore(storeName: String): JSONArray {
        val sp = getSharedPreferences("liberdom_prefs", Context.MODE_PRIVATE)
        val raw = sp.getString("liberdom_$storeName", "[]") ?: "[]"
        return try {
            JSONArray(raw)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun addToLocalStore(storeName: String, res: DomainResult) {
        val list = getLocalStore(storeName)
        
        // Check duplication
        val newList = JSONArray()
        val obj = JSONObject().apply {
            put("domain", res.domain)
            put("status", res.status)
            put("detail", res.detail)
            put("ip", res.ip)
            put("registrar", res.registrar)
            put("creationDate", res.creationDate)
            put("method", res.method)
            put("timestamp", System.currentTimeMillis())
        }
        
        newList.put(obj)
        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            if (item.getString("domain").lowercase(Locale.getDefault()) != res.domain.lowercase(Locale.getDefault())) {
                newList.put(item)
            }
        }

        // Limit size to 50 items
        val cappedList = JSONArray()
        val limit = minOf(newList.length(), 50)
        for (i in 0 until limit) {
            cappedList.put(newList.getJSONObject(i))
        }

        val sp = getSharedPreferences("liberdom_prefs", Context.MODE_PRIVATE)
        sp.edit().putString("liberdom_$storeName", cappedList.toString()).apply()
    }

    private fun isDomainSaved(domainName: String): Boolean {
        val saved = getLocalStore("saved")
        for (i in 0 until saved.length()) {
            if (saved.getJSONObject(i).getString("domain").lowercase(Locale.getDefault()) == domainName.lowercase(Locale.getDefault())) {
                return true
            }
        }
        return false
    }

    private fun toggleSavedDomain(res: DomainResult): Boolean {
        val saved = getLocalStore("saved")
        val isAlreadySaved = isDomainSaved(res.domain)
        val newList = JSONArray()

        if (isAlreadySaved) {
            // Remove
            for (i in 0 until saved.length()) {
                val item = saved.getJSONObject(i)
                if (item.getString("domain").lowercase(Locale.getDefault()) != res.domain.lowercase(Locale.getDefault())) {
                    newList.put(item)
                }
            }
        } else {
            // Add
            val obj = JSONObject().apply {
                put("domain", res.domain)
                put("status", res.status)
                put("detail", res.detail)
                put("ip", res.ip)
                put("registrar", res.registrar)
                put("creationDate", res.creationDate)
                put("method", res.method)
                put("timestamp", System.currentTimeMillis())
            }
            newList.put(obj)
            for (i in 0 until saved.length()) {
                newList.put(saved.getJSONObject(i))
            }
        }

        val sp = getSharedPreferences("liberdom_prefs", Context.MODE_PRIVATE)
        sp.edit().putString("liberdom_saved", newList.toString()).apply()
        return !isAlreadySaved
    }

    private fun deleteDomainFromStore(storeName: String, domainName: String) {
        val list = getLocalStore(storeName)
        val newList = JSONArray()
        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            if (item.getString("domain").lowercase(Locale.getDefault()) != domainName.lowercase(Locale.getDefault())) {
                newList.put(item)
            }
        }
        val sp = getSharedPreferences("liberdom_prefs", Context.MODE_PRIVATE)
        sp.edit().putString("liberdom_$storeName", newList.toString()).apply()
        refreshLibraryPanel()
    }


    // ==========================================
    // FEATURE 2: BULK SCAN (BÚSQUEDA POR LOTE)
    // ==========================================
    private fun ejecutarChequeoLote() {
        val rawInput = edtBulkInput.text.toString().trim()
        if (rawInput.isEmpty()) {
            Toast.makeText(this, if (currentLang == "es") "Escribe al menos un dominio" else "Write at least one domain", Toast.LENGTH_SHORT).show()
            return
        }

        val rawLines = rawInput.split("\n")
        val domainsToCheck = ArrayList<String>()
        for (line in rawLines) {
            val clean = line.trim().lowercase(Locale.getDefault())
                .replace("https://", "")
                .replace("http://", "")
                .replace("www.", "")
                .split("/")[0]
            if (clean.isNotEmpty() && clean.contains(".")) {
                domainsToCheck.add(clean)
            }
        }

        if (domainsToCheck.isEmpty()) {
            Toast.makeText(this, if (currentLang == "es") "Ningún dominio válido detectado" else "No valid domains detected", Toast.LENGTH_SHORT).show()
            return
        }

        // Setup stats & progress bar
        containerBulkResults.removeAllViews()
        containerBulkStats.visibility = View.VISIBLE
        bulkProgressBar.visibility = View.VISIBLE
        bulkProgressBar.max = domainsToCheck.size
        bulkProgressBar.progress = 0

        var checkedCount = 0
        var freeCount = 0
        var takenCount = 0

        val updateStats = {
            txtBulkStatChecked.text = if (currentLang == "es") "Vistos: $checkedCount/${domainsToCheck.size}" else "Checked: $checkedCount/${domainsToCheck.size}"
            txtBulkStatFree.text = if (currentLang == "es") "Libres: $freeCount" else "Free: $freeCount"
            txtBulkStatTaken.text = if (currentLang == "es") "Ocupados: $takenCount" else "Taken: $takenCount"
            bulkProgressBar.progress = checkedCount
        }

        updateStats()

        Thread {
            for (domain in domainsToCheck) {
                try {
                    val res = performSingleLookup(domain)
                    runOnUiThread {
                        checkedCount++
                        if (res.status == "disponible") freeCount++ else takenCount++
                        updateStats()
                        addBulkResultRow(res)
                        
                        // Add to history
                        addToLocalStore("history", res)
                    }
                    // Small delay to prevent rate limits
                    Thread.sleep(300)
                } catch (e: Exception) {
                    runOnUiThread {
                        checkedCount++
                        updateStats()
                    }
                }
            }
            runOnUiThread {
                bulkProgressBar.visibility = View.GONE
            }
        }.start()
    }

    private fun addBulkResultRow(res: DomainResult) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@MainActivity, R.drawable.card_background_default)
            setPadding(32, 24, 32, 24)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 16, 0, 16)
            layoutParams = params
        }

        val topLayout = RelativeLayout(this)
        
        val txtDom = TextView(this).apply {
            text = res.domain.uppercase(Locale.getDefault())
            textColor = Color.WHITE
            textSize = 15sp
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.ALIGN_PARENT_START)
            params.addRule(RelativeLayout.CENTER_VERTICAL)
            layoutParams = params
        }
        
        val txtBadge = TextView(this).apply {
            text = if (res.status == "disponible") {
                if (currentLang == "es") "Libre" else "Free"
            } else if (res.status == "comprado") {
                if (currentLang == "es") "Ocupado" else "Taken"
            } else {
                if (currentLang == "es") "Dudoso" else "Limit"
            }
            
            setTextColor(
                if (res.status == "disponible") Color.parseColor("#00e676")
                else if (res.status == "comprado") Color.parseColor("#ef4444")
                else Color.parseColor("#eab308")
            )
            
            setBackgroundColor(
                if (res.status == "disponible") Color.parseColor("#1a00e676")
                else if (res.status == "comprado") Color.parseColor("#1aef4444")
                else Color.parseColor("#1aeab308")
            )
            
            setPadding(24, 8, 24, 8)
            textSize = 11sp
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.ALIGN_PARENT_END)
            params.addRule(RelativeLayout.CENTER_VERTICAL)
            layoutParams = params
        }

        topLayout.addView(txtDom)
        topLayout.addView(txtBadge)
        row.addView(topLayout)

        // Show quick registrar recommendations if free
        if (res.status == "disponible") {
            val linkLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 0)
                layoutParams = params
            }

            val makeLinkBtn = { name: String, url: String ->
                Button(this).apply {
                    text = name
                    textSize = 10sp
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#1C2436"))
                    setPadding(12, 4, 12, 4)
                    val p = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    p.setMargins(4, 0, 4, 0)
                    layoutParams = p
                    setOnClickListener { openRegistrarLink(url) }
                }
            }

            linkLayout.addView(makeLinkBtn("Namecheap", "https://www.namecheap.com/domains/registration/results/?domain=${res.domain}"))
            linkLayout.addView(makeLinkBtn("Cloudflare", "https://dash.cloudflare.com/domains/register?query=${res.domain}"))
            linkLayout.addView(makeLinkBtn("GoDaddy", "https://www.godaddy.com/domainfind/search?domainToCheck=${res.domain}"))
            row.addView(linkLayout)
        }

        containerBulkResults.addView(row)
    }


    // ==========================================
    // FEATURE 3: BRAND NAME GENERATOR
    // ==========================================
    private fun ejecutarGeneracionNombres() {
        val keyword = edtGeneratorKeyword.text.toString().trim().lowercase(Locale.getDefault())
        if (keyword.length < 2) {
            Toast.makeText(
                this,
                if (currentLang == "es") "Escribe una palabra de 2+ letras" else "Write a word of 2+ letters",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val checkedTlds = ArrayList<String>()
        if (chkCom.isChecked) checkedTlds.add("com")
        if (chkNet.isChecked) checkedTlds.add("net")
        if (chkOrg.isChecked) checkedTlds.add("org")
        if (chkIo.isChecked) checkedTlds.add("io")
        if (chkApp.isChecked) checkedTlds.add("app")

        if (checkedTlds.isEmpty()) {
            checkedTlds.add("com")
        }

        val suffixes = listOf("ai", "hub", "flow", "tech", "ify", "ly", "base", "smart", "labs", "sys", "zone", "net")
        val prefixes = listOf("get", "try", "go", "my", "the", "we", "neo", "mega", "alpha")

        val generatedList = ArrayList<String>()
        
        // Create 15 domain suggestion options
        for (tld in checkedTlds) {
            generatedList.add("${keyword}.${tld}") // keyword directly
            for (p in prefixes.shuffled().take(2)) {
                generatedList.add("${p}${keyword}.${tld}") // getKeyword.com
            }
            for (s in suffixes.shuffled().take(2)) {
                generatedList.add("${keyword}${s}.${tld}") // keywordai.com
            }
        }

        val uniqueDomains = generatedList.distinct().take(12)

        containerGeneratorResults.removeAllViews()

        for (domainName in uniqueDomains) {
            addGeneratorRow(domainName)
        }
    }

    private fun addGeneratorRow(domainName: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            background = ContextCompat.getDrawable(this@MainActivity, R.drawable.card_background_default)
            setPadding(24, 16, 24, 16)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 12, 0, 12)
            layoutParams = params
        }

        val txtDom = TextView(this).apply {
            text = domainName
            textColor = Color.WHITE
            textSize = 14sp
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            layoutParams = params
        }

        val rowActionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val btnRowCheck = Button(this).apply {
            text = if (currentLang == "es") "Verificar" else "Check"
            textSize = 11sp
            setTextColor(Color.parseColor("#070b13"))
            setBackgroundColor(Color.parseColor("#00e676"))
            setPadding(16, 4, 16, 4)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }

        row.addView(txtDom)
        row.addView(rowActionLayout)
        rowActionLayout.addView(btnRowCheck)

        btnRowCheck.setOnClickListener {
            btnRowCheck.text = "..."
            btnRowCheck.isEnabled = false
            Thread {
                try {
                    val result = performSingleLookup(domainName)
                    runOnUiThread {
                        btnRowCheck.visibility = View.GONE
                        
                        // Add Status Badge instead of check button
                        val txtBadge = TextView(this@MainActivity).apply {
                            text = if (result.status == "disponible") {
                                if (currentLang == "es") "Libre" else "Free"
                            } else {
                                if (currentLang == "es") "Ocupado" else "Taken"
                            }
                            
                            setTextColor(
                                if (result.status == "disponible") Color.parseColor("#00e676")
                                else Color.parseColor("#ef4444")
                            )
                            
                            setBackgroundColor(
                                if (result.status == "disponible") Color.parseColor("#1a00e676")
                                else Color.parseColor("#1aef4444")
                            )
                            
                            setPadding(16, 6, 16, 6)
                            textSize = 11sp
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        }

                        // Bookmark Save button
                        val btnSave = ImageButton(this@MainActivity).apply {
                            setImageResource(R.drawable.ic_star_border)
                            background = null
                            val p = LinearLayout.LayoutParams(72, 72)
                            p.setMargins(12, 0, 0, 0)
                            layoutParams = p
                            setOnClickListener {
                                val saved = toggleSavedDomain(result)
                                setImageResource(if (saved) R.drawable.ic_star else R.drawable.ic_star_border)
                            }
                        }

                        rowActionLayout.addView(txtBadge)
                        rowActionLayout.addView(btnSave)

                        // Add to history
                        addToLocalStore("history", result)
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        btnRowCheck.text = "Check"
                        btnRowCheck.isEnabled = true
                    }
                }
            }.start()
        }

        containerGeneratorResults.addView(row)
    }


    // ==========================================
    // FEATURE 4: LIBRARY AND HISTORY DISPLAY
    // ==========================================
    private fun refreshLibraryPanel() {
        containerSavedResults.removeAllViews()

        val list = if (libraryTabMode == "saved") getLocalStore("saved") else getLocalStore("history")
        
        lblListCount.text = if (currentLang == "es") {
            "${list.length()} dominios"
        } else {
            "${list.length()} domains"
        }

        if (list.length() == 0) {
            val emptyMsg = TextView(this).apply {
                text = if (currentLang == "es") "Lista vacía por ahora." else "List is currently empty."
                textColor = Color.GRAY
                textSize = 14sp
                gravity = android.view.Gravity.CENTER
                setPadding(0, 48, 0, 48)
            }
            containerSavedResults.addView(emptyMsg)
            return
        }

        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            val domainName = item.getString("domain")
            val status = item.getString("status")
            val ip = item.optString("ip", "")
            val registrar = item.optString("registrar", "")
            val creationDate = item.optString("creationDate", "")
            val method = item.optString("method", "")

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(this@MainActivity, R.drawable.card_background_default)
                setPadding(24, 16, 24, 16)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 12, 0, 12)
                layoutParams = params
            }

            // Top Info relative layout
            val topLayout = RelativeLayout(this)

            val txtDom = TextView(this).apply {
                text = domainName.uppercase(Locale.getDefault())
                textColor = Color.WHITE
                textSize = 14sp
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.ALIGN_PARENT_START)
                params.addRule(RelativeLayout.CENTER_VERTICAL)
                layoutParams = params
            }

            val badgeLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                params.addRule(RelativeLayout.ALIGN_PARENT_END)
                params.addRule(RelativeLayout.CENTER_VERTICAL)
                layoutParams = params
            }

            val txtBadge = TextView(this).apply {
                text = if (status == "disponible") {
                    if (currentLang == "es") "Libre" else "Free"
                } else if (status == "comprado") {
                    if (currentLang == "es") "Ocupado" else "Taken"
                } else {
                    if (currentLang == "es") "Dudoso" else "Limit"
                }

                setTextColor(
                    if (status == "disponible") Color.parseColor("#00e676")
                    else if (status == "comprado") Color.parseColor("#ef4444")
                    else Color.parseColor("#eab308")
                )

                setBackgroundColor(
                    if (status == "disponible") Color.parseColor("#1a00e676")
                    else if (status == "comprado") Color.parseColor("#1aef4444")
                    else Color.parseColor("#1aeab308")
                )

                setPadding(16, 4, 16, 4)
                textSize = 10sp
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            val btnDelete = ImageButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_delete)
                background = null
                imageTintList = ContextCompat.getColorStateList(this@MainActivity, android.R.color.holo_red_light)
                setPadding(12, 0, 0, 0)
                setOnClickListener {
                    deleteDomainFromStore(libraryTabMode, domainName)
                }
            }

            badgeLayout.addView(txtBadge)
            badgeLayout.addView(btnDelete)

            topLayout.addView(txtDom)
            topLayout.addView(badgeLayout)
            row.addView(topLayout)

            // Inline action shortcuts inside list items
            val actionsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.END
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 8, 0, 0)
                layoutParams = params
            }

            val makeActionShortcut = { iconRes: Int, onClick: () -> Unit ->
                ImageButton(this).apply {
                    setImageResource(iconRes)
                    background = null
                    setPadding(12, 12, 12, 12)
                    setOnClickListener { onClick() }
                }
            }

            // Quick pdf shortcut
            actionsLayout.addView(makeActionShortcut(R.drawable.ic_pdf) {
                val resObj = DomainResult(domainName, status, "", ip, registrar, creationDate, method)
                exportResultToPdf(resObj)
            })

            // Quick share shortcut
            actionsLayout.addView(makeActionShortcut(R.drawable.ic_share) {
                val resObj = DomainResult(domainName, status, "", ip, registrar, creationDate, method)
                shareDomainDetails(resObj)
            })

            // Quick single check retry shortcut
            actionsLayout.addView(makeActionShortcut(R.drawable.ic_search) {
                switchPanel("search")
                edtDomainInput.setText(domainName)
                ejecutarChequeo()
            })

            row.addView(actionsLayout)

            containerSavedResults.addView(row)
        }
    }
}
