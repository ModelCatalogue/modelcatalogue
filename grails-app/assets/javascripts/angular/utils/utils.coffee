#= require angular/utils/thingPicker.js

utils = angular.module('utils', ['ngResource', 'utils.thingPicker'])

# FIXME refactor everything below this into separate modules and include above
#
# The Grails resource, used for all interaction with the Grails environment.
#
utils.service 'Grails', ($resource) ->
	# private REST resource URL resolver
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