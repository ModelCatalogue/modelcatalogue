﻿
// setup some defaults for jsPlumb.	
			jsPlumb.importDefaults({
				Endpoint : [ "Dot", {
					radius : 2
				} ],
				HoverPaintStyle : {
					strokeStyle : "#1e8151",
					lineWidth : 2
				},
				ConnectionOverlays : [ [ "Arrow", {
					location : 1,
					id : "arrow",
					length : 14,
					foldback : 0.8
				} ],]
			});

// Usage: <div class="node" data-bind="makeNode: $data">....</div>
ko.bindingHandlers.makeNode = {
    init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
        var value = valueAccessor();
        
        //console.log('making load nodes')
        
        //Turn binded element into jsPlumb source node
        jsPlumb.makeSource($('.ep', element), {
            parent: $(element),
            connector: 'StateMachine',
            anchor: 'Continuous',
            connector : [ "StateMachine", {curviness : 20} ],
            connectorStyle : {
                    strokeStyle : "#5c96bc",
                    lineWidth : 2,
                    outlineColor : "transparent",
                    outlineWidth : 4
            }
        });

        //Turn binded element into jsPlumb target node
        jsPlumb.makeTarget($(element), {
            anchor: 'Continuous'
        });

        //Enable dragging of nodes
        jsPlumb.draggable($(element), {
            containment: "parent",
            stop: function( event, ui ) {
            	//node = ko.contextFor(element)
            	value.y = Math.round(ui.position.top) + "px"
            	value.x = Math.round(ui.position.left) + "px"
            }
        });
        
        $(element).bind('dblclick', function(){
        	$( "#dialog-confirm" ).text('Delete node?');
        	$( "#dialog-confirm" ).dialog({
   	   		 resizable: false,
   	   		 height:140,
   	   		 modal: true,
   	   		 title: 'delete node',
   	   		 buttons: {
   	   		 "Delete Node": function() {
   	   			$( this ).dialog( "close" );
   	   			nodeInfo = ko.dataFor(element)
   	   			////console.log(nodeInfo.id)
   	   			vm.deleteNode(nodeInfo.id)
   	   			jsPlumb.remove($(element))
   	   		 },
   	   		 Cancel: function() {
   	   			 $( this ).dialog( "close" );
   	   		 }
   	   		 }
   	   	 });
        	
        });
        
    },
    update: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
        
    	//console.log('testing update')
    	
    }
};




//Listening for connection event
jsPlumb.bind("connection", function (info) {
	//console.log('makeConnectionBinding')
	
	 var connectionId = null;
	 connectionId = info.connection.getParameter("connectionId", connectionId)
	 
	 //console.log(connectionId)
	 
	if(connectionId==null){

		//console.log('create with conn id')
		
	    var source = ko.dataFor(info.source); //Get the source node model instance            
	    var target = ko.dataFor(info.target); //Get the target node model instance
	
	    connectionId = 'connection_' + (new Date().getTime())
	   // //console.log(connectionId)
	    info.connection.setParameter("connectionId", connectionId)
	    vm.createLink(source, target, connectionId);
	
	}
	 
    //binding for connection double click
    info.connection.bind("dblclick", function() {
    	$( "#dialog-confirm" ).text('Delete connection?');
	   	$( "#dialog-confirm" ).dialog({
	   		 resizable: false,
	   		 height:140,
	   		 modal: true,
	   		 title: 'delete connection',
	   		 buttons: {
	   		 "Delete Connection": function() {
	   			$( this ).dialog( "close" );

	   			var params = info.connection.getParameters()
	   			
	   			vm.deleteLink(params.connectionId);
	   			jsPlumb.detach(info.connection);
	   			
	   		 },
	   		 Cancel: function() {
	   			 $( this ).dialog( "close" );
	   		 }
	   		 }
	   	 });
    	
    });
    
});
