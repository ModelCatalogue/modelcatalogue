package uk.co.mdc.pages.pathways;

import uk.co.mdc.pages.BasePageWithNav;

class PathwayListPage extends BasePageWithNav{
	
	static url = "pathway/list"
	
	static at = {
		url == "pathway/list" &&
		title == "All pathways"
	}
	
	
	
	static content = {
        pathwayList { $('#pathwayList')}
		dataTableRows(wait:true)  { pathwayList.find("tbody tr") }
		dataTableFirstRow  { pathwayList.find("tbody tr", 0) }
		dataTableSecondRow  { pathwayList.find("tbody tr", 1) }
		dataTableFirstRowLink { pathwayList.find("a") }
		dataTableSecondRowLink { pathwayList.find("a") }
		dataTableTMLink { pathwayList.find("a", text: "Transplanting and Monitoring Pathway") }

        // The following pathway* elements are designed to replace the dataTable* elements above in a more useable fashion
        pathwayListURLs { pathwayList.find("a")*.href() }
	}

    def getDeleteButton(def pathway) {
        pathway.parent().siblings().find("button", text: contains("Delete"))
    }
    def getDeleteConfirmationButton(def pathway){
        pathway.parent().siblings().find("button", text: contains("Confirm"))
    }
    def getDeleteAbortButton(def pathway){
        pathway.parent().siblings().find("button", text: contains("Abort"))
    }

    def getPathwayLinks(){
        return dataTableRows.find("a")
    }
    boolean goToPathway(String title){
        def link = pathwayList.find("a", text: "$title")
        if(link){
            link.click()
            return true
        }else{
            return false
        }

    }

    boolean goToPathwayInList(int index){
        def link = dataTableRows.find("a")[index]
        if(link){
            link.click()
            return true
        }else{
            return false
        }
    }

    def getPathwayLink(pathwayName){
        return pathwayList.find("a", text: pathwayName)
    }
}