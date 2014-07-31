package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.AssetListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.AssetShowPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ConceptualDomainShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class AssetListPageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
	}

	def "Asset list page shows a list of assets and Status filter button"() {
		when: "at Asset list page"
		to AssetListPage
		waitFor {
			at AssetListPage
		}

		then:"it shows a list of assets and Status filter button"
		waitFor {
			$(actionListButton).displayed
		}
		waitFor {
			$(assetList).displayed
		}
		waitFor {
			//first row should have name column
			$(assetList).find("tbody tr td",0).displayed
		}
//		waitFor {
//			asset name should be "DraftDefaultLayout"
//			$(assetList).find("tbody tr td",0).text() == "defaultLayout"
//		}
	}



	def "Selecting Draft status filter will just show the Draft assets"(){
		when:"Go to Asset list page"
		to AssetListPage
		waitFor {
			at AssetListPage
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



		then:"Asset table just shows Draft assets"
		//dataElement list should be displayed
		waitFor {
			$(assetList).displayed
		}
		waitFor {
			//first row should have name column
			$(assetList).find("tbody tr td",0).displayed
		}
		waitFor {
			//asset name should be "DraftDefaultLayout"
			$(assetList).find("tbody tr td",0).text() == "DraftDefaultLayout"
		}
	}


	def "Clicking on an asset name, will lead us to asset show page"() {
		when: "Clicking on an asset name"
		to AssetListPage

		waitFor {
			at AssetListPage
		}
		waitFor {
			$(assetList).displayed
		}
		waitFor {
			//first row should have name column
			$(assetList).find("tbody tr td",0).displayed
		}

		$(assetList).find("tbody tr td",0).click()

		then:"it goes to the asset show page"
		waitFor {
			at AssetShowPage
		}

	}
}