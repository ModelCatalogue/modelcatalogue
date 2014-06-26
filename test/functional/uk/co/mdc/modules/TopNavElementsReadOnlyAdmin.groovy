package uk.co.mdc.modules

import geb.Module

class TopNavElementsReadOnlyAdmin extends Module{

	static String searchTextInput = "input#search-term"
	static String searchResultUl = "form[role='search'] ul.dropdown-menu"

	static String catalogueElementLink = "a#catalogueElementLink"
	static String conceptualDomainLink = "a#conceptualDomainLink"

	static String accountLink = "a#accountLink"
	static String changePasswordLink = "li#changePasswordLink a"


	static content = {


		dataElementLink(wait:true) {$("li a#dataElementLink")}
		valueDomainLink(wait:true) {$("li a#valueDomainLink")}
		dataTypeLink(wait:true) {$("li a#dataTypeLink")}
		modelLink(wait:true) {$("li a#modelLink")}






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