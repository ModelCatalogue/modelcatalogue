package uk.co.mdc.pages.authentication

import geb.Page

/**
 * Created by soheil on 18/05/2014.
 */
class ReadOnlyUnAuthorizedPage extends Page{

	static at = {
		title == "Denied" &&
		$("body div.errors").text() == "Sorry, you're not authorized to view this page."
	}
}
