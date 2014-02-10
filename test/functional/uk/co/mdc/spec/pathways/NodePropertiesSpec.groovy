package uk.co.mdc.spec.pathways

import geb.spock.GebReportingSpec
import org.openqa.selenium.Keys
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.pathways.PathwayListPage
import uk.co.mdc.pages.pathways.PathwayShowPage

/**
 * Created by rb on 22/01/2014.
 */
class NodePropertiesSpec extends GebReportingSpec{

    def newName = "My new name"
    def newDesc = "A swanky description"

    def setup(){
        to LoginPage
        loginRegularUser()
        at DashboardPage

        nav.goToPathwayListPage()
        at PathwayListPage

        getPathwayLinks()[0].click()
        at PathwayShowPage
    }

    def "Updating properties and clicking out cancels the update"(){
        when: "I edit the name of a node and leave the textbox and refresh the page"
        def node = getNode( getNodeIds()[0])
        node.click()

        def oldName = propertiesName.text()

        propertiesName.click()
        propertiesEditName.value("")
        propertiesEditName <<  newName
        pathwayName.click() // clicking anywhere should do it

        then:
        waitFor {
            node.text() == oldName
        }
    }

    def "Updating properties and clicking OK updates the view"(){

        when: "I edit the name of a node and leave the textbox and refresh the page"
        def node = getNode( getNodeIds()[0])
        node.click()

        propertiesName.click()
        propertiesEditName.value("")
        propertiesEditName <<  newName
        getXeditableSubmit(propertiesName).click() // clicking anywhere should do it

        then:
        waitFor {
            node.text() == newName
        }
    }

    def "Updating properties when pressing return updates the view"(){

        when: "I edit the name of a node and leave the textbox and refresh the page"
        def node = getNode( getNodeIds()[0])
        node.click()

        propertiesName.click()
        propertiesEditName.value("")
        propertiesEditName <<  newName
        propertiesEditName <<  Keys.ENTER

        then:
        waitFor {
            node.text() == newName
        }
    }
}