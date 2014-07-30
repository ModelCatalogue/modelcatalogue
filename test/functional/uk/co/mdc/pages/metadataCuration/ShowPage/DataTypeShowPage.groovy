package uk.co.mdc.pages.metadataCuration.ShowPage

import uk.co.mdc.pages.BasePageWithNav
import uk.co.mdc.pages.BasePageWithNavReadOnly

/**
 * Created by soheil on 15/05/2014.
 */
class DataTypeShowPage extends BasePageWithNavReadOnly {

	static url = "metadataCurator/#/catalogue/dataType/"

	static at = {
		url == "metadataCurator/#/catalogue/dataType/" &&
		title == "Metadata Registry"
	}

	static content = {
		mainLabel(wait:true) {  $("h3.ce-name") }
		description(wait:true) { $("blockquote.ce-description")}

		createRelationButton(required:false) { $("button#create-new-relationshipBtn")}

		relationDialogue(required:false)      {$("div.modal-dialog",1)}
		relationDialogueTitle(required:false) {relationDialogue.find("h4",text:"Create Relationship")}

		relationDialogueReltSelector(required:false)  {relationDialogue.find("select#type")}
		relationDialogueItemSelector(required:false)  {relationDialogue.find("input#element")}
		relationDialogueItemSearchResult(required:false)  {relationDialogue.find("ul.dropdown-menu.ng-isolate-scope")}



		relationDialogueSaveBtn(required:false)      {relationDialogue.find("button.btn-primary")}


		propertiesTable(required:false) {waitFor {$("table#Properties")}}
		synonymsTable {waitFor {$("table#-synonyms")}}

		propertiesTab {waitFor {$("div.tabbable ul li[heading='Properties']")}}
		synonymsTab   {waitFor {$("div.tabbable ul li[heading='Synonyms']")}}



	}
}