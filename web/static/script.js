/* ==========================================================================
   INTERACTIVIDAD Y CONSUMO DE API - LIBERDOM (CON SOPORTE MULTIIDIOMA I18N)
   ========================================================================== */

// ==========================================
# Diccionario global de Traducciones (i18n)
// ==========================================
const translations = {
    es: {
        "nav.single": '<i class="fa-solid fa-search"></i> Individual',
        "nav.bulk": '<i class="fa-solid fa-layer-group"></i> Masivo (Pegar)',
        "nav.cli": '<i class="fa-solid fa-terminal"></i> Guía Termux',
        "nav.github": '<i class="fa-brands fa-github"></i> Código Abierto',
        
        "hero.single_title": "Encuentra tu próximo dominio de internet",
        "hero.single_subtitle": "Búsqueda directa e híbrida DNS/WHOIS de bajo nivel sin intermediarios ni cobros.",
        "search.placeholder": "Introduce tu dominio... (ej: miweb.com, proyecto.co)",
        "search.submit": "<span>Comprobar disponibilidad</span> <i class=\"fa-solid fa-arrow-right\"></i>",
        
        "hero.bulk_title": "Escáner masivo de dominios",
        "hero.bulk_subtitle": "Pega una lista de palabras, dominios o texto libre. Los procesamos en paralelo en segundos.",
        "bulk.label": '<i class="fa-solid fa-paste"></i> Pega tu lista de dominios aquí:',
        "bulk.textarea_placeholder": "google.com\ngithub.com, facebook.com\nhttp://misitioquenoexiste99.org\n# Puedes separar por comas, espacios o saltos de línea...",
        "bulk.scan_mode_label": "Método de análisis:",
        "bulk.scan_mode_dns": "Modo Ultra-Rápido (Solo DNS - Sin límites)",
        "bulk.scan_mode_hybrid": "Modo Preciso (DNS + WHOIS - Híbrido)",
        "bulk.start_btn": '<i class="fa-solid fa-play"></i> Iniciar escaneo masivo',
        
        "bulk.progress_processing": "Procesando: {processed} / {total} dominios",
        "bulk.progress_scanning": "Escaneando: {processed} de {total} dominios...",
        "bulk.progress_finished": '<i class="fa-solid fa-circle-check" style="color: var(--neon-green);"></i> ¡Escaneo masivo finalizado con éxito! ({processed}/{total})',
        
        "bulk.stat_avail": '<i class="fa-solid fa-check-circle"></i> Disponibles: <strong id="stat-avail-count">{count}</strong>',
        "bulk.stat_taken": '<i class="fa-solid fa-times-circle"></i> Ocupados: <strong id="stat-taken-count">{count}</strong>',
        "bulk.stat_unknown": '<i class="fa-solid fa-question-circle"></i> Dudosos: <strong id="stat-unk-count">{count}</strong>',
        
        "bulk.results_title": '<i class="fa-solid fa-list-check"></i> Resultados en tiempo real:',
        "bulk.download_txt": '<i class="fa-solid fa-file-lines"></i> Reporte TXT',
        "bulk.download_pdf": '<i class="fa-solid fa-file-pdf"></i> Reporte PDF',
        "bulk.empty_state": '<i class="fa-solid fa-server"></i><p>Pega tus dominios arriba y haz clic en iniciar para ver las tarjetas aquí.</p>',
        
        "hero.cli_title": "LiberDom en tu bolsillo con Termux",
        "hero.cli_subtitle": "Cómo instalar y correr la versión CLI rápida optimizada para celulares Android.",
        
        "cli.step1_title": "Instalación en un solo comando",
        "cli.step1_desc": "Abre Termux en tu Android, copia la línea de abajo y pégala directamente para instalar todo el entorno de forma automática:",
        "cli.step2_title": "Cómo ejecutar la aplicación CLI",
        "cli.step2_desc": "Una vez completado el instalador, puedes arrancar la herramienta en cualquier momento desde cualquier carpeta escribiendo:",
        "cli.step2_helper": '<i class="fa-solid fa-info-circle"></i> Se creará un alias directo en los binarios de tu terminal.',
        
        "cli.step3_title": '<i class="fa-solid fa-shield-halved"></i> ¿Por qué usar la versión CLI de Termux?',
        "cli.step3_item1": '<i class="fa-solid fa-bolt"></i> <strong>Mayor rendimiento:</strong> Sin renderizado de navegadores, ideal para terminales antiguas.',
        "cli.step3_item2": '<i class="fa-solid fa-file-import"></i> <strong>Carga física de archivos:</strong> Procesa directamente archivos locales `.txt` que tengas guardados en tu almacenamiento.',
        "cli.step3_item3": '<i class="fa-solid fa-microchip"></i> <strong>Consumo nulo de datos extra:</strong> Consultas directas socket-a-socket sin recargas visuales pesadas.',
        
        "footer.text": "Diseñado con amor y código abierto por <strong>NeoTurcios</strong> | Licencia No Comercial © 2026",
        
        // Cadenas de traducción internas de JS
        "js.querying": "Consultando base de datos mundial para {domain}...",
        "js.api_error": "Error en la respuesta de la API",
        "js.connection_error": '<i class="fa-solid fa-wifi"></i> <strong>Error de Conexión:</strong> No se pudo contactar al servidor. Asegúrate de tener levantado el backend Flask.',
        "js.alert_enter_domain": "Por favor, introduce o pega al menos un dominio.",
        "js.alert_no_valid": "No se encontraron dominios con formato válido (ej. dominio.com).",
        "js.scanning_btn": '<i class="fa-solid fa-circle-notch fa-spin"></i> Escaneando...',
        
        "js.status.dudoso": "Dudoso",
        "js.status.disponible": "Disponible",
        "js.status.registrado": "Registrado",
        
        "js.msg.dudoso": "No se pudo determinar con certeza (Límite de peticiones WHOIS o TLD no soportado).",
        "js.msg.disponible": "¡Felicidades! Este dominio está libre. Puedes registrarlo ahora mismo en tu proveedor favorito.",
        "js.msg.registrado": "Dominio ya ocupado. Está comprado y registrado en internet.",
        
        "js.label.technical_status": "Estado Técnico:",
        "js.label.server_ip": "IP del Servidor:",
        "js.label.registrar": "Registrador:",
        "js.label.creation_date": "Fecha de Creación:",
        "js.label.detection_method": "Método de Detección:",
        
        // Reportes PDF y TXT
        "pdf.report_title": "Reporte Masivo de Disponibilidad",
        "pdf.report_subtitle": "DETECTOR DE DISPONIBILIDAD DE DOMINIOS",
        "pdf.date_label": "Fecha del análisis: {date}",
        "pdf.stat_avail": "Dominios Libres",
        "pdf.stat_taken": "Dominios Ocupados",
        "pdf.stat_unknown": "Excedidos o Desconocidos",
        "pdf.list_title": "Listado Detallado de Resultados",
        "pdf.header_num": "#",
        "pdf.header_domain": "Dominio Consultando",
        "pdf.header_status": "Estado",
        "pdf.header_method": "Método / Detección Técnica",
        "pdf.status_avail": "DISPONIBLE",
        "pdf.status_taken": "REGISTRADO",
        "pdf.status_unknown": "DESCONOCIDO",
        "pdf.page_label": "Página {page}",
        
        "txt.header": "====================================================\n      REPORTE DE DISPONIBILIDAD - LIBERDOM 🔎      \n====================================================\n",
        "txt.date": "Fecha del Escaneo: {date}\n",
        "txt.total": "Total de dominios analizados: {total}\n\n",
        "txt.detailed": "RESULTADOS DETALLADOS:\n----------------------------------------------------\n",
        "txt.row": "{num}. DOMINIO: {domain} | ESTADO: {status} | MÈTODO: {method} ({detail})\n",
        "txt.footer": "\n====================================================\nDesarrollado por NeoTurcios - LiberDom CLI & Web 🚀\n====================================================\n"
    },
    en: {
        "nav.single": '<i class="fa-solid fa-search"></i> Individual',
        "nav.bulk": '<i class="fa-solid fa-layer-group"></i> Bulk (Paste)',
        "nav.cli": '<i class="fa-solid fa-terminal"></i> Termux Guide',
        "nav.github": '<i class="fa-brands fa-github"></i> Open Source',
        
        "hero.single_title": "Find your next internet domain",
        "hero.single_subtitle": "Low-level direct and hybrid DNS/WHOIS search without intermediaries or fees.",
        "search.placeholder": "Enter your domain... (e.g. myweb.com, project.co)",
        "search.submit": "<span>Check availability</span> <i class=\"fa-solid fa-arrow-right\"></i>",
        
        "hero.bulk_title": "Bulk Domain Scanner",
        "hero.bulk_subtitle": "Paste a list of words, domains, or free text. We process them in parallel in seconds.",
        "bulk.label": '<i class="fa-solid fa-paste"></i> Paste your domain list here:',
        "bulk.textarea_placeholder": "google.com\ngithub.com, facebook.com\nhttp://mysite-that-does-not-exist.org\n# You can separate by commas, spaces, or line breaks...",
        "bulk.scan_mode_label": "Scan method:",
        "bulk.scan_mode_dns": "Ultra-Fast Mode (DNS Only - No limits)",
        "bulk.scan_mode_hybrid": "Precise Mode (DNS + WHOIS - Hybrid)",
        "bulk.start_btn": '<i class="fa-solid fa-play"></i> Start bulk scan',
        
        "bulk.progress_processing": "Processing: {processed} / {total} domains",
        "bulk.progress_scanning": "Scanning: {processed} of {total} domains...",
        "bulk.progress_finished": '<i class="fa-solid fa-circle-check" style="color: var(--neon-green);"></i> Bulk scan successfully finished! ({processed}/{total})',
        
        "bulk.stat_avail": '<i class="fa-solid fa-check-circle"></i> Available: <strong id="stat-avail-count">{count}</strong>',
        "bulk.stat_taken": '<i class="fa-solid fa-times-circle"></i> Taken: <strong id="stat-taken-count">{count}</strong>',
        "bulk.stat_unknown": '<i class="fa-solid fa-question-circle"></i> Unknown: <strong id="stat-unk-count">{count}</strong>',
        
        "bulk.results_title": '<i class="fa-solid fa-list-check"></i> Real-time results:',
        "bulk.download_txt": '<i class="fa-solid fa-file-lines"></i> TXT Report',
        "bulk.download_pdf": '<i class="fa-solid fa-file-pdf"></i> PDF Report',
        "bulk.empty_state": '<i class="fa-solid fa-server"></i><p>Paste your domains above and click start to see the cards here.</p>',
        
        "hero.cli_title": "LiberDom in your pocket with Termux",
        "hero.cli_subtitle": "How to install and run the fast CLI version optimized for Android phones.",
        
        "cli.step1_title": "One-command installation",
        "cli.step1_desc": "Open Termux on your Android, copy the line below and paste it directly to install the entire environment automatically:",
        "cli.step2_title": "How to run the CLI application",
        "cli.step2_desc": "Once the installer is complete, you can start the tool at any time from any folder by typing:",
        "cli.step2_helper": '<i class="fa-solid fa-info-circle"></i> A direct alias will be created in your terminal binaries.',
        
        "cli.step3_title": '<i class="fa-solid fa-shield-halved"></i> Why use the Termux CLI version?',
        "cli.step3_item1": '<i class="fa-solid fa-bolt"></i> <strong>Higher performance:</strong> No browser rendering, ideal for older terminals.',
        "cli.step3_item2": '<i class="fa-solid fa-file-import"></i> <strong>Physical file upload:</strong> Directly process local `.txt` files saved in your storage.',
        "cli.step3_item3": '<i class="fa-solid fa-microchip"></i> <strong>Zero extra data consumption:</strong> Direct socket-to-socket queries without heavy visual reloads.',
        
        "footer.text": "Designed with love and open source by <strong>NeoTurcios</strong> | Non-Commercial License © 2026",
        
        // JS internal translations
        "js.querying": "Querying global database for {domain}...",
        "js.api_error": "API response error",
        "js.connection_error": '<i class="fa-solid fa-wifi"></i> <strong>Connection Error:</strong> Could not contact the server. Make sure you have the Flask backend running.',
        "js.alert_enter_domain": "Please enter or paste at least one domain.",
        "js.alert_no_valid": "No domains found with a valid format (e.g. domain.com).",
        "js.scanning_btn": '<i class="fa-solid fa-circle-notch fa-spin"></i> Scanning...',
        
        "js.status.dudoso": "Unknown",
        "js.status.disponible": "Available",
        "js.status.registrado": "Registered",
        
        "js.msg.dudoso": "Could not be determined with certainty (WHOIS rate limit or unsupported TLD).",
        "js.msg.disponible": "Congratulations! This domain is free. You can register it right now with your favorite provider.",
        "js.msg.registrado": "Domain already taken. It is purchased and registered on the internet.",
        
        "js.label.technical_status": "Technical Status:",
        "js.label.server_ip": "Server IP:",
        "js.label.registrar": "Registrar:",
        "js.label.creation_date": "Creation Date:",
        "js.label.detection_method": "Detection Method:",
        
        // PDF and TXT translations
        "pdf.report_title": "Bulk Availability Report",
        "pdf.report_subtitle": "DOMAIN AVAILABILITY DETECTOR",
        "pdf.date_label": "Analysis date: {date}",
        "pdf.stat_avail": "Free Domains",
        "pdf.stat_taken": "Taken Domains",
        "pdf.stat_unknown": "Exceeded or Unknown",
        "pdf.list_title": "Detailed List of Results",
        "pdf.header_num": "#",
        "pdf.header_domain": "Domain Checked",
        "pdf.header_status": "Status",
        "pdf.header_method": "Method / Technical Detection",
        "pdf.status_avail": "AVAILABLE",
        "pdf.status_taken": "REGISTERED",
        "pdf.status_unknown": "UNKNOWN",
        "pdf.page_label": "Page {page}",
        
        "txt.header": "====================================================\n      AVAILABILITY REPORT - LIBERDOM 🔎      \n====================================================\n",
        "txt.date": "Scan Date: {date}\n",
        "txt.total": "Total domains analyzed: {total}\n\n",
        "txt.detailed": "DETAILED RESULTS:\n----------------------------------------------------\n",
        "txt.row": "{num}. DOMAIN: {domain} | STATUS: {status} | METHOD: {method} ({detail})\n",
        "txt.footer": "\n====================================================\nDeveloped by NeoTurcios - LiberDom CLI & Web 🚀\n====================================================\n"
    }
};

