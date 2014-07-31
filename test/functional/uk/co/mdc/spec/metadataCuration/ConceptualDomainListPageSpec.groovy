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
class ConceptualDomainListPageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage
	}

	def "Clicking on conceptualDomain name will lead to its show page"() {

		when: "at conceptualDomainList Page and clicking on a conceptualDomain name"
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}

		to ConceptualDomainListPage
		waitFor {
			at ConceptualDomainListPage
		}
		waitFor {
			$(ConceptualDomainListPage.elementsTable).displayed
		}
		def nameElement = getRow(0)["name"]
		waitFor {
			nameElement.displayed
		}
		nameElement.click()

		waitFor {
			at ConceptualDomainShowPage
		}

		then: "it redirects to conceptualDomain show page"
		waitFor {
			at ConceptualDomainShowPage
			mainLabel.displayed
		}
		mainLabel.text().contains("NHIC")
		description.text() == "NHIC Test Description"
		waitFor {
			$(ConceptualDomainShowPage.valueDomainsTab).displayed
		}
		waitFor{
			$(ConceptualDomainShowPage.modelsTab).displayed
		}
		waitFor{
			$(ConceptualDomainShowPage.propertiesTab).displayed
		}

	}

	def "ConceptualDomain list page has exportButton"() {

		setup:"Go to conceptualDomain page as a List page that contains ExportButton"
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}

		to ConceptualDomainListPage

		when: "at conceptualDomainList Page"
		waitFor {
			at ConceptualDomainListPage
		}

		then: "it should have export button"
		waitFor {
			$(ConceptualDomainListPage.exportButton).displayed
		}
	}

	def "ConceptualDomain list page does not show administrative menus to readonly users"() {

		setup:"Go to conceptualDomain list page as readonly users"
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}
		to ConceptualDomainListPage

		when:"at conceptualDomain list page"
		waitFor {
			at ConceptualDomainListPage
		}

		then:"it should not show administrative menus"
		!(newButton.displayed)
	}

	def "ConceptualDomain list page shows administrative menus to admin users"() {

		setup:"Go to conceptualDomain list page as an admin user"
		loginAsAdmin()

		to ConceptualDomainListPage
		waitFor {
			at ConceptualDomainListPage
		}

		when:"at conceptualDomain list page"
		waitFor {
			at ConceptualDomainListPage
		}

		then:"it should show administrative menus"
		waitFor {
			newButton
			newButton.displayed
		}
	}

	def "ConceptualDomain List Page can add a new conceptual domain"() {
		setup:"Go to conceptualDomain list page as an admin user and press 'New Conceptual Domain'"
		loginAsAdmin()

		to ConceptualDomainListPage
		waitFor {
			at ConceptualDomainListPage
		}
		waitFor {
			newButton.displayed
		}
		newButton.click()


		waitFor {
			newCDModelDialogue.displayed
		}
		waitFor {
			newCDModelDialogueTitle.displayed
		}
		when:"Fill and save the new ConceptualDomain"
		//fill the model dialogue form
		newCDModelDialogueName << "ZNEW-NAME"
		newCDModelDialogueDescription << "ZNEW-DESC"
		//click on Save button
		newCDModelDialogueSaveBtn.click()

		then:"it should add a new ConceptualDomain and show conceptualDomain show page"
		waitFor {
			ConceptualDomainShowPage
		}
	}

	private  def loginAsAdmin(){
		loginAdminUser()
		waitFor {
			at DashboardPage
		}
		go "metadataCurator"
		waitFor (30){
			at ModelListPage
		}
	}

}