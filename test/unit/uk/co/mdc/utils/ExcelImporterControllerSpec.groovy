package uk.co.mdc.utils

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.multipart.MultipartHttpServletRequest
import spock.lang.Specification
import uk.co.mdc.utils.importers.ICUExcelImporterService

/**
 * Created by soheil on 20/03/2014.
 */
@TestFor(ExcelImporterController)
class ExcelImporterControllerSpec extends  Specification{

    def serviceMock= Mock(uk.co.mdc.utils.importers.ICUExcelImporterService)


    def setup()
    {
        controller.ICUExcelImporterService =serviceMock;
    }

    void "Test if controller rejects files other than Excel"()
    {
        given:"An image file is uploaded"
        def contentType='image/jpeg'
        def mockFile = new MockMultipartFile('excelFile', 'input.jpg',contentType , "TestMockContent" as byte[])

        when:"uploading a file content other than excel"
        controller.metaClass.request = new MockMultipartHttpServletRequest();
        controller.request.addFile(mockFile)
        controller.upload();

        then:"the file upload should be rejected"
        0 * serviceMock.GetICUDataElementNames(mockFile.inputStream) >>{ -> return []  }
        0 * serviceMock.SaveICUDataElement([])>>{-> return [conceptualDomain: null,pathway:null]}
        flash.message =="Input should be an Excel file!\n"+"but uploaded content is "+contentType
        controller.modelAndView.viewName == "/excelImporter/index"

    }

    void "Test if controller rejects Excel files with size zero"()
    {
        when:"uploading a file content other than excel"
        def contentType='application/vnd.ms-excel'
        controller.metaClass.request = new MockMultipartHttpServletRequest()
        controller.request.addFile(new MockMultipartFile('excelFile', 'myDataFile.xls',contentType , "" as byte[]))
        controller.upload();

        then:"the file upload should be rejected"
        flash.message =="The uploaded file is empty!"
        controller.modelAndView.viewName == "/excelImporter/index"
    }


    void "Test if controller returns correct error message in case of exception"()
    {

        given:"An excel file is uploaded"
        def contentType='application/vnd.ms-excel'
        def mockFile = new MockMultipartFile('excelFile', 'myFile.xls',contentType , "TestMockContent" as byte[])


        when:"the upload is called"
        controller.metaClass.request = new MockMultipartHttpServletRequest();
        controller.request.addFile(mockFile)
        controller.upload();

        then:"the service will throw an exception"
        serviceMock.SaveICUDataElement(_) >>{ -> throw new Exception("some exceptions")  }


        and:"the file upload should be rejected"
        flash.message =="Error in importing the excel file."
        controller.modelAndView.viewName == "/excelImporter/index"


    }
}