package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import uk.co.mdc.pages.DashboardPage
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataTypeListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ConceptualDomainShowPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataElementShowPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataTypeShowPage

/**
 * Created by soheil on 17/05/2014.
 */
class DataTypeListPageSpec extends GebReportingSpec {

	def setup() {
		to LoginPage

	}

	def "Clicking on dataType name will lead to its show page"() {

		setup:""
		waitFor {
			at LoginPage
		}
		loginReadOnlyUser()
		waitFor {
			at ModelListPage
		}

		when: "at dataTypeList Page and clicking on a dataType name"
		to DataTypeListPage
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
		mainLabel.text() == "Boolean (Data Type: 4)"
		description.text() == "java.lang.Boolean"
	}




//	def "'Create Relationship' on dataType show page will create relationship between two dataTypes"(){
//
//		setup:"at dataType show page"
//		loginAsAdmin()
//
//		to DataTypeListPage
//		waitFor {
//			at DataTypeListPage
//		}
//		//go and select a DataType Element
//		goToBooleanDataTypeShowPage()
//		waitFor {
//			at DataTypeShowPage
//		}
//		waitFor {
//			createRelationButton.displayed
//		}
//		createRelationButton.click()
//
//
//		waitFor {
//			relationDialogue.displayed
//		}
//		waitFor {
//			relationDialogueTitle.displayed
//		}
//
//		when:"Fill and save the new Relationship"
//
//		//select "is synonym for"
//		relationDialogueReltSelector = "1"
//
//		//type the target element
//		relationDialogueItemSelector << "String"
//
//
//		//it should display a list of items
//		waitFor {
//			relationDialogueItemSearchResult.displayed
//		}
//
//		//the first one is String DataType
//		waitFor {
//			relationDialogueItemSearchResult.find("li",0).text().trim().contains("String (Data Type: 1)")
//		}
//
//		//select the first item in the result which is String DataType
//		relationDialogueItemSearchResult.find("li",0).click()
//
//
//		//save button should be displayed
//		waitFor {
//			relationDialogueSaveBtn.displayed
//		}
//
//		//click on Save button
//		relationDialogueSaveBtn.click()
//
//		//now it returns to dataType show page
//		waitFor {
//			at DataTypeShowPage
//		}
//		//Synonyms Tab is displayed
//		waitFor {
//			$("div.tabbable ul li[heading='Synonyms']").displayed
//		}
//
//
//		waitFor {
//			$("div.tabbable ul li[heading='Synonyms']").find("a",0).displayed
//		}
//
//		$("div.tabbable ul li[heading='Synonyms']").find("a",0).click()
//
//
//		then:"it should add a new relationship and add it in Synonyms tab"
//		//Synonyms Table is shown
//		//wait about 20 second in this case, as it usually fails due to the delay
//		waitFor(20) {
//			$("table#-synonyms").displayed
//		}
//		waitFor {
//			$("table#-synonyms").find("tbody tr td",0).displayed
//		}
//		waitFor {
//			$("table#-synonyms").find("tbody tr td",0).text() == "is synonym for"
//		}
//
//		waitFor {
//			$("table#-synonyms").find("tbody tr td",1).displayed
//		}
//		waitFor {
//			$("table#-synonyms").find("tbody tr td",1).text() == "String"
//		}
//
//	}


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