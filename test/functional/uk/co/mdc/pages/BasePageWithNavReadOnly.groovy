package uk.co.mdc.pages

import geb.Page
import uk.co.mdc.modules.FooterNav
import uk.co.mdc.modules.TopNavElementsReadOnlyAdmin

class BasePageWithNavReadOnly extends Page{
	
	static at = {
		assert navPresentAndVisible
	}

	static String catalogueElementLink = "a#catalogueElementLink"
	static String conceptualDomainLink = "a#conceptualDomainLink"

	static String  dataElementLink = "li a#dataElementLink"
	static String  valueDomainLink = "li a#valueDomainLink"
	static String  dataTypeLink    = "li a#dataTypeLink"
	static String  modelLink       = "li a#modelLink"
	static String  accountLink 	   = "li a#accountLink"
	static String assetLink = "li a#assetLink"

	static String  changePasswordLink = "li#changePasswordLink a"




	static content = {
		nav { module TopNavElementsReadOnlyAdmin }
		footer { module FooterNav }
	}
}