let currentLang = localStorage.getItem("liberdom_lang") || "es";

document.addEventListener("DOMContentLoaded", () => {
    inicializarPestanas();
    inicializarCopiarCodigo();
    inicializarBusquedaIndividual();
    inicializarBusquedaMasiva();
    inicializarSelectorIdioma();
    
    // Aplicar idioma inicial en carga
    aplicarIdioma(currentLang);
});

// ==========================================
// 0. CONTROLADOR DE IDIOMA (I18n Switcher)
// ==========================================
function inicializarSelectorIdioma() {
    const langBtn = document.getElementById("lang-btn");
    const langDropdown = document.getElementById("lang-dropdown");
    const langOptions = document.querySelectorAll(".lang-option");

    // Toggle Dropdown
    langBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        langDropdown.classList.toggle("show");
    });

    // Close Dropdown on outside click
    document.addEventListener("click", () => {
        langDropdown.classList.remove("show");
    });

    // Change language on option click
    langOptions.forEach(option => {
        option.addEventListener("click", () => {
            const lang = option.getAttribute("data-lang");
            aplicarIdioma(lang);
        });
    });
}

function aplicarIdioma(lang) {
    currentLang = lang;
    localStorage.setItem("liberdom_lang", lang);

    // Traducir todos los elementos con data-i18n
    document.querySelectorAll("[data-i18n]").forEach(elem => {
        const key = elem.getAttribute("data-i18n");
        if (translations[lang] && translations[lang][key]) {
            // Preservar iconos e insertar texto
            const hasIcon = elem.querySelector("i");
            if (hasIcon) {
                const iconHtml = hasIcon.outerHTML;
                const cleanText = translations[lang][key].replace(/<i[^>]*><\/i>/i, "").trim();
                elem.innerHTML = `${iconHtml} ${cleanText}`;
            } else {
                elem.innerHTML = translations[lang][key];
            }
        }
    });

    // Traducir placeholders de inputs
    const singleInput = document.getElementById("single-domain-input");
    if (singleInput) {
        singleInput.placeholder = translations[lang]["search.placeholder"];
    }
    const bulkTextarea = document.getElementById("bulk-textarea");
    if (bulkTextarea) {
        bulkTextarea.placeholder = translations[lang]["bulk.textarea_placeholder"];
    }

    // Actualizar badges e items de selectores
    const scanMode = document.getElementById("scan-mode");
    if (scanMode) {
        scanMode.options[0].text = translations[lang]["bulk.scan_mode_dns"];
        scanMode.options[1].text = translations[lang]["bulk.scan_mode_hybrid"];
    }

    // Actualizar contadores del panel de progreso manteniendo los numeros
    const availCount = document.getElementById("stat-avail-count").innerText;
    const takenCount = document.getElementById("stat-taken-count").innerText;
    const unkCount = document.getElementById("stat-unk-count").innerText;

    const statAvailBadge = document.getElementById("stat-avail-badge");
    const statTakenBadge = document.getElementById("stat-taken-badge");
    const statUnknownBadge = document.getElementById("stat-unknown-badge");

    if (statAvailBadge) statAvailBadge.innerHTML = translations[lang]["bulk.stat_avail"].replace("{count}", availCount);
    if (statTakenBadge) statTakenBadge.innerHTML = translations[lang]["bulk.stat_taken"].replace("{count}", takenCount);
    if (statUnknownBadge) statUnknownBadge.innerHTML = translations[lang]["bulk.stat_unknown"].replace("{count}", unkCount);

    // Actualizar estado de dropdown visual
    document.querySelectorAll(".lang-option").forEach(opt => {
        if (opt.getAttribute("data-lang") === lang) {
            opt.classList.add("active");
        } else {
            opt.classList.remove("active");
        }
    });

    const currentLangLabel = document.getElementById("current-lang-label");
    if (currentLangLabel) {
        currentLangLabel.innerText = lang === "es" ? "Español" : "English";
    }

    // Traducir empty state si es visible
    const resultsGrid = document.getElementById("bulk-results-grid");
    if (resultsGrid) {
        const emptyState = resultsGrid.querySelector(".empty-state");
        if (emptyState) {
            emptyState.innerHTML = translations[lang]["bulk.empty_state"];
        }
    }
}

