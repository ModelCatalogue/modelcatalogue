package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.AssetShowPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ConceptualDomainShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class ConceptualDomainListPageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}

	def "Clicking on conceptualDomain name will lead to its show page"() {

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
			$(ConceptualDomainShowPage.propertiesTab).displayed
		}

	}



	def "ConceptualDomain list page has exportButton"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		to ConceptualDomainListPage

		when: "at conceptualDomainList Page"
		waitFor {
			at ConceptualDomainListPage
		}

		then: "it should have export button"
		waitFor {
			$(ConceptualDomainListPage.exportButton).displayed
		}
	}

	def "Clicking on exportButton in conceptualDomain list page will show the list of available reports"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		to ConceptualDomainListPage

		when: "at conceptualDomainList Page"
		waitFor {
			at ConceptualDomainListPage
		}
		waitFor {
			$(ConceptualDomainListPage.exportButton).displayed
		}

		$(ConceptualDomainListPage.exportButton).click()

		then: "list of available reports will be displayed in a menu"
		$(ConceptualDomainListPage.exportButtonItems).displayed
		$(ConceptualDomainListPage.exportButtonItems).find("li",0).displayed
	}

	def "ExportButton in conceptualDomain list page will export conceptualDomain list as an excel file"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		to ConceptualDomainListPage

		when: "at conceptualDomainList Page"
		waitFor {
			at ConceptualDomainListPage
		}
		waitFor {
			$(ConceptualDomainListPage.exportButton).displayed
		}
		$(ConceptualDomainListPage.exportButton).click()

		waitFor {
			$(ConceptualDomainListPage.exportButtonItems).displayed
		}

		waitFor {
			$(ConceptualDomainListPage.exportButtonItems).find("li",1).displayed
		}

		waitFor {
			$(ConceptualDomainListPage.exportButtonItems).find("li",1).find("a",0).displayed
		}

		$(ConceptualDomainListPage.exportButtonItems).find("li",1).find("a",0).click()

		then: "it downloads the excel file"
		waitFor {
			at AssetShowPage
		}
	}


}