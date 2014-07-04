package uk.co.mdc.pages.authentication

import geb.Page

class UserCreatePage extends Page{
	
	static url = "user/create"
	
	static at = {
		url == "user/create" &&
		title == "Create User"
	}
}