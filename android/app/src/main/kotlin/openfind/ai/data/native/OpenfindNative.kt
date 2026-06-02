package openfind.ai.data.native

import openfind.ai.domain.model.BrandScore
import openfind.ai.domain.model.DomainResult

object OpenfindNative {

    private var isLoaded = false

    init {
        try {
            System.loadLibrary("openfind_core")
            isLoaded = true
        } catch (e: UnsatisfiedLinkError) {
            isLoaded = false
        }
    }

    private fun ensureLoaded(): Boolean = isLoaded

    external fun checkDomain(domain: String, doAudit: Boolean): DomainResult
    external fun checkBulk(domains: Array<String>, doAudit: Boolean, delayMs: Int): Array<DomainResult>
    external fun generateNames(keyword: String, tlds: Array<String>, maxCount: Int): Array<String>
    external fun evaluateBrand(domain: String): BrandScore

    fun checkDomainSafe(domain: String, doAudit: Boolean): DomainResult? =
        if (ensureLoaded()) checkDomain(domain, doAudit) else null

    fun evaluateBrandSafe(domain: String): BrandScore? =
        if (ensureLoaded()) evaluateBrand(domain) else null
}
