package uk.co.mdc

import org.modelcatalogue.core.*
import spock.lang.Specification

/**
 * Created by adammilward on 11/02/2014.
 */
class ImportNHICServiceSpec extends Specification {

    def importNHICService

    def setupSpec(){
    }

    def "import nhic spreadsheet"() {

        when:
        importNHICService.singleImport('/CAN/CAN_CUH.csv')

        def core = Model.findByName("CORE")
        def patientIdentity = Model.findByName("PATIENT IDENTITY DETAILS")
        def NHICConceptualDomain = ConceptualDomain.findByName("NHIC : Ovarian Cancer")
        def indicatorCode = DataType.findByName("NHS_NUMBER_STATUS_INDICATOR_CODE")
        def valueDomain = ValueDomain.findByName("NHS_NUMBER_STATUS_INDICATOR_CODE")
        def dataElement = DataElement.findByName("NHS NUMBER STATUS INDICATOR CODE")

        then:
        core.id
        patientIdentity.id
        indicatorCode.id
        NHICConceptualDomain.id
        valueDomain.id
        dataElement.id
        patientIdentity.childOf.contains(core)
        core.parentOf.contains(patientIdentity)
        patientIdentity.hasContextOf.contains(NHICConceptualDomain)
        core.hasContextOf.contains(NHICConceptualDomain)
        HashMap<String, String> icodehash = new HashMap(
                '01': 'Number present and verified',
                '02': 'Number present but not traced',
                '03': 'Trace required',
                '04': 'Trace attempted - No match or multiple match found',
                '05': 'Trace needs to be resolved - (NHS Number or patient detail conflict)',
                '06': 'Trace in progress',
                '07': 'Number not present and trace not required',
                '08': 'Trace postponed (baby under six weeks old)'
        )
        def icodeEnumerations = new HashMap<String, String>(indicatorCode.enumerations)
        assert icodehash.entrySet().containsAll(icodeEnumerations.entrySet())

        valueDomain.includedIn == [NHICConceptualDomain]
        valueDomain.instantiates == [dataElement]

    }

    def "test two imports of the same data produce"(){

        when:
        def error1 = importNHICService.singleImport('/TRA/TRA_OUH.csv')
        def error2 = importNHICService.singleImport('/TRA/TRA_OUH.csv')

        then:
        error2.toMapString() == "[model exists:model already exists for TRA_OUH]"

    }

}
