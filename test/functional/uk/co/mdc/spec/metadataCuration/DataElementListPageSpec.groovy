package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Stepwise
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataElementShowPage

/**
 * Created by soheil on 17/05/2014.
 */
@Stepwise
class DataElementListPageSpec extends GebReportingSpec {

	def setupSpec() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.catalogueElementLink).displayed
		}
		$(ModelListPage.catalogueElementLink).click()


		waitFor {
			$(ModelListPage.dataElementLink).displayed
		}

		$(ModelListPage.dataElementLink).click()

		waitFor {
			at DataElementListPage
		}

	}


	def "Clicking on dataElement name will lead to its show page"() {

		when: "at dataElementList Page and clicking on a dataElement name"
		to DataElementListPage
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

		nameElement.click()

		then: "it redirects to dataElement show page"
		waitFor {
			at DataElementShowPage
			mainLabel.displayed
		}
		mainLabel.text().contains("DE1 (Data Element:")
		description.text() ==  "DE1 Desc"
		waitFor {
			propertiesTab.displayed
			dataTypesTab.displayed
			metadataTab.displayed
			modelsTab.displayed
			relationshipsTab.displayed
		}

	}

	def "Clicking on dataElement catalogueId will lead to its show page"() {

		when: "at dataElementList Page and clicking on a dataElement name"
		to DataElementListPage
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

		nameElement.click()



		then: "it redirects to dataElement show page"
		waitFor{
			at DataElementShowPage
			mainLabel.displayed
		}
		mainLabel.text().contains("DE1 (Data Element:")
		description.text() == "DE1 Desc"
		waitFor {
			propertiesTab.displayed
			dataTypesTab.displayed
			metadataTab.displayed
			modelsTab.displayed
			relationshipsTab.displayed
		}
	}
}