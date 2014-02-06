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
        when:
        createPathway("Test pathway 4")

        to PathwayListPage
        def pathway = getPathwayLink("Test pathway 4")

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

        /**
         * Let's create a pathway to delete (so we don't hurt any other tests.
         */
        when: "I create a pathway"
        at PathwayListPage
        createPathway("This is a sample pathway")

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
        assert goToPathway("This is a sample pathway")

        then: "I'm at the right page"
        at PathwayShowPage
        driver.currentUrl == pathwayShowURL

        when:
        to PathwayListPage
        def pathwayItem = getPathwayLink("This is a sample pathway")

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
            !pathwayList.find("a", text: "This is a sample pathway")
        }

    }
}
