package uk.co.mdc.pages.authentication

import geb.Page

class RegistrationCodePage extends Page{
	
	static url = "registrationCode/search"
	
	static at = {
		url == "registrationCode/search" &&
		title == "Registration Code Search"
	}
}