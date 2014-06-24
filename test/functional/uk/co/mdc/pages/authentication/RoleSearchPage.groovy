package uk.co.mdc.pages.authentication

import geb.Page

class RoleSearchPage extends Page{
	
	static url = "role/search"
	
	static at = {
		url == "role/search" &&
		title == "Role Search"
	}
}