// ==========================================
// 1. MANEJO DE PESTAÑAS (Tabs)
// ==========================================
function inicializarPestanas() {
    const tabButtons = document.querySelectorAll(".tab-btn");
    const tabPanels = document.querySelectorAll(".tab-panel");

    tabButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const targetTab = btn.getAttribute("data-tab");

            tabButtons.forEach(b => b.classList.remove("active"));
            tabPanels.forEach(p => p.classList.remove("active"));

            btn.classList.add("active");
            document.getElementById(`tab-${targetTab}`).classList.add("active");
        });
    });
}

// ==========================================
// 2. COPIAR CÓDIGO (Clipboard Copy)
// ==========================================
function inicializarCopiarCodigo() {
    const copyButtons = document.querySelectorAll(".copy-code-btn");

    copyButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-target");
            const codeText = document.getElementById(targetId).innerText;

            navigator.clipboard.writeText(codeText).then(() => {
                const originalIcon = btn.innerHTML;
                btn.innerHTML = `<i class="fa-solid fa-check" style="color: #00e676;"></i>`;
                btn.style.pointerEvents = "none";

                setTimeout(() => {
                    btn.innerHTML = originalIcon;
                    btn.style.pointerEvents = "auto";
                }, 2000);
            }).catch(err => {
                console.error("Error al copiar al portapapeles: ", err);
            });
        });
    });
}

