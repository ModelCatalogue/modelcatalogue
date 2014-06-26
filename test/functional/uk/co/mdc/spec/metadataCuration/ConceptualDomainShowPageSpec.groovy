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
		 nameElement.click()

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
			$(ConceptualDomainShowPage.valueDomainsTab).displayed
		}
		waitFor{
			$(ConceptualDomainShowPage.modelsTab).displayed
		}
		waitFor{
			$(ConceptualDomainShowPage.valueDomainsTab).displayed
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
			$(ConceptualDomainShowPage.valueDomainsTab).displayed
			$(ConceptualDomainShowPage.valueDomainsTab).find("a").displayed
		}
		when:"Clicking on valueDomains Tab"
		$(ConceptualDomainShowPage.valueDomainsTab).find("a").click()

		then:"valueDomains Table will be displayed"
		waitFor {
			valueDomainsTable.displayed
		}

		when:"Clicking on model Tab"
		waitFor {
			$(ConceptualDomainShowPage.modelsTab).displayed
			$(ConceptualDomainShowPage.modelsTab).find("a").displayed
		}

		$(ConceptualDomainShowPage.modelsTab).find("a").click()

		then:"model Table will be displayed"
		waitFor {
			modelsTable.displayed
		}
	}
}