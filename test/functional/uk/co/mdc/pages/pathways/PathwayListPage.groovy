package uk.co.mdc.pages.pathways;

import uk.co.mdc.pages.BasePageWithNav;

class PathwayListPage extends BasePageWithNav{
	
	static url = "pathways"
	
	static at = {
		url == "pathways" &&
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
        createButton{$('button', text:contains('Create a new pathway'))}

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
        def link = getPathwayLink(title)
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

    def getPathwayLink(String pathwayName){
        return pathwayList.find("a", text: pathwayName)
    }

    /**
     * Create a pathway. TODO this should be available everywhere, but it feels wrong to put it in NavElements...
     * @param name
     * @return
     */
    def createPathway(String name) {

        nav.expandPathwayMenuLink.click()
        waitFor{
            nav.createPathwayLink.displayed
        }

        nav.createPathwayLink.click()
        waitFor{
            nav.pathwayCreationModal.displayed
        }

        nav.newPathwayName = name
        nav.newPathwayDescription = "This is a sample pathway"
        nav.newPathwayVersionNo = "1a"
        nav.newPathwayIsDraft = "false"
        nav.newPathwaySubmit.click()


        // FIXME go to list and confirm it's right there too
    }
}