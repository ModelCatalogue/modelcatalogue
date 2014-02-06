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

    def "Check that When I create a node then the description is blank" () {

        at PathwayShowPage

        when: "I create a Node"
        def node = createNode()
        node.click()

        then: "the description of the node is empty"
        propertiesDescription == ""

        cleanup: "I delete the node"
        deleteSelectedElementButton.click()

    }

    /**
     * added by Soheil to solve MC-125
     (Problem:Creating a second node places it over the first one)
     */
    def "When a new node is added, make it as selected"()
    {
        at PathwayShowPage

        when:"I  add a new node"
        def node = createNode()
        def selectedNode = getSelectedNode()

        then:"The New node should be selected automatically"
        node.@id == selectedNode.@id
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
        node1 = createNode();
        selectedNode = getSelectedNode()


        then:"It should be added and selected"
        node1.attr("id") == selectedNode.attr("id")


        when: "I create another node"
        node2  = createNode();
        selectedNode = getSelectedNode()


        then:"the second node should be selected"
        node2.attr("id") == selectedNode.attr("id")


        and: "And the second node should be aligned properly bedside the first node"
        node2.x > node1.x + getNodeWidth(node2.attr("id")) + 50 //50: the default value used in AppViewModel.js(self.saveNode method)
        node2.y == node1.y

    }
}
