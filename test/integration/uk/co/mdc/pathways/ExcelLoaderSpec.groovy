package uk.co.mdc.pathways

import grails.test.mixin.TestFor
import uk.co.mdc.Importers.ExcelLoader

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class ExcelLoaderSpec extends spock.lang.Specification {

    def fileName= "test/integration/resources/ICUData.xls"

    void "Test if parse will load an excel file"() {

        when:"the file is loaded"
        def importer = new ExcelLoader(fileName)
        def (headers, rows) = importer.parse()


        then:"The file content is loaded properly"
        headers
        rows
    }

    void "Test if parse can load byte content"()
    {
        when:"Passing an inputstream to excelLoader"
        InputStream inputStream = new FileInputStream(fileName)

        def importer = new ExcelLoader(inputStream)
        def (headers, rows) = importer.parse()

        then:"The file content is loaded properly"
        headers
        rows

    }
}
