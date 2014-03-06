pathwayEditor = angular.module('pathway.controllers', ['pathway.services'])

.controller('PathwayEditorCtrl',
		['$scope', 'Grails', 'NodeSelector', 'PathwayPersistence', ($scope, Grails, NodeSelector, PathwayPersistence) ->
			$scope.controller = "pathways" # override because we want to use the RestController endpoints
			$scope.pathway = Grails.getRestResource($scope).get {action: 'show'}

			$scope.selectNode = NodeSelector.selectNode
			$scope.isSelected = NodeSelector.isSelected
			$scope.selectedNode = NodeSelector.getSelectedNode

			$scope.deleteNode = (node) ->
				NodeSelector.deleteNode($scope.pathway, node)

			$scope.save = () ->
				grailsResponse = PathwayPersistence.save($scope, $scope.pathway)
				if grailsResponse.hasErrors
					for error in grailsResponse.errors
						console.log error
				else
					# node IDs may have been fixed, redraw
					jsPlumb.repaintEverything();

		])
.controller('NodePropertiesCtrl', ['$scope', 'NodeSelector','Grails',($scope, NodeSelector,Grails) ->

		$scope.allFormsResource = Grails.getRestAPIResource('forms')
		$scope.allDataElementsResource = Grails.getRestAPIResource('dataelements')

		$scope.selectedNode = null
		$scope.switchToSubPathway = ->
			console.log("FIXME: this should switch the pathway viewer's scope to node " + $scope.selectedNode.id)

		$scope.deleteNode = ->
			debugger;
			# FIXME, change this so it doesn't reference parent
			$scope.$parent.deleteNode($scope.selectedNode)


		# Watch the NodeSelector function for changes. The second function actions a change, setting the
		# selectedNode scope variable to be the new value retrieved from the service
		$scope.$watch(->
			NodeSelector.getSelectedNode()
		, (selectedNode) ->
			$scope.selectedNode = selectedNode

		, false # Just check for object equality
		)
	])

.controller('GraphCanvasCtrl', ['$scope', 'NodeSelector', ($scope, NodeSelector) ->
		$scope.pathway = $scope.$parent.pathway

		$scope.selectNode = NodeSelector.selectNode
		$scope.isSelected = NodeSelector.isSelected
		$scope.unSelectNode = NodeSelector.unSelectNode

		$scope.deleteKeyPressed = (event, node) ->
			if event && event.keyCode == 46
				NodeSelector.deleteNode($scope.pathway, node)


		$scope.viewSubpathway = (node) ->
			jsPlumb.deleteEveryEndpoint(); # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
			$scope.pathway = node


		$scope.$watch(->
			NodeSelector.getSelectedNode()
		, (selectedNode) ->
			return if selectedNode is null
			newParent = getParentOfSelectedNode($scope.$parent.pathway, selectedNode)
			if (newParent isnt $scope.pathway)
				jsPlumb.deleteEveryEndpoint() # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
				$scope.pathway = newParent
		, false) # Just check for object equality


		$scope.levelsAbove = false

		$scope.$watch 'pathway', ->
			$scope.levelsAbove = ($scope.pathway != $scope.$parent.pathway)

		$scope.upALevel = ->
			newParent = getParentOfSelectedNode($scope.$parent.pathway, $scope.pathway)
			if newParent isnt $scope.pathway
				jsPlumb.deleteEveryEndpoint(); # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
				$scope.pathway = newParent

		getParentOfSelectedNode = (pathway, node)->
			if pathway is node
				return node
			if pathway and pathway.nodes.length > 0
				if pathway.nodes.indexOf(node) != -1
					return pathway
				for subPathway in pathway.nodes
					response = getParentOfSelectedNode(subPathway, node)
					if(response?)
						return response

		# TODO is it a good idea to use DOM IDs here? Probably not...
		$scope.addNode = ->
			nodeX = 0
			nodeY = 0
			selectedNode = NodeSelector.getSelectedNode()
			if selectedNode
				nodeX = selectedNode.x
				nodeY = selectedNode.y + $('#node' + selectedNode.id).outerHeight() + 50
			else
				# FIXME get rid of the JQuery selector here - DOM should occur in the directive...
				nodeX = $('.ui-layout-center').scrollLeft() + 50 + (Math.random() * 150)
				nodeY = $('.ui-layout-center').scrollTop() + 50 + (Math.random() * 150)

			newNode = {
				name: "New node"
				id: "LOCAL" + nextLocalId()
				pathway: $scope.pathway.id
				nodes: []
				links: []
				x: nodeX
				y: nodeY
			}
			$scope.pathway.nodes.push newNode
			NodeSelector.selectNode(newNode)

		lastLocalId = 0
		nextLocalId = ->
			lastLocalId++
			return lastLocalId
	])

.controller('TreeViewCtrl', ['$scope', 'NodeSelector', ($scope, NodeSelector) ->
		$scope.rootPathway = $scope.pathway = $scope.$parent.pathway

		# These are already in $parent scope
#		$scope.selectNode = NodeSelector.selectNode
#		$scope.isSelected = NodeSelector.isSelected
#		$scope.unSelectNode = NodeSelector.unSelectNode

		$scope.deleteKeyPressed = (event, node) ->
			if event && event.keyCode == 46
				NodeSelector.deleteNode($scope.pathway, node)

		$scope.$watch(->
			NodeSelector.getSelectedNode()
		, (selectedNode) ->
			return if selectedNode is null
			newParent = getParentOfSelectedNode($scope.$parent.pathway, selectedNode)
			if (newParent isnt $scope.pathway)
				$scope.pathway = newParent
		, false) # Just check for object equality

		getParentOfSelectedNode = (pathway, node)->
			if pathway is node
				return node
			if pathway and pathway.nodes.length > 0
				if pathway.nodes.indexOf(node) != -1
					return pathway
				for subPathway in pathway.nodes
					response = getParentOfSelectedNode(subPathway, node)
					if(response?)
						return response
	])

