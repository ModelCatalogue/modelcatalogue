package uk.co.mdc.pathways

import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by rb on 26/02/2014.
 */
@TestFor(PathwayController)
class PathwayControllerSpec extends Specification {

	static VALIDATION_EXCEPTION = new Exception("Something failed to validate")
	def "defaultAction is list"(){
		expect:
		controller.defaultAction == "list"
	}

	@Unroll
	def "createResouce (validates: #validates)"(){

		given:
		def pathway
		Exception exceptionThrown = null
		def mockPathway = new Pathway()
		def pathwayService = Mock(PathwayService)
		controller.pathwayService = pathwayService

		when: "createResource is called"
		try{
			pathway = controller.createResource(inputParams)
		}catch(Exception ve){
			exceptionThrown = ve
		}

		then: "the pathwayService.create() method is called with the parameters"
		1 * pathwayService.create(inputParams) >> {
			if(validates){ return mockPathway }
			else{ throw VALIDATION_EXCEPTION }
		}

		and: "an exception was bubbled up if the pathway didn't validate"
		validates || exceptionThrown == VALIDATION_EXCEPTION

		and: "the controller returns the new pathway (if an exception wasn't thrown)"
		!validates || pathway == mockPathway

		where:
		validates | inputParams
		true      | [ name: "Some name"]
		true      | [ name: "Some name", description: "a description"]
		false     | [ name: "Some name", description: "a description"]
	}



}
