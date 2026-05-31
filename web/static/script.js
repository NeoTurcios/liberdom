/* ==========================================================================
   INTERACTIVIDAD Y CONSUMO DE API - LIBERDOM
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
    inicializarPestanas();
    inicializarCopiarCodigo();
    inicializarBusquedaIndividual();
    inicializarBusquedaMasiva();
});

// ==========================================
// 1. MANEJO DE PESTAÑAS (Tabs)
// ==========================================
function inicializarPestanas() {
    const tabButtons = document.querySelectorAll(".tab-btn");
    const tabPanels = document.querySelectorAll(".tab-panel");

    tabButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const targetTab = btn.getAttribute("data-tab");

            // Desactivar botones y paneles
            tabButtons.forEach(b => b.classList.remove("active"));
            tabPanels.forEach(p => p.classList.remove("active"));

            // Activar actual
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
                // Feedback visual al copiar
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

        // Limpiar resultados anteriores e inyectar cargador (Spinner)
        resultWrapper.innerHTML = `
            <div class="spinner-container">
                <div class="spinner"></div>
                <div class="loading-text">Consultando base de datos mundial para ${rawDomain}...</div>
            </div>
        `;

        // Fetch a la API Flask
        fetch(`/api/check?domain=${encodeURIComponent(rawDomain)}&dns_only=false`)
            .then(res => {
                if (!res.ok) throw new Error("Error en la respuesta de la API");
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
                        <i class="fa-solid fa-wifi"></i> <strong>Error de Conexión:</strong> No se pudo contactar al servidor. Asegúrate de tener levantado el backend Flask.
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
    let statusLabel = "Dudoso";
    let statusIcon = "fa-question-circle";
    let messageText = "No se pudo determinar con certeza (Límite de peticiones WHOIS o TLD no soportado).";

    if (estado === "disponible") {
        cardClass = "available";
        badgeClass = "available";
        statusLabel = "Disponible";
        statusIcon = "fa-check-circle";
        messageText = "¡Felicidades! Este dominio está libre. Puedes registrarlo ahora mismo en tu proveedor favorito.";
    } else if (estado === "comprado") {
        cardClass = "taken";
        badgeClass = "taken";
        statusLabel = "Registrado";
        statusIcon = "fa-times-circle";
        messageText = "Dominio ya ocupado. Está comprado y registrado en internet.";
    }

    // Inyectar HTML de tarjeta estilizada
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
                    <span class="info-label">Estado Técnico:</span>
                    <span class="info-value" style="color: ${estado === 'disponible' ? 'var(--neon-green)' : 'var(--neon-red)'}">${detalle}</span>
                </div>
                ${info.ip ? `
                <div class="info-row">
                    <span class="info-label">IP del Servidor:</span>
                    <span class="info-value">${info.ip}</span>
                </div>` : ''}
                ${info.registrador ? `
                <div class="info-row">
                    <span class="info-label">Registrador:</span>
                    <span class="info-value">${info.registrador}</span>
                </div>` : ''}
                ${info.fecha_creacion ? `
                <div class="info-row">
                    <span class="info-label">Fecha de Creación:</span>
                    <span class="info-value">${info.fecha_creacion}</span>
                </div>` : ''}
                <div class="info-row">
                    <span class="info-label">Método de Detección:</span>
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

    startBtn.addEventListener("click", async () => {
        const text = textarea.value.trim();
        if (!text) {
            alert("Por favor, introduce o pega al menos un dominio.");
            return;
        }

        // 1. Limpiar y Normalizar lista
        const lines = text.split(/[\n,]+/); // separar por saltos de línea o comas
        const rawDomains = [];

        lines.forEach(line => {
            let clean = line.trim();
            if (!clean || clean.startsWith("#")) return; // saltar vacías o comentarios

            // Limpieza básica de protocolos y subdominios
            let dom = clean.toLowerCase();
            dom = dom.replace(/^(https?:\/\/)?(www\.)?/, "");
            dom = dom.split("/")[0];

            if (dom.includes(".") && dom.length >= 4) {
                rawDomains.push(dom);
            }
        });

        // Eliminar duplicados
        const domains = [...new Set(rawDomains)];

        if (domains.length === 0) {
            alert("No se encontraron dominios con formato válido (ej. dominio.com).");
            return;
        }

        // 2. Reiniciar interfaz del escaneo
        resultsGrid.innerHTML = "";
        bulkResultsData = [];
        progressPanel.style.display = "block";
        downloadBtn.classList.add("disabled");
        downloadBtn.disabled = true;
        startBtn.disabled = true;
        startBtn.innerHTML = `<i class="fa-solid fa-circle-notch fa-spin"></i> Escaneando...`;

        // Estadísticas generales
        let counts = { disponible: 0, comprado: 0, desconocido: 0 };
        const total = domains.length;
        let processed = 0;

        actualizarProgresoMasivo(0, total, counts);

        const dnsOnly = scanMode.value === "dns_only";
        
        // Ejecución en paralelo con Throttling / Concurrencia controlada
        // 10 hilos en paralelo para DNS-Only, 3 hilos para modo Híbrido (evitar rate limit global)
        const limitConcurrencia = dnsOnly ? 10 : 3;
        const cola = [...domains];
        const promesasActivas = [];

        const procesarDominio = async (dom) => {
            try {
                // Hacer consulta individual de la API
                const res = await fetch(`/api/check?domain=${encodeURIComponent(dom)}&dns_only=${dnsOnly}`);
                if (!res.ok) throw new Error();
                const data = await res.json();

                if (data.status === "success") {
                    counts[data.estado]++;
                    processed++;
                    
                    // Guardar para la descarga
                    bulkResultsData.push({
                        domain: dom,
                        estado: data.estado.toUpperCase(),
                        metodo: data.info.metodo,
                        detalle: data.detalle
                    });

                    // Inyectar tarjeta mini interactiva en tiempo real
                    inyectarTarjetaMiniBulk(data);
                    actualizarProgresoMasivo(processed, total, counts);
                }
            } catch (err) {
                // Manejo de fallas de red en bloque
                counts.desconocido++;
                processed++;
                bulkResultsData.push({
                    domain: dom,
                    estado: "ERROR",
                    metodo: "Falla de conexión",
                    detalle: "Falla en fetch API"
                });
                inyectarTarjetaMiniBulk({
                    domain: dom,
                    estado: "desconocido",
                    detalle: "Falla al conectar API",
                    info: { metodo: "Falla de red" }
                });
                actualizarProgresoMasivo(processed, total, counts);
            }
            
            // Si el modo es híbrido, añadir un minúsculo retraso para suavizar WHOIS
            if (!dnsOnly) {
                await new Promise(r => setTimeout(r, 400));
            }
        };

        // Bucle de control de concurrencia
        while (cola.length > 0 || promesasActivas.length > 0) {
            while (promesasActivas.length < limitConcurrencia && cola.length > 0) {
                const siguienteDom = cola.shift();
                const promesa = procesarDominio(siguienteDom).then(() => {
                    // Remover promesa finalizada de las activas
                    promesasActivas.splice(promesasActivas.indexOf(promesa), 1);
                });
                promesasActivas.push(promesa);
            }
            // Esperar a que al menos una promesa termine antes de continuar cargando hilos
            await Promise.race(promesasActivas);
        }

        // 3. Finalización del escaneo masivo
        startBtn.disabled = false;
        startBtn.innerHTML = `<i class="fa-solid fa-play"></i> Iniciar escaneo masivo`;
        
        // Habilitar descarga
        downloadBtn.classList.remove("disabled");
        downloadBtn.disabled = false;
    });

    // Evento de Descargar Reporte
    downloadBtn.addEventListener("click", () => {
        if (bulkResultsData.length === 0) return;
        descargarReporteTxt();
    });
}

function inyectarTarjetaMiniBulk(data) {
    const resultsGrid = document.getElementById("bulk-results-grid");
    
    // Eliminar empty state si existe
    const emptyState = resultsGrid.querySelector(".empty-state");
    if (emptyState) emptyState.remove();

    const { domain, estado, detalle, info } = data;

    let cardClass = "unknown";
    let badgeClass = "unknown";
    let label = "Dudoso";

    if (estado === "disponible") {
        cardClass = "available";
        badgeClass = "available";
        label = "Libre";
    } else if (estado === "comprado") {
        cardClass = "taken";
        badgeClass = "taken";
        label = "Comprado";
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
    document.getElementById("stat-avail-count").innerText = counts.disponible;
    document.getElementById("stat-taken-count").innerText = counts.comprado;
    document.getElementById("stat-unk-count").innerText = counts.desconocido;

    const percentage = total > 0 ? Math.round((processed / total) * 100) : 0;
    
    progressFill.style.width = `${percentage}%`;
    progressPerc.innerText = `${percentage}%`;
    
    if (processed === total && total > 0) {
        progressText.innerHTML = `<i class="fa-solid fa-circle-check" style="color: var(--neon-green);"></i> ¡Escaneo masivo finalizado con éxito! (${processed}/${total})`;
    } else {
        progressText.innerText = `Escaneando: ${processed} de ${total} dominios...`;
    }
}

function descargarReporteTxt() {
    let contenido = `====================================================\n`;
    contenido += `      REPORTE DE DISPONIBILIDAD - LIBERDOM 🔎      \n`;
    contenido += `====================================================\n`;
    contenido += `Fecha del Escaneo: ${new Date().toLocaleString()}\n`;
    contenido += `Total de dominios analizados: ${bulkResultsData.length}\n\n`;
    
    contenido += `RESULTADOS DETALLADOS:\n`;
    contenido += `----------------------------------------------------\n`;
    
    bulkResultsData.forEach((row, idx) => {
        contenido += `${(idx + 1).toString().padStart(3, '0')}. DOMINIO: ${row.domain.padEnd(30)} | ESTADO: ${row.estado.padEnd(12)} | MÈTODO: ${row.metodo} (${row.detalle})\n`;
    });
    
    contenido += `\n====================================================\n`;
    contenido += `Desarrollado por NeoTurcios - LiberDom CLI & Web 🚀\n`;
    contenido += `====================================================\n`;

    // Descarga nativa vía Blob
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
