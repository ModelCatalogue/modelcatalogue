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
            render view:""
            return
        }

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
        List<MultipartFile> files = multiRequest.getFiles("excelFile");

        files.each{ MultipartFile file ->
            def okContentTypes = ['application/vnd.ms-excel'];
            def confType=file.getContentType();
            if (okContentTypes.contains(confType) && file.size > 0){
                def dataElements
                try {
                     dataElements= ICUExcelImporterService.GetICUDataElementNames(file.inputStream);
                     def result=ICUExcelImporterService.SaveICUDataElement(dataElements);
                     flash.message = "Pathway and DataElements are created."
                }
                catch(Exception ex)
                {
                    flash.message =ex.message;
                }
             }
        }

        render view: 'index'
    }
}