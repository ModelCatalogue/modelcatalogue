/**
 * Author: Ryan Brooks (ryan.brooks@ndm.ox.ac.uk)
 * 		   Adam Milward (adam.milward@outlook.com)
 */

package uk.co.mdc.pages.pathways;

import uk.co.mdc.pages.BasePageWithNav;

class PathwayShowPage extends BasePageWithNav{
	
	static url = "/pathwaysModel/show/*" 
	
	static at = {
		url == "/pathwaysModel/show/*" &&
		title == "Show Pathway"
	}
	
	
	
	static content = {
		pathwayName  { 	$("h1#pathwayName") }
		addNodeButton { $("#addNode") }
		node2(required:false) { $("#node7") }
		addFormModal { $("#AddFormModal") }
		addFormButton { $("#addFormToNode") }
		formDesignTableFirstRow { $("#formDesignTable tbody tr", 0) }
		formDesignTableRows { $("#formDesignTable tbody tr") }
		formDesignTableFRLink { formDesignTableFirstRow.find("a") }
		formDesignCartListFirstItem { $("#formCartList li") }
        editInfoButton { $("#editPathwayInfo") }
		viewSubPathwayButton { $("#viewSubPathway") }
		addSubPathwayButton { $("#addSubPathway") }
		updatePathwayModal { $("#updatePathwayModal") }
		createPathwayForm { $("form#createPathwayForm") }
		pathwayInfoName { js.exec("return document.getElementById('txt-nameUpdate').value")}
		pathwayInfoDescription { js.exec("return document.getElementById('txt-descUpdate').value")}
		pathwayInfoVersionNo { js.exec("return document.getElementById('txt-versionNoUpdate').value")}
		pathwayInfoIsDraft  { js.exec("return document.getElementById('select-isDraftUpdate').value")}
		pathwayCanvas { $(".jsplumb-container") }

        goToParentButton { $("#goToParent") }

		deleteSelectedElementButton {$("#deleteSelectedElement")}
		propertiesName {js.exec("return document.getElementById('txt-properties-name').value")}
        propertiesDescription{js.exec("return document.getElementById('txt-desc').value")}
		modalLabel { $("#createNodeModalLabel") }
        createNode{$("#CreateNode")}
		createNodeName { $("#createNodeName") }
		createNodeDescription { $("#createNodeDescription") }
		createNodeButton { $("#createNodeButton") }
        cancelCreateNodeButton {$("#cancelCreateNodeButton")}
        errorNodeName { $('#errorNodeName')}
		newNodeTitleDiv { pathwayCanvas.find("div", text: "testNode")}

        editModal { module PathwayEditModal }

	}

    def getNodeIds(){
        def ids = []
        $(".node").each { element ->
            ids.add( element.attr("id") )
        }
        return ids
    }

    /**
     * Return all nodes on the pathway at that level.
     * @return
     */
    def getAllNodes(){
        return $(".node")
    }

    /**
     * Get a node given it's ID on the page
     * @param nodeId
     * @return
     */
    def getNode(String nodeId){
        return pathwayCanvas.find("div", id: nodeId)
    }

    /**
     * Create a subpathway for a given node element on the page
     * @param node
     * @return
     */
    def createSubpathway(def node){

        node.click()
        viewSubPathwayButton.click()

        waitFor{
            goToParentButton.displayed
        }
        // Return to the original screen
        goToParentButton.click()
    }

    /**
     * Create a node on the canvas and return it
     */
    def createNode(String name,String description) {
        def preCreationNodeIds = getNodeIds()

        addNodeButton.click()

        waitFor{
            modalLabel.displayed
        }

        createNodeName = name
        createNodeDescription = description
        createNodeButton.click()
        waitFor {
            !modalLabel.displayed
        }

        waitFor{
            addNodeButton.displayed
        }

        def postCreationNodeIds = getNodeIds()

        assert postCreationNodeIds.size() == preCreationNodeIds.size() + 1

        Boolean changed= postCreationNodeIds.removeAll(preCreationNodeIds)
        assert  changed

        assert postCreationNodeIds.size() == 1
        return getNode(postCreationNodeIds[0])
    }

    /**
     * Delete a node from the canvas if it exists. If it doesn't no worries.
     */
    def deleteNode(def node){
        if(node != null){
            node.click()
            deleteSelectedElementButton.click()
        }
    }

    /**
     * Checks if the style is of a parent node
     */
    Boolean hasParentNodeStyle(def node){
        return node.find("i").classes().contains("fa-sitemap")
    }

    /**

     * Checks if the errorNodeName label is in red => the node name textbox is empty
     * @return
     */
    Boolean isErrorNodeLabelRed(){
        return (errorNodeName.style.color == 'red')
    }


    /*
     * returns the selected node
     */
    def getSelectedNode()
    {
        return  pathwayCanvas.find("div.selectedItem");
    }


    /**
     * Get the node width based of the node id
     */
    def getNodeWidth(def nodeId)
    {
        return  $('#node'+nodeId).width;
    }

}