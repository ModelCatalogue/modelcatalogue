package uk.co.mdc.utils

import grails.test.mixin.TestFor
import spock.lang.Specification
import uk.co.mdc.utils.importers.ImportNHICService

/**
 * Created by Ryan Brooks on 19/02/2014.
 */
@TestFor(DataImportController)
class DataImportControllerTest extends Specification {

    def mockService = mockFor(ImportNHICService)
    def fileList = ["filea", "bc/fileb", "some/other/file"]

    def setup() {
        controller.importNHICService = mockService.createMock()
    }

    def "getting the list of available files"(){

        given: "a list of files to be returned by our mock service"
        mockService.demand.getNhicFiles { -> return fileList}

        when: "the controller is called"
        def model = controller.index()

        then: "the model contains the correct file list"
        model['nhicFiles'] == fileList
    }

    def "not specifying a dataset to import"(){

        when: "an import with no parameters"
        controller.importDataSet()

        then: "the controller should return a flash error"
        flash.message == "dataimport.paramError"
        flash.default == "Error: invalid dataset"
    }

    def "importing the entire NHIC dataset"(){

        Boolean imported = false

        given:
        mockService.demand.importData { ->
            imported = true
        }

        when: "the controller is called for the NHIC dataset"
        params.dataset = 'nhic'
        controller.importDataSet()

        then:
        imported
    }

    def "importing a single NHIC dataset item"(){

        Boolean imported = false

        given:
        mockService.demand.singleImport { filename ->
            if(filename == params.nhicFile) imported = true
        }
        params.dataset = 'nhic'
        params.nhicFile = nhicFile
        controller.importDataSet()

        expect:
        imported

        where:
        nhicFile << [
                "file1",
                "/a/root/directory/",
                "a/relative/directory/",
                "/a/root/file",
                "a/relative/file"
        ]
    }
}
