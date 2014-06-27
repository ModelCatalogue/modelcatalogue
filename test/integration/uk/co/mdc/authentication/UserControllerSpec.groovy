package uk.co.mdc.authentication

 import grails.test.spock.IntegrationSpec
import uk.co.mdc.UserController

/**
 * Created by soheil on 09/06/2014.
 */
class UserControllerSpec extends  IntegrationSpec{


	def mailMessageBuilderFactory
	def grailsApplication
	def controller


	def setup(){
		//initialize the controller
		controller = new UserController()
		//inject grailsApplication, as it is not injected by default
		grailsApplication = new org.codehaus.groovy.grails.commons.DefaultGrailsApplication()
	}


	def "save will send Email to the new user"(){

		setup:"Mock send mail"
		def messageBuilder
		def callableParam

		//Mock sendMail method which is injected into Controller by Mail plugin
		controller.metaClass.sendMail = { Closure callable ->
			callableParam = callable
			def mailConfig =  grailsApplication.config.grails.mail
			messageBuilder = mailMessageBuilderFactory.createBuilder(mailConfig)
			callable.delegate = messageBuilder
			callable.resolveStrategy = Closure.DELEGATE_FIRST
			callable.call()
		}

		when:"save method is called"
		controller.params.username = "testUser"
		controller.params.password = "passw65%9&^hywo"
		controller.params.email = "test@test.com"
		controller.params.firstName = "testFirstName"
		controller.params.lastName  = "testLastName"
		controller.params.enabled = true
		controller.params.ROLE_READONLY_ADMIN = "on"
		controller.params.enabled = true
		controller.save()

		then:"a html mail content should be sent"
		messageBuilder != null
		messageBuilder.htmlContent != null
	}


	def "Sent Email to the new user, contains his/her details"(){

		setup:"Mock send mail"
		def messageBuilder
		def callableParam

		//Mock sendMail method which is injected into Controller by Mail plugin
		controller.metaClass.sendMail = { Closure callable ->
			callableParam = callable
			def mailConfig =  grailsApplication.config.grails.mail
			messageBuilder = mailMessageBuilderFactory.createBuilder(mailConfig)
			callable.delegate = messageBuilder
			callable.resolveStrategy = Closure.DELEGATE_FIRST
			callable.call()
		}

		when:"save method is called"
		controller.params.username = "testUser"
		controller.params.password = "passw65%9&^hywo"
		controller.params.email = "test@test.com"
		controller.params.firstName = "testFirstName"
		controller.params.lastName  = "testLastName"
		controller.params.enabled = true
		controller.params.ROLE_READONLY_ADMIN = "on"
		controller.params.enabled = true
		controller.save()

		then:"generated html mail content should contain user firstName, lastName and username"
		messageBuilder != null
		messageBuilder.htmlContent != null
		messageBuilder.htmlContent.contains(
			"Your account details are as follows:<br>\n"+
			"Username: ${controller.params.username}<br>\n"+
			"First name: ${controller.params.firstName}<br>\n"+
			"Last name: ${controller.params.lastName}<br>")
	}
}