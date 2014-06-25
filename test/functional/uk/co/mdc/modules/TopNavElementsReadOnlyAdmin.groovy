package uk.co.mdc.modules

import geb.Module

class TopNavElementsReadOnlyAdmin extends Module{

	static String searchTextInput = "input#search-term"
	static String searchResultUl = "form[role='search'] ul.dropdown-menu"

	static String catalogueElementLink = "a#catalogueElementLink"
	static String conceptualDomainLink = "a#conceptualDomainLink"


	static content = {


		dataElementLink(wait:true) {$("li a#dataElementLink")}
		valueDomainLink(wait:true) {$("li a#valueDomainLink")}
		dataTypeLink(wait:true) {$("li a#dataTypeLink")}
		modelLink(wait:true) {$("li a#modelLink")}

		accountLink(required: false)  {$("li a#accountLink")}


		changePasswordLink(required: false)  {$("li#changePasswordLink a")}




//		relationshipTypeLink {$("li a#relationshipTypeLink")}
//		uninstantiatedElementsLink {$("li a#uninstantiatedElements")}
//		metadataKeyCheckLink {$("li a#metadataKeyCheck")}
//		exportDataElementsLink {$("li a#exportDataElements")}
//		exportuninstantiatedElementsLink {$("li a#exportuninstantiatedElements")}

		navPresentAndVisible(required:false) {
			$("nav", class: "navbar")
		}
	}

    void goToModelPage(){
		modelLink.click()
    }


	def goToConceptualListPage(){
		catalogueElementLink.click()
		conceptualDomainLink.click()
	}
}