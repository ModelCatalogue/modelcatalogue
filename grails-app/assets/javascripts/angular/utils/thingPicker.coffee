module = angular.module('utils.thingPicker', ["ngTable"])

module.controller('ThingPickerCtrl',['$scope', 'ngTableParams', ($scope, ngTableParams) ->

	$scope.allThings = new Array()
	$scope.selectedThings = new Array()

	$scope.addMode  = false
	$scope.compress = false
	$scope.tempSelectedThings = new Array()


	$scope.toggleSelection = (thing) ->
		index = $scope.tempSelectedThings.indexOf(thing)
		if index > -1
			$scope.tempSelectedThings.splice(index, 1)
		else
			$scope.tempSelectedThings.push(thing)


	$scope.removeThing = (thing) ->
		index = $scope.selectedThings.indexOf(thing)
		if index > -1
			$scope.selectedThings.splice(index, 1)


	$scope.confirm = ->
		# Replace the selectedThings without changing the reference
		$scope.selectedThings.length = 0
		Array.prototype.push.apply($scope.selectedThings,$scope.tempSelectedThings);
		clearTempThings()

	$scope.cancel = ->
		clearTempThings()

	$scope.setAddMode = ->
		$scope.addMode = true
		$scope.compress = false

	$scope.tableParams = new ngTableParams {
		page: 2,  # show first page
		count: 10 # count per page
	}, {
		total: $scope.allThings.length,
		getData: ($defer, params) ->
			console.log $scope.allThings.length
			$defer.resolve($scope.allThings.slice((params.page() - 1) * params.count(), params.page() * params.count()))
	}

	clearTempThings = ->
		# Empty the tempSelectedThings
		$scope.tempSelectedThings.length = 0
		# Get out of addMode
		$scope.addMode = false

	return
])

#
# A widget to select one or more things from a list of things
#
module.directive 'mcThingPicker', ->
	return{
		replace: true,
		templateUrl: '/'+grailsAppName+'/assets/angular/utils/thingPicker.html',
		scope: {
			widgetName: '@'
			allThings: '='
			selectedThings: '='
		},
		controller: 'ThingPickerCtrl'
	}