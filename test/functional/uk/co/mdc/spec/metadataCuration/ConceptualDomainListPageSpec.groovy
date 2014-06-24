package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
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
		waitFor {
			nav.catalogueElementLink.displayed
		}
		interact {
			click(nav.catalogueElementLink)
		}
		waitFor {
			nav.conceptualDomainLink.displayed
		}
		interact {
			click(nav.conceptualDomainLink)
		}
		waitFor {
			ConceptualDomainListPage
		}
	}


	def "Clicking on conceptualDomain name will lead to its show page"() {

		when: "at conceptualDomainList Page and clicking on a conceptualDomain name"
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
}