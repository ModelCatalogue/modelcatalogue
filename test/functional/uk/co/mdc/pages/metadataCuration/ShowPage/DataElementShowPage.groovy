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
				title == "Metadata Registry"
	}

	static content = {
		mainLabel(wait:true) {  $("h3.ce-name") }
		description(wait:true) { $("blockquote.ce-description")}

		propertiesTab {waitFor {$("div.tabbable ul li.ng-isolate-scope a",text:contains('Properties'))}}
//		valueDomainsTab{waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Value Domains'))}}
		metadataTab {waitFor {$("div.tabbable ul li.ng-isolate-scope a",text:contains('Metadata'))}}
		modelsTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Models'))}}
		relationshipsTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Relationships'))}}


		propertiesTable(required:false) {waitFor {$("table#Properties")}}
//		valueDomainsTable {waitFor {$("table#-instantiatedBy")}}
		metadataTable {waitFor {$("table#Metadata")}}
		modelsTable {waitFor {$("div#-containedIn")}}
		relationshipsTable {waitFor {$("div#-relationships")}}
	}
}