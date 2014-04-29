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

        addNodeButton { pathwayCanvas.find("i", class: "fa-plus-square") }
        node2(required:false) { $("#node7") }
        node1(required:false) { $("#node6") }
        node3(required:false) { $("#node2") }

        subNode1(required:false) { $("#node3") }
        subNode2(required:false) { $("#node4") }
        subNode3(required:false) { $("#node5") }

        addFormModal { $("#AddFormModal") }
        addFormButton { $("h5", text: "Forms").find("i") }
        formDesignTableFirstRow { $("#formDesignTable tbody tr", 0) }
        formDesignTableRows { $("#formDesignTable tbody tr") }
        formDesignTableFRLink { formDesignTableFirstRow.find("a") }
        formDesignCartListFirstItem { $("#formCartList li") }

        pathwayCanvas { $(".jsplumb-container") }
		selectedNode(required: false) { $(".jsplumb-container .selectedItem") }

        goToParentButton { $("i", class: "fa-reply") }
        deleteSelectedElementButton {$("div", text: "Properties").parent().find("button", text: contains("Delete"))}

        propertiesName(required:false) { $("a", 'editable-text':"selectedNode.name")}
        propertiesEditName(required:false) { propertiesName.siblings("form").find("input", type: "text")}
        propertiesDescription(required:false) { $("a", 'editable-text':"selectedNode.description")}

        NodePropertiesPanel {$("#NodePropertiesPanel")}
        saveButton { $("button", text: "Save pathway") }

        LinkPropertiesPanel(required:false) {$("#LinkPropertiesPanel")}
        link3(required:false){$("div#link3")}
        link4(required:false){$("div#link4")}
        link2(required:false){$("div#link2")}

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
		$(".node")
    }

    /**
     * Get a node given it's ID on the page
     * @param nodeId
     * @return
     */
    def getNode(String nodeId){
        $(".jsplumb-container #${nodeId}" )
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
		def highlightedNode
		if(selectedNode.present){
			highlightedNode = selectedNode
		}else{
			highlightedNode = null
		}

        addNodeButton.click()

		// Wait for the node to be added and selected
        waitFor {
			selectedNode != highlightedNode
        }
        return selectedNode
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

    def getNodeId(node) {
        node.attr("id")
    }


    def getNodeEndPoint(node, endpointType) {
        node.find("div.ep."+endpointType)
    }

    def getLinkIds() {
        def ids = []
        $(".link").each { link ->
            ids.add( link.attr("id") )
        }
        return ids
    }

    def getLink(linkId) {
        $(".link[id^='"+linkId+"']")
    }

    def getLocalLinkIds() {
        def ids = []
        $(".link[id^='linkLOCAL']").each { link ->
            ids.add( link.attr("id") )
        }
        return ids
    }

    def createLink(sourceNode,sourceEndPoint,targetNode,targetEndpoint) {
        def linkIdsPre
        def linkIdsPost

        linkIdsPre = getLinkIds()

        def nodeAEndpoint = getNodeEndPoint(sourceNode,sourceEndPoint)
        def nodeBEndpoint = getNodeEndPoint(targetNode,targetEndpoint)
        def nodeAEndpointX = nodeAEndpoint.x
        def nodeAEndpointY = nodeAEndpoint.y

        def nodeBEndpointX = nodeBEndpoint.x
        def nodeBEndpointY = nodeBEndpoint.y

        def xOffset = nodeBEndpointX - nodeAEndpointX
        def yOffset = nodeBEndpointY  - nodeAEndpointY

        interact {
            clickAndHold(nodeAEndpoint)
            moveByOffset(xOffset,yOffset)
            release()
        }

        linkIdsPost = getLinkIds()
        linkIdsPost.removeAll(linkIdsPre)

        //return the created link
        if(linkIdsPost.size()==1)
            return getLink(linkIdsPost[0])
        else
            return  null
    }

    def doubleClickOnNode(node) {
        interact { doubleClick(node) }
    }
}
