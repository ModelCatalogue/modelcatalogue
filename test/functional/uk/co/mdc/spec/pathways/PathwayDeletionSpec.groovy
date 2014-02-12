package uk.co.mdc.spec.pathways

import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.pathways.PathwayListPage
import uk.co.mdc.pages.pathways.PathwayShowPage

/**
 * Created by Ryan Brooks on 18/12/2013.
 */
class PathwayDeletionSpec extends GebReportingSpec {

    /**
     * Navigate to the pathway show page for every test.
     * @return
     */
    def setup(){
        driver.manage().window().setSize(new Dimension(1028, 768))
        to LoginPage
        loginRegularUser()
        at DashboardPage
        nav.goToPathwayListPage()
        at PathwayListPage
    }

    def "Check deletion can be cancelled"(){

        String pathwayName = "test pathway 4"
        when:
        createPathway(pathwayName)

        to PathwayListPage
        waitFor {
            getPathwayLink(pathwayName)
        }
        def pathway = getPathwayLink(pathwayName)

        then: "The delete button is visible but the confirmation is not"

        getDeleteButton(pathway).displayed
        !getDeleteAbortButton(pathway).displayed
        !getDeleteConfirmationButton(pathway).displayed

        when: "I click on the delete button"
        getDeleteButton(pathway).click()

        then: "The confirmation is displayed"
        !getDeleteButton(pathway).displayed
        getDeleteAbortButton(pathway).displayed
        getDeleteConfirmationButton(pathway).displayed

        when: "I click 'cancel'"
        getDeleteAbortButton(pathway).click()

        then: "The confirmation is hidden and the delete button becomes visible again"
        getDeleteButton(pathway).displayed
        !getDeleteAbortButton(pathway).displayed
        !getDeleteConfirmationButton(pathway).displayed


        cleanup:
        getDeleteButton(pathway).click()
        getDeleteConfirmationButton(pathway).click()

    }

    def "Check deletion can be confirmed"(){

        String pathwayName = "Deletable pathway"
        /**
         * Let's create a pathway to delete (so we don't hurt any other tests.
         */
        when: "I create a pathway"
        at PathwayListPage
        createPathway(pathwayName)

        then: "I'm taken to the pathway editor"
        waitFor{
            at PathwayShowPage
        }

        def pathwayShowURL = driver.currentUrl
        def matcher = pathwayShowURL =~ /pathways\/(\d+)$/
        matcher.size() == 1
        def createdPathwayID = matcher[0][1]

        /**
         * Let's quickly check it's there
         */
        when: "I go to the page and click on the link"
        to PathwayListPage
        waitFor{
            goToPathway(pathwayName)
        }

        then: "I'm at the right page"
        at PathwayShowPage
        driver.currentUrl == pathwayShowURL

        when:
        to PathwayListPage
        waitFor {
            getPathwayLink(pathwayName)
        }
        def pathwayItem = getPathwayLink(pathwayName)

        then: "The delete button is visible but the confirmation is not"
        getDeleteButton(pathwayItem).displayed
        !getDeleteAbortButton(pathwayItem).displayed
        !getDeleteConfirmationButton(pathwayItem).displayed

        when: "I click on the delete button"
        getDeleteButton(pathwayItem).click()

        then: "The confirmation is displayed"
        !getDeleteButton(pathwayItem).displayed
        getDeleteAbortButton(pathwayItem).displayed
        getDeleteConfirmationButton(pathwayItem).displayed

        when: "I click 'confirm'"
        getDeleteConfirmationButton(pathwayItem).click()

        then: "the deleted item isn't present"
        waitFor{
            !pathwayList.find("a", text: pathwayName)
        }

    }
}
