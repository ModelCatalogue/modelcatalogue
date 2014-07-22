package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.AssetListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.AssetShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class AssetShowPageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}


	def "Asset show page contains download button"() {
		when: "Asset show page"
		to AssetListPage

		waitFor {
			at AssetListPage
		}
		waitFor {
			$(assetList).displayed
		}
		waitFor {
			//first row should have name column
			$(assetList).find("tbody tr td", 0).displayed
		}

		$(assetList).find("tbody tr td a", 0).click()

		waitFor {
			at AssetShowPage
		}

		then: "should contain download button"
		waitFor {
			at AssetShowPage
		}
		waitFor {
			propertiesTab.displayed
		}
		waitFor {
			metadataTab.displayed
		}
		waitFor {
			$(actionButtons, text: "Download").displayed
		}
	}
}