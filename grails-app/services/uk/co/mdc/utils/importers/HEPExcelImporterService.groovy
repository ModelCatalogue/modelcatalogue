package uk.co.mdc.utils.importers

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class HEPExcelImporterService {

    def excelImportService

    static Map DEFAULT_HEP_COLUMN_MAP = [
            sheet:'NHIC_DEE_07_HEP',
            startRow: 4,
            columnMap:  [
                    'A': 'itemNumber',
                    'C': 'section',
                    'D': 'subSection',
                    'F': 'pathway',
                    'H': 'name',
                    'I': 'description',
                    'J': 'dataType'
            ]
    ]

    def loadDataElementFromExcel(File file) {
        loadDataElementFromExcel(file.inputStream, DEFAULT_HEP_COLUMN_MAP)
    }

    def loadDataElementFromExcel(InputStream is) {
        loadDataElementFromExcel(is, DEFAULT_HEP_COLUMN_MAP)
    }

    def loadDataElementFromExcel(InputStream is, Map mapping) {
        Workbook workbook = WorkbookFactory.create(is)
        def mapList = excelImportService.columns(workbook, mapping)

        mapList
    }

}

