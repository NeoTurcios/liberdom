package openfind.ai.data.native

import openfind.ai.domain.model.BrandScore
import openfind.ai.domain.model.DomainResult

object OpenfindNative {

    init {
        System.loadLibrary("openfind_core")
    }

    external fun checkDomain(domain: String, doAudit: Boolean): DomainResult
    external fun checkBulk(domains: Array<String>, doAudit: Boolean, delayMs: Int): Array<DomainResult>
    external fun generateNames(keyword: String, tlds: Array<String>, maxCount: Int): Array<String>
    external fun evaluateBrand(domain: String): BrandScore
}
