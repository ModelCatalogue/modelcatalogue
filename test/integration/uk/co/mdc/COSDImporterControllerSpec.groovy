package uk.co.mdc

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import spock.lang.Ignore
import uk.co.mdc.utils.COSDImporterController
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest


/**
 * Created by sus_avi on 01/05/2014.
 */

class COSDImporterControllerSpec extends IntegrationSpec {

    def fileName= "test/unit/resources/COSD/COSD.xls"

	//This test takes a long time to load excel file and run !
    def "Test the dataImportService in the COSDImporterController"()
    {
        when: "The dataImportService is called"
        def cosdImporterController = new COSDImporterController()

        def numElements = DataElement.count()
        cosdImporterController.metaClass.request = new MockMultipartHttpServletRequest()
        InputStream inputStream = new FileInputStream(fileName)
        cosdImporterController.request.addFile(new MockMultipartFile('excelFile', fileName,"application/octet-stream" , inputStream))
        cosdImporterController.upload()

        then: "The number of Elements must be greater"
        def numElementsAfter = DataElement.count()
        assert  numElements<numElementsAfter
    }


}