// ==========================================
// 3. BÚSQUEDA INDIVIDUAL (Single Search)
// ==========================================
function inicializarBusquedaIndividual() {
    const searchForm = document.getElementById("single-search-form");
    const domainInput = document.getElementById("single-domain-input");
    const resultWrapper = document.getElementById("single-result-wrapper");

    searchForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const rawDomain = domainInput.value.trim();
        
        if (!rawDomain) return;

        // Limpiar cargadores
        const textQuerying = translations[currentLang]["js.querying"].replace("{domain}", rawDomain);
        resultWrapper.innerHTML = `
            <div class="spinner-container">
                <div class="spinner"></div>
                <div class="loading-text">${textQuerying}</div>
            </div>
        `;

        // Fetch a la API Flask enviando lang actual
        fetch(`/api/check?domain=${encodeURIComponent(rawDomain)}&dns_only=false&lang=${currentLang}`)
            .then(res => {
                if (!res.ok) throw new Error(translations[currentLang]["js.api_error"]);
                return res.json();
            })
            .then(data => {
                if (data.status === "success") {
                    mostrarResultadoIndividual(data);
                } else {
                    resultWrapper.innerHTML = `<div class="glass-card result-card taken" style="border-color: var(--neon-red);"><i class="fa-solid fa-triangle-exclamation"></i> Error: ${data.message}</div>`;
                }
            })
            .catch(err => {
                console.error(err);
                resultWrapper.innerHTML = `
                    <div class="glass-card result-card taken" style="border-color: var(--neon-red);">
                        ${translations[currentLang]["js.connection_error"]}
                    </div>
                `;
            });
    });
}

