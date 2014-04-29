package uk.co.mdc.utils

import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

@Secured(['ROLE_ADMIN'])
class HEPExcelImporterController {

    def HEPExcelImporterService

    def index() {}

    def upload() {
        if(!(request instanceof MultipartHttpServletRequest))
        {
            flash.error="No File to process!"
            render view:"index"
            return
        }

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
        MultipartFile  file = multiRequest.getFile("excelFile");

        def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
        def confType=file.getContentType();
        if (okContentTypes.contains(confType) && file.size > 0){
            try {
                def diList = HEPExcelImporterService.loadDataElementFromExcel(file.inputStream)
                render view: 'index', model: [diList:diList]

//                def dataElements= ICUExcelImporterService.GetICUDataElementNames(file.inputStream);
//                def result=ICUExcelImporterService.SaveICUDataElement(dataElements);
//                if(result)
//                    flash.message = "Pathway and DataElements are created.\n"+
//                            dataElements.size()+" records in input file are processed."
            }
            catch(Exception ex)
            {
                println('sada   ')
                //log.error("Exception in handling excel file " + ex.message)
                flash.message ="Error in importing the excel file.";
                render view: 'index'
            }
        }
        else
        {
            if(!okContentTypes.contains(confType))
                flash.message ="Input should be an Excel file!\n"+
                        "but uploaded content is "+confType
            else if (file.size<=0)
                flash.message ="The uploaded file is empty!"

            render view: 'index'
        }


    }
}
