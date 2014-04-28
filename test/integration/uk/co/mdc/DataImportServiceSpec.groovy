package uk.co.mdc

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import uk.co.mdc.Importers.ExcelLoader

class DataImportServiceSpec extends IntegrationSpec {

    def fileName= "test/unit/resources/DataTemplate.xls"
    def fileNameError="test/unit/resources/ICUDataError.xls"
    def dataImportService

    def setup(){}

    void "Test if DataElements name is not empty"()
    {
        when:"loading the dataElements"
        def inputStream = new FileInputStream(fileName)
        ExcelLoader parser = new ExcelLoader(inputStream)
        def (headers, rows) = parser.parse()
        dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1"])

        then:"the dataElement should have name"
        DataElement.findByName("NHS number")

    }


}