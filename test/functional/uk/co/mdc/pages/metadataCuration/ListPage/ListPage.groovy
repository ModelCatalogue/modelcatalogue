package uk.co.mdc.pages.metadataCuration.ListPage

 import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 17/05/2014.
 */
class ListPage extends  BasePageWithNavReadOnly{

	static String elementsTable = "table[list='list']"
	static String exportButton  = "span button#exportBtn"
	static String exportButtonItems = "span ul#exportBtnItems"

	static content = {
		mainLabel{  $("h2.ng-binding") }
	}

	def getRow(rowIndex){
		def row = ["object":null,"name":null,"desc":null];
		def table =  $(ListPage.elementsTable)
		waitFor {
			table.displayed
		}

		def object = $(ListPage.elementsTable).find("tbody tr",rowIndex)

		if(object){
			row = ["object":object,
					"name" :$(ListPage.elementsTable).find("tbody tr",rowIndex).find("td",0).find("a"),
					"desc":$(ListPage.elementsTable).find("tbody tr",rowIndex).find("td",1).find("span")]
		}
		return row
	}
}
