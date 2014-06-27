package uk.co.mdc.authentication

import grails.plugins.springsecurity.SpringSecurityService
import grails.plugins.springsecurity.ui.SpringSecurityUiService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Specification
import uk.co.mdc.SecAuth
import uk.co.mdc.SecUser
import uk.co.mdc.SecUserSecAuth
import uk.co.mdc.RegisterController
import uk.co.mdc.ResetPasswordCommand

/**
 * Created by soheil on 28/05/2014.
 */
@TestFor(RegisterController)
@Mock([SecUser,SecAuth,SecUserSecAuth])
class RegisterControllerSpec extends  Specification{



	def setup(){
		controller.springSecurityService = Mock(SpringSecurityService)
		controller.springSecurityUiService = Mock(SpringSecurityUiService)
 	}


	def "ChangePassword will return to its main page, if it receives a non-POST request"(){

		when:""
		def command = new ResetPasswordCommand(password:"123user@newpass",password2:"123user@newpass")
		request.method = 'GET'
 		def result = controller.changePassword(command)

		then:""
		1 * controller.springSecurityService.getCurrentUser() >> {return new SecUser(username: "ruser1");}
		result.command
		result.command.password  == null
		result.command.password2 == null

	}

	def "ChangePassword will reject if user is not logged in"(){

		when:"ChangePassword is called before getting logged in"
		def command = new ResetPasswordCommand(password:"123user@newpass",password2:"123user@newpass")
		request.method = 'POST'
		controller.changePassword(command)

		then:"It will redirect user to the main page"
		1 * controller.springSecurityService.getCurrentUser() >> {return null}
		response.redirectedUrl == SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
	}


	def "ChangePassword will validate user new passwords"(){

		when:"User provides invalid passwords"
		def command = new ResetPasswordCommand(password:"123",password2:"123")
		request.method = 'POST'
		def result = controller.changePassword(command)


		then:"ChangePassword returns validation errors"
		1 * controller.springSecurityService.getCurrentUser() >> {return new SecUser(username: "ruser1");}
		result.command
		result.command.errors != null
	}


	def "ChangePassword will change users password"(){

		when:""
		def command = new ResetPasswordCommand(password:"123user@newpass",password2:"123user@newpass")
		request.method = 'POST'
		def result = controller.changePassword(command)


		then:""
		1 * controller.springSecurityService.getCurrentUser() >> {return new SecUser(username: "ruser1");}
		1 * controller.springSecurityUiService.encodePassword(_,_) >> { return "123user@newpass--SALT--";}
		1 * controller.springSecurityService.reauthenticate(_)

		flash.success == "Your password has been successfully updated."
		result.command
	}



}
