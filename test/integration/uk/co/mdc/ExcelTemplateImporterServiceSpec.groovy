package uk.co.mdc

import grails.plugin.springsecurity.acl.AclUtilService
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.*
import uk.co.mdc.pathways.Pathway

class ExcelTemplateImporterServiceSpec extends IntegrationSpec {

    def fileName= "test/unit/resources/DataTemplate.xls"
    def fileNameError="test/unit/resources/ICUDataError.xls"
    def excelTemplateImporterService

    def setup(){}

    void "Test if DataElements name is not empty"()
    {
        when:"loading the dataElements"
        def InputStream = new FileInputStream(fileName)
        excelTemplateImporterService.importData(InputStream)

        then:"the dataElement should have name"

        DataElement.findByName("NHS number")

    }


}