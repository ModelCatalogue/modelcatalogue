module = angular.module('utils.thingPicker', ["ngTable"])

module.controller 'ThingPickerCtrl', ($scope) ->
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

	$scope.setAddMode = ->
		$scope.addMode = true
		$scope.compress = false
	clearTempThings = ->
		# Empty the tempSelectedThings
		$scope.tempSelectedThings.length = 0
		# Get out of addMode
		$scope.addMode = false
	return

#
# A widget to select one or more things from a list of things
#
module.directive 'mcThingPicker', ->
	return{
		replace: true,
		templateUrl: '/model_catalogue/assets/angular/utils/thingPicker.html',
		scope: {
			widgetName: '@'
			allThings: '='
			selectedThings: '='
		},
		controller: 'ThingPickerCtrl'
	}