function mostrarResultadoIndividual(data) {
    const resultWrapper = document.getElementById("single-result-wrapper");
    const { domain, estado, detalle, info } = data;

    let cardClass = "unknown";
    let badgeClass = "unknown";
    let statusLabel = translations[currentLang]["js.status.dudoso"];
    let statusIcon = "fa-question-circle";
    let messageText = translations[currentLang]["js.msg.dudoso"];

    if (estado === "disponible") {
        cardClass = "available";
        badgeClass = "available";
        statusLabel = translations[currentLang]["js.status.disponible"];
        statusIcon = "fa-check-circle";
        messageText = translations[currentLang]["js.msg.disponible"];
    } else if (estado === "comprado") {
        cardClass = "taken";
        badgeClass = "taken";
        statusLabel = translations[currentLang]["js.status.registrado"];
        statusIcon = "fa-times-circle";
        messageText = translations[currentLang]["js.msg.registrado"];
    }

    // Inyectar HTML de tarjeta estilizada localizada
    resultWrapper.innerHTML = `
        <div class="result-card ${cardClass}">
            <div class="card-top">
                <span class="card-domain">${domain.toUpperCase()}</span>
                <span class="status-badge ${badgeClass}"><i class="fa-solid ${statusIcon}"></i> ${statusLabel}</span>
            </div>
            
            <p style="font-size: 15px; margin-bottom: 20px; line-height: 1.4; color: ${estado === 'disponible' ? 'var(--neon-green)' : 'var(--text-muted)'}">
                ${messageText}
            </p>

            <div class="card-body">
                <div class="info-row">
                    <span class="info-label">${translations[currentLang]["js.label.technical_status"]}</span>
                    <span class="info-value" style="color: ${estado === 'disponible' ? 'var(--neon-green)' : 'var(--neon-red)'}">${detalle}</span>
                </div>
                ${info.ip ? `
                <div class="info-row">
                    <span class="info-label">${translations[currentLang]["js.label.server_ip"]}</span>
                    <span class="info-value">${info.ip}</span>
                </div>` : ''}
                ${info.registrador ? `
                <div class="info-row">
                    <span class="info-label">${translations[currentLang]["js.label.registrar"]}</span>
                    <span class="info-value">${info.registrador}</span>
                </div>` : ''}
                ${info.fecha_creacion ? `
                <div class="info-row">
                    <span class="info-label">${translations[currentLang]["js.label.creation_date"]}</span>
                    <span class="info-value">${info.fecha_creacion}</span>
                </div>` : ''}
                <div class="info-row">
                    <span class="info-label">${translations[currentLang]["js.label.detection_method"]}</span>
                    <span class="info-value method">${info.metodo}</span>
                </div>
            </div>
        </div>
    `;
}

// ==========================================
// 4. BÚSQUEDA MASIVA EN PARALELO (Bulk Search)
// ==========================================
let bulkResultsData = []; // Guardará el reporte actual en memoria

