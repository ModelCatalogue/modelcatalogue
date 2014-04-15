package uk.co.mdc.Importers

import org.apache.poi.ss.util.*
import org.apache.poi.ss.usermodel.*

public class COSDExcelSheet {
        String name;
        def headers = [];
        def rows = [];
}

class COSDExcelLoader {

    def String[] sheetNamesToImport = [
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

    String[] headerNamesToImport = [
            "Data item No.", "Data Item Section", "Data Item Name",
            "Format", "National Code", "National code definition", "Data Dictionary Element",
            "Current Collection", "Schema Specification"
    ]

    private static InputStream

    public COSDExcelLoader(String path)
    {
        InputStream = new FileInputStream(path)
    }

    public COSDExcelLoader(InputStream inputStream)
    {
        InputStream  = inputStream
    }

    def checkSheetNames(Workbook wb)
    {
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

    def checkHeaders(def headers)
    {
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



    def parseCOSD() {
        Workbook wb = WorkbookFactory.create(InputStream);
        COSDExcelSheet[] excelSheets = new COSDExcelSheet[sheetNamesToImport.size()];
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

                //Finding the first Data item row
                while (rowIt.hasNext()) {
                    row = rowIt.next()
                    // the Data item No has the format aa0000[0]
                    def regularExpression = ~/[a-zA-Z]{2}[0-9]{4,5}/

                    def rowData = getRowData(row)
                    def text = rowData[0];
                    if ((text ==~ regularExpression) || (rowData[0] == "" && rowData[5].toString() != "" && rowData[6].toString() != ""))
                        rows << getRowData(row);
                }
            }

            //add the excelSheet to the excelSheets collection
            if (headers.size()!=0 & rows.size()!=0)
                excelSheets[cont] =  new COSDExcelSheet(name: sheetName, headers: headers, rows: rows);
            else
                throw new Exception("'" + sheetName + "' sheet is empty")
        }
        return excelSheets
    }

	def getRowData(Row row) {
		def data = []
		for (Cell cell : row) {
			getValue(row, cell, data)
		}
		data
	}

	def getRowReference(Row row, Cell cell) {
		def rowIndex = row.getRowNum()
		def colIndex = cell.getColumnIndex()
		CellReference ref = new CellReference(rowIndex, colIndex)
		ref.getRichStringCellValue().getString()
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
