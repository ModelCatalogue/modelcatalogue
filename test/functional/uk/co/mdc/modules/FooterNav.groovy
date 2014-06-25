package uk.co.mdc.modules

import geb.Module

/**
 * Created by soheil on 18/05/2014.
 */
class FooterNav extends Module{

	static content = {
		feedbackLink(wait:true) {$("p.feedback#feedback")}
	}
}
