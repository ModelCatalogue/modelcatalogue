package uk.co.mdc.pages.metadataCuration.ShowPage

import uk.co.mdc.pages.BasePageWithNav
import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 15/05/2014.
 */
class ConceptualDomainShowPage extends BasePageWithNavReadOnly{

	static url = "metadataCurator/#/catalogue/conceptualDomain/"

	static String propertiesTab = "div.tabbable ul li[heading='Properties']"
	static String modelsTab     = "div.tabbable ul li[heading='Models']"
	static String dataTypesTab  = "div.tabbable ul li[heading='Data Types']"


	static at = {
		url == "metadataCurator/#/catalogue/conceptualDomain/" &&
		title == "Metadata Curation"
	}

	static content = {
		mainLabel { waitFor { $("h3.ce-name") }}
		description {waitFor { $("blockquote.ce-description")}}

//		propertiesTab {waitFor {$("div.tabbable ul li[heading='Properties']")}}
//		modelsTab {waitFor { $("div.tabbable ul li[heading='Models']")}}
//		dataTypesTab{waitFor { $("div.tabbable ul li[heading='DataTypes']")}}

		propertiesTable(required:false) {waitFor {$("table#Properties")}}
		modelsTable {waitFor {$("table#-isContextFor")}}
		dataTypesTable {waitFor {$("table#-includes")}}
	}
}