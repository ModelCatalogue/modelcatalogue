package uk.co.mdc.Importers

import org.apache.poi.ss.util.*
import org.apache.poi.ss.usermodel.*

public class COSDExcelSheet {
        String name;
        def headers = [];
        def rows = [];
}

class COSDExcelLoader {

    private static InputStream

    public COSDExcelLoader(String path)
    {
        InputStream = new FileInputStream(path)
    }

    public COSDExcelLoader(InputStream inputStream)
    {
        InputStream  = inputStream
    }

    def parseCOSD() {

        def String[] sheetNamesToImport = [
                "Core",
                "Breast", "CNS", "Colorectal", "CTYA ", "Gynaecology",
                "Haematology", "Head & Neck", "Lung", "Sarcoma", "Skin",
                "Upper GI", "Urology", "Reference - Other Sources"
        ];

        Workbook wb = WorkbookFactory.create(InputStream);
        COSDExcelSheet[] excelSheets = new COSDExcelSheet[sheetNamesToImport.size()];

        for (def cont=0; cont<sheetNamesToImport.size();cont++) {
            def sheetName = sheetNamesToImport[cont];
            Sheet sheet = wb.getSheetAt(wb.getSheetIndex(sheetName));

            Iterator<Row> rowIt = sheet.rowIterator()
            Row row = rowIt.next()
            def headers = [];
            def rows = [];

            //Finding header
            while (row.getCell(0).getRichStringCellValue().getString().trim() != "Data item No." && rowIt.hasNext()) {

                row = rowIt.next();
            }
            if (row.getCell(0).getRichStringCellValue().getString().trim() == "Data item No.") {
                headers = getRowData(row);
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
            excelSheets[cont] =  new COSDExcelSheet(name: sheetName, headers: headers, rows: rows);
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