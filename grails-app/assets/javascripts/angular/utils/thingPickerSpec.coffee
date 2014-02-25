'use strict';

describe 'ThingPickerCtrl', ->

	beforeEach module('utils.thingPicker')
	beforeEach inject ($rootScope, $controller) ->
		this.$scope = $rootScope.$new()
		this.createController = (scope) ->
			return $controller 'ThingPickerCtrl', {
				'$scope': scope
			}
		this.controller = this.createController(this.$scope)

		this.$scope.listOfEverything = [
			{name: "thing 1",id:1},
			{name: "thing 2",id:2},
			{name: "thing 3",id:3},
			{name: "thing 4",id:4},
			{name: "thing 5",id:5},
			{name: "thing 6",id:6},
			{name: "thing 7",id:7},
			{name: "thing 8",id:8},
			{name: "thing 9",id:9},
			{name: "thing 10",id:10},
			{name: "thing 11",id:11},
			{name: "thing 12",id:12},
		]

		this.$scope.selectedThings = [
			this.$scope.listOfEverything[0],
			this.$scope.listOfEverything[1],
			this.$scope.listOfEverything[3],
			this.$scope.listOfEverything[4],
		]

		this.$scope.tempSelectedThings=[]
		Array.prototype.push.apply(this.$scope.tempSelectedThings,this.$scope.listOfEverything)
		this.$scope.tempSelectedThings[0].selected = true
		this.$scope.tempSelectedThings[1].selected = true
		this.$scope.tempSelectedThings[2].selected = true
		this.$scope.tempSelectedThings[3].selected = true
		this.$scope.tempSelectedThings[4].selected = true
		this.$scope.tempSelectedThings[5].selected = true
		this.$scope.tempSelectedThings[6].selected = true


	describe 'removeThing', ->
		it "should only remove the specified element from the list", ->
			thingToRemove = this.$scope.selectedThings[0]
			this.$scope.removeThing thingToRemove
			expect(this.$scope.selectedThings.length).toBe 3
			expect(this.$scope.selectedThings).not.toContain thingToRemove

	describe 'confirm', ->
		it 'should set the contents of selectedThings to be the same as tempSelectedThings', ->
			this.$scope.addMode = true
			this.$scope.confirm()
			expect(this.$scope.selectedThings.length).toBe 7

		it 'should return to non-add mode', ->
			this.$scope.addMode = true
			this.$scope.confirm()
			expect(this.$scope.addMode).toBe(false)


		it 'should make tempSelectedThing empty', ->
			this.$scope.addMode = true
			this.$scope.confirm()
			expect(this.$scope.tempSelectedThings.length).toBe 0


	describe 'cancel', ->
		it 'should reset the tempSelectedThings, and not modify the selectedThings.', ->
			this.$scope.addMode = true
			this.$scope.cancel()
			expect(this.$scope.selectedThings.length).toBe 4
			expect(this.$scope.addMode).toBe(false)


	describe 'reRenderItems', ->
		it 'should be called when setAddMode is called', ->
			spyOn(this.$scope, "reRenderItems");
			this.$scope.setAddMode()
			expect(this.$scope.reRenderItems).toHaveBeenCalled();


