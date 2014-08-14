package uk.co.mdc

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import uk.co.mdc.utils.SACTImporterController

/**
 * Created by sus_avi on 13/08/2014.
 */

class SACTImporterControllerSpec extends IntegrationSpec  {

    String filenameSACT = "test/unit/resources/SACT/SACTSACT_XMLSchema_EXAMPLE.xsd"
    String filenameCommonTypes = "test/unit/resources/SACT/SACT_XMLDataTypes_EXAMPLE.xsd"

    //This test takes a long time to load excel file and run !
    def "Test the dataImportService in the COSDImporterController"()
    {
        when: "The dataImportService is called"
        def sactImporterController = new SACTImporterController()

        def numElements = DataElement.count()
        def numDataTypes = DataType.count()
        def numValueDomains = ValueDomain.count()
        def numModels = Model.count()
        sactImporterController.metaClass.request = new MockMultipartHttpServletRequest()
        InputStream inputStreamSACT = new FileInputStream(filenameSACT)
        InputStream inputStreamCommonTypes = new FileInputStream(filenameCommonTypes)
        sactImporterController.request.addFile(new MockMultipartFile('xsdSACTFile', filenameSACT,"application/octet-stream" , inputStreamSACT))
        sactImporterController.request.addFile(new MockMultipartFile('xsdSACTTypesFile', filenameCommonTypes,"application/octet-stream" , inputStreamCommonTypes))
        sactImporterController.upload()

        then: "The number of Elements must be greater"
        def numElementsAfter = DataElement.count()
        def numDataTypesAfter = DataType.count()
        def numValueDomainsAfter = ValueDomain.count()
        def numModelsAfter = Model.count()

        assert  numElements < numElementsAfter
        assert  numDataTypes < numDataTypesAfter
        assert  numValueDomains < numValueDomainsAfter
        assert  numModels < numModelsAfter
    }
}
