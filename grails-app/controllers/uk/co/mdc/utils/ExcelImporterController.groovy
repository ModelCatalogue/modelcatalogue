package uk.co.mdc.utils

import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.mdc.Importers.ExcelLoader
import uk.co.mdc.utils.importers.ICUExcelImporterService

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Secured(['ROLE_ADMIN'])
class ExcelImporterController {

    def ICUExcelImporterService

    def index() {}

    def upload()
    {
        if(!(request instanceof MultipartHttpServletRequest))
        {
            flash.error="No File to process!"
            render view:"index"
            return
        }

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
        MultipartFile  file = multiRequest.getFile("excelFile");

        //Microsoft Excel files
        //Microsoft Excel 2007 files
        def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
        def confType=file.getContentType();
        if (okContentTypes.contains(confType) && file.size > 0){
            try {
                 def dataElements= ICUExcelImporterService.GetICUDataElementNames(file.inputStream);
                 def result=ICUExcelImporterService.SaveICUDataElement(dataElements);
                 if(result)
                    flash.message = "Pathway and DataElements are created.\n"+
                                     dataElements.size()+" records in input file processed."
            }
            catch(Exception ex)
            {
                log.error("Exception in handling excel file :"+ex.message)
                flash.message ="Error in importing the excel file.";
            }
         }
        else
        {
            if(!okContentTypes.contains(confType))
               flash.message ="Input should be an Excel file!\n"+
                          "but uploaded content is "+confType
            else if (file.size<=0)
                flash.message ="The uploaded file is empty!"
        }

        render view: 'index'
    }
}