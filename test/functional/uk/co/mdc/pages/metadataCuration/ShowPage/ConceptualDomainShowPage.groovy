package uk.co.mdc.pages.metadataCuration.ShowPage

import uk.co.mdc.pages.BasePageWithNav
import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 15/05/2014.
 */
class ConceptualDomainShowPage extends BasePageWithNavReadOnly{

	static url = "metadataCurator/#/catalogue/conceptualDomain/"

//	static String propertiesTab = "div.tabbable ul li[heading='Properties']"
//	static String modelsTab     = "div.tabbable ul li[heading='Models']"
//	static String valueDomainsTab  = "div.tabbable ul li[heading='Value Domains']"


	static at = {
		url == "metadataCurator/#/catalogue/conceptualDomain/" &&
		title == "Metadata Registry"
	}

	static content = {
		mainLabel { waitFor { $("h3.ce-name") }}
		description {waitFor { $("blockquote.ce-description")}}

		propertiesTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Properties'))}}
		modelsTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Models'))}}
		valueDomainsTab{waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Value Domains'))}}


//		dataTypesTab{waitFor { $("div.tabbable ul li[headiDataTypeListPageSpec)ng='DataTypes']")}}

		propertiesTable(required:false) {waitFor {$("table#Properties")}}
		modelsTable {waitFor {$("div#-isContextFor")}}
		valueDomainsTable {waitFor {$("div#-valueDomains")}}
	}
}