package uk.co.mdc.Importers

import org.apache.poi.ss.util.*
import org.apache.poi.ss.usermodel.*


class COSDExcelLoader extends ExcelLoader {

    public static final String[] sheetNamesToImport = [
            "Core",
            "Breast", "CNS", "Colorectal", "CTYA ", "Gynaecology",
            "Haematology", "Head & Neck", "Lung", "Sarcoma", "Skin",
            "Upper GI", "Urology", "Reference - Other Sources"
    ];

    //"Data Item Description", "Description", are not included. Special case since there are different string are used for this header.
    // Data Item Description is a special case:
    // a) No 'Data Item Description' column in the Reference - Other Sources sheet .
    // b) The columns name 'Description is also used.
    // therefore this would be considered as optional.

    public static final String[] headerNamesToImport = [
            "Data item No.", "Data Item Section", "Data Item Name",
            "Format", "National Code", "National code definition", "Data Dictionary Element",
            "Current Collection", "Schema Specification"
    ]

    private static fileInputStream

    public COSDExcelLoader(String path)
    {
        super(path)
    }

    public COSDExcelLoader(InputStream inputStream)
    {
        super(inputStream)
        fileInputStream=inputStream
    }

    def checkSheetNames(Workbook wb){
        def indexSheet
        def message=""
        sheetNamesToImport.eachWithIndex{ String sheetName, int i ->
            indexSheet = wb.getSheetIndex(sheetName)
            //checks the sheet is defined in the excel file.
            if (indexSheet == -1)
                message += ("\r\n" + sheetName)
        }
        return message
    }

    def checkHeaders(def headers){
        // Data Item Description is a special case:
        // a) No 'Data Item Description' column in the Reference - Other Sources sheet .
        // b) The columns name 'Description is also used.
        // therefore this would be considered as optional.

        def message=""
        def headerIndex
        for (int i=0; i< headerNamesToImport.size(); i++)
        {
            headerIndex = headers.findIndexOf {it.toLowerCase().trim() == headerNamesToImport[i].toLowerCase().trim()}
            if (headerIndex == -1 )
                    message += ("\r\n " + headerNamesToImport[i])
        }
        return message
    }

    def parse() {
        Workbook wb = WorkbookFactory.create(fileInputStream);
        ExcelSheet[] excelSheets = new ExcelSheet[sheetNamesToImport.size()];
        def indexSheet
        def sheetName
        def message= checkSheetNames(wb)
        if (message!="")
            throw new Exception ("COSD File does not have the following sheets: " + message)

        for (def cont=0; cont<sheetNamesToImport.size();cont++) {
            sheetName = sheetNamesToImport[cont];
            indexSheet = wb.getSheetIndex(sheetName)
            //checks the sheet is defined in the excel file.
            if (indexSheet == -1)
                throw new Exception("Sheet: '" + sheetName + "' does not exist in the excel file")
            Sheet sheet = wb.getSheetAt(indexSheet);

            Iterator<Row> rowIt = sheet.rowIterator()
            Row row = rowIt.next()
            def headers = [];
            def rows = [];

            //Finding header
            while (row.getCell(0)!= null && row.getCell(0).getRichStringCellValue().getString().trim() != "Data item No." && rowIt.hasNext()) {
                row = rowIt.next();
            }

            if (row.getCell(0)!=null &&  row.getCell(0).getRichStringCellValue().getString().trim() == "Data item No.") {
                headers = getRowData(row);

                message = checkHeaders(headers)
                if (message != "")
                    throw new Exception("Sheet: '" + sheetName + "' does not have the following headers:" + message)

                def dataItemNumberIndex = headers.indexOf("Data item No.")
                def dataItemNationalCodeIndex = headers.indexOf("National Code")
                def dataItemNationalCodeDefinitionIndex = headers.indexOf("National code definition")

                //Finding the first Data item row
                while (rowIt.hasNext()) {
                    row = rowIt.next()
                    // the Data item No has the format aa0000[0]
                    def regularExpression = ~/[a-zA-Z]{2}[0-9]{4,5}/

                    def rowData = getRowData(row)
                    def text = rowData[dataItemNumberIndex];
                    if ((text ==~ regularExpression) || (rowData[dataItemNumberIndex] == "" && (rowData[dataItemNationalCodeIndex].toString() != "" || rowData[dataItemNationalCodeDefinitionIndex].toString() != "")))
                        rows << getRowData(row);
                }
            }

            //add the excelSheet to the excelSheets collection
            if (headers.size()!=0 & rows.size()!=0)
                excelSheets[cont] =  new ExcelSheet(sheetName: sheetName, headers: headers, rows: rows);
            else
                throw new Exception("'" + sheetName + "' sheet is empty")
        }
        return excelSheets
    }

