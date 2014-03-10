/**
 * Author: Adam Milward (adam.milward@outlook.com)
 */
package uk.co.mdc.spec.pathways;
import geb.error.RequiredPageContentNotPresent
import geb.error.UnresolvablePropertyException
import geb.spock.GebReportingSpec

import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.pathways.PathwayListPage
import uk.co.mdc.pages.pathways.PathwayShowPage

import org.openqa.selenium.Dimension

class PathwayAddDeleteNodeSpec extends GebReportingSpec {

    def setup() {
        driver.manage().window().setSize(new Dimension(1024, 768))
        to LoginPage
        loginAdminUser()
        at DashboardPage

        nav.goToPathwayListPage()
        at PathwayListPage

        goToPathwayInList(0)
        waitFor{
            at PathwayShowPage
        }
    }

	def "View a Pathway add a new node and then delete as admin"() {

					
					when: "I click on add node"
					addNodeButton.click()
					
					then: "the node appears in the interface with the testNode title"

                    def newNode = $(".node", text: "New node")
					waitFor{
						newNode.displayed
					}

					when: "I click on the node I have just created"
                    newNode.click()
				
					then: "the delete node button is visible in the properties panel"
					waitFor{
						deleteSelectedElementButton.displayed
					}
					
					when: "I click on the delete node button"
					deleteSelectedElementButton.click()
					
					then: "the node is deleted"
					waitFor{
							try {
                                $(".node", text: "New node").displayed == false
							} catch (UnresolvablePropertyException e) {
								return true
							} catch (RequiredPageContentNotPresent e) {
								return true
							} 
						}
							
		}

    def "Check that When I create a node then the description is blank" () {

        at PathwayShowPage

        when: "I create a Node"
        def node = createNode()
        node.click()

        then: "the description of the node is empty"
        waitFor{
            propertiesDescription.displayed
            propertiesDescription.text() == "empty"
        }

        cleanup: "I delete the node"
        deleteSelectedElementButton.click()

    }

    def "When a new node is added, make it as selected"()
    {
        at PathwayShowPage

        when:"I  add a new node"
        def node = createNode()
        def selectedNode = getSelectedNode()

        then:"The New node should be selected automatically"
        node.attr("id") == selectedNode.attr("id")
    }

    def "When new nodes are added, the second one should appear properly underneath the first one"()
    {
        at PathwayShowPage

        when: "I create a node"
        def node1 = createNode();
        def selectedNode = getSelectedNode()


        then:"It should be added and selected"
        node1.attr("id") == selectedNode.attr("id")


        when: "I create another node"
        def node2  = createNode();
        selectedNode = getSelectedNode()


        then:"the second node should be selected"
        node2.attr("id") == selectedNode.attr("id")


        and: "And the second node should be aligned properly underneath the first node"
        node2.x == node1.x
        node2.y == node1.y + selectedNode.height + 50

    }

    def "When on pathway and double click on a blank space, a new node should be added"()
    {
        setup:"on pathway"
        at PathwayShowPage
        def preCreationNodeIds = getNodeIds()


        when:"double-clicking in blank space area"
        interact { doubleClick(pathwayCanvas) }
        def postCreationNodeIds      = getNodeIds()
        postCreationNodeIds.removeAll(preCreationNodeIds)

        then:"a new node is added"
        postCreationNodeIds.size() == 1

        and: "the new node is selected"
        postCreationNodeIds[0]
        getSelectedNode()
        postCreationNodeIds[0] == getSelectedNode().attr("id")

    }

    def "When on Pathway and click on a blank space, the selected node should be unSelected" ()
    {
        setup:"on the pathway"
        at PathwayShowPage
        def allNodes = getAllNodes()

        expect:"Some nodes should be there"
        allNodes[0]

        when: "click on a node"
        def selectedNode=allNodes[0]
        selectedNode.click()


        then:"the node should be selected"
        selectedNode.attr("id") == getSelectedNode().attr("id")


        when:"click on a blank space on Container"
        pathwayCanvas.click()


        then: "the selected node should be unSelected and no node is selected"
        waitFor{
            getSelectedNode().size()==0
        }
        !selectedNode.classes().contains('selectedItem')
    }


    def "Check if endpoints exists for a new created local node"()
    {
        when: "Creating a node"
        def nodeA= createNode()

        then:"node and its four endpoints to be created"
        nodeA
        getNodeEndPoint(nodeA,'up')
        getNodeEndPoint(nodeA,'down')
        getNodeEndPoint(nodeA,'left')
        getNodeEndPoint(nodeA,'right')
    }

}
