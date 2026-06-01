/* ==========================================================================
   INTERACTIVIDAD Y TRADUCCIONES - OPENFIND AI LANDING SHOWCASE
   ========================================================================= */

// ==========================================================================
// 1. Diccionario Global de Traducciones (I18n Engine)
// ==========================================================================
const translations = {
    es: {
        "nav.start": '<i class="fa-solid fa-house"></i> Inicio',
        "nav.androidapp": '<i class="fa-solid fa-mobile-screen-button"></i> App Android',
        "nav.termuxcli": '<i class="fa-solid fa-terminal"></i> Termux CLI',
        "nav.bot": '<i class="fa-solid fa-robot"></i> Bot Telegram',
        "nav.about": '<i class="fa-solid fa-user-gear"></i> Nosotros',
        "nav.github": '<i class="fa-brands fa-github"></i> Código',

        "hero.badge": '<i class="fa-solid fa-sparkles"></i> Ecosistema 100% Autónomo & Local',
        "hero.title": "Búsqueda Inteligente de Dominios a Nivel de Red",
        "hero.subtitle": "El motor híbrido definitivo que combina resolución DNS ultrarrápida, sockets TCP recursivos WHOIS y agentes locales de marca. Diseñado para Android, terminales Termux y Telegram.",
        "hero.btn_android": "Ver App Android",
        "hero.btn_cli": "Ver Termux CLI",

        "tools.section_title": '<i class="fa-solid fa-cubes"></i> El Ecosistema OpenFind AI',
        "tools.android_desc": "Cliente premium nativo para teléfonos Android. Incluye evaluación semántica local, biblioteca persistente limitada y visualizaciones neón en tiempo real.",
        "tools.cli_desc": "Utilería de terminal ultra-rápida escrita en Python nativo puro. Optimizado para Termux con consumo nulo de datos extra e importación de archivos locales.",
        "tools.bot_desc": "Agente conversacional autónomo para comprobar la disponibilidad y estado técnico de cualquier dominio en milisegundos directamente en Telegram.",
        "tools.learn_more": "Explorar detalles",

        "app.tech_label": "Arquitectura Móvil Novedosa",
        "app.title": "OpenFind App - Cliente Android Premium",
        "app.feat1_title": "Código Kotlin Puro (openfind.ai)",
        "app.feat1_desc": "Compilado bajo Android 16 (SDK 36) con soporte retrospectivo estable hasta SDK 26. Código 100% Kotlin limpio organizado en dos niveles de paquetes, libre de archivos Java.",
        "app.feat2_title": "Agente IA de Marca Lingüístico",
        "app.feat2_desc": "Evalúa dominios localmente según fluidez silábica, memorabilidad, idoneidad del TLD, y se personaliza aprendiendo de los intereses almacenados en tu dispositivo.",
        "app.feat3_title": "Gestión de Almacenamiento Inteligente (Límite: 10)",
        "app.feat3_desc": "Evita bases de datos infinitas o lentitud limitando el almacenamiento a 10 elementos. Avisa al usuario con un elegante diálogo para realizar limpiezas periódicas (Opción A / Opción B / Cancelar).",
        "app.feat4_title": "Sistema de Iconos XML Sin Emojis",
        "app.feat4_desc": "Cumple con las normas visuales de las tiendas de apps modernas, utilizando exclusivamente drawables vectoriales XML nítidos para todas las acciones de la interfaz.",
        "app.download_btn": "Descargar APK Compilada",

        "cli.tech_label": "Consola y Red a bajo nivel",
        "cli.title": "OpenFind Termux - La versión CLI definitiva",
        "cli.desc_p": "Especialmente diseñado para ejecutarse en terminales de celulares Android (a través de la app Termux) y servidores Linux. Escrito en Python nativo puro, sin dependencias pesadas ni necesidad de compiladores.",
        "cli.step1_title": "<i class=\"fa-solid fa-terminal\"></i> Instalación en un solo comando:",
        "cli.step2_title": "<i class=\"fa-solid fa-terminal\"></i> Ejecución rápida (Alias directo):",
        "cli.step2_helper": '<i class="fa-solid fa-circle-info"></i> El instalador crea de forma segura un alias de sistema en tu terminal.',

        "bot.tech_label": "Agente de Red Conversacional",
        "bot.title": "Telegram Bot Autónomo",
        "bot.desc_p": "Integra un script de bot en Python que opera de manera asíncrona sobre los servidores de Telegram. Te permite interactuar y auditar dominios enviando mensajes de chat simples desde cualquier lugar.",
        "bot.feat1_title": "Respuestas en Milisegundos",
        "bot.feat1_desc": "Combina los mismos motores de sockets TCP para procesar de forma inmediata la disponibilidad de dominios y devolver tarjetas con formato HTML.",
        "bot.feat2_title": "Configuración en Consola Segura",
        "bot.feat2_desc": "Al ejecutar <code>python telegram_bot.py</code> por primera vez, el CLI te pedirá de forma segura tu token de BotFather y creará un archivo de configuración local cifrado.",

        "arch.title": '<i class="fa-solid fa-gears"></i> ¿Cómo funciona bajo el capó?',
        "arch.subtitle": "El motor de OpenFind no depende de intermediarios costosos. Funciona directamente en las capas de red de internet.",
        "arch.step1_title": "Paso 1: Resolución DNS",
        "arch.step1_desc": "Realiza una consulta ultra-veloz a los servidores DNS. Si el dominio responde con una dirección IP activa, se marca como Ocupado inmediatamente sin consumir recursos adicionales.",
        "arch.step2_title": "Paso 2: Consulta Socket WHOIS",
        "arch.step2_desc": "Si no hay registros DNS, abre un socket TCP directo en el puerto 43 hacia el servidor de IANA y redirige recursivamente la consulta al servidor TLD oficial para confirmar si está libre.",
        "arch.step3_title": "Paso 3: Evaluación de Marca",
        "arch.step3_desc": "El agente de inteligencia local del dispositivo procesa el nombre y genera calificaciones dinámicas de memorabilidad y fluidez del nombre de marca.",

        "about.title": '<i class="fa-solid fa-user-shield"></i> Nosotros y Licencia del Proyecto',
        "about.creator_title": "Desarrollador Principal",
        "about.bio": "Creador de herramientas autónomas, optimizadas para el rendimiento móvil y de red local. Desarrollando soluciones libres de APIs comerciales invasivas.",
        "about.license_title": '<i class="fa-solid fa-file-signature"></i> Licencia de Uso No Comercial',
        "about.license_p1": "Este software está distribuido bajo los términos de una licencia personalizada de <strong>Uso Personal No Comercial con Atribución Obligatoria</strong>.",
        "about.license_item1": '<i class="fa-solid fa-circle-xmark" style="color: var(--neon-red)"></i> Prohibido monetizar: Queda estrictamente prohibido vender, alquilar, sublicenciar o colocar publicidad comercial en este software o sus derivados.',
        "about.license_item2": '<i class="fa-solid fa-circle-check" style="color: var(--neon-green)"></i> Atribución obligatoria: Al redistribuir, se debe mantener de manera visible en la interfaz el enlace al repositorio original del motor.',
        "about.license_btn": "Leer Licencia Completa",
        "about.projects_title": '<i class="fa-solid fa-folder-open"></i> Más Proyectos de NeoTurcios',
        "about.proj1_desc": "Explora la lista completa de utilidades de consola y scripts en GitHub.",
        "about.proj2_desc": "Plataforma oficial y núcleo del ecosistema digital de desarrollo.",
        "about.privacy_btn": "Política de Privacidad de la Play Console",

        "footer.text": "Diseñado con amor y código abierto por <strong>NeoTurcios</strong> | Licencia No Comercial © 2026"
    },
    en: {
        "nav.start": '<i class="fa-solid fa-house"></i> Home',
        "nav.androidapp": '<i class="fa-solid fa-mobile-screen-button"></i> Android App',
        "nav.termuxcli": '<i class="fa-solid fa-terminal"></i> Termux CLI',
        "nav.bot": '<i class="fa-solid fa-robot"></i> Telegram Bot',
        "nav.about": '<i class="fa-solid fa-user-gear"></i> About Us',
        "nav.github": '<i class="fa-brands fa-github"></i> Code',

        "hero.badge": '<i class="fa-solid fa-sparkles"></i> 100% Autonomous & Local Ecosystem',
        "hero.title": "Smart Domain Audit at Network Level",
        "hero.subtitle": "The ultimate hybrid engine combining ultra-fast DNS resolution, recursive WHOIS TCP sockets, and local brand agents. Built for Android, Termux terminals, and Telegram.",
        "hero.btn_android": "Explore Android App",
        "hero.btn_cli": "Explore Termux CLI",

        "tools.section_title": '<i class="fa-solid fa-cubes"></i> The OpenFind AI Ecosystem',
        "tools.android_desc": "Native premium client for Android smartphones. Features local semantic analysis, persistency capping, and real-time neon metrics.",
        "tools.cli_desc": "Ultra-fast terminal utility written in pure native Python. Optimized for Termux with zero extra data consumption and local file importing.",
        "tools.bot_desc": "Conversational autonomous agent to verify availability and technical status of any domain in milliseconds directly on Telegram.",
        "tools.learn_more": "Explore details",

        "app.tech_label": "Novel Mobile Architecture",
        "app.title": "OpenFind App - Premium Android Client",
        "app.feat1_title": "Pure Kotlin Code (openfind.ai)",
        "app.feat1_desc": "Compiled under Android 16 (SDK 36) with stable backward compatibility down to SDK 26. Clean 100% Kotlin codebase structured in two package levels, free of Java.",
        "app.feat2_title": "Linguistic Brand AI Agent",
        "app.feat2_desc": "Evaluates domains locally based on syllable rhythm, memorability, TLD fitness, and customizes itself by learning from saved items on your device.",
        "app.feat3_title": "Smart Storage Capping (Limit: 10)",
        "app.feat3_desc": "Prevents database bloating or lag by restricting local database items to 10. Prompts the user with an elegant dialog to clear or auto-clean storage.",
        "app.feat4_title": "XML Vector Icons System (No Emojis)",
        "app.feat4_desc": "Strictly complies with modern app store guidelines, using exclusively vector XML drawables for all visual actions.",
        "app.download_btn": "Download Compiled APK",

        "cli.tech_label": "Low-level Console & Network",
        "cli.title": "OpenFind Termux - The Ultimate CLI Utility",
        "cli.desc_p": "Specially designed to run on Android phone terminal emulators (Termux) and Linux servers. Written in pure native Python, with zero external dependencies.",
        "cli.step1_title": "<i class=\"fa-solid fa-terminal\"></i> One-command Installation:",
        "cli.step2_title": "<i class=\"fa-solid fa-terminal\"></i> Fast Execution (Direct Alias):",
        "cli.step2_helper": '<i class="fa-solid fa-circle-info"></i> The installer securely registers a binary system alias in your terminal path.',

        "bot.tech_label": "Conversational Network Agent",
        "bot.title": "Autonomous Telegram Bot",
        "bot.desc_p": "Integrates an asynchronous Python bot script operating over Telegram APIs. It enables auditing and inspecting domains directly on the go via chat messages.",
        "bot.feat1_title": "Millisecond Response Cards",
        "bot.feat1_desc": "Utilizes the same low-level TCP socket engines to process domains instantly and return cards formatted with clean HTML tags.",
        "bot.feat2_title": "Secure Console Setup",
        "bot.feat2_desc": "Running <code>python telegram_bot.py</code> for the first time will securely prompt for your BotFather token and generate a local encrypted config file.",

        "arch.title": '<i class="fa-solid fa-gears"></i> How does it work under the hood?',
        "arch.subtitle": "The OpenFind engine does not rely on third-party brokers. It runs directly on internet network layers.",
        "arch.step1_title": "Step 1: DNS Resolution",
        "arch.step1_desc": "Executes an ultra-fast DNS lookup. If the domain resolves to an active IP address, it is immediately marked as Taken, saving network resources.",
        "arch.step2_title": "Step 2: WHOIS Socket Query",
        "arch.step2_desc": "If no DNS records exist, it opens a direct TCP socket on port 43 to IANA and recursively queries official TLD servers to confirm availability.",
        "arch.step3_title": "Step 3: Brand Assessment",
        "arch.step3_desc": "The local device AI agent processes the brand name and calculates dynamic scores for memorability and syllable pronunciation.",

        "about.title": '<i class="fa-solid fa-user-shield"></i> Repository Details & License',
        "about.creator_title": "Lead Developer",
        "about.bio": "Creator of autonomous utilities, optimized for mobile and local network performance. Developing lightweight software free of commercial APIs.",
        "about.license_title": '<i class="fa-solid fa-file-signature"></i> Non-Commercial Use License',
        "about.license_p1": "This software is distributed under a customized license of <strong>Personal Non-Commercial Use with Mandatory Attribution</strong>.",
        "about.license_item1": '<i class="fa-solid fa-circle-xmark" style="color: var(--neon-red)"></i> Monetization Banned: Selling, renting, sublicensing, or putting commercial ads in this software is strictly prohibited.',
        "about.license_item2": '<i class="fa-solid fa-circle-check" style="color: var(--neon-green)"></i> Mandatory Attribution: When redistributing, you must preserve a visible link to the original repository.',
        "about.license_btn": "Read Full License Terms",
        "about.projects_title": '<i class="fa-solid fa-folder-open"></i> More Projects by NeoTurcios',
        "about.proj1_desc": "Explore the complete portfolio of CLI scripts and tools on GitHub.",
        "about.proj2_desc": "Official core and portal of the digital development ecosystem.",
        "about.privacy_btn": "Play Console Privacy Policy",

        "footer.text": "Designed with love and open source by <strong>NeoTurcios</strong> | Non-Commercial License © 2026"
    }
};

