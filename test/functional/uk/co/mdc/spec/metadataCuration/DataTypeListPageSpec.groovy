package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataTypeListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataTypeShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class DataTypeListPageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
		interact {
			click(nav.catalogueElementLink)

		}
		waitFor {
			nav.conceptualDomainLink.displayed
		}
		interact {
			click(nav.dataTypeLink)
		}
		waitFor {
			DataTypeListPage
		}
	}

	def "Clicking on dataType name will lead to its show page"() {

		when: "at dataTypeList Page and clicking on a dataType name"
		waitFor {
			at DataTypeListPage
		}

		waitFor {
			$(DataTypeListPage.elementsTable).displayed
			getRow(0)["object"].displayed
		}



		def nameElement = getRow(0)["name"]
		waitFor {
			nameElement.displayed
		}
		interact {
			click(nameElement)
		}

		then: "it redirects to dataType show page"
		waitFor {
			at DataTypeShowPage
			mainLabel.displayed
			description.displayed
		}
		mainLabel.text() == "String (Data Type: 1)"
		description.text() == "java.lang.String"
	}
}