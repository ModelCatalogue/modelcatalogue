package uk.co.mdc

import grails.test.spock.IntegrationSpec
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import spock.lang.Specification
import uk.co.mdc.Importers.COSDExcelLoader
import uk.co.mdc.Importers.ExcelSheet

/**
* Created by sus_avi on 28/04/2014.
*/


class COSDExcelLoaderSpec extends IntegrationSpec {

    // List the things I want to test.
    // File exists

    def fileName= "test/unit/resources/COSD/COSD.xls"
    // COSD_Error has:
    //                  * no CORE sheet.
    //                  * Breast sheet has different headers title names. i.e. Data Item Name => Data Item Names.

    def fileNameError="test/unit/resources/COSD/COSD_Error.xls"
    def fileNameErrorEmptySheet="test/unit/resources/COSD/COSDErrorEmptySheet.xls"

    String[] sheetNamesToImport = [
            "Core",
            "Breast", "CNS", "Colorectal", "CTYA ", "Gynaecology",
            "Haematology", "Head & Neck", "Lung", "Sarcoma", "Skin",
            "Upper GI", "Urology", "Reference - Other Sources"
    ];

    String[] headerNamesToImport = [
            "Data item No.", "Data Item Section", "Data Item Name",
            "Format", "National Code", "National code definition", "Data Dictionary Element",
            "Current Collection", "Schema Specification"
    ]

    String[] sheetNamesToImportError = [
            "Breast", "CNS", "Colorectal", "CTYA ", "Gynaecology",
            "Haematology", "Head & Neck", "Lung", "Sarcoma", "Skin",
            "Upper GI", "Urology", "Reference - Other Sources"
    ];

//    void "Test that parser loads successfully all the COSD sheets in the file" ()
//    {
//        when: "COSD file is loaded and parsed"
//        def cosdImporter = new COSDExcelLoader(fileName)
//        ExcelSheet[] excelSheets = cosdImporter.parse()
//
//        then: ""
//        excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
//            assert sheet.sheetName == sheetNamesToImport[contSheet]
//        }
//    }
//
//    void "Test that parser loads successfully all the headers in the COSD sheets in the file" ()
//    {
//        when: " COSD file is loaded and parsed"
//        def cosdImporter = new COSDExcelLoader(fileName)
//        ExcelSheet[] excelSheets = cosdImporter.parse()
//
//        excelSheets.eachWithIndex{ ExcelSheet sheet, int contSheet ->
//            headerNamesToImport.eachWithIndex{ String header, int contHeader ->
//                def index = sheet.headers.indexOf(header)
//                assert  index!= -1
//            }
//        }
//    }
//
//    // Duplicate data elements
//    // Check if the dataElements are added to the collection
//    // Check that the value domains are correct
//    void "Test that duplicated data elements are not allowed" ()
//    {
//        when:"file is loaded and parsed"
//        def exception
//
//        try {
//            def cosdImporter = new COSDExcelLoader(fileNameError)
//            cosdImporter.parse()
//
//        }
//        catch (Exception ex)
//        {
//            exception = ex;
//        }
//
//        then:"It should send an exception to indicate the there is a duplicate data element in the file"
//        exception.message == "Data Item Number:'CR0010' in Sheet:'Core' is duplicated"
//
//    }
//
//

}

