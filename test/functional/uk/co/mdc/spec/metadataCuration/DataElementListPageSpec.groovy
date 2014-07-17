package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Stepwise
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.AssetShowPage
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
		mainLabel.text().contains("DE1 FINALIZED (Data Element:")
		description.text() ==  "DE1 Desc"
		waitFor {
			propertiesTab.displayed
		}
		waitFor {
			valueDomainsTab.displayed
		}
		waitFor {
			metadataTab.displayed
		}
		waitFor {
			modelsTab.displayed
		}
		waitFor {
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
		mainLabel.text().contains("DE1 FINALIZED (Data Element:")
		description.text() == "DE1 Desc"
		waitFor {
			propertiesTab.displayed
		}

		waitFor {
			valueDomainsTab.displayed
		}
		waitFor {
			metadataTab.displayed
		}
		waitFor {
			modelsTab.displayed
		}
		waitFor {
			relationshipsTab.displayed
		}
	}

	def "Status action button is displayed"() {
		when:"Go to dataElement page as a List page"
		to DataElementListPage
		waitFor {
			at DataElementListPage
		}
		then: "it should contain Status action button"
		//the first item is status filter button
		waitFor {
			getStatusActionButton().displayed
		}
	}

	def "Selecting Draft status filter will just show the Draft dataElements"(){
		when:"Go to dataElement page as a List page and clicking on Draft status"
		to DataElementListPage
		waitFor {
			at DataElementListPage
		}
		waitFor {
			getStatusActionButton().displayed
		}

		//click on status Action
		getStatusActionButton().click()
		//the list of status filter (Draft,Finalized,..) are displayed
		waitFor {
			$(subActionList).displayed
		}
		//DRAFT is shown
		waitFor {
			getDraftStatusButton().displayed
		}

		//DRAFT item has 'DRAFT' as its text
		waitFor {
			getDraftStatusButton().text() == "Draft"
		}

		//its <a> link is accessible
		waitFor {
			getDraftStatusButton().find("a",0).displayed
		}

		getDraftStatusButton().find("a",0).click()



		then:"DataElement table just shows Draft DataElements"
		//dataElement list should be displayed
		waitFor {
			$(dataElementList).displayed
		}
		//first row should have name column
		$(dataElementList).find("tbody tr td",1).displayed
		//dataElement name should be "DE3"
		$(dataElementList).find("tbody tr td",1).text() == "DE3"
	}
}