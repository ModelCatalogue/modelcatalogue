package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataElementShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class DataElementListPageSpec extends GebReportingSpec {

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
			click(nav.dataElementLink)
		}
		waitFor {
			DataElementListPage
		}

	}


	def "Clicking on dataElement name will lead to its show page"() {

		when: "at dataElementList Page and clicking on a dataElement name"
		waitFor {
			at DataElementListPage
		}
		waitFor {
			$(DataElementListPage.elementsTable).displayed
		}
		def nameElement = getRow(0)["name"]
		waitFor {
			nameElement.displayed
		}
		interact {
			click(nameElement)
		}

		then: "it redirects to dataElement show page"
		waitFor {
			at DataElementShowPage
			mainLabel.displayed
		}
		mainLabel.text().contains("DE (Data Element:")
		description.text() == "Desc"
		waitFor {
			propertiesTab.displayed
			dataTypesTab.displayed
			metadataTab.displayed
			modelsTab.displayed
		}

//		relatedToTab

	}

	def "Clicking on dataElement catalogueId will lead to its show page"() {

		when: "at dataElementList Page and clicking on a dataElement name"
		waitFor {
			at DataElementListPage
		}
		waitFor {
			$(DataElementListPage.elementsTable).displayed
		}

		def nameElement = getRow(0)["catalogueId"]
		waitFor {
			nameElement.displayed
		}
		interact {
			click(nameElement)
		}



		then: "it redirects to dataElement show page"
		waitFor{
			at DataElementShowPage
			mainLabel.displayed
		}
		mainLabel.text().contains("DE (Data Element:")
		description.text() == "Desc"
		waitFor {
			propertiesTab.displayed
			dataTypesTab.displayed
			metadataTab.displayed
			modelsTab.displayed
		}

//		relatedToTab
	}
}