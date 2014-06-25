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
		title == "Metadata Curation"
	}

	static content = {
		mainLabel(wait:true) { $("h3.ce-name") }

		propertiesTab {waitFor {$("div.tabbable ul li[heading='Properties']")}}
		childOfTab{waitFor { $("div.tabbable ul li[heading='Parent']")}}

		conceptualDomainTab {waitFor { $("div.tabbable ul li[heading='Conceptual Domains']")}}
		dataElementsTab {waitFor { $("div.tabbable ul li[heading='Data Elements']")}}

		metadataTab {waitFor { $("div.tabbable ul li[heading='Metadata']")}}
		parentOfTab {waitFor { $("div.tabbable ul li[heading='Children']")}}
	}

}