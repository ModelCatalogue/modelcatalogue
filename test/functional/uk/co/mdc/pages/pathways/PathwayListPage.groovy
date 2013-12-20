package uk.co.mdc.pages.pathways;

import uk.co.mdc.pages.BasePageWithNav;

class PathwayListPage extends BasePageWithNav{
	
	static url = "pathwaysModel/list"
	
	static at = {
		url == "pathwaysModel/list" &&
		title == "List Pathways Models"
	}
	
	
	
	static content = {
		dataTableRows(wait:true)  { $("#documentTable tbody tr") }
		dataTableFirstRow  { $("#documentTable tbody tr", 0) }
		dataTableSecondRow  { $("#documentTable tbody tr", 1) }
		dataTableFirstRowLink { dataTableFirstRow.find("a") }
		dataTableSecondRowLink { dataTableSecondRow.find("a") }
		dataTableTMLink { dataTableRows.find("a", text: "Transplanting and Monitoring Pathway") }

        // The following pathway* elements are designed to replace the dataTable* elements above in a more useable fashion
        pathwayListURLs { dataTableRows.find("a")*.href() }

		searchBox  { 	$("#documentTable_filter input") }
	}

    boolean goToPathway(id){
        def link = dataTableFirstRow.find("a", id: "$id")
        if(link){
            dataTableFirstRow.find("a", id: "$id").click()
            return true
        }else{
            return false
        }

    }
}