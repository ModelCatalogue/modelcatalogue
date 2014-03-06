pathwayEditor = angular.module('pathway.controllers', ['pathway.services'])

.controller('PathwayEditorCtrl',
		['$scope', 'Grails', 'NodeSelector','LinkSelector', 'PathwayPersistence','Currentpathway', ($scope, Grails, NodeSelector,LinkSelector, PathwayPersistence,Currentpathway) ->
			$scope.controller = "pathways" # override because we want to use the RestController endpoints
			$scope.pathway = Grails.getRestResource($scope).get {action: 'show'}
			Currentpathway.setPathway($scope.pathway)

			$scope.selectNode = NodeSelector.selectNode
			$scope.isSelected = NodeSelector.isSelected
			$scope.selectedNode = NodeSelector.getSelectedNode

			$scope.deleteNode = (node) ->
				currentPathway = Currentpathway.getPathway()
				NodeSelector.deleteNode(currentPathway, node)


			$scope.deleteLink = (link) ->
				currentPathway = Currentpathway.getPathway()
				index = currentPathway.links.indexOf(link)
				if(index>-1)
					currentPathway.links.splice(index, 1);
					#select the connection based on it'd conenctionId
					connections = jsPlumb.getConnections()
					for con in connections
						if(con._jsPlumb.parameters.connectionId == link.id )
							jsPlumb.detach(con)
							LinkSelector.selectLink(null)
							return



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



.controller('LinkPropertiesCtrl',['$scope','LinkSelector',($scope,LinkSelector)->
		$scope.selectedLink = null

		$scope.deleteLink = ->
			$scope.$parent.deleteLink($scope.selectedLink)

		$scope.$watch(->
			LinkSelector.getSelectedLink()
		, (selectedLink) ->
			console.log('link clicked in LinkPropertiesCtrl')
			$scope.selectedLink = selectedLink
		, false
		)

	])

.controller('GraphCanvasCtrl', ['$scope', 'NodeSelector','LinkSelector','Currentpathway', ($scope, NodeSelector,LinkSelector,Currentpathway) ->
		$scope.pathway = $scope.$parent.pathway


		$scope.selectNode = NodeSelector.selectNode
		$scope.isSelected = NodeSelector.isSelected
		$scope.unSelectNode = NodeSelector.unSelectNode


		$scope.selectLink = LinkSelector.selectLink
		$scope.unSelectLink = LinkSelector.unSelectLink
		$scope.isLinkSelected = LinkSelector.isSelected




		$scope.deleteKeyPressed = (event, node) ->
			if event && event.keyCode == 46
				NodeSelector.deleteNode($scope.pathway, node)


		$scope.viewSubpathway = (node) ->
			jsPlumb.deleteEveryEndpoint(); # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
			$scope.pathway = node
			Currentpathway.setPathway(node)



		$scope.$watch(->
			NodeSelector.getSelectedNode()
		, (selectedNode) ->
			if(selectedNode)
				LinkSelector.unSelectLink() #unselect links, if any selected
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
				Currentpathway.setPathway(newParent)

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

.controller('TreeViewCtrl', ['$scope', 'NodeSelector','Currentpathway', ($scope, NodeSelector,Currentpathway) ->
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
				Currentpathway.setPathway(newParent)
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

