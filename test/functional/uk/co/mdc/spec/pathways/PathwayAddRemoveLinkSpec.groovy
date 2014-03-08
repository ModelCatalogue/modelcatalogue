package uk.co.mdc.spec.pathways

import geb.spock.GebReportingSpec
import org.junit.internal.builders.IgnoredBuilder
import org.openqa.selenium.Dimension
import spock.lang.Ignore
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.pathways.PathwayListPage
import uk.co.mdc.pages.pathways.PathwayShowPage

/**
 * Created by soheil on 25/02/14.
 */
class PathwayAddRemoveLinkSpec extends GebReportingSpec{

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


    def "Check if endpoints exists for a new created local node"()
    {
        def nodeA
        def upEndpoint
        def downEndpoint
        def leftEndpoint
        def rightEndpoint

        when: "Creating a node"
        nodeA= createNode()
        upEndpoint = getNodeEndPoint(nodeA,'up')
        downEndpoint = getNodeEndPoint(nodeA,'down')
        leftEndpoint = getNodeEndPoint(nodeA,'left')
        rightEndpoint = getNodeEndPoint(nodeA,'right')

        then: "It should have 4 endpoints"
        assert nodeA
        assert upEndpoint
        assert upEndpoint.attr('id').startsWith('jsPlumb_1')
        assert downEndpoint
        assert downEndpoint.attr('id').startsWith('jsPlumb_1')
        assert leftEndpoint
        assert leftEndpoint.attr('id').startsWith('jsPlumb_1')
        assert rightEndpoint
        assert rightEndpoint.attr('id').startsWith('jsPlumb_1')
    }


    def "Check if a link is created by dragging and dropping from one new local node to another new local node"()
    {
        def nodeA
        def nodeB
        def link

        when: "Creating two nodes and drag&drop a link between them"
        nodeA= createNode()
        nodeB= createNode()
        link = createLink(nodeA,'down',nodeB,'up')

        then: "A link should be created between them"
        assert link
        assert link.attr('id') == "linkLOCAL1"

    }


    def "Check if Id of local link are updated after saving the pathway"()
    {
        def nodeA
        def nodeB
        def link

        when: "Creating two nodes and drag&drop a link between them"
        nodeA= createNode()
        nodeB= createNode()
        link = createLink(nodeA,'down',nodeB,'up')

        then: "A link should be created between them"
        assert link
        assert link.attr('id') == "linkLOCAL1"

        when:"saving the pathway"
        saveButton.click()

        then:"all ids of local links are updated"
        waitFor(10) {
            getLocalLinkIds().size() == 0
        }
    }


    def "Check if it stops creating duplicate links between two new local nodes"()
    {
        def nodeA
        def nodeB
        def link1
        def link2
        def link3
        def link4

        when: "Creating two nodes "
        nodeA= createNode()
        nodeB= createNode()

        then: "there should be no link"
        waitFor (10){
            getLocalLinkIds().size() == 0
        }

        when:"drag&drop a link between them AND again add more links between them"
        link1=createLink(nodeA,'down',nodeB,'up')
        link2=createLink(nodeA,'right',nodeB,'up')
        link3=createLink(nodeA,'left',nodeB,'left')
        link4=createLink(nodeA,'right',nodeB,'bottom')

        then: "Just one node should be created between them"
//        waitFor (10){
//            getLocalLinkIds().size()== 1
//        }
        assert link1
        assert !link2
        assert !link3
        assert !link4
    }


    def "Check if clicking on a link, shows its properties panel"()
    {
        when:"At pathway"
        at PathwayShowPage

        then:"Link properties panel is not displayed"
        assert !LinkPropertiesPanel

        when: "Clicking on a link"
        assert link3
        link3.click()


        then: "Its properties panel is displayed"
        waitFor {
            LinkPropertiesPanel
            LinkPropertiesPanel.displayed
        }

        when:"and then clicking on another node"
        node1.click()

        then: "properties panel should disappear"
        assert !LinkPropertiesPanel.displayed
    }


    def "Check when deleting a link, it will be removed from pathway"()
    {
        when: "In a subPathway and Clicking on a link and deleting it"
        at PathwayShowPage
        assert link4
        link4.click()
        deleteSelectedElementButton.click()

        then: "the link should be removed"
        !link4
    }

    def "Check when we are in a subPathway and deleting a link, it will be removed from pathway"()
    {
        def link2Id

        when: "In a subPathway and Clicking on a link and deleting it"
        at PathwayShowPage
        assert node3
        doubleClickOnNode(node3)
        assert getAllNodes().size()==3
        assert getLinkIds().size()==2
        link2Id= link2.attr('id')
        link2.click()
        deleteSelectedElementButton.click()

        then: "the link should be removed"
        !getLink(link2Id)
        getLinkIds().size()==1
    }
}