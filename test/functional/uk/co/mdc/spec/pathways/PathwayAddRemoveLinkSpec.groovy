package uk.co.mdc.spec.pathways
import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
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




    def "Check if a link is created by dragging and dropping from one new local node to another new local node"()
    {
        setup: "Creating two nodes and drag&drop a link between them"
        def nodeA= createNode()
        def nodeB= createNode()

        expect:"no local link exits"
        getLocalLinkIds().size()==0

        when:"creating a new link between them"
        def link = createLink(nodeA,'down',nodeB,'up')

        then: "A local link should be created between them"
        link
        getLocalLinkIds().size() == 1
    }


    def "Check if Id of local link are updated after saving the pathway"()
    {
        setup: "Create a pathway and add a couple of nodes and a link to it"
        to PathwayListPage
        createPathway("test") //do this test in a new pathway as it's going to save it
        at  PathwayShowPage
        createLink(createNode(),'down',createNode(),'up')

        expect: "some local IDs to be present before the save"
        getLocalLinkIds().size() == 1

        when: "I click save"
        saveButton.click()

        then:"all ids of local links are updated"
        waitFor {
            getLocalLinkIds().size() == 0
        }
    }


    def "Check if it stops creating duplicate links between two new local nodes"()
    {
        setup: "Create a pathway and add a couple of nodes and a link to it"
        to PathwayListPage
        createPathway("test") //do this test in a new pathway as it's going to save it
        waitFor {
            at  PathwayShowPage
        }
        def nodeA = createNode()
        def nodeB = createNode()
        createLink(nodeA,'down',nodeB,'up')

        expect: "a local links should be created"
        getLocalLinkIds().size() == 1


        when:"creating several links between the two nodes"
        createLink(nodeA,'down',nodeB,'up')
        createLink(nodeA,'right',nodeB,'up')
        createLink(nodeA,'left',nodeB,'left')
        createLink(nodeA,'right',nodeB,'bottom')

        then: "Just one node should be created between them"
        getLocalLinkIds().size()== 1
    }


    def "Check if clicking on a link, shows its properties panel"()
    {
        setup:"At pathway"
        at PathwayShowPage

        expect:"Link properties panel is not displayed and a link exists"
        !LinkPropertiesPanel
        link3

        when: "Clicking on a link"
        //link3.click()
        interact {
            click(link3)
        }


        then: "Its properties panel is displayed"
        waitFor {
            LinkPropertiesPanel
            LinkPropertiesPanel.displayed
        }
    }

    def "Check when deleting a link, it will be removed from pathway"()
    {
        setup: "In a pathway and clicking on a link and deleting it"
        at PathwayShowPage

        expect:"a link to be available"
        link4

        when:"deleting a link"
        //link4.click()
        interact {
            click(link4)
        }
        deleteSelectedElementButton.click()

        then: "the link should be removed"
        !link4
        waitFor {
            getLocalLinkIds().size() == 0
        }

    }

    def "Check when we are in a subPathway and deleting a link, it will be removed from pathway"()
    {
        setup: "In a subPathway"
        at PathwayShowPage
        node3
        doubleClickOnNode(node3)


        expect:"a link to be available"
        link2

        when:"deleting the link"
        link2.click()
        deleteSelectedElementButton.click()

        then: "the link should be removed"
        !link2
        waitFor {
            getLocalLinkIds().size() == 0
        }
    }

}