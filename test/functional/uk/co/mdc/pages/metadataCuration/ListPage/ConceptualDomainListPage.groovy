package uk.co.mdc.pages.metadataCuration.ListPage
/**
 * Created by soheil on 15/05/2014.
 */
class ConceptualDomainListPage extends ListPage  {

	static url = "metadataCurator/#/catalogue/conceptualDomain/all"


	static at = {
		url == "metadataCurator/#/catalogue/conceptualDomain/all" &&
		 title == "Metadata Curation"
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