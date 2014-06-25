package uk.co.mdc.pages.metadataCuration.ListPage

import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 17/05/2014.
 */
class ModelListPage extends BasePageWithNavReadOnly {

	static url = "metadataCurator#/catalogue/model/all"

	static at = {
				url == "metadataCurator#/catalogue/model/all" &&
				title == "Metadata Curation"
	}


	static String modelTree = "div.catalogue-element-treeview-list-container[list='list']"


	static content = {
 		mainLabel { $("h3.ng-binding") }
		descriptionLabel {$("blockquote.ce-description") }

		NHIC_Model_Item { $(modelTree).find("span.catalogue-element-treeview-labels", 0) }
		NHIC_Model_Item_Icon { NHIC_Model_Item.find("span.glyphicon-folder-close") }
		NHIC_Model_Item_Show { NHIC_Model_Item.find("a.btn[title='Show']") }
		NHIC_Model_Item_Name { NHIC_Model_Item.find("span.catalogue-element-treeview-name") }

		ParentModel1_Item { $(modelTree).find("span.catalogue-element-treeview-labels", 1) }
		ParentModel1_Item_Icon { ParentModel1_Item.find("span.glyphicon-folder-close") }
		ParentModel1_Item_Show { ParentModel1_Item.find("a.btn[title='Show']") }
		ParentModel1_Item_Name { ParentModel1_Item.find("span.catalogue-element-treeview-name") }

		Model1_Item { $(modelTree).find("span.catalogue-element-treeview-labels", 2) }
		Model1_Item_Icon { Model1_Item.find("span.glyphicon-folder-close") }
		Model1_Item_Show { Model1_Item.find("a.btn[title='Show']") }
		Model1_Item_Name { Model1_Item.find("span.catalogue-element-treeview-name") }

		dataElementsTable { $("table[list='contained.elements']") }
	}


	def getDataElementRow(rowIndex) {
		def object = dataElementsTable.find("tbody tr", rowIndex)
		if (!object) {
			return ["object": null, "name": null, "desc": null];
		}

		def row = ["object": object,
				"name": object.find("td", 0).find("a"),
				"desc": object.find("td", 1).find("span")]
		return row
	}


	def goToModelShowPage() {
		waitFor {
			NHIC_Model_Item_Icon.displayed
		}
		interact {
			click(NHIC_Model_Item_Icon)
		}

		waitFor {
			ParentModel1_Item.displayed
		}
		interact {
			click(ParentModel1_Item_Icon)
		}

		waitFor {
			Model1_Item.displayed
		}
		waitFor {
			Model1_Item_Show.displayed
		}

		interact {
			click(Model1_Item_Show)
		}
	}

}

