package uk.co.mdc.pages.authentication

import geb.Page

class UserSearchPage extends Page{
	
	static url = "user/search"
	
	static at = {
		url == "user/search" &&
		title == "User Search"
	}
}