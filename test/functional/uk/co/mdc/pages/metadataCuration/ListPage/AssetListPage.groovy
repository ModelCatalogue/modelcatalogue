package uk.co.mdc.pages.metadataCuration.ListPage
/**
 * Created by soheil on 15/05/2014.
 */
class AssetListPage extends ListPage {

	static url = "metadataCurator#/catalogue/asset/all"

	static String actionListButton = "span.btn-group button"
	static String subActionList    = "ul#switch-statusBtnItems"
	static String assetList  = "table[list='list']"

	static at = {
		url == "metadataCurator#/catalogue/asset/all" &&
		title == "Metadata Registry"
	}

	def getStatusActionButton(){
		$(AssetListPage.actionListButton,0)
	}

	def getDraftStatusButton(){
		$(subActionList).find("li",0)
	}

}