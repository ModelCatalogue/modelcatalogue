package uk.co.mdc.spec.model

import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.model.ModelListPage

/**
 * A specification for lists of pathways, including the ones
 * on the dashboard and pathways list page.
 *
 * Created by Ryan, Soheil and Susana on 15/01/2014.
 */
class DataElementListSpec extends GebReportingSpec{

    def setup(){
        driver.manage().window().setSize(new Dimension(1028, 768))
        to LoginPage
        loginRegularUser()
        at DashboardPage
    }

    def "go to metadata home screen and see that dashboard list"(){

        when: "I go to the dashboard metadata screen"
        $("#metadata").click()

        then:
        waitFor{
            at ModelListPage
        }

    }


}
