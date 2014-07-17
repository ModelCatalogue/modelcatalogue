package uk.co.mdc.pages.metadataCuration.ListPage
/**
 * Created by soheil on 15/05/2014.
 */
class ConceptualDomainListPage extends ListPage  {

	static url = "metadataCurator#/catalogue/conceptualDomain/all"


	static at = {
		url == "metadataCurator#/catalogue/conceptualDomain/all" &&
		 title == "Metadata Registry"
	}


	static content = {


		exportButtonContent{  $("span button#exportBtn") }


		newButton(required:false) { $("button#create-catalogue-elementBtn")}

		newCDModelDialogue(required:false)      {$("div.modal-dialog",1)}
		newCDModelDialogueTitle(required:false) {newCDModelDialogue.find("h4",text:"Create Conceptual Domain")}
		newCDModelDialogueName(required:false)  	   {newCDModelDialogue.find("input#name")}
		newCDModelDialogueDescription(required:false)  {newCDModelDialogue.find("input#description")}
		newCDModelDialogueSaveBtn(required:false)      {newCDModelDialogue.find("button.btn-success")}
	}


	@Override
	def getRow(rowIndex){
		def row = ["object":null,"name":null,"desc":null];
		def table =  $("table[list='list']")
		waitFor {
			table.displayed
		}

		def object = $("table[list='list']").find("tbody tr",rowIndex)

		if(object){
			row = ["object":object,
					"name" :$("table[list='list']").find("tbody tr",rowIndex).find("td",0).find("a"),
					"desc":$("table[list='list']").find("tbody tr",rowIndex).find("td",1).find("span")]
		}
		return row
	}

}