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
        at PathwayShowPage
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

    def "Check that When I create a node with no description then the description is blank" () {

        at PathwayShowPage

        when: "I click on create Node"
        addNodeButton.click()

        then: "the create node modal pops up"
        waitFor{
            modalLabel.text() == "Create Node"
        }

        when: "I fill in the name and click create node"
        createNodeName = "testNode"
        createNodeButton.click()

        then: "the node appears in the interface with the testNode title"

        waitFor{
            newNodeTitleDiv.displayed
        }

        when: "I click on the node I have just created"
        newNodeTitleDiv.click()

        then: "the description of the node is null"
        propertiesDescription == ""

        then: "I delete the node"
        deleteSelectedElementButton.click()

    }

    def "Clear content of the Create node form after the user has cancelled the action"(){
        at PathwayShowPage

        when: "I click on create Node"
        addNodeButton.click()

        then: "the create node modal pops up"
        waitFor{
            modalLabel.text() == "Create Node"
        }

        when: "I fill in the name, click cancel node and click on create Node again"
        createNodeName = "testNode"
        createNodeDescription = "testDesc"
        cancelCreateNodeButton.click()
        addNodeButton.click()

        then: "Name and description textboxes are empty"
        createNodeName == ""
        createNodeDescription==""
    }


    def "Validate node name is not blank in Create Node Modal"(){

        at PathwayShowPage

        when: "I click on create Node"
        addNodeButton.click()

        then: "the create node modal pops up"
        waitFor{
            modalLabel.text() == "Create Node"
        }

        when: "The node name is blank and click create node"
        createNodeName = ""
        createNodeDescription = ""
        createNodeButton.click()

        then: "the system sends a message to the user to note name is a compulsory field"
        !isErrorNodeName()
    }
    /**
     * added by Soheil to solve MC-125
     (Problem:Creating a second node places it over the first one)
     */
    def "When a new node is added, make it as selected"()
    {
        at PathwayShowPage

        def nodeNumber1
        def selectedNode

        when:"I click on create Node and add a new node"
        nodeNumber1 = createNode("Node Number 1","Description Number 1");
        selectedNode = getSelectedNode()

        then:"The New node should be selected automatically"
        nodeNumber1.attr("id") == selectedNode.attr("id")
    }

    /**
     * added by Soheil to solve MC-125
     (Problem:Creating a second node places it over the first one)
     */
    def "When new nodes are added, the second one should appear properly besides the first one"()
    {
        at PathwayShowPage

        def node1
        def node2
        def selectedNode

        when: "I create a node"
        node1 = createNode("Node Number 1","Description Number 1");
        selectedNode = getSelectedNode()


        then:"It should be added and selected"
        node1.attr("id") == selectedNode.attr("id")


        when: "I create another node"
        node2  = createNode("Node Number 2","Description Number 2");
        selectedNode = getSelectedNode()


        then:"the second node should be selected"
        node2.attr("id") == selectedNode.attr("id")


        and: "And the second node should be aligned properly bedside the first node"
        node2.x > node1.x +   getNodeWidth(node2.attr("id")) + 50 //50: the default value used in AppViewModel.js(self.saveNode method)
        node2.y == node1.y

    }
}
