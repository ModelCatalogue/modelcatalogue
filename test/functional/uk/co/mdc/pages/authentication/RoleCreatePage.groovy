package uk.co.mdc.pages.authentication

import geb.Page

class RoleCreatePage extends Page{

	static url = "role/create"

	static at = {
		url == "role/create" &&
				title == "Create Role"
	}
}