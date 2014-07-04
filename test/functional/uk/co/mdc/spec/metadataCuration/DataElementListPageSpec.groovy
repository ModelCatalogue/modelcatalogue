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
		mainLabel.text().contains("DE1 (Data Element:")
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



	def "ExportButton in dataElement List page contains several default reports"() {

		setup:"Go to dataElement List page as a List page that contains ExportButton"
		to DataElementListPage

		when: "at DataElementListPage"
		waitFor {
			at DataElementListPage
		}
		waitFor {
			$(DataElementListPage.exportButton).displayed
		}

		$(DataElementListPage.exportButton).click()


		then: "list of available reports will be displayed in a menu"
		waitFor {
			$(DataElementListPage.exportButtonItems).displayed
		}
		waitFor {
			$(DataElementListPage.exportButtonItems).find("li",0).displayed
		}
		$(DataElementListPage.exportButtonItems).find("li",0).find("a").size() == 4
		$(DataElementListPage.exportButtonItems).find("li",0).find("a")[index].text() == label

		where:
		index | label
		0	  | "Catalogue Elements to Excel"
		1	  | "Data Elements XLSX"
		2	  | "COSD"
		3	  | "NHIC"
	}

	def "ExportButton in dataElement list page will export dataElement list as an excel file"() {

		setup:"Go to dataElement page as a List page that contains ExportButton"
		to DataElementListPage

		when: "at dataElementList Page"
		waitFor {
			at DataElementListPage
		}
		waitFor {
			$(DataElementListPage.exportButton).displayed
		}
		$(DataElementListPage.exportButton).click()

		waitFor {
			$(DataElementListPage.exportButtonItems).displayed
		}
		waitFor {
			$(DataElementListPage.exportButtonItems).find("li",0).displayed
		}
		waitFor {
			$(DataElementListPage.exportButtonItems).find("li",0).find("a",index).displayed
		}

		//$("div.export.open ul#exportBtnItems").find("li",0).find("a",0).click()
		//Instead of clicking on the link, we will get the link href and download the file directly
		//and make sure that the content of the file is not empty
		def downloadLink = $(DataElementListPage.exportButtonItems).find("li",0).find("a",index)
		def bytes = downloadBytes(downloadLink.@href)

		then: "it downloads the excel file"
		bytes.size() != 0

		where:""
		index << [0,1,2]
	}

}