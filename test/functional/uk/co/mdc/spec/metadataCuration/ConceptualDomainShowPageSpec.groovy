package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Stepwise
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ConceptualDomainShowPage

/**
 * Created by soheil on 15/05/2014.
 */
@Stepwise
class ConceptualDomainShowPageSpec extends GebReportingSpec {


	def setupSpec() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}

	def "At ConceptualDomainShowPage, it shows properties, models and valueDomains"() {

		when: "at conceptualDomainList Page and clicking on a conceptualDomain name"
		to ConceptualDomainListPage
		waitFor {
			at ConceptualDomainListPage
		}

		waitFor {
			$(ConceptualDomainListPage.elementsTable).displayed
		}
		def nameElement = getRow(0)["name"]
		waitFor {
			nameElement.displayed
		}
		interact{
			click(nameElement)
		}

		waitFor {
			at ConceptualDomainShowPage
		}

		then: "it redirects to conceptualDomain show page"
		waitFor {
			at ConceptualDomainShowPage
			mainLabel.displayed
		}
		mainLabel.text().contains("NHIC")
		description.text() == "NHIC Test Description"
		waitFor {
			valueDomainsTab.displayed
		}
		waitFor{
			modelsTab.displayed
		}
		waitFor{
			valueDomainsTab.displayed
		}

	}


	def "At ConceptualDomainShowPage, clicking on its tabs will show related table"() {

		when: "at conceptualDomainList Page and clicking on a conceptualDomain name"
		to ConceptualDomainListPage
		waitFor {
			at ConceptualDomainListPage
		}
		waitFor {
			$(ConceptualDomainListPage.elementsTable).displayed
		}
		def nameElement = getRow(0)["name"]
		waitFor {
			nameElement.displayed
		}

		nameElement.click()

		then: "it redirects to conceptualDomain show page"
		waitFor {
			at ConceptualDomainShowPage
		}
		waitFor {
			valueDomainsTab.displayed
		}
		when:"Clicking on valueDomains Tab"
		interact{
			click(valueDomainsTab)
		}

		then:"valueDomains Table will be displayed"
		waitFor {
			valueDomainsTable.displayed
		}

		when:"Clicking on model Tab"
		waitFor {
			modelsTab.displayed
		}

		modelsTab.click()

		then:"model Table will be displayed"
		waitFor {
			modelsTable.displayed
		}
	}
}