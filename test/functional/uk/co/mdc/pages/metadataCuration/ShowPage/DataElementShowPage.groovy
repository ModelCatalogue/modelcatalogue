package uk.co.mdc.pages.metadataCuration.ShowPage

import uk.co.mdc.pages.BasePageWithNav
import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 15/05/2014.
 */
class DataElementShowPage extends BasePageWithNavReadOnly {

	static url = "metadataCurator/#/catalogue/dataElement/"

	static at = {
		url == "metadataCurator/#/catalogue/dataElement/" &&
				title == "Metadata Curation"
	}

	static content = {
		mainLabel(wait:true) {  $("h3.ce-name") }
		description(wait:true) { $("blockquote.ce-description")}

		propertiesTab {waitFor {$("div.tabbable ul li[heading='Properties']")}}
		dataTypesTab{waitFor { $("div.tabbable ul li[heading='DataType']")}}
		metadataTab {waitFor { $("div.tabbable ul li[heading='Metadata']")}}
		modelsTab {waitFor { $("div.tabbable ul li[heading='Models']")}}
		relatedToTab {waitFor { $("div.tabbable ul li[heading='Related To']")}}


		propertiesTable {waitFor {$("table#-properties")}}
		dataTypesTable {waitFor {$("table#-instantiatedBy")}}
		metadataTable {waitFor {$("table#-ext")}}
		modelsTable {waitFor {$("table#-containedIn")}}
//		relatedToTable {waitFor {$("table#-relatedTo")}}
	}
}