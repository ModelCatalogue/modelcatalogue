module = angular.module('utils.thingPicker', ["ngTable"])

module.controller('ThingPickerCtrl',['$scope', 'ngTableParams', ($scope, ngTableParams) ->

#	$scope.allThings = new Array()
#	$scope.selectedThings = new Array()

	$scope.addMode  = false
	$scope.compress = false
	$scope.tempSelectedThings = new Array()

	$scope.isSelected = (thing) ->
		return true for tempThing in $scope.tempSelectedThings when thing.id == tempThing.id
		return false

	$scope.toggleSelection = (thing) ->
		if($scope.isSelected(thing))
			notFound = true
			for tempThing, i in $scope.tempSelectedThings when notFound && thing.id == tempThing.id
				$scope.tempSelectedThings.splice(i, 1)
				notFound = false
		else
			$scope.tempSelectedThings.push(thing)


	$scope.removeThing = (thing) ->
		notFound = true
		for tempThing, i in $scope.selectedThings when notFound && thing.id == tempThing.id
			$scope.selectedThings.splice(i, 1)
			notFound = false


	$scope.confirm = ->
		# Replace the selectedThings without changing the reference
		$scope.selectedThings.length = 0
		Array.prototype.push.apply($scope.selectedThings,$scope.tempSelectedThings);
		clearTempThings()

	$scope.cancel = ->
		clearTempThings()

	$scope.setAddMode = ->
		# Replace the tempSelectedThings without changing the reference
		$scope.tempSelectedThings.length = 0
		Array.prototype.push.apply($scope.tempSelectedThings,$scope.selectedThings);

		$scope.addMode = true
		$scope.compress = false

	$scope.tableParams = new ngTableParams {
		page: 2,  # show first page
		count: 10 # count per page
	}, {
		total: $scope.allThings.length,
		getData: ($defer, params) ->
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