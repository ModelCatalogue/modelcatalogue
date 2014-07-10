package uk.co.mdc.pages.authentication

import geb.Page

class ChangePasswordPage extends Page{
	
	static url = "register/changePassword"
	
	static at = {
		url == "register/changePassword" &&
		title == "Change Password"
	}
	static content = {
		changePasswordForm 	{ $("form[name='resetPasswordForm']") }
		password 	{ changePasswordForm.find("input[name='password']") }
		password2 	{ changePasswordForm.find("input[name='password2']") }
		submitButton {$("a.btn#reset")	}
		successAlert {	$("div.alert-success")}
	}
}