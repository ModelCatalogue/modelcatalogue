﻿
    var NodeModel = function () {
        var self = this;

        //TODO: better id generation
        self.id = undefined
        self.name = undefined
        self.version = undefined
        self.description = undefined;
        self.type = 'node' //'node' | 'pathway'
        self.x = undefined
        self.y = undefined
        self.subPathway = undefined;
        self.subPathwayId = undefined;
        self.subPathwayName = undefined;
        self.subNodes = [];
        self.inputs = [];
        self.outputs = [];
        self.forms = [];
        self.collections = [];
    	

        ko.track(self);

        self.setForms = function(JSONforms){
        	
        	//console.log(JSONforms)
        	$.each(JSONforms, function(index, formJSON){
        			
	        	var form = new FormModel()
	        	form.id = formJSON.id
	        	form.name = formJSON.name
	        	
	        	self.forms.push(form)
        	});
        	
        }
        
        self.getSubNodes = function(){
        	if(self.subPathwayId){
        		
        	$.when(pathwayService.getPathwayNodes(self.subPathwayId)).done(function (data) {
            	if(data.success===true){
            		console.log(data.nodes)
            		//reset subNodes
            		self.subNodes = [];
            		$.each(data.nodes, function(index, value){
            			var node = new NodeModel()
                		node.id = value.id
                		node.name = value.name
                		if(value.subModelId){
                			node.subPathwayId = value.subModelId;
                		}else{
                			node.subPathwayId = null;
                		}
                		self.subNodes.push(node);
            		})
            		console.log(self.subNodes)
            		}
            	});
        	}
        }
        
        self.addForm = function(form){
        	self.forms.push(form)
        	var jsonNodeToServer = pathwayService.createJsonNode(self)
        	//console.log(jsonNodeToServer)
        	$.when(pathwayService.updateNode(jsonNodeToServer)).done(function (data) {
            	if(data.success===true){
            		console.log('form added on server')
            		}
            	});
        }
        

        self.addDEDialog = function(){
        	console.log('add DE dialog')
        }
        
        self.addFormDialog = function(){
        	//console.log('addingForm');
        	//Initial action on page load
            $('#AddFormModal').modal({ show: true, keyboard: false, backdrop: 'static' });
            formDesignListDraggable();
            /* bind the droppable behaviour for the data elements in the collection basket
        	* This allows you to drag data elements out of the collection basket. This in bound
        	* to the whole page so that the user can drag a data element out of the collections cart 
        	* anywhere on the page to remove them
        	*/
        	
        	$("#formDesignCart").droppable({
                drop: function(event, ui) {
                	if(c.id){
                		console.log('test')
                		var form = new FormModel();
                		form.id = c.id
                		form.name = c.name
                		
        	            $(c.li).remove();
        	            $(c.helper).remove();
        	            //removeFromCollectionBasket(c.id)
        	            self.addForm(form)
                	}
                }
        	});	
        	
        	//on close delete binding
        };
        
        
        //create a subpathway
        //called from show.gsp
        //creates a subPathway in the Node
        //and adds pathway on the server
        
        self.createSubPathway = function(data, e) {
        	var bindingContext = ko.contextFor(e.target);
            var subPathway = new PathwayModel();
            subPathway.name = self.name;
            subPathway.parentNodeId = self.id
            
            $.when(pathwayService.savePathway(subPathway)).done(function (data) {
            	if(data.success===true){
            		
            		self.subPathwayId = data.pathwayId;
            		
            		}
            	});
            
            //root.pathwayModel = self.subPathway;
            //root.savePathway();
            
        };
        
        self.viewSubPathway = function(data, e) {
            var bindingContext = ko.contextFor(e.target);
            
             $.when(pathwayService.loadPathway(self.subPathwayId)).done(function (pathwayJSON) {
            		console.log('test')
            		
            		var containerPathway = bindingContext.$root.pathwayModel;
                    //containerPathway.subPathwayId = self.subPathwayId;
                    //self.subPathway.parentPathwayId = containerPathway.id;
                    
            		//console.log(pathwayJSON)
                    bindingContext.$root.containerPathway = containerPathway;
                    bindingContext.$root.loadPathway(pathwayJSON.pathwaysModelInstance);
                    
            	});
            
            
            
            
        
        };
        
    };
    
    
    
    
  //json marshaller(so we don't get cyclical problems)
    
    NodeModel.prototype.toJSON = function() {
        var copy = ko.toJS(this); //easy way to get a clean copy
        delete copy.inputs; //remove an extra property
        delete copy.outputs; //remove an extra property
        return copy; //return the copy to be serialized
    };
