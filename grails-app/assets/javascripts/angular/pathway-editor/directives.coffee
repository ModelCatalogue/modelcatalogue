###

# Directives for the pathway editor

###
module = angular.module('pathway.directives', [])


###
    @ngdoc directive
    @name mc-graph-container
    @element div

    @param pathway the pathway (or subpathway to bind to
    @param options jsPlumb options
    @param upALevel a function which returns true if the pathway has a parent which can be navigated to

    @description
    A canvas element to draw jsPLumb nodes and connections on.
###
module.directive('mcGraphContainer', ->
	defaultOptions = {
		Endpoint: [ 'Dot', { radius: 1 } ],
		Anchor: 'Continuous',
		Connector: 'Flowchart',
		ConnectorStyle: {
			strokeStyle: '#5c96bc',
			lineWidth: 2,
			outlineColor: 'transparent',
			outlineWidth: 4
		},
		ConnectionOverlays: [
			[
				'Arrow', {
					location: 1,
					id: 'arrow',
					length: 10,
					foldback: 1,
					width: 10
				 }
			]
		],
		HoverPaintStyle: {
			strokeStyle: '#1e8151',
			lineWidth: 2
		},
		PaintStyle: {
			strokeStyle: '#5c96bc',
			lineWidth: 2,
			outlineColor: 'transparent',
			outlineWidth: 4
		}
	}
	return {
	replace: false,
	scope: {
		pathway: '='
		options: '@',
		upALevel: '&',
	},
	controller: ($scope) ->
		$scope.hasParent = true
		nextId = 0
		$scope.getNextLinkId = ->
			nextId++
			return nextId

	#templateUrl: 'templates/pathway/jsPlumbCanvas.html',
	link: (scope, iElement, $compile) ->
		if scope.options
			jsPlumb.importDefaults(scope.options)
		else
			jsPlumb.importDefaults(defaultOptions)

		jsPlumb.unbind("dblclick");
		# Listen for link creation and add to the list of links

		#connectionId = info.connection.getParameter("connectionId", connectionId)
		jsPlumb.bind "connection", (info) ->
			sourceId = "#{info.source.id}".replace(/node/, "")
			targetId = "#{info.target.id}".replace(/node/, "")

			connectionId = info.connection.getParameter("connectionId", connectionId)
			if not connectionId
				console.log "evaluation"
				# We only allow one link to occur between two nodes
				exists = false
				for link in scope.pathway.links
					# Need to coerce the IDs to strings just in case they're numbers in the map
					if sourceId == "#{link.source}" and targetId == "#{link.target}"
						exists = true
						console.log 'source: ' + link.source + ', target: ' + link.target + ' already exists'
				if exists
					jsPlumb.detach(info.connection)
				else
					console.log "Adding link, source: " + sourceId + ', target: ' + targetId
					# TODO this should be extracted so the directive takes a function to add the link as a parameter
					scope.pathway.links.push {
						id: 'LOCALLINK' + scope.getNextLinkId(),
						source: sourceId,
						target: targetId
					}
			return
	}
) # end directive


###
    @ngdoc directive
    @name mc-graph-node
    @element div

    @param node the node in the model to bind to
    @param isSelected: a function which returns true if the node is currently selected
    @param selectNode: a function which is called if a node is clicked (selected), passing the node as a parameter
    @param dblClick: a function which is called if a node is double clicked, passing the node as a parameter

    @description
    A canvas element to draw jsPLumb nodes and connections on.
###
module.directive('mcGraphNode', ->
	return {
	replace: true,
	transclude: true,
	requires: '^mcGraphContainer', # Tie this directive to mcGraphContainer
	scope: {
		node: '=graphNode',
		isSelected: '&',
		selectNode: '&',
		dblClick: '&'
	},


	templateUrl: 'templates/pathway/jsPlumbNode.html',
	link: (scope, iElement, iAttrs) ->

		jsPlumb.makeSource($('.ep', iElement), {
			parent: iElement
		});

		jsPlumb.makeTarget(iElement);

		jsPlumb.draggable(iElement, {
			containment: 'parent',
			stop: (event, ui) ->
				scope.node.y = Math.floor(ui.position.top);
				scope.node.x = Math.floor(ui.position.left);

		});
		scope.$watch 'node.name', ->
			jsPlumb.repaintEverything()
	}
) # End of directive

#Handle the links
module.directive('mcGraphLink', ["$timeout", ($timeout) ->
	return {
	restrict: 'A',
	requires: '^graphContainer', #Tie this directive to graphContainer
	scope: {
		link: '=graphLink',
	},
	link: (scope, iElement, iAttrs) ->
		#FIXME: Needed the timeout to make sure the dom nodes are available. Need a better solution.
		$timeout(->
			jsPlumb.connect({
				source: "node" + scope.link.source,
				target: "node" + scope.link.target,
				parameters: {
					connectionId: scope.link.id
				}
			}).canvas.id = scope.link.id; # Give the resulting svg node an id for simpler retrieval
		, 1)
	}
])