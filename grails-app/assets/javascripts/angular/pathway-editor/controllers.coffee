pathwayEditor = angular.module('pathway.controllers', ['pathway.services'])

.controller('PathwayEditorCtrl',
		['$scope', 'Grails', 'NodeService', 'PathwayPersistence','Currentpathway','ItemSelector','LinkService', ($scope, Grails, NodeService, PathwayPersistence,Currentpathway,ItemSelector,LinkService) ->
			$scope.controller = "pathways" # override because we want to use the RestController endpoints
			$scope.pathway = Grails.getRestResource($scope).get {action: 'show'}
			Currentpathway.setPathway($scope.pathway)


			#	Treeview uses this as this controller is its parent controller
			$scope.selectItem = ItemSelector.selectItem

			$scope.isItemSelected = ItemSelector.isItemSelected


			$scope.deleteNode = (node) ->
				currentPathway = Currentpathway.getPathway()
				NodeService.deleteNode(currentPathway, node)


			$scope.deleteLink = (link) ->
				currentPathway = Currentpathway.getPathway()
				LinkService.deleteLink(currentPathway,link)


			$scope.save = () ->
				grailsResponse = PathwayPersistence.save($scope, $scope.pathway)
				if grailsResponse.hasErrors
					for error in grailsResponse.errors
						console.log error
				else
					# node IDs may have been fixed, redraw
					jsPlumb.repaintEverything();

		])

.controller('NodePropertiesCtrl', ['$scope', 'ItemSelector','NodeService','Currentpathway','Grails', ($scope, ItemSelector,NodeService,Currentpathway,Grails) ->

		#$scope.allFormsResource = Grails.getRestAPIResource('forms')
		$scope.allDataElementsResource = Grails.getRestAPIResource('dataElement')

		$scope.selectedNode = null

		$scope.switchToSubPathway = ->
			console.log("FIXME: this should switch the pathway viewer's scope to node " + $scope.selectedNode.id)

		$scope.deleteNode = ->
			currentPathway = Currentpathway.getPathway()
			NodeService.deleteNode(currentPathway, $scope.selectedNode)


		# Watch the ItemSelector function for changes. The second function actions a change, setting the
		# selectedNode scope variable to be the new value retrieved from the service
		$scope.$watch(->
			ItemSelector.getSelectedItem()
		, (selectedItem) ->
			if(selectedItem && selectedItem.type== 'node')
				$scope.selectedNode = selectedItem.item
		, false # Just check for object equality
		)
	])

.controller('LinkPropertiesCtrl',['$scope','ItemSelector','LinkService','Currentpathway',($scope,ItemSelector,LinkService,Currentpathway)->
		$scope.selectedLink = null

		$scope.deleteLink = ->
			currentPathway = Currentpathway.getPathway()
			LinkService.deleteLink(currentPathway,$scope.selectedLink)


		# Watch the ItemSelector function for changes. The second function actions a change, setting the
		# selectedLink scope variable to be the new value retrieved from the service
		$scope.$watch(->
			ItemSelector.getSelectedItem()
		, (selectedItem) ->
			if(selectedItem && selectedItem.type== 'link')
				$scope.selectedLink = selectedItem.item
		, false
		)

	])

.controller('GraphCanvasCtrl', ['$scope', 'NodeService','Currentpathway','ItemSelector','LinkService', ($scope, NodeService,Currentpathway,ItemSelector,LinkService) ->
		$scope.pathway = $scope.$parent.pathway

		$scope.selectItem = ItemSelector.selectItem
		$scope.unSelectItem = ItemSelector.unSelectItem
		$scope.isItemSelected = ItemSelector.isItemSelected


		$scope.deleteKeyPressed = (event, item) ->
			if event && event.keyCode == 46
				selectedItem = ItemSelector.getSelectedItem()
				if(selectedItem && selectedItem.type=='node')
					NodeService.deleteNode($scope.pathway, item)
				else if (selectedItem && selectedItem.type=='link')
					LinkService.deleteLink($scope.pathway, item)


		$scope.viewSubpathway = (node) ->
			jsPlumb.deleteEveryEndpoint(); # FIXME this should be handled in the directive. Controllers are for data model, directives are for DOM manipulation
			$scope.pathway = node
			Currentpathway.setPathway(node)



		$scope.$watch(->
			ItemSelector.getSelectedItem()
		, (selectedItem) ->
				if(selectedItem && selectedItem.type== 'node')
					newParent = getParentOfSelectedNode($scope.$parent.pathway, selectedItem.item)
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
			selectedItem = ItemSelector.getSelectedItem()

			if selectedItem && selectedItem.type=='node'
				nodeX = selectedItem.item.x
				nodeY = selectedItem.item.y + $('#node' + selectedItem.item.id).outerHeight() + 50
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
			ItemSelector.selectItem(newNode,'node')

		lastLocalId = 0
		nextLocalId = ->
			lastLocalId++
			return lastLocalId
	])

.controller('TreeViewCtrl', ['$scope', 'ItemSelector','Currentpathway','NodeService', ($scope, ItemSelector,Currentpathway,NodeService) ->
		$scope.rootPathway = $scope.pathway = $scope.$parent.pathway

		$scope.deleteKeyPressed = (event, node) ->
			if event && event.keyCode == 46
				currentPathway = Currentpathway.getPathway()
				NodeService.deleteNode(currentPathway, node)

		$scope.$watch(->
			ItemSelector.getSelectedItem()
		, (selectedItem) ->
			if(selectedItem && selectedItem.type== 'node')
				newParent = getParentOfSelectedNode($scope.$parent.pathway, selectedItem.item)
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