function inicializarBusquedaMasiva() {
    const startBtn = document.getElementById("start-bulk-btn");
    const textarea = document.getElementById("bulk-textarea");
    const scanMode = document.getElementById("scan-mode");
    const resultsGrid = document.getElementById("bulk-results-grid");
    const progressPanel = document.getElementById("bulk-progress-panel");
    const downloadBtn = document.getElementById("download-results-btn");
    const downloadPdfBtn = document.getElementById("download-pdf-btn");

    startBtn.addEventListener("click", async () => {
        const text = textarea.value.trim();
        if (!text) {
            alert(translations[currentLang]["js.alert_enter_domain"]);
            return;
        }

        const lines = text.split(/[\n,]+/);
        const rawDomains = [];

        lines.forEach(line => {
            let clean = line.trim();
            if (!clean || clean.startsWith("#")) return;

            let dom = clean.toLowerCase();
            dom = dom.replace(/^(https?://)?(www\.)?/, "");
            dom = dom.split("/")[0];

            if (dom.includes(".") && dom.length >= 4) {
                rawDomains.push(dom);
            }
        });

        const domains = [...new Set(rawDomains)];

        if (domains.length === 0) {
            alert(translations[currentLang]["js.alert_no_valid"]);
            return;
        }

        resultsGrid.innerHTML = "";
        bulkResultsData = [];
        progressPanel.style.display = "block";
        downloadBtn.classList.add("disabled");
        downloadBtn.disabled = true;
        downloadPdfBtn.classList.add("disabled");
        downloadPdfBtn.disabled = true;
        startBtn.disabled = true;
        startBtn.innerHTML = translations[currentLang]["js.scanning_btn"];

        let counts = { disponible: 0, comprado: 0, desconocido: 0 };
        const total = domains.length;
        let processed = 0;

        actualizarProgresoMasivo(0, total, counts);

        const dnsOnly = scanMode.value === "dns_only";
        const limitConcurrencia = dnsOnly ? 10 : 3;
        const cola = [...domains];
        const promesasActivas = [];

        const procesarDominio = async (dom) => {
            try {
                // Fetch API enviando idioma dinámico
                const res = await fetch(`/api/check?domain=${encodeURIComponent(dom)}&dns_only=${dnsOnly}&lang=${currentLang}`);
                if (!res.ok) throw new Error();
                const data = await res.json();

                if (data.status === "success") {
                    counts[data.estado]++;
                    processed++;
                    
                    bulkResultsData.push({
                        domain: dom,
                        estado: data.estado.toUpperCase(),
                        metodo: data.info.metodo,
                        detalle: data.detalle
                    });

                    inyectarTarjetaMiniBulk(data);
                    actualizarProgresoMasivo(processed, total, counts);
                }
            } catch (err) {
                counts.desconocido++;
                processed++;
                bulkResultsData.push({
                    domain: dom,
                    estado: "ERROR",
                    metodo: "Falla de red",
                    detalle: "Fetch failed"
                });
                inyectarTarjetaMiniBulk({
                    domain: dom,
                    estado: "desconocido",
                    detalle: "Falla conexión API",
                    info: { metodo: "Falla de red" }
                });
                actualizarProgresoMasivo(processed, total, counts);
            }
            
            if (!dnsOnly) {
                await new Promise(r => setTimeout(r, 400));
            }
        };

        while (cola.length > 0 || promesasActivas.length > 0) {
            while (promesasActivas.length < limitConcurrencia && cola.length > 0) {
                const siguienteDom = cola.shift();
                const promesa = procesarDominio(siguienteDom).then(() => {
                    promesasActivas.splice(promesasActivas.indexOf(promesa), 1);
                });
                promesasActivas.push(promesa);
            }
            await Promise.race(promesasActivas);
        }

        startBtn.disabled = false;
        startBtn.innerHTML = translations[currentLang]["bulk.start_btn"];
        
        downloadBtn.classList.remove("disabled");
        downloadBtn.disabled = false;
        downloadPdfBtn.classList.remove("disabled");
        downloadPdfBtn.disabled = false;
    });

    downloadBtn.addEventListener("click", () => {
        if (bulkResultsData.length === 0) return;
        descargarReporteTxt();
    });

    downloadPdfBtn.addEventListener("click", () => {
        if (bulkResultsData.length === 0) return;
        descargarReportePdf();
    });
}

function inyectarTarjetaMiniBulk(data) {
    const resultsGrid = document.getElementById("bulk-results-grid");
    
    const emptyState = resultsGrid.querySelector(".empty-state");
    if (emptyState) emptyState.remove();

    const { domain, estado, detalle, info } = data;

    let cardClass = "unknown";
    let badgeClass = "unknown";
    let label = translations[currentLang]["js.status.dudoso"];

    if (estado === "disponible") {
        cardClass = "available";
        badgeClass = "available";
        label = currentLang === "es" ? "Libre" : "Free";
    } else if (estado === "comprado") {
        cardClass = "taken";
        badgeClass = "taken";
        label = translations[currentLang]["js.status.registrado"];
    }

    const miniCard = document.createElement("div");
    miniCard.className = `bulk-mini-card ${cardClass}`;
    miniCard.innerHTML = `
        <div class="mini-left">
            <span class="mini-domain" title="${domain}">${domain}</span>
            <div style="font-size: 11px; color: var(--text-muted); margin-top: 4px; font-family: var(--font-mono)">
                ${info.metodo}
            </div>
        </div>
        <span class="mini-badge ${badgeClass}">${label}</span>
    `;

    resultsGrid.appendChild(miniCard);
}

function actualizarProgresoMasivo(processed, total, counts) {
    const progressFill = document.getElementById("progress-bar-fill");
    const progressText = document.getElementById("progress-text");
    const progressPerc = document.getElementById("progress-percentage");

    // Contadores en tiempo real
    const statAvailBadge = document.getElementById("stat-avail-badge");
    const statTakenBadge = document.getElementById("stat-taken-badge");
    const statUnknownBadge = document.getElementById("stat-unknown-badge");

    if (statAvailBadge) statAvailBadge.innerHTML = translations[currentLang]["bulk.stat_avail"].replace("{count}", counts.disponible);
    if (statTakenBadge) statTakenBadge.innerHTML = translations[currentLang]["bulk.stat_taken"].replace("{count}", counts.comprado);
    if (statUnknownBadge) statUnknownBadge.innerHTML = translations[currentLang]["bulk.stat_unknown"].replace("{count}", counts.desconocido);

    const percentage = total > 0 ? Math.round((processed / total) * 100) : 0;
    
    progressFill.style.width = `${percentage}%`;
    progressPerc.innerText = `${percentage}%`;
    
    if (processed === total && total > 0) {
        progressText.innerHTML = translations[currentLang]["bulk.progress_finished"].replace("{processed}", processed).replace("{total}", total);
    } else {
        progressText.innerText = translations[currentLang]["bulk.progress_scanning"].replace("{processed}", processed).replace("{total}", total);
    }
}

function descargarReporteTxt() {
    const header = translations[currentLang]["txt.header"];
    const dateStr = translations[currentLang]["txt.date"].replace("{date}", new Date().toLocaleString());
    const totalStr = translations[currentLang]["txt.total"].replace("{total}", bulkResultsData.length);
    const detailed = translations[currentLang]["txt.detailed"];
    
    let contenido = header + dateStr + totalStr + detailed;
    
    bulkResultsData.forEach((row, idx) => {
        let stateText = row.estado;
        if (currentLang === "en") {
            if (row.estado === "DISPONIBLE") stateText = "FREE";
            if (row.estado === "COMPRADO") stateText = "TAKEN";
        }
        contenido += translations[currentLang]["txt.row"]
            .replace("{num}", (idx + 1).toString().padStart(3, '0'))
            .replace("{domain}", row.domain.padEnd(30))
            .replace("{status}", stateText.padEnd(12))
            .replace("{method}", row.metodo)
            .replace("{detail}", row.detalle);
    });
    
    contenido += translations[currentLang]["txt.footer"];

    const blob = new Blob([contenido], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `reporte_liberdom_${Date.now()}.txt`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
}

function descargarReportePdf() {
    if (bulkResultsData.length === 0) return;
    
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF('p', 'pt', 'a4');
    
    const margin = 40;
    let y = 50;
    const width = 595;
    
    const darkSlate = [15, 23, 42];
    const neonGreen = [16, 185, 129];
    const neonRed = [239, 68, 68];
    const neutralGray = [148, 163, 184];
    const lightGray = [248, 250, 252];
    const tableBorder = [241, 245, 249];
    
    // --- CABECERA DE HOJA (Header Bar) ---
    doc.setFillColor(...darkSlate);
    doc.rect(margin, y, width - (margin * 2), 55, 'F');
    
    doc.setTextColor(255, 255, 255);
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(20);
    doc.text("LIBERDOM", margin + 20, y + 34);
    
    doc.setFont("Helvetica", "normal");
    doc.setFontSize(9);
    doc.setTextColor(148, 163, 184);
    doc.text(translations[currentLang]["pdf.report_subtitle"], width - margin - (currentLang === "es" ? 240 : 200), y + 33);
    
    y += 55;
    doc.setFillColor(...neonGreen);
    doc.rect(margin, y, width - (margin * 2), 3, 'F');
    
    y += 35;
    
    // --- TÍTULO DEL DOCUMENTO ---
    doc.setTextColor(15, 23, 42);
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(15);
    doc.text(translations[currentLang]["pdf.report_title"], margin, y);
    
    y += 18;
    doc.setFont("Helvetica", "normal");
    doc.setFontSize(9);
    doc.setTextColor(100, 116, 139);
    doc.text(translations[currentLang]["pdf.date_label"].replace("{date}", new Date().toLocaleString()), margin, y);
    
    y += 25;
    
    // --- TARJETAS RESUMEN ---
    const disponibles = bulkResultsData.filter(d => d.estado === 'DISPONIBLE').length;
    const ocupados = bulkResultsData.filter(d => d.estado === 'COMPRADO').length;
    const dudosos = bulkResultsData.filter(d => d.estado === 'DESCONOCIDO' || d.estado === 'ERROR').length;
    
    // Tarjeta 1: Disponibles (Verde Pastel)
    doc.setFillColor(240, 253, 244); 
    doc.setDrawColor(...neonGreen);
    doc.roundedRect(margin, y, 155, 42, 4, 4, 'FD');
    doc.setTextColor(...neonGreen);
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(14);
    doc.text(`${disponibles}`, margin + 15, y + 20);
    doc.setFont("Helvetica", "normal");
    doc.setFontSize(8.5);
    doc.setTextColor(22, 101, 52);
    doc.text(translations[currentLang]["pdf.stat_avail"], margin + 15, y + 32);
    
    // Tarjeta 2: Ocupados (Rojo Pastel)
    doc.setFillColor(254, 242, 242); 
    doc.setDrawColor(...neonRed);
    doc.roundedRect(margin + 170, y, 155, 42, 4, 4, 'FD');
    doc.setTextColor(...neonRed);
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(14);
    doc.text(`${ocupados}`, margin + 185, y + 20);
    doc.setFont("Helvetica", "normal");
    doc.setFontSize(8.5);
    doc.setTextColor(153, 27, 27);
    doc.text(translations[currentLang]["pdf.stat_taken"], margin + 185, y + 32);

    // Tarjeta 3: Dudosos / Errores (Gris Pastel)
    doc.setFillColor(248, 250, 252); 
    doc.setDrawColor(...neutralGray);
    doc.roundedRect(margin + 340, y, 175, 42, 4, 4, 'FD');
    doc.setTextColor(71, 85, 105);
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(14);
    doc.text(`${dudosos}`, margin + 355, y + 20);
    doc.setFont("Helvetica", "normal");
    doc.setFontSize(8.5);
    doc.setTextColor(71, 85, 105);
    doc.text(translations[currentLang]["pdf.stat_unknown"], margin + 355, y + 32);
    
    y += 65;
    
    // --- CABECERA DE LA TABLA ---
    doc.setFont("Helvetica", "bold");
    doc.setFontSize(10.5);
    doc.setTextColor(15, 23, 42);
    doc.text(translations[currentLang]["pdf.list_title"], margin, y);
    
    y += 14;
    
    doc.setFillColor(...lightGray);
    doc.rect(margin, y, width - (margin * 2), 22, 'F');
    doc.setFontSize(8.5);
    doc.setTextColor(71, 85, 105);
    doc.text(translations[currentLang]["pdf.header_num"], margin + 12, y + 14);
    doc.text(translations[currentLang]["pdf.header_domain"], margin + 35, y + 14);
    doc.text(translations[currentLang]["pdf.header_status"], margin + 240, y + 14);
    doc.text(translations[currentLang]["pdf.header_method"], margin + 340, y + 14);
    
    y += 22;
    
    // --- FILAS DE RESULTADOS ---
    let pageNum = 1;
    doc.setFont("Helvetica", "normal");
    
    bulkResultsData.forEach((row, idx) => {
        if (y > 760) {
            doc.setFontSize(7.5);
            doc.setTextColor(...neutralGray);
            doc.text(translations[currentLang]["pdf.page_label"].replace("{page}", pageNum), width / 2 - 15, 805);
            
            doc.addPage();
            pageNum++;
            y = 50;
            
            doc.setFillColor(...lightGray);
            doc.rect(margin, y, width - (margin * 2), 22, 'F');
            doc.setFont("Helvetica", "bold");
            doc.setFontSize(8.5);
            doc.setTextColor(71, 85, 105);
            doc.text(translations[currentLang]["pdf.header_num"], margin + 12, y + 14);
            doc.text(translations[currentLang]["pdf.header_domain"], margin + 35, y + 14);
            doc.text(translations[currentLang]["pdf.header_status"], margin + 240, y + 14);
            doc.text(translations[currentLang]["pdf.header_method"], margin + 340, y + 14);
            y += 22;
            doc.setFont("Helvetica", "normal");
        }
        
        if (idx % 2 === 1) {
            doc.setFillColor(252, 252, 252);
            doc.rect(margin, y, width - (margin * 2), 19, 'F');
        }
        
        doc.setDrawColor(...tableBorder);
        doc.line(margin, y + 19, width - margin, y + 19);
        
        doc.setTextColor(51, 65, 85);
        doc.setFontSize(8.5);
        doc.text(`${(idx + 1).toString().padStart(2, '0')}`, margin + 12, y + 12);
        
        doc.setFont("Helvetica", "bold");
        doc.text(row.domain, margin + 35, y + 12);
        doc.setFont("Helvetica", "normal");
        
        if (row.estado === 'DISPONIBLE') {
            doc.setTextColor(...neonGreen);
            doc.setFont("Helvetica", "bold");
            doc.text(translations[currentLang]["pdf.status_avail"], margin + 240, y + 12);
            doc.setFont("Helvetica", "normal");
        } else if (row.estado === 'COMPRADO') {
            doc.setTextColor(...neonRed);
            doc.setFont("Helvetica", "bold");
            doc.text(translations[currentLang]["pdf.status_taken"], margin + 240, y + 12);
            doc.setFont("Helvetica", "normal");
        } else {
            doc.setTextColor(148, 163, 184);
            doc.text(translations[currentLang]["pdf.status_unknown"], margin + 240, y + 12);
        }
        
        doc.setTextColor(100, 116, 139);
        doc.setFontSize(8);
        doc.text(`${row.metodo} (${row.detalle.split('(')[0].trim()})`, margin + 340, y + 12);
        
        y += 19;
    });
    
    doc.setFontSize(7.5);
    doc.setTextColor(...neutralGray);
    doc.text(translations[currentLang]["pdf.page_label"].replace("{page}", pageNum), width / 2 - 15, 805);
    
    doc.save(`reporte_liberdom_${Date.now()}.pdf`);
}
