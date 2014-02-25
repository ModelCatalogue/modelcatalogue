module = angular.module('utils.thingPicker', ["ngTable"])

module.controller('ThingPickerCtrl',['$scope', ($scope) ->
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
		for tempItem in $scope.tempSelectedThings
			if(tempItem.selected)
				delete tempItem.selected
				$scope.selectedThings.push(tempItem)
		clearTempThings()
		#Clear everything out using the cancel


	$scope.cancel = ->
		clearTempThings()

	$scope.setAddMode = ->
		$scope.addMode = true
		$scope.compress = false
		$scope.reRenderItems()


	$scope.reRenderItems = ->
		$scope.tempSelectedThings.length = 0
		Array.prototype.push.apply($scope.tempSelectedThings,$scope.allThings);
		for tempItem in $scope.tempSelectedThings
			tempItem.selected = false
			for selectedThing in $scope.selectedThings
				if (selectedThing.id == tempItem.id)
					tempItem.selected = true
		return



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
	templateUrl: '/model_catalogue/assets/angular/utils/thingPicker.html',
	scope: {
		widgetName: '@'
		allThings: '='
		selectedThings: '='
	},
	controller: 'ThingPickerCtrl',
	link:(scope,element,attr) ->
		scope.$watch('selectedThings',(newValue,oldValue)->
			scope.reRenderItems();
			return
		)
	}