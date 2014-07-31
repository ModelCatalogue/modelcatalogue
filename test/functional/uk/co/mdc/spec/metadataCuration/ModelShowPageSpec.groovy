package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ModelShowPage

/**
 * Created by soheil on 15/05/2014.
 */
class ModelShowPageSpec extends GebReportingSpec {


	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}


	def "At modelShowPage, it shows model properties, conceptualDomains, metadata and dataElements"() {
		when: "Click on a model"
		waitFor {
			at ModelListPage
		}
		goToModelShowPage()

		then: "its properties, conceptualDomains and dataElements will be displayed"
		waitFor {
			at ModelShowPage
		}
		waitFor {
			propertiesTab.displayed
		}

		waitFor {
			childOfTab.displayed
		}
		waitFor {
			conceptualDomainTab.displayed
		}
		waitFor {
			dataElementsTab.displayed
		}
		waitFor {
			metadataTab.displayed
		}
		waitFor {
			parentOfTab.displayed
		}
		waitFor {
			finalizedIcon.displayed
		}
		waitFor {
			finalizedIcon.text() == "FINALIZED"
		}
	}

	def "At modelShowPage for a Draft Model, it shows Draft icon" (){
		when: "Click on a model"
		waitFor {
			at ModelListPage
		}
		goToDraftModelShowPage()

		then: "Draft icon is displayed"
		waitFor {
			at ModelShowPage
		}
		waitFor {
			draftIcon.displayed
		}
		waitFor {
			draftIcon.text() == "DRAFT"
		}
	}

}