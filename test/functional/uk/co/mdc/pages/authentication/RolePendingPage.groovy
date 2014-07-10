package uk.co.mdc.pages.authentication

import geb.Page

class RolePendingPage extends Page{
	
		static url = "role/pendingUsers"

	static at = {
		url == "role/pendingUsers" &&
				title == "Activate pending users"
	}

}