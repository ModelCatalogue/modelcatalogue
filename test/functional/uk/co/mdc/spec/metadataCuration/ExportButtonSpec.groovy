package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Stepwise
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage

/**
 * Created by soheil on 22/05/2014.
 */
@Stepwise
class ExportButtonSpec extends GebReportingSpec {

	def setupSpec(){
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}

	def gotToConceptualDomainListPage() {
		to ModelListPage
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


	def gotToConceptualDataElementListPage() {
		to ModelListPage
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


	def "ConceptualDomain list page has exportButton"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		gotToConceptualDomainListPage()

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
		gotToConceptualDomainListPage()

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


	def "Clicking on exportButton in dataElement List page will show the list of available reports"() {

		setup:"Go to dataElement List page as a List page that contains ExportButton"
		gotToConceptualDataElementListPage()

		when: "at DataElementListPage"
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
		then: "list of available reports will be displayed in a menu"
		waitFor {
			$(DataElementListPage.exportButtonItems).find("li",0).displayed
		}
		$(DataElementListPage.exportButtonItems).find("li",0).find("a").size() == 3
		$(DataElementListPage.exportButtonItems).find("li",0).find("a")[index].text() == label

		where:
		index | label
		0	  | "General"
		1	  | "NHI C"
		2	  | "COS D"
	}


	def "ExportButton in dataElement list page will export dataElement list as an excel file"() {

		setup:"Go to dataElement page as a List page that contains ExportButton"
		gotToConceptualDataElementListPage()

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
		def downloadLink = $("div.export.open ul#exportBtnItems").find("li",0).find("a",index)
		def bytes = downloadBytes(downloadLink.@href)

		then: "it downloads the excel file"
		bytes.size() != 0

		where:""
		index << [0,1,2]
	}


	def "ExportButton in conceptualDomain list page will export conceptualDomain list as an excel file"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		gotToConceptualDomainListPage()

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
			$(ConceptualDomainListPage.exportButtonItems).find("li",0).displayed
			$(ConceptualDomainListPage.exportButtonItems).find("li",0).find("a",0).displayed
		}

		//$("div.export.open ul#exportBtnItems").find("li",0).find("a",0).click()
		//Instead of clicking on the link, we will get the link href and download the file directly
		//and make sure that the content of the file is not empty
		def downloadLink = $("div.export.open ul#exportBtnItems").find("li",0).find("a",0)
		def bytes = downloadBytes(downloadLink.@href)

		then: "its download the excel file"
		bytes.size() != 0
	}
}