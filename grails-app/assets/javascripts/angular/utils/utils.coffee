angular.module('utils', ['ngResource'])

#
# The Grails resource, used for all interaction with the Grails environment.
#
.service 'Grails', ($resource) ->

		# private REST resource URL resolver
		getRestResource: (scope) ->
			$resource "/:grailsAppName/:controller/:id.json", {grailsAppName: scope.grailsAppName || '', controller: scope.controller || '', id: scope.id || ''}, {'update': { method: 'PUT'} }

		getResource: (scope) ->
			$resource "/:grailsAppName/:controller/:action/:id.json",
				{grailsAppName: scope.grailsAppName || '', controller: scope.controller || '', action: scope.action || '', id: scope.id || ''}


#
# A delete button which prompts the user to confirm their deletion.
# To use, simply pass the delete function to the "on-confirm" attribute
#
.directive 'confirmDelete', ->
		return {
			replace: true,
			templateUrl: 'templates/deleteConfirmation.html',
			scope: {
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


#
# A widget to select one or more things from a list of things
#
.directive 'mcThingPicker', ->
		return{
			replace: true,
			templateUrl: '/model_catalogue/assets/angular/utils/_thing-picker-widget.html',
			scope: {
				widgetName: '@'
				allThings: '='
				selectedThings: '='
			}
			controller: ($scope) ->
				$scope.addMode  = false
				$scope.compress = false
				$scope.tempSelectedThings = new Array()
				$scope.removeThing = (thing) ->
					index = $scope.selectedThings.indexOf(thing)
					if index > -1
						$scope.selectedThings.splice(index, 1)
				$scope.confirm = ->
					# Replace the selectedThings without changing the reference
					$scope.selectedThings.length = 0;
					Array.prototype.push.apply($scope.selectedThings, $scope.tempSelectedThings);
					# Clear everything out using the cancel
					clearTempThings()
				$scope.cancel = ->
					clearTempThings()

				clearTempThings = ->
					# Empty the tempSelectedThings
					$scope.tempSelectedThings.length = 0
					# Get out of addMode
					$scope.addMode = false
		}
