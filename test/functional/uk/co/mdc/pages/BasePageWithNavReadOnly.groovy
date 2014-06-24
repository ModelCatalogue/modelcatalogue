package uk.co.mdc.pages

import geb.Page
import uk.co.mdc.modules.FooterNav
import uk.co.mdc.modules.TopNavElementsReadOnlyAdmin

class BasePageWithNavReadOnly extends Page{
	
	static at = {
		assert navPresentAndVisible
	}
	
	static content = {
		nav { module TopNavElementsReadOnlyAdmin }
		footer { module FooterNav }
	}
}