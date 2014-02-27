package uk.co.mdc.pathways

import grails.converters.JSON
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.testing.GrailsMockErrors
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by rb on 26/02/2014.
 */
@TestFor(PathwayController)
@grails.test.mixin.Mock(Pathway)
class PathwayControllerSpec extends Specification {

	static VALIDATION_EXCEPTION = new Exception("Something failed to validate")

	PathwayService pathwayService

	def setup(){
		pathwayService = Mock(PathwayService)
		controller.pathwayService = pathwayService
	}

	def "defaultAction is list"(){
		expect:
		controller.defaultAction == "list"
	}

	def "show"(){
		//TODO
		// 404 jsonmodel [ success: false, msg: [ code: 404, text: "The item could not be found"]]
		// 404 html: [status: 404, view: 'error404']
		// success [pathway: pathway]
	}

	@Unroll
	def "createResource (validates: #validates)"(){

		given:
		def pathway
		Exception exceptionThrown = null
		def mockPathway = new Pathway()

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

	@Unroll
	def "list called with params: #parameters"(){

		given:
		def expectedPathways = [ new Pathway(), new Pathway() ]

		when: "I call list()"
		def result = controller.listAllResources(parameters)

		then: "The list parameters are passed to the service"
		1 * pathwayService.topLevelPathways(parameters) >> { expectedPathways }

		and: "The service contents are passed back"
		result == expectedPathways

		where:
		parameters << [
				[name: "pathway 1"],
				[name: "pathway 1", description: "some description"],
				[name: "pathway 1", max: 100],
				[name: "pathway 1", max: 100, offset: 200]
		]
	}

	@Unroll
	def "delete pathway #pathway, #outcome"(){

		given:
		Integer serviceCalled = 0
		// we need to mock message(..) so we don't need actual keys
		controller.metaClass.message = { LinkedHashMap args -> return "${args.code}" }
		def expectedResponse = [
		        success: [success: true, details: controller.message(code: 'default.deleted.message', args: [controller.message(code: 'pathway.label', default: 'Pathway'), 100])],
				exception: [errors: true, details: controller.message(code: 'default.not.deleted.message', args: [controller.message(code: 'pathway.label', default: 'Pathway'), 100])],
				nullModel: [errors: true, details: controller.message(code: 'default.not.found.message', args: [controller.message(code: 'pathway.label', default: 'Pathway'), 100])]
		]

		when:
		params.id = 100
		controller.delete(pathway)

		then:
		pathwayService.delete(pathway) >> {
			serviceCalled++
			if(outcome == 'exception') throw new DataIntegrityViolationException("Some error")
		}
		controller.modelAndView.model.booleanInstanceMap == expectedResponse[outcome]
		serviceCallCount == serviceCalled

		where:
		pathway 						| outcome     || serviceCallCount
		null 							| 'nullModel' || 0
		new Pathway(name: "pw1").save()	| 'success'   || 1
		new Pathway(name: "pw2").save()	| 'exception' || 1
	}

	def "update with nonexistant pathway ID"(){

		when:
		params.id = 100
		controller.update()

		then:
		0 * pathwayService.update(_, _, _)
		response.status == 404
		response.text == ""
	}

	@Unroll
	def "update with valid pathway ID, errors: #hasErrors"(){

		setup:
		persistedPathway.save()
		paramsPathway.id = persistedPathway.id
		assert Pathway.get(paramsPathway.id)
		def responseMap = [pathway: persistedPathway, idMappings: idMappings ]

		when:
		request.JSON = paramsPathway as JSON
		params << paramsPathway
		controller.update()

		then:
		1 * pathwayService.update(_, paramsPathway, _) >> { pw, params, mappings ->
			if(hasErrors){
				persistedPathway.name = null
				persistedPathway.validate()
				responseMap << [hasErrors: hasErrors, errors: persistedPathway.errors]
			}else{
				mappings << idMappings
			}
			persistedPathway
		}

		controller.modelAndView.model.pathwayInstanceMap == responseMap

		where:
		[persistedPathway, paramsPathway, idMappings, hasErrors] << [
				[ new Pathway(name: "some pathway"), [:], [somekey: "somenewkey"], false],
				[ new Pathway(name: "some pathway"), [:], [:], true]

		]
	}

}
