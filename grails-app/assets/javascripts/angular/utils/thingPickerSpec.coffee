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
			{name: "thing 1"},
			{name: "thing 2"},
			{name: "thing 3"},
			{name: "thing 4"},
			{name: "thing 5"},
			{name: "thing 6"},
			{name: "thing 7"},
			{name: "thing 8"},
			{name: "thing 9"},
			{name: "thing 10"},
			{name: "thing 11"},
			{name: "thing 12"},
		]

		this.$scope.selectedThings = [
			this.$scope.listOfEverything[0],
			this.$scope.listOfEverything[1],
			this.$scope.listOfEverything[3],
			this.$scope.listOfEverything[4],
		]

		this.$scope.tempSelectedThings = [
			this.$scope.listOfEverything[0],
			this.$scope.listOfEverything[1],
			this.$scope.listOfEverything[3],
			this.$scope.listOfEverything[4],
			this.$scope.listOfEverything[5],
			this.$scope.listOfEverything[8],
			this.$scope.listOfEverything[9]
		]

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

	describe 'cancel', ->
		it 'should reset the tempSelectedThings, and not modify the selectedThings.', ->
			this.$scope.addMode = true
			this.$scope.cancel()
			expect(this.$scope.selectedThings.length).toBe 4
			expect(this.$scope.addMode).toBe(false)

	describe 'setAddMode', ->
		it 'should uncompress the widget', ->
			this.$scope.compress = true
			this.$scope.setAddMode()
			expect(this.$scope.compress).toBe false

		it 'should update addMode', ->
			this.$scope.addMode = false
			this.$scope.setAddMode()
			expect(this.$scope.addMode).toBe true

		it "shouldn't do anything if we're already uncompressed and in add mode", ->
			this.$scope.setAddMode()
			expect(this.$scope.addMode).toBe true
			expect(this.$scope.compress).toBe false
			this.$scope.setAddMode()
			expect(this.$scope.addMode).toBe true
			expect(this.$scope.compress).toBe false