let currentLang = localStorage.getItem("openfind_lang") || "es";

// ==========================================================================
// 2. Inicialización
// ==========================================================================
document.addEventListener("DOMContentLoaded", () => {
    inicializarSelectorIdioma();
    inicializarCopiarCodigo();
    inicializarNavegacionScroll();
    
    // Aplicar traducción inicial
    aplicarTraducciones(currentLang);
});

// ==========================================================================
// 3. Sistema de Idioma e Interfaz (I18n Switcher)
// ==========================================================================
function inicializarSelectorIdioma() {
    const langBtn = document.getElementById("lang-btn");
    const langDropdown = document.getElementById("lang-dropdown");
    const langOptions = document.querySelectorAll(".lang-option");

    langBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        langDropdown.classList.toggle("show");
    });

    document.addEventListener("click", () => {
        langDropdown.classList.remove("show");
    });

    langOptions.forEach(opt => {
        opt.addEventListener("click", () => {
            const lang = opt.getAttribute("data-lang");
            currentLang = lang;
            localStorage.setItem("openfind_lang", lang);
            aplicarTraducciones(lang);
        });
    });
}

function aplicarTraducciones(lang) {
    document.querySelectorAll("[data-i18n]").forEach(elem => {
        const key = elem.getAttribute("data-i18n");
        if (translations[lang] && translations[lang][key]) {
            // Preservar iconos internos si existen
            const icon = elem.querySelector("i");
            if (icon) {
                const iconHtml = icon.outerHTML;
                const cleanText = translations[lang][key].replace(/<i[^>]*><\/i>/i, "").trim();
                elem.innerHTML = `${iconHtml} ${cleanText}`;
            } else {
                elem.innerHTML = translations[lang][key];
            }
        }
    });

    // Actualizar etiquetas visuales de idioma
    document.getElementById("current-lang-label").innerText = lang === "es" ? "Español" : "English";
    
    document.querySelectorAll(".lang-option").forEach(opt => {
        if (opt.getAttribute("data-lang") === lang) {
            opt.classList.add("active");
        } else {
            opt.classList.remove("active");
        }
    });
}

