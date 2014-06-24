package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ConceptualDomainShowPage

/**
 * Created by soheil on 15/05/2014.
 */
class ConceptualDomainShowPageSpec extends GebReportingSpec {


	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}

	}


	def "At ConceptualDomainShowPage, it shows properties, models and dataTypes"() {

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
		interact {
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
		description.text() == "Test Description"
		waitFor {
			propertiesTab.displayed
			modelsTab.displayed
			dataTypesTab.displayed
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
		interact {
			click(nameElement)
		}


		then: "it redirects to conceptualDomain show page"
		waitFor {
			at ConceptualDomainShowPage
		}
		waitFor {
			dataTypesTab.displayed
			dataTypesTab.find("a").displayed
		}
		when:"Clicking on dataTypes Tab"
		interact {
			click(dataTypesTab.find("a"))
		}

		then:"dataTypes Table will be displayed"
		waitFor {
			dataTypesTable.displayed
		}

		when:"Clicking on model Tab"
		waitFor {
			modelsTab.displayed
			modelsTab.find("a").displayed
		}
		interact {
			click(modelsTab.find("a"))
		}

		then:"model Table will be displayed"
		waitFor {
			modelsTable.displayed
		}
	}
}