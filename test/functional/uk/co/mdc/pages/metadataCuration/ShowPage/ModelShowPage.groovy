package uk.co.mdc.pages.metadataCuration.ShowPage

import uk.co.mdc.pages.BasePageWithNav
import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 15/05/2014.
 */
class ModelShowPage extends BasePageWithNavReadOnly{

	static url = "metadataCurator/#/catalogue/model/"


	static at = {
		url == "metadataCurator/#/catalogue/model/" &&
		title == "Metadata Registry"
	}

	static content = {
		mainLabel(wait:true) { $("h3.ce-name") }

		propertiesTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Properties'))}}
		childOfTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Parent'))}}

		conceptualDomainTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Conceptual Domains'))}}
		dataElementsTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Data Elements'))}}

		metadataTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Metadata'))}}
		parentOfTab {waitFor { $("div.tabbable ul li.ng-isolate-scope a",text:contains('Children'))}}


		draftIcon     {waitFor {$("h3 span.label-warning")}}
		finalizedIcon {waitFor {$("h3 span.label-primary")}}
	}
}