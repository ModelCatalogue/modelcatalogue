#= require angular/utils/thingPicker.js

utils = angular.module('utils', ['ngResource', 'utils.thingPicker'])

# FIXME refactor everything below this into separate modules and include above
#
# The Grails resource, used for all interaction with the Grails environment.
#
utils.service 'Grails', ($resource) ->

	# This is the preferred method for retrieving resources
	#
	# 	controller: the API endpoint, following "api/", e.g. 'pathways'
	#	id: (optional) the ID of the resource to retrieve.
	getRestAPIResource: (controller, id) ->
		$resource "/:grailsAppName/api/modelCatalogue/core/:controller/:id", {grailsAppName: grailsAppName || '', controller: controller || '', id: id || ''}, { 'get': { method: 'GET', isArray: false }, 'update': { method: 'PUT'} }
		# if ID set -> GET, UPDATE, DELETE, all URL api/controller/id
		# else 		-> GET, POST, all URL api/controller

	getRestResource: (scope) ->
		isArray = !scope.id?
		$resource "/:grailsAppName/:controller/:id.json", {grailsAppName: scope.grailsAppName || '', controller: scope.controller || '', id: scope.id || ''}, { 'get': { method: 'GET', isArray: isArray, }, 'update': { method: 'PUT'} }
	getResource: (scope) ->
		$resource "/:grailsAppName/:controller/:action/:id.json", {grailsAppName: scope.grailsAppName || '', controller: scope.controller || '', action: scope.action || '', id: scope.id || ''}

#
# A delete button which prompts the user to confirm their deletion.
# To use, simply pass the delete function to the "on-confirm" attribute
#
utils.directive 'confirmDelete', ->
	return {
		replace: true,
		templateUrl: 'templates/deleteConfirmation.html',
		scope:{
			onConfirm: '&'
		}
		controller: ($scope) ->
			$scope.isDeleting = false
			$scope.startDelete = ->
				$scope.isDeleting = true
			$scope.cancel = ->
				$scope.isDeleting = false
			$scope.confirm = ->
				$scope.onConfirm()
	}
