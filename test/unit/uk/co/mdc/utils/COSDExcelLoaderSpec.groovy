package uk.co.mdc.utils

import grails.test.mixin.TestFor
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

    def fileNameError="test/unit/resources/COSD/COSD_Error.xls"
    def fileNameErrorSheetName="test/unit/resources/COSD/COSD_Error_SheetName.xls"
    def fileNameErrorHeaders="test/unit/resources/COSD/COSD_Error_Header.xls"
    def fileNameErrorEmptySheet="test/unit/resources/COSD/COSDErrorEmptySheet.xls"


    void "Test that an Excel file is loaded properly"() {

        when:"An Excel file is loaded"
        def cosdImporter = new COSDExcelLoader(fileName)
        ExcelSheet[] excelSheets  = cosdImporter.parse()
        then: "File content is loaded"
        assert excelSheets.size() !=0
        excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
            assert sheet.headers.size()!=0
            assert sheet.rows.size()!=0
        }
    }

    void "Test if parse() can load byte content"()
    {
        when:"Passing an inputstream to COSDExcelLoader"
        InputStream inputStream = new FileInputStream(fileName)
        def cosdImporter = new COSDExcelLoader()
        ExcelSheet[] excelSheets  = cosdImporter.parse(inputStream)
        then: "File content is loaded"
        assert excelSheets.size() !=0
        excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
            assert sheet.headers.size()!=0
            assert sheet.rows.size()!=0
        }
    }

    void "Test that the file contains the sheets to be imported"()
    {
        when:"An Excel file is loaded"
        def exception

        try{
            def cosdImporter = new COSDExcelLoader(fileNameErrorSheetName)
            ExcelSheet[] excelSheets  = cosdImporter.parse()
        }
        catch(Exception ex)
        {
            exception=ex
        }

        then:"It should throw an exception 'Sheet: 'Core' does not exist in the excel file'"
        // The COSD_Error.xls file has no sheet named 'Core => Cores'.

        exception.message == "COSD File does not have the following sheets: \r\nCore"
    }

    void "Test that the file has the correct header format" ()
    {
        when:"file is loaded and parsed"
        def exception

        try {
            def cosdImporter = new COSDExcelLoader(fileNameErrorHeaders)
            ExcelSheet[] excelSheets  = cosdImporter.parse()

        }
        catch (Exception ex)
        {
            exception = ex;
        }

        then:"It should send an exception to indicate the headers not present in a given sheet"
        exception.message == "Sheet: 'Core' does not have the following headers:\r\n Data Item Name"

    }

    void "Test that the COSD sheet is not empty" ()
    {
        when:"file is loaded and parsed"
        def exception

        try {

            def cosdImporter = new COSDExcelLoader(fileNameErrorEmptySheet)
            cosdImporter.parse()

        }
        catch (Exception ex)
        {
            exception = ex;
        }

        then:"It should send an exception to indicate the headers not present in a given sheet"
        exception.message == "'Core' sheet is empty"
    }
    // Duplicate data elements
    // Check if the dataElements are added to the collection
    // Check that the value domains are correct
    void "Test that duplicated data elements are not allowed" ()
    {
        when:"file is loaded and parsed"
        def exception

        try {
            def cosdImporter = new COSDExcelLoader(fileNameError)
            cosdImporter.parse()

        }
        catch (Exception ex)
        {
            exception = ex;
        }

        then:"It should send an exception to indicate the there is a duplicate data element in the file"
        exception.message == "Data Item Number:'CR0010' in Sheet:'Core' is duplicated"

    }

}

