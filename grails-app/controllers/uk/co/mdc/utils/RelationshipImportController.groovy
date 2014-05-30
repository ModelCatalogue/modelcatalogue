package uk.co.mdc.utils

import org.modelcatalogue.core.Model
import org.modelcatalogue.core.dataarchitect.ExcelLoader
import org.modelcatalogue.core.dataarchitect.HeadersMap
import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

@Secured(['ROLE_ADMIN'])
class RelationshipImportController {

    def relationshipImporterService

    def index() {}

    def upload()
    {
        if(!(request instanceof MultipartHttpServletRequest))
        {
            flash.error="No File to process!"
            render view:"index"
            return
        }
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request
        MultipartFile  file = multiRequest.getFile("excelFile")

        //Microsoft Excel files
        //Microsoft Excel 2007 files
        def okContentTypes = ['application/vnd.ms-excel','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'application/octet-stream']
        def confType=file.getContentType()
        if (okContentTypes.contains(confType) && file.size > 0){
            try {
                ExcelLoader parser = new ExcelLoader(file.inputStream)
                def (headers, rows) = parser.parse()

                def errors = relationshipImporterService.importRelationships(headers, rows)
                //if (result) {
                flash.message = "DataElements have been created.\n with {$errors.size()} errors ( ${errors.toString()} )"
                //}
            }
            catch(Exception ex)
            {
                //log.error("Exception in handling excel file: "+ ex.message)
                log.error("Exception in handling excel file")
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