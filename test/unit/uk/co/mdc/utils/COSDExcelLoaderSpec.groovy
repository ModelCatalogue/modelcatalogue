package uk.co.mdc.utils

import grails.test.mixin.TestFor
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import spock.lang.Specification
import uk.co.mdc.Importers.COSDExcelLoader
import uk.co.mdc.Importers.ExcelSheet

/**
 * Created by sus_avi on 28/04/2014.
 */


class COSDExcelLoaderSpec extends Specification {

    // List the things I want to test.
    // File exists

    def fileName= "test/unit/resources/COSD/COSD.xls"
    // COSD_Error has:
    //                  * no CORE sheet.
    //                  * Breast sheet has different headers title names. i.e. Data Item Name => Data Item Names.

    String[] sheetNamesToImport = [
            "Core",
            "Breast", "CNS", "Colorectal", "CTYA ", "Gynaecology",
            "Haematology", "Head & Neck", "Lung", "Sarcoma", "Skin",
            "Upper GI", "Urology", "Reference - Other Sources"
    ];

    def headerNamesToImport = [
            "Data item No.", "Data Item Section", "Data Item Name",
            "Format", "National Code", "National code definition", "Data Dictionary Element",
            "Current Collection", "Schema Specification"
    ]

    String[] sheetNamesToImportError = [
            "Breast", "CNS", "Colorectal", "CTYA ", "Gynaecology",
            "Haematology", "Head & Neck", "Lung", "Sarcoma", "Skin",
            "Upper GI", "Urology", "Reference - Other Sources"
    ]


    Workbook wbErrorCoreSheetEmpty
    Workbook wbErrorDuplicatedRow
    Workbook wbErrorSheetMissing
    Workbook wb
    def rows = [["CR0010", "CORE - PATIENT IDENTITY DETAILS","NHS NUMBER*", "*For linkage purposes NHS NUMBER",  "n10", "", "", "NHS NUMBER"],
                    ["CR0020", "CORE - PATIENT IDENTITY DETAILS", "LOCAL PATIENT IDENTIFIER*", "*For linkage purposes NHS NUMBER", "an10", "","","LOCAL PATIENT IDENTIFIER"],
                    ["CR1350", "CORE - PATIENT IDENTITY DETAILS", "NHS NUMBER STATUS INDICATOR CODE", "The NHS NUMBER STATUS  INDICATOR CODE", "an2", "1", "Number present and verified", "NHS NUMBER STATUS INDICATOR CODE"],
                            ["", "", "", "", "", "2", "Number present but not traced", ""],
                    ["", "", "", "", "", "3", "Trace required", ""],
                    ["", "", "", "", "", "4", "Trace attempted - No match or multiple match found", ""],
                    ["", "", "", "", "", "5", "Trace needs to be resolved - (NHS Number or patient detail conflict)", ""],
                    ["", "", "", "", "", "6", "Trace in progress", ""],
                    ["", "", "", "", "", "7", "Number not present and trace not required", ""],
                    ["", "", "", "", "", "8", "Trace postponed (baby under six weeks old)", ""]]


    def rowsDuplicated = [ ["CR0020", "CORE - PATIENT IDENTITY DETAILS", "LOCAL PATIENT IDENTIFIER*", "*For linkage purposes NHS NUMBER", "an10", "","","LOCAL PATIENT IDENTIFIER"],
                ["CR0020", "CORE - PATIENT IDENTITY DETAILS", "LOCAL PATIENT IDENTIFIER*", "*For linkage purposes NHS NUMBER", "an10", "","","LOCAL PATIENT IDENTIFIER"]]

    def setup(){
        //Create a workbook
        wb = new HSSFWorkbook()
        def contSheetRow=0
        sheetNamesToImport.eachWithIndex{ String sheetName, int contSheet ->
            Sheet sheet = wb.createSheet(sheetName)
            Row rowHeader = sheet.createRow(contSheetRow)
            contSheetRow++
            headerNamesToImport.eachWithIndex{ String headerName, int headerCont ->
                Cell cell = rowHeader.createCell(headerCont)
                cell.setCellValue(headerName)
            }
            rows.eachWithIndex{ List<String> rowIt, int contRow ->
                Row row = sheet.createRow(contSheetRow)
                contSheetRow++
                rowIt.eachWithIndex{ String rowCell, int contCell ->
                    Cell cell = row.createCell(contCell)
                    cell.setCellValue(rowCell)
                }
            }
        }

        //Create a Errorworkbook
        wbErrorSheetMissing = new HSSFWorkbook();
        sheetNamesToImportError.eachWithIndex{ String sheetName, int contSheet ->
            Sheet sheet = wbErrorSheetMissing.createSheet(sheetName)
            Row row = sheet.createRow(0)
            headerNamesToImport.eachWithIndex{ String headerName, int headerCont ->
                Cell cell = row.createCell(headerCont)
                cell.setCellValue(headerName)
            }
        }

        //Create workbook with duplicated elements
        wbErrorDuplicatedRow = new HSSFWorkbook()
        contSheetRow=0
        sheetNamesToImport.eachWithIndex{ String sheetName, int contSheet ->
            Sheet sheet = wbErrorDuplicatedRow.createSheet(sheetName)
            Row rowHeader = sheet.createRow(contSheetRow)
            contSheetRow++
            headerNamesToImport.eachWithIndex{ String headerName, int headerCont ->
                Cell cell = rowHeader.createCell(headerCont)
                cell.setCellValue(headerName)
            }
            rowsDuplicated.eachWithIndex{ List<String> rowIt, int contRow ->
                Row row = sheet.createRow(contSheetRow)
                contSheetRow++
                rowIt.eachWithIndex{ String rowCell, int contCell ->
                    Cell cell = row.createCell(contCell)
                    cell.setCellValue(rowCell)
                }
            }
        }

        //Create a Errorworkbook Core sheet empty
        wbErrorCoreSheetEmpty = new HSSFWorkbook();
        sheetNamesToImport.eachWithIndex{ String sheetName, int contSheet ->
            Sheet sheet = wbErrorCoreSheetEmpty.createSheet(sheetName)
            Row row = sheet.createRow(0)
            headerNamesToImport.eachWithIndex{ String headerName, int headerCont ->
                Cell cell = row.createCell(headerCont)
                cell.setCellValue(headerName)
            }
        }
    }

    void "Test that an Excel file is loaded properly"() {

        when:"An Excel file is loaded"
        def cosdImporter = new COSDExcelLoader(fileName)
        ExcelSheet[] excelSheets  = cosdImporter.parse()
        then: "File content is loaded"
        def headersOK= 0
        def rowsOK=0
        excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
            if (sheet.headers.size()==0) headersOK++
            if (sheet.rows.size()==0) rowsOK++
        }
        assert excelSheets.size() !=0
        assert !headersOK
        assert !rowsOK
    }

    void "Test if parse() can load byte content"()
    {
        when:"Passing an inputstream to COSDExcelLoader"
        InputStream inputStream = new FileInputStream(fileName)
        def cosdImporter = new COSDExcelLoader(inputStream)
        ExcelSheet[] excelSheets  = cosdImporter.parse()
        then: "File content is loaded"
        assert excelSheets.size() !=0
        excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
            assert sheet.headers.size()!=0
            assert sheet.rows.size()!=0
        }
    }

    void "Test that checkHeaders successfully detects 'Data item No' missing"()
    {

        def headerNames = [
                "Data Item Section", "Data Item Name",
                "Format", "National Code", "National code definition", "Data Dictionary Element",
                "Current Collection", "Schema Specification"]

        when: "checkHeaders is called with a Data item No. header missing"
        def cosdImporter = new COSDExcelLoader(fileName)
        def msg = cosdImporter.checkHeaders(headerNames)


        then: "the message returned should not be empty and contain the message of this header missing"
        assert  msg == "\r\n Data item No."
    }

    void "Test that checkHeaders successfully detects 'Data ITEM No' with capital letters"()
    {
        def headerNames = [
                "Data ITEM No. ", "Data Item Section", "Data Item Name",
                "Format", "National Code", "National code definition", "Data Dictionary Element",
                "Current Collection", "Schema Specification"]

        when: "checkHeaders is called with a Data ITEM No."
        def cosdImporter = new COSDExcelLoader(fileName)
        def msg = cosdImporter.checkHeaders(headerNames)

        then: "The headers array is correct and the message return is empty"
        assert  msg == ""

    }

    void "Test that checkHeaders successfully detects 'Data item No.' with spaces in between"()
    {
        def headerNames = [
                "   Data    item  No.   ", "Data Item Section", "Data Item Name",
                "Format", "National Code", "National code definition", "Data Dictionary Element",
                "Current Collection", "Schema Specification"]

        when: "checkHeaders is called with a Data ITEM No."
        def cosdImporter = new COSDExcelLoader(fileName)
        def msg = cosdImporter.checkHeaders(headerNames)

        then: "The headers array is correct and the message return is empty"
        assert  msg == ""

    }

    void "Test that checkSheetNames successfully detects all the sheets to be imported"()
    {
        when:"the checkSheetNames is called with a workbook with the correct headers"
        def cosdImporter = new COSDExcelLoader(fileName)
        def msg = cosdImporter.checkSheetNames(wb)

        then:"The message returned must be empty"
        assert  msg == ""
    }

    void "Test that checkSheetNames successfully detects 'Core' sheet missing in Workbook"()
    {
        when:"the checkSheetNames is called with a workbook with the 'Core' header missing"
        def cosdImporter = new COSDExcelLoader(fileName)
        def msg = cosdImporter.checkSheetNames(wbErrorSheetMissing)

        then:"The message returned should include the associated info"
        assert  msg == "\r\n Core"
    }

    void "Test that getValue returns 1 instead of 1.0 for integer numeric cells"()
    {

        when:"the getValue function is called to retrieve an integer"
        def cosdImporter = new COSDExcelLoader(fileName)

        def data = []
        Sheet sheet = wb.getSheetAt(0)
        Row row = sheet.getRow(3)

        for (Cell cell : row) {
            cosdImporter.getValue(row, cell, data)
        }
        then: "The value should be equal to 1 and not to 1.0"
        assert data[5]=="1"
    }

    void "Test that the parser detects that 'Core' sheet is empty"()
    {
        when:"file is loaded and parsed"
        def exception

        try {

            def cosdImporter = new COSDExcelLoader(fileName)
            cosdImporter.wb = wbErrorCoreSheetEmpty;
            cosdImporter.parse()

        }
        catch (Exception ex)
        {
            exception = ex;
        }

        then:"It should send an exception to indicate the headers not present in a given sheet"
        exception.message == "'Core' sheet is empty"
    }

    void "Test that generateCOSDInfoArray detects duplicated data elements" ()
    {
        when:"file is loaded and parsed"
        def cosdImporter = new COSDExcelLoader(fileName)
        def (COSDHeaders, cosdRows, logMessage) = cosdImporter.generateCOSDInfoArray( "Core", headerNamesToImport,rowsDuplicated)

        then:"It should send an exception to indicate the there is a duplicate data element in the file"
        logMessage == "Data Item Number:'CR0020' in Sheet:'Core' is duplicated \r\n"

    }

}

