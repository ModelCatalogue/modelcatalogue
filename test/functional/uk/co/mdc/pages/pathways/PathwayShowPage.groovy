/**
 * Author: Ryan Brooks (ryan.brooks@ndm.ox.ac.uk)
 * 		   Adam Milward (adam.milward@outlook.com)
 */

package uk.co.mdc.pages.pathways;

import uk.co.mdc.pages.BasePageWithNav;

class PathwayShowPage extends BasePageWithNav{
	
	static url = "/pathways/*"
	
	static at = {
		url == "/pathways/*" &&
		title == "Pathway Editor"
	}
	
	
	
	static content = {
		pathwayName  { 	$("#pathwayName") }
        pathwayUserVersion { $("#userVersion") }
        pathwayDescription { $("#pathwayDescription") }
        pathwayIsDraft { $("#pathwayIsDraft") }

		addNodeButton { pathwayCanvas.find("i", class: "fa-plus-square-o") }
		node2(required:false) { $("#node7") }
		addFormModal { $("#AddFormModal") }
		addFormButton { $("h5", text: "Forms").find("i") }
		formDesignTableFirstRow { $("#formDesignTable tbody tr", 0) }
		formDesignTableRows { $("#formDesignTable tbody tr") }
		formDesignTableFRLink { formDesignTableFirstRow.find("a") }
		formDesignCartListFirstItem { $("#formCartList li") }

		pathwayCanvas { $(".jsplumb-container") }

        goToParentButton { $("i", class: "fa-reply") }
		deleteSelectedElementButton {$("div", text: "Properties").parent().find("button", text: contains("Delete"))}

        propertiesName { $("a", 'editable-text':"selectedNode.name")}
        propertiesEditName { propertiesName.siblings("form").find("input", type: "text")}
        propertiesDescription { $("a", 'editable-text':"selectedNode.description")}

        saveButton { $("button", text: "Save") }

	}

    def getXeditableSubmit(def editableValue){
        return editableValue.siblings("form").find("button", type: "submit")
    }

    def getXeditableCancel(def editableValue){
        return editableValue.siblings("form").find("button", type: "button")
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
        return pathwayCanvas?.find("div", id: nodeId)
    }

    /**
     * Create a subpathway for a given node element on the page
     * @param node
     * @return
     */
    def createSubpathway(def node){
        waitFor{
            node.displayed
        }

        interact { doubleClick(node) }

        waitFor{
            goToParentButton.displayed
        }
        createNode()
        // Return to the original screen
        goToParentButton.click()
    }

    /**
     * Create a node on the canvas and return it
     */
    def createNode() {

        def preCreationNodes = getNodeIds()
        addNodeButton.click()

        def postCreationNodes = getNodeIds()
        postCreationNodes.removeAll(preCreationNodes)

        assert postCreationNodes.size() == 1
        def node = getNode(postCreationNodes[0])

        return getNode(postCreationNodes[0])

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
