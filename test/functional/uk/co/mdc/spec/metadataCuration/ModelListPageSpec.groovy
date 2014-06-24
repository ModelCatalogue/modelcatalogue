package uk.co.mdc.spec.metadataCuration

import geb.spock.GebReportingSpec
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
@Stepwise
class ModelListPageSpec extends GebReportingSpec{



	def setupSpec(){
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
		mainLabel.text() == "NHIC Datasets Data Elements"
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
		ParentModel1_Item_Name.displayed
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
			descriptionLabel.displayed
		}
		mainLabel.text() == "ParentModel1 Data Elements"
		descriptionLabel.text() == "Test Description"
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
		dataElementsTable.displayed
		(getDataElementRow(0)["name"]).text() == "DE"
		(getDataElementRow(0)["desc"]).text() == "Desc"
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
		at ModelShowPage
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
			nav.catalogueElementLink.displayed
		}
		nav.catalogueElementLink.click()
		waitFor {
			nav.conceptualDomainLink.displayed
		}
		nav.conceptualDomainLink.click()

		then:"will redirect us to ConceptualDomain List page"
		waitFor {
			at ConceptualDomainListPage
		}
	}

	def "DataElements subMenu will redirect us to DataElements List page"(){

		when: "Click on ConceptualDomain List sub-menu"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			nav.catalogueElementLink.displayed
		}
		nav.catalogueElementLink.click()
		waitFor {
			nav.dataElementLink.displayed
		}
		nav.dataElementLink.click()


		then:"will redirect us to ConceptualDomain List page"
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
			nav.catalogueElementLink.displayed
		}
		nav.catalogueElementLink.click()

		waitFor {
			nav.dataTypeLink.displayed
		}

		nav.dataTypeLink.click()

		then:"will redirect us to DataType List page"
		waitFor {
			at DataTypeListPage
		}
	}


	def "Model subMenu will redirect us to ModelShowPage"(){

		when: "Click on ModelShowpage sub-menu"
		to ModelListPage
		waitFor {
			at ModelListPage
		}
		waitFor {
			nav.catalogueElementLink.displayed
		}
		nav.catalogueElementLink.click()
		waitFor {
			nav.modelLink.displayed
		}
		nav.modelLink.click()

		then:"it will redirect us to ModelShowPage"
		waitFor {
			at ModelListPage
		}
	}
}