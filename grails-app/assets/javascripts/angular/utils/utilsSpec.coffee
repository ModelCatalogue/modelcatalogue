'use strict';

describe 'Grails', ->
	setup = {}
	beforeEach module('utils')

	beforeEach inject (_$httpBackend_, $rootScope, Grails) ->
		setup.httpBackend = _$httpBackend_
		setup.scope = $rootScope.$new()
		setup.scope.grailsAppName = 'testapp'
		setup.grails = Grails

	afterEach ->
		setup.httpBackend.verifyNoOutstandingExpectation();
		setup.httpBackend.verifyNoOutstandingRequest();

	it 'should call base URL with controller', ->
		setup.httpBackend.expectGET('/testapp/pathways.json').respond()
		setup.scope.controller = 'pathways'
		setup.grails.getRestResource(setup.scope).get()
		setup.httpBackend.flush()

	it 'should call base URL with ID when ID is specified', ->
		setup.httpBackend.expectGET('/testapp/pathways/2.json').respond()
		setup.scope.controller = 'pathways'
		setup.scope.action = 'show'
		setup.scope.id = '2'
		setup.grails.getRestResource(setup.scope).get()
		setup.httpBackend.flush()

	it 'should use a PUT request for update', ->
		setup.httpBackend.expectPUT('/testapp/pathways/2.json').respond()
		setup.scope.controller = 'pathways'
		setup.scope.action = 'update'
		setup.scope.id = '2'
		setup.grails.getRestResource(setup.scope).update("somepayload")
		setup.httpBackend.flush()

	it 'should call url with controller, action, and id', ->
		setup.httpBackend.expectGET('/testapp/grailsControllerName/grailsActionName/grailsId.json').respond()
		setup.scope.grailsAppName = 'testapp'
		setup.scope.controller = 'grailsControllerName'
		setup.scope.action = 'grailsActionName'
		setup.scope.id = 'grailsId'
		setup.grails.getResource(setup.scope).get()
		setup.httpBackend.flush()

	it 'should call url with only controller and action', ->
		setup.httpBackend.expectGET('/testapp/grailsControllerName/grailsActionName.json').respond()
		setup.scope.grailsAppName = 'testapp'
		setup.scope.controller = 'grailsControllerName'
		setup.scope.action = 'grailsActionName'
		setup.grails.getResource(setup.scope).get()
		setup.httpBackend.flush()

	it 'should call url with only controller and specified action', ->
		setup.httpBackend.expectGET('/testapp/grailsControllerName/alternateGrailsActionName.json').respond()
		setup.scope.grailsAppName = 'testapp'
		setup.scope.controller = 'grailsControllerName'
		setup.scope.action = 'grailsActionName'
		setup.grails.getResource(setup.scope).get {action: 'alternateGrailsActionName'}
		setup.httpBackend.flush()

	it 'should call url without controller, action, or id', ->
		setup.scope.grailsAppName = 'testapp'
		setup.httpBackend.expectGET('/testapp//.json').respond()
		setup.grails.getResource(setup.scope).get()
		setup.httpBackend.flush()
