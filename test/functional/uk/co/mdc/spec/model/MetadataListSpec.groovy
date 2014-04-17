package uk.co.mdc.spec.model

import geb.spock.GebReportingSpec
import org.openqa.selenium.Dimension
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.model.MetadataListPage

/**
 * A specification for lists of pathways, including the ones
 * on the dashboard and pathways list page.
 *
 * Created by Ryan, Soheil and Susana on 15/01/2014.
 */
class MetadataListSpec extends GebReportingSpec{

    def setup(){
        driver.manage().window().setSize(new Dimension(1028, 768))
        to LoginPage
        loginRegularUser()
        at DashboardPage
    }

    def "go to metadata home screen and see that navigation works"(){

        when: "I go to the dashboard metadata screen"
        $("#metadata").click()

        then:
        waitFor{
            at MetadataListPage
            heading.text() =="DataElements"
        }


        when: "I click on the model link"
        $("#modelLink").click()

        then: "I go to the model page"
        waitFor {
            heading.text() == "Models"
        }

        when: "I click on the value domain link"
        $("#valueDomainLink").click()

        then: "I go to the value domain page"
        waitFor {
            heading.text() == "ValueDomains"
        }

        when: "I click on the conceptual domain link"
        $("#conceptualDomainLink").click()

        then: "I go to the value domain page"
        waitFor {
            heading.text() == "ConceptualDomains"
        }

        when: "I click on the conceptual domain link"
        $("#dataTypeLink").click()

        then: "I go to the value domain page"
        waitFor {
            heading.text() == "DataTypes"
        }

        when: "I click on the relationship types link"
        $("#relationshipTypeLink").click()

        then: "I go to the value domain page"
        waitFor {
            heading.text() == "RelationshipTypes"
        }

        when: "I go back"
        driver.navigate().back()

        then: "I go to the DataTypes page"
        waitFor {
            heading.text() == "DataTypes"
        }


        when: "I go back"
        driver.navigate().back()

        then: "I go to the ConceptualDomains page"
        waitFor {
            heading.text() == "ConceptualDomains"
        }

        when: "I go back"
        driver.navigate().back()

        then: "I go to the ValueDomains page"
        waitFor {
            heading.text() == "ValueDomains"
        }

        when: "I go back"
        driver.navigate().back()

        then: "I go to the Models page"
        waitFor {
            heading.text() == "Models"
        }

        when: "I go back"
        driver.navigate().back()

        then: "I go to the DataElements page"
        waitFor {
            heading.text() == "DataElements"
        }

    }

    def "go to home page and check pagination works"(){


        when: "I go to the dashboard metadata screen"
        $("#metadata").click()

        then:
        waitFor{
            at MetadataListPage
            heading.text() =="DataElements"
            actor.text()=="actor"
        }

        when:
        page3.click()

        then:
        waitFor{
            enumerations.text()=="enumerations"
        }

        when:
        previous.click()

        then:
        waitFor{
            version.text()=="version"
        }

        when:
        next.click()

        then:
        waitFor{
            enumerations.text()=="enumerations"
        }


    }

    def "go to models list navigate to a particular model and then to the elements it contains"(){
        when: "I go to the dashboard metadata screen"
        $("#metadata").click()

        then:
        waitFor{
            at MetadataListPage
            heading.text() =="DataElements"
        }


        when: "I click on the model link"
        $("#modelLink").click()

        then: "I go to the model page"
        waitFor {
            heading.text() == "Models"
        }


        when: "I click on conceptualDomain"
        conceptualDomain.click()

        then: "I go to the show screen for conceptual domain"
        waitFor {
            heading3.text() == "ConceptualDomain (Model: 14)"
        }
        when: "I click on the contains tab"
        containsTab.click()

        then: "I go to the elements contained in the conceptualdomain screen"
        waitFor {
            dataElement.text() == "Data Element: 123"
        }

        when: "I click on the name data element"
        dataElement.click()

        then: "I go to the name data element"
        waitFor {
            heading3.text() == "name (Data Element: 123)"
        }
    }


}