// ==========================================================================
// 4. Copiar Código en Portapapeles (Clipboard Copy)
// ==========================================================================
function inicializarCopiarCodigo() {
    const copyButtons = document.querySelectorAll(".copy-code-btn");

    copyButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-target");
            const codeText = document.getElementById(targetId).innerText;

            navigator.clipboard.writeText(codeText).then(() => {
                const originalIcon = btn.innerHTML;
                btn.innerHTML = `<i class="fa-solid fa-check" style="color: var(--neon-green);"></i>`;
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

// ==========================================================================
// 5. Navegación por Scroll Activa (Active Section Scroll Highlighter)
// ==========================================================================
function inicializarNavegacionScroll() {
    const tabLinks = document.querySelectorAll(".tab-link");
    const sections = document.querySelectorAll("section[id]");

    // Escuchar el evento scroll para resaltar enlaces
    window.addEventListener("scroll", resaltarEnlacesNavegacion);

    function resaltarEnlacesNavegacion() {
        let scrollY = window.pageYOffset;
        
        sections.forEach(current => {
            const sectionHeight = current.offsetHeight;
            // Un desplazamiento de 120px para mejorar la precisión del resalte al hacer scroll
            const sectionTop = current.offsetTop - 120;
            const sectionId = current.getAttribute("id");
            
            if (scrollY > sectionTop && scrollY <= sectionTop + sectionHeight) {
                tabLinks.forEach(link => {
                    link.classList.remove("active");
                    if (link.getAttribute("href") === `#${sectionId}`) {
                        link.classList.add("active");
                    }
                });
            }
        });
    }

    // Asegurar el clic suave y la clase activa inmediata al hacer clic
    tabLinks.forEach(link => {
        link.addEventListener("click", (e) => {
            tabLinks.forEach(l => l.classList.remove("active"));
            link.classList.add("active");
        });
    });
}
