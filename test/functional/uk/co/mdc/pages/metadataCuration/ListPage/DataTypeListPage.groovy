package uk.co.mdc.pages.metadataCuration.ListPage

import uk.co.mdc.pages.metadataCuration.ShowPage.DataTypeShowPage

/**
 * Created by soheil on 15/05/2014.
 */
class DataTypeListPage extends ListPage {

	static url = "metadataCurator#/catalogue/dataType/all"

	static at = {
		url == "metadataCurator#/catalogue/dataType/all" &&
		title == "Metadata Registry"
	}



	def goToBooleanDataTypeShowPage(){
		waitFor {
			$(DataTypeListPage.elementsTable).displayed
			getRow(0)["object"].displayed
		}

		def nameElement = getRow(0)["name"]
		waitFor {
			nameElement.displayed
		}
		interact {
			click(nameElement)
		}
	}
}