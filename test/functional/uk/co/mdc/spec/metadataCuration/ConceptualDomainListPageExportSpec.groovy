package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.AssetShowPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ConceptualDomainShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class ConceptualDomainListPageExportSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}

	def "Clicking on exportButton in conceptualDomain list page will show the list of available reports"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		to ConceptualDomainListPage

		when: "at conceptualDomainList Page"
		waitFor {
			at ConceptualDomainListPage
		}

		//this line will create some sort of delay on the page,
		//as GEB used to fail on accessing 'exportButtonContent',
		//I had to add this check here
		waitFor{
			$("h2",text:"Conceptual Domain List").displayed
		}

		waitFor {
			exportButtonContent.displayed
		}
		interact {
			click(exportButtonContent)
		}

		then: "list of available reports will be displayed in a menu"
		waitFor {
			$(ConceptualDomainListPage.exportButtonItems).displayed
		}
		waitFor {
			$(ConceptualDomainListPage.exportButtonItems).find("li",0).displayed
		}
	}

	def "ExportButton in conceptualDomain list page will export conceptualDomain list as an excel file"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		to ConceptualDomainListPage

		when: "at conceptualDomainList Page"
		waitFor {
			at ConceptualDomainListPage
		}

		//this line will create some sort of delay on the page,
		//as GEB used to fail on accessing 'exportButtonContent',
		//I had to add this check here
		waitFor{
			$("h2",text:"Conceptual Domain List").displayed
		}

		waitFor {
			exportButtonContent.displayed
		}
		interact {
			click(exportButtonContent)
		}

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