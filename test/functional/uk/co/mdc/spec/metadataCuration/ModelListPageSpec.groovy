package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
import spock.lang.Ignore
import spock.lang.Stepwise
import uk.co.mdc.pages.authentication.LoginPage
import uk.co.mdc.pages.metadataCuration.ListPage.ConceptualDomainListPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataElementListPage
import uk.co.mdc.pages.metadataCuration.ListPage.DataTypeListPage
import uk.co.mdc.pages.metadataCuration.ListPage.ModelListPage
import uk.co.mdc.pages.metadataCuration.ShowPage.DataElementShowPage
import uk.co.mdc.pages.metadataCuration.ShowPage.ModelShowPage

/**
 * Created by soheil on 15/05/2014.
 */

class ModelListPageSpec extends GebReportingSpec{

	def setup(){
		to LoginPage
		loginReadOnlyUser()
		waitFor{
			at ModelListPage
		}
	}


	def "Navigating to ModelShowPage, it shows TreeModel"(){

		when: "I'm at main Metadata showPage"
		to ModelListPage
		waitFor {
			at ModelListPage
		}

		then: "it shows model treeView"
		waitFor {
			$(ModelListPage.modelTree).displayed
			mainLabel.displayed
		}
		waitFor {
			mainLabel.text().contains("NHIC Datasets Data Elements")
		}
	}

	def "Clicking on a collapse icon top level model will show its sub models"(){
		when: "Click on a top level parent node"
		to ModelListPage
		waitFor {
			at ModelListPage
		}

		waitFor {
			NHIC_Model_Item_Icon.displayed
		}
		interact {
			click(NHIC_Model_Item_Icon)
		}
		waitFor {
			ParentModel1_Item.displayed
		}
		then: "it shows its sub model"
		waitFor {
			ParentModel1_Item_Name.displayed
		}
	}

	def"Clicking on a model name, its name and description will be displayed on the main label"(){

		when: "Click on a model"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.modelTree).displayed
		}
		waitFor {
			NHIC_Model_Item_Icon.displayed
		}
		interact {
			click(NHIC_Model_Item_Icon)
		}


		waitFor {
			ParentModel1_Item.displayed
		}
		interact {
			click(ParentModel1_Item_Name)
		}

		then: "its name will be displayed on the main label"
		waitFor {
			mainLabel.displayed
		}
		waitFor {
			descriptionLabel.displayed
		}
		waitFor {
			mainLabel.text().contains("ParentModel1 Data Elements")
			descriptionLabel.text().contains("Test Description")
		}

	}

	def "Clicking on a model name, its dataElements will be displayed on the table"(){

		when: "Click on a model"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.modelTree).displayed
		}

		waitFor {
			NHIC_Model_Item_Icon.displayed
		}
		interact {
			click(NHIC_Model_Item_Icon)
		}


		waitFor {
			ParentModel1_Item.displayed
		}
		interact {
			click(ParentModel1_Item_Icon)
		}

		waitFor{
			Model1_Item.displayed
		}
		interact {
			click(Model1_Item_Name)
		}

		waitFor {
			dataElementsTable.displayed
			getDataElementRow(0)["name"]
			getDataElementRow(0)["desc"]
		}

		then: "its dataElements will be displayed on the table"
		waitFor {
			dataElementsTable.displayed
			(getDataElementRow(0)["name"]).text() == "DE1"
			(getDataElementRow(0)["desc"]).text() == "DE1 Desc"
		}
	}

	def "Clicking on a model show icon, will show the model page"(){

		when: "Click on a model"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.modelTree).displayed
		}
		waitFor {
			NHIC_Model_Item_Icon.displayed
		}
		interact {
			click(NHIC_Model_Item_Icon)
		}

		waitFor {
			ParentModel1_Item.displayed
		}
		interact {
			click(ParentModel1_Item_Icon)
		}

		waitFor{
			Model1_Item.displayed
		}
		interact {
			click(Model1_Item_Show)
		}

		then: "it will redirect to the model show page"
		waitFor {
			at ModelShowPage
		}
	}

	def "Clicking on a dataElement, will redirect us to dataElement show page"(){

		when: "Click on a dataElement in dataElement table"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			NHIC_Model_Item_Icon.displayed
		}
		interact {
			click(NHIC_Model_Item_Icon)
		}

		waitFor {
			ParentModel1_Item.displayed
		}
		interact {
			click(ParentModel1_Item_Icon)
		}

		waitFor{
			Model1_Item.displayed
		}

		interact {
			click(Model1_Item_Name)
		}

		waitFor {
			dataElementsTable.displayed
			getDataElementRow(0)["name"]
		}

		getDataElementRow(0)["name"].click()

		then: "it will redirect to dataElement show page"
		waitFor {
			at DataElementShowPage
		}
	}

	def "Conceptual Domains subMenu will redirect us to ConceptualDomain List page"(){

		when: "Click on ConceptualDomain List sub-menu"
		to ModelListPage

		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.catalogueElementLink).displayed
		}
		$(ModelListPage.catalogueElementLink).click()

		waitFor {
			$(ModelListPage.conceptualDomainLink).displayed
		}
		$(ModelListPage.conceptualDomainLink).click()


		then:"will redirect us to ConceptualDomain List page"
		waitFor {
			at ConceptualDomainListPage
		}
	}

	def "DataElements subMenu will redirect us to DataElements List page"(){

		when: "Click on DataElements List sub-menu"
		to ModelListPage

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


		then:"will redirect us to DataElement List page"
		waitFor {
			at DataElementListPage
		}

	}

	def "DataType subMenu will redirect us to DataType List page"(){

		when: "Click on DataType List sub-menu"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.catalogueElementLink).displayed
		}
		$(ModelListPage.catalogueElementLink).click()


		waitFor {
			$(ModelListPage.dataTypeLink).displayed
		}

		$(ModelListPage.dataTypeLink).click()

		then:"will redirect us to DataType List page"
		waitFor {
			at DataTypeListPage
		}
	}

	def "Model subMenu will redirect us to ModelShowPage"(){

		when: "Click on ModelList page sub-menu"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			$(ModelListPage.catalogueElementLink).displayed
		}
		$(ModelListPage.catalogueElementLink).click()
		waitFor {
			$(ModelListPage.modelLink).displayed
		}
		$(ModelListPage.modelLink).click()

		then:"it will redirect us to ModelListPage"
		waitFor {
			at ModelListPage
		}
	}



	def "Clicking on Draft action, will just show draft models"(){

		when: "Click on ModelList page sub-menu"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			//the first button is Status Filter(Draft,Finalized,....)
			$(ModelListPage.leftActionList)[0].displayed
		}

		//click on Status button
		($(ModelListPage.leftActionList)[0]).click()

		//sub action list should be shown
		waitFor {
			$(ModelListPage.leftSubActionList).displayed
		}

		//Draft item should be displayed
		waitFor {
			$(ModelListPage.leftSubActionList).find("li a",0)
		}

		//click on Draft item
		$(ModelListPage.leftSubActionList).find("li a",0).click()

		then:"it will redirect us to ModelListPage"
		waitFor {
			at ModelListPage
		}
		waitFor {
			Draft_Model_Item.displayed
		}
		waitFor {
			Draft_Model_Item_Name.text() == "Draft Datasets"
		}
	}
}