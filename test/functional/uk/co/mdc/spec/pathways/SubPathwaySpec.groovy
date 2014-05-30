/**
 * Author: Adam Milward (adam.milward@outlook.com)
 */
package uk.co.mdc.spec.pathways;
import geb.spock.GebReportingSpec

import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.pathways.PathwayListPage
import uk.co.mdc.pages.pathways.PathwayShowPage

import org.openqa.selenium.Dimension

class SubPathwaySpec extends GebReportingSpec {

    def setup(){
        driver.manage().window().setSize(new Dimension(1024, 768))
        to LoginPage
        loginRegularUser()
        at DashboardPage

        nav.goToPathwayListPage()
        at PathwayListPage

        goToPathwayInList(0)
        at PathwayShowPage

    }

    def "Identifying parent nodes"(){

        at PathwayShowPage

        def nodeElement
        def newNode

        when: "I create a subpathway"
        def nodeId = getNodeIds()[0]
        createSubpathway( getNode(nodeId) )
        nodeElement = getNode(nodeId)

        then: "The node is highlighted"
        hasParentNodeStyle(nodeElement)

        when: "I create a new node"
        newNode = createNode()

        then: "That node is not marked as a parent"
        !hasParentNodeStyle(newNode)

    }
}
