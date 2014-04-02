package uk.co.mdc.utils

/**
 * Created by sus_avi on 26/03/2014.
 */
import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

@Secured(['ROLE_ADMIN'])
class COSDImporterController {

    def COSDImporterService

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
        def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream'];
        def confType=file.getContentType();
        if (okContentTypes.contains(confType) && file.size > 0){
            try {

                //Clean the Excel file first from Headers
                // When adding elements consider the blank rows (to match the value domain) for the dataElements.
                def totalElementsImported =  COSDImporterService.saveCOSDDataElements(file.inputStream);

                if(totalElementsImported!=null)
                    flash.message = "DataElementConcepts and DataElements are created.\n"+
                            totalElementsImported + " records in input file were processed."
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