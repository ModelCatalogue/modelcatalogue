'use strict';

describe 'ThingPickerCtrl', ->

	beforeEach module('utils.thingPicker')
	beforeEach inject ($rootScope, $controller) ->
		this.$scope = $rootScope.$new()
		this.createController = (scope) ->
			return $controller 'ThingPickerCtrl', {
				'$scope': scope
			}

		this.$scope.allThings = [
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
			{name: "thing 12",id:12}
		]

		this.controller = this.createController(this.$scope)

		this.$scope.selectedThings = [
			this.$scope.allThings[0],
			this.$scope.allThings[1],
			this.$scope.allThings[3],
			this.$scope.allThings[4]
		]

		this.$scope.tempSelectedThings= [
			this.$scope.allThings[0],
			this.$scope.allThings[1],
			this.$scope.allThings[2],
			this.$scope.allThings[6],
			this.$scope.allThings[7],
			this.$scope.allThings[10],
			this.$scope.allThings[11]
		]

	describe 'isSelected', ->
		it 'should return true/false if an item with the same ID is/isnt present in the list', ->
			expectations = [
				[true, this.$scope.allThings[0]],
				[false, this.$scope.allThings[5]],
				[true, {name: "thing 1", id: 1}],
				[false, {name: "thing 1", id: 100}],
			]
			for expectation in expectations
				result = this.$scope.isSelected(expectation[1])
				expect(result).toBe(expectation[0])

	describe 'toggleSelection', ->
		it 'should add the thing if it isnt in the list', ->
			thing = this.$scope.allThings[5]
			expect(this.$scope.isSelected(thing)).toBe(false)
			this.$scope.toggleSelection(thing)
			expect(this.$scope.isSelected(thing)).toBe(true)

		it 'should remove the thing if it is in the list', ->
			thing = this.$scope.allThings[0]
			expect(this.$scope.isSelected(thing)).toBe(true)
			this.$scope.toggleSelection(thing)
			expect(this.$scope.isSelected(thing)).toBe(false)

		it 'should remove the thing if something with the same ID is in the list', ->
			thing = {name: "thing 1", id: 1}
			expect(this.$scope.isSelected(thing)).toBe(true)
			this.$scope.toggleSelection(thing)
			expect(this.$scope.isSelected(thing)).toBe(false)

	describe 'removeThing', ->
		it "should only remove the specified element from the list", ->
			thingToRemove = this.$scope.allThings[0]
			this.$scope.removeThing thingToRemove
			expect(this.$scope.selectedThings.length).toBe 3
			expect(this.$scope.selectedThings).not.toContain thingToRemove

		it "should remove an element with the matching ID", ->
			thingToRemove =  {name: "thing 1", id: 1}
			this.$scope.removeThing thingToRemove
			expect(this.$scope.selectedThings.length).toBe 3
			expect(this.$scope.selectedThings).not.toContain thingToRemove
			expect(this.$scope.selectedThings).not.toContain this.$scope.allThings[0]

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

	describe 'setAddMode', ->
		it 'should make the temporarily selected list equal to the current selection', ->
			this.$scope.setAddMode()
			expect(this.$scope.tempSelectedThings).toEqual this.$scope.selectedThings