package uk.co.mdc.utils

import org.modelcatalogue.core.Model
import org.modelcatalogue.core.dataarchitect.HeadersMap

/**
 * Created by sus_avi on 26/03/2014.
 */
import org.springframework.security.access.annotation.Secured
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.mdc.Importers.COSDExcelLoader
import uk.co.mdc.Importers.ExcelSheet

@Secured(['ROLE_ADMIN'])
class COSDImporterController {

    def dataImportService

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

                COSDExcelLoader parser = new COSDExcelLoader(file.inputStream)
                ExcelSheet[] excelSheets = parser.parse();
                ExcelSheet[] excelCOSDSheets = new ExcelSheet[excelSheets.size()]
                excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
                    def headers = excelSheets[contSheet].headers
                    def sheetName = excelSheets[contSheet].sheetName
                    def rows = excelSheets[contSheet].rows
                    def (headersCOSDSheet, rowsCOSDSheet, logMessage) = parser.generateCOSDInfoArray(sheetName, headers, rows)
                    excelCOSDSheets[contSheet] = new ExcelSheet(sheetName:sheetName, headers:headersCOSDSheet, rows:rowsCOSDSheet)
                    if (logMessage!="") {
                        flash.message = logMessage
                    }

                    HeadersMap headersMap = new HeadersMap()
                    headersMap.dataElementCodeRow = ""
                    headersMap.dataElementNameRow = "Data Item Name"
                    headersMap.dataElementDescriptionRow = "Data Item Description"
                    headersMap.dataTypeRow = "List content"
                    headersMap.parentModelNameRow = "Parent Model"
                    headersMap.parentModelCodeRow = ""
                    headersMap.containingModelNameRow = "Containing Model"
                    headersMap.containingModelCodeRow = ""
                    headersMap.measurementUnitNameRow = ""
                    headersMap.metadataRow = "Metadata"
                    dataImportService.importData(headersCOSDSheet, rowsCOSDSheet, "COSD", "Cancer Outcomes and Services Dataset", headersMap)
                }
                excelCOSDSheets
                flash.message = "DataElements have been created.\n"
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