package uk.co.mdc.utils.importers

import grails.validation.Validateable
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import uk.co.mdc.model.ConceptualDomain
import uk.co.mdc.model.DataElementConcept

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

    def loadDataElementFromExcel(File file, Map mapping = null) {
        loadDataElementFromExcel(file.inputStream, mapping)
    }

    def loadDataElementFromExcel(InputStream is, Map mapping = null) {
        Workbook workbook = WorkbookFactory.create(is)
        def mapList = mapping ? excelImportService.columns(workbook, mapping) : excelImportService.columns(workbook, DEFAULT_HEP_COLUMN_MAP)

        mapList
    }

    //@Transactional
    def importDataElementFromExcel(def excel, String cdName, Map mapping = null)
    {
        def items = loadDataElementFromExcel(excel, mapping)

        def invalidEntries = []

        items.eachWithIndex { def entry, int i ->
            HEPDataItem hdi = new HEPDataItem(entry)
            if (!hdi.validate()) {
                invalidEntries.add([index: i, entry: hdi])
                return
            }

        }

        invalidEntries
    }

}

/**
 * This class is used to validate the data items loaded from the HEP dataset. Object of this class is not persisted and
 * is registered in Config.groovy (grails.validateable.classes) to enable validation support.
 */
@Validateable
class HEPDataItem {
    String itemNumber
    String section
    String subSection
    String pathway
    String name
    String description
    String dataType

    static constraints = {
        itemNumber nullable: true
        section nullable: true
        subSection nullable: true
        pathway nullable: true
        name nullable: false
        description nullable: true
        dataType nullable: false
    }
}