    def generateCOSDInfoArray(sheetName, headers, rows){

        // List content => Has the National Code and National Code Definition formatted as a list in the following format:
        // [national code]=[national code definition] separated by new line: \r\n
        // Question? => is Data Item Number the Unique Code field?
        def COSDHeaders = ["Unique Code",
                           "Data Item Name","Data Item Description",
                           "Parent Section", "Template",
                           "List content", "Metadata","Data Dictionary Element",
                           "Current Collection", "Schema Specification"]



        def cosdRows =[];
        def nextDataItemNumber;
        def activeSectionDataElementConceptIndex=-1;
        def dataItemNumber
        def dataItemSection
        def dataItemName
        def dataItemDescription
        def dataItemFormat
        def dataItemNationalCode
        def dataItemNationalCodeDefinition


        ArrayList itemSectionArray = new ArrayList()



        def dataItemNumberIndex = headers.indexOf("Data item No.")
        def dataItemNameIndex = headers.indexOf("Data Item Name")
        def dataItemDescriptionIndex = headers.indexOf("Data Item Description")
        def dataItemSectionIndex = headers.indexOf("Data Item Section")
        def dataItemFormatIndex = headers.indexOf("Format")
        def dataItemNationalCodeIndex = headers.indexOf("National Code")
        def dataItemNationalCodeDefinitionIndex = headers.indexOf("National code definition")
        def dataItemDataDictionaryElementIndex = headers.indexOf("Data Dictionary Element")
        def dataItemCurrentCollectionIndex = headers.indexOf("Current Collection")
        def dataItemSchemaSpecificationIndex = headers.indexOf("Schema Specification")


        def cosdUniqueCodeIndex = COSDHeaders.indexOf("Unique Code")
        def cosdDataItemNameIndex = COSDHeaders.indexOf("Data Item Name")
        def cosdDataItemDescriptionIndex = COSDHeaders.indexOf("Data Item Description")
        def cosdParentSection = COSDHeaders.indexOf("Parent Section")
        def cosdTemplateIndex = COSDHeaders.indexOf("Template")
        def cosdListContentIndex = COSDHeaders.indexOf("List content")
        def cosdDataDictionaryElementIndex = COSDHeaders.indexOf("Data Dictionary Element")
        def cosdCurrentCollectionIndex= COSDHeaders.indexOf("Current Collection")
        def cosdSchemaSpecificationIndex = COSDHeaders.indexOf("Schema Specification")

        def dataElements = []

        String logMessage = ""
        //Check the Data Item Name column exists
        if (dataItemNameIndex == -1)
            throw new Exception("Cannot find 'Data Item Name' column")


        for (int cont = 0; cont < rows.size(); cont++) {

            def cosdRow = []
            dataItemNumber = rows[cont][dataItemNumberIndex];
            //Check dataItemNumber follows the format aa0000[0]
            if (dataItemNumber ==~ /[a-zA-z]{2}[0-9]{4,5}/) {

                dataItemSection = rows[cont][dataItemSectionIndex];
                dataItemName = rows[cont][dataItemNameIndex];
                if (dataItemDescriptionIndex == -1) {
                    dataItemDescriptionIndex = headers.indexOf("Description")
                    if (dataItemDescriptionIndex == -1)
                        dataItemDescription = ""
                    else
                        dataItemDescription = rows[cont][dataItemDescriptionIndex]
                } else {
                    dataItemDescription = rows[cont][dataItemDescriptionIndex];
                }

                dataItemFormat = rows[cont][dataItemFormatIndex];
                dataItemNationalCode = rows[cont][dataItemNationalCodeIndex].toString();
                dataItemNationalCodeDefinition = rows[cont][dataItemNationalCodeDefinitionIndex].toString();
                if (dataElements.count { it == dataItemNumber } == 0) {
                    dataElements.add(dataItemNumber)
                    cosdRow[cosdUniqueCodeIndex] = rows[cont][dataItemNumberIndex]
                    cosdRow[cosdDataItemNameIndex] = dataItemName
                    cosdRow[cosdDataItemDescriptionIndex] = dataItemDescription
                    cosdRow[cosdParentSection] = dataItemSection
                    cosdRow[cosdTemplateIndex] = dataItemFormat
                    cosdRow[cosdListContentIndex] = ""
                    cosdRow[cosdDataDictionaryElementIndex] = rows[cont][dataItemDataDictionaryElementIndex]
                    cosdRow[cosdCurrentCollectionIndex] = rows[cont][dataItemCurrentCollectionIndex]
                    cosdRow[cosdSchemaSpecificationIndex] = rows[cont][dataItemSchemaSpecificationIndex]


                    //Create the sectionDataElement if this doesn't exist
                    def dataSectionNameNoSpaces = dataItemSection.toString().replaceAll(" ", "");
                    if (activeSectionDataElementConceptIndex == -1 || itemSectionArray.count {
                        it.toString().replaceAll(" ", "") == dataSectionNameNoSpaces
                    } == 0) {
                        def index = activeSectionDataElementConceptIndex == -1 ? 0 : itemSectionArray.size();
                        itemSectionArray[index] = dataItemSection
                        activeSectionDataElementConceptIndex = index;

                    } else //Check is the sectionDataElement-DataElementConcept already exists
                    if (itemSectionArray[activeSectionDataElementConceptIndex].toString().replaceAll(" ", "") != dataItemSection.toString().replaceAll(" ", "")) {
                        def indexAtSectionDataElementConcepts = itemSectionArray.indexOf {
                            it.replaceAll(" ", "") == dataSectionNameNoSpaces
                        }

                        if (indexAtSectionDataElementConcepts != -1) {
                            //sectionDataElementConcept = sectionDataElementConcepts[indexAtSectionDataElementConcepts]
                            activeSectionDataElementConceptIndex = indexAtSectionDataElementConcepts;
                        }
                    }
                    //Look for any additional value domain
                    //Check if NationalCode is not empty, since National Code Definition may have some
                    // text that shouldn't be considered as part of the value domain.

                    if (dataItemNationalCode.toString().trim() != "" || dataItemNationalCodeDefinition.toString().trim() != "") {
                        def key = rows[cont][dataItemNationalCodeIndex].toString();
                        def value = rows[cont][dataItemNationalCodeDefinitionIndex].toString().size() <= 255 ? rows[cont][dataItemNationalCodeDefinitionIndex].toString() : rows[cont][dataItemNationalCodeDefinitionIndex].toString().substring(0, 254);
                        cosdRow[cosdListContentIndex] = (key + "=" + value + "\r\n")

                        if (cont + 1 < rows.size()) {
                            nextDataItemNumber = rows[cont + 1][dataItemNumberIndex];
                            while (nextDataItemNumber == "" && (dataItemNationalCode.toString().trim() != "" || dataItemNationalCodeDefinition.toString().trim() != "")) {
                                //create a list of value domain
                                cont++
                                key = rows[cont][dataItemNationalCodeIndex].toString();
                                value = rows[cont][dataItemNationalCodeDefinitionIndex].toString().size() <= 255 ? rows[cont][dataItemNationalCodeDefinitionIndex].toString() : rows[cont][dataItemNationalCodeDefinitionIndex].toString().substring(0, 254);
                                cosdRow[cosdListContentIndex] += (key + "=" + value + "\r\n")
                                if (cont + 1 < rows.size())
                                    nextDataItemNumber = rows[cont + 1][dataItemNumberIndex];
                                else
                                    break;
                            }
                        }
                    }

                cosdRows.add(cosdRow)
                } else {
                    logMessage += ("Data Item Number:'" + dataItemNumber + "' in Sheet:'" + sheetName + "' is duplicated \r\n")
                }
            }
        }

        return [COSDHeaders, cosdRows, logMessage]
    }


    def getValue(Row row, Cell cell, List data) {
		def rowIndex = row.getRowNum()
		def colIndex = cell.getColumnIndex()
		def value = ""
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getRichStringCellValue().getString().trim();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue();
				} else {
                    // Read integers as displayed in Excel using the DataFormatter.
                    DataFormatter formatter = new DataFormatter();
                    value = formatter.formatCellValue(cell);
					//value = cell.getNumericCellValue();
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				value = cell.getCellFormula();
				break;
			default:
				value = ""
		}
		data[colIndex] = value
		data
	}

}
