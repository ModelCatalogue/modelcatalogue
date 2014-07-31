package uk.co.mdc.pages.metadataCuration.ShowPage

import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 15/05/2014.
 */
class AssetShowPage extends BasePageWithNavReadOnly{

	static url = "metadataCurator/#/catalogue/asset/"

	static String actionButtons = "button span"

	static at = {
		url == "metadataCurator/#/catalogue/asset/" &&
		title == "Metadata Registry"
	}

	static content = {
		mainLabel   {waitFor { $("h3.ce-name") }}
		description {waitFor { $("blockquote.ce-description")}}

		propertiesTab {waitFor {$("div.tabbable ul li[heading='Properties']")}}
		metadataTab {waitFor { $("div.tabbable ul li[heading='Metadata']")}}
	}

}