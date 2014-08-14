$(function() {

    $('a[rel=tooltip]').tooltip();
    $().tooltip();

    // make code pretty
    window.prettyPrint && prettyPrint();

    $('.minimize-box').on('click', function(e){
        e.preventDefault();
        var $icon = $(this).children('i');
        if($icon.hasClass('icon-chevron-down')) {
            $icon.removeClass('icon-chevron-down').addClass('icon-chevron-up');
        } else if($icon.hasClass('icon-chevron-up')) {
            $icon.removeClass('icon-chevron-up').addClass('icon-chevron-down');
        }
    });
    $('.minimize-box').on('click', function(e){
        e.preventDefault();
        var $icon = $(this).children('i');
        if($icon.hasClass('icon-minus')) {
            $icon.removeClass('icon-minus').addClass('icon-plus');
        } else if($icon.hasClass('icon-plus')) {
            $icon.removeClass('icon-plus').addClass('icon-minus');
        }
    });

    $('.close-box').click(function() {
        $(this).closest('.box').hide('slow');
    });

    $('#changeSidebarPos').on('click', function(e) {
        $('body').toggleClass('hide-sidebar');
    });
});



$(function() {
	if (typeof jQuery !== 'undefined') {
		(function($) {
			$('#spinner').ajaxStart(function() {
				$(this).fadeIn();
			}).ajaxStop(function() {
				$(this).fadeOut();
			});
		})(jQuery);
	}
});


$('#createFormLink').click(
	    function() {
	    	createForm();
	    });


function createForm(){
	$('#createFormModal').modal({ show: true, keyboard: false, backdrop: 'static' });
    $(this).closest(".dropdown").removeClass("open");
}


$('#createFormSubmit')
.click(
    function() {
  	  $('#createFormForm').submit();
      return false;
    });

$('#createPathwaySubmit')
.click(
    function() {
  	  $('#createPathwayForm').submit();
      return false;
    });



/*--------------------------------------------------------
SHARED FUNCTIONS
---------------------------------------------------------*/

var c = {};


//global func tion that gets used by all the data table lists to render data table stuff

function getObjectTable(jsonArray, classname){
	
	var table
	
	if(jsonArray!=null && jsonArray.length>0){
		table = '<table>';
		
		$.each(jsonArray, function(key, value) {
			table = table + '<tr><td><a href="' + root +'/' + classname +  '/show/' + value.id +'">'+ value.name +'</a></td></tr>';
		});
		
		table = table + '</table>' 
		
		
	}else{
		
		table = null
		
	}
	
	return table;

}

function deleteItem(item){
	
	$( "#dialog-confirm .modal-header h4" ).text('Delete ' + item + '?');
	$( "#dialog-confirm" ).modal({ show: true, keyboard: false, backdrop: 'static' });
	$( "#deleteModalButton" ).bind('click', function(){
   			
    		$('#deleteForm').submit();
    		$( "#deleteModalButton" ).unbind();
   			$('.modal').modal('hide');
	
	});
	
	
}

function updateForm(){
	 
	$('#updateForm').submit();

}

function saveCreate(){
	$('#createForm').submit();
}

function formMap(){
		$('#add').button().click(function(event){
				event.preventDefault();
				$('.mapTable tbody').append('<tr><td><input type="text" name="map_key" /></td><td><input type="text" name="map_value" /></td></tr>');
			});
			
			$('#remove').button().click(function(event){
				event.preventDefault();
				if( $('.mapTable tr').size()<=2) {
				    alert('cannot delete last row');
				}
				else {
				    $('.mapTable tr:last').remove();
				}
			});
}


/*--------------------------------------------------------
END SHARED FUNCTIONS
---------------------------------------------------------*/





/*--------------------------------------------------------
END COLLECTION CART SCRIPTS
---------------------------------------------------------*/



/*--------------------------------------------------------
 BEGIN PROGRESS.HTML SCRIPTS
 ---------------------------------------------------------*/

function progRess() {
    /* required bootstrap-progressbar.min.js*/
    
        $('.progress .bar.text-no').progressbar();
        $('.progress .bar.text-filled').progressbar({
            display_text: 1
        });
        $('.progress .bar.text-centered').progressbar({
            display_text: 2
        });
}
/*--------------------------------------------------------
 END PROGRESS.HTML SCRIPTS
 ---------------------------------------------------------*/




/*--------------------------------------------------------
START CONCEPTUALDOMAIN LIST  SCRIPTS
---------------------------------------------------------*/

function conceptualDomainList(){
	
	//initialise datatable with custom hide/show columns
	//and server side processing
	//and draggable columns - so that the data elements within the table can be dragged onto the collection cart
	
	$('#conceptualDomainList').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-condensed table-hover table-striped" id="conceptualDomainTable"></table>' );
	oTable = $('#conceptualDomainTable').dataTable( {
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "dataTables",
        "sEmptyTable": "Loading data from server",
        "bAutoWidth": false,
        "aaSorting": [[ 1, "asc" ]],
		"aoColumns": [
			{ "mDataProp": "refId", "sTitle":"Ref ID", "sWidth":"10%"},
			{
				    // `data` refers to the data for the cell (defined by `mData`, which
				    // defaults to the column being worked with, in this case is the first
				    // Using `row[0]` is equivalent.
				"mRender": function ( data, type, row ) {		
					return '<a id="'+ row.id + '" href="' + root +'/conceptualDomain/show/' + row.id + '">' + data + '</a>'
			    },
			    "mDataProp": "name",
			    "sWidth":"30%",
			    "sTitle":"name"
			},
			{
			    // `data` refers to the data for the cell (defined by `mData`, which
			    // defaults to the column being worked with, in this case is the first
			    // Using `row[0]` is equivalent.
			"mRender": function ( data, type, row ) {		
				return data + '<img class="floatright" src="../images/details_open.png" />'
		    },
		   "mDataProp": "description", 
		   "sWidth":"60%",
		   "sTitle":"Description"
			}
		],
		"fnDrawCallback": function () {
			//bind the click handler to the + image within the datatable to show information that is too long for data columns i.e. description/definition
			
			
			$('#conceptualDomainTable tbody td img').on( 'click', function () {
				var nTr = $(this).parents('tr')[0];
				if ( oTable.fnIsOpen(nTr) )
				{
				/* This row is already open - close it */
				this.src = "../images/details_open.png";
				oTable.fnClose( nTr );
				}
				else
				{
				/* Open this row */
				this.src = "../images/details_close.png";
				oTable.fnOpen( nTr, formatConceptualDomainDetails(nTr), 'details' );
				}
			} );
			
		}
	} );	
	
	oTable.fnSetFilteringDelay(1000);
	
}

function formatConceptualDomainDetails ( nTr )
{
	var aData = oTable.fnGetData( nTr );
	var valueDomains = getObjectTable(aData.valueDomains, "valueDomain");

	
	var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
	
	if(valueDomains!=null){sOut += '<tr><td class="labelCol">Value Domains: </td><td>' + valueDomains + '</td></tr>'}

	sOut += '</table>';
	 
	return sOut;
}


/*--------------------------------------------------------
END CONCEPTUALDOMAIN LIST  SCRIPTS
---------------------------------------------------------*/







/*--------------------------------------------------------
START DOCUMENT LIST  SCRIPTS
---------------------------------------------------------*/

function documentList(){
	
	$('#documentList').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-condensed table-hover table-striped" id="documentTable"></table>' );
	oTable = $('#documentTable').dataTable( {
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "dataTables",
        "sEmptyTable": "Loading data from server",
        "bAutoWidth": false,
        "aaSorting": [[ 1, "asc" ]],
		"aoColumns": [
		    { "mDataProp": "refId", "sTitle":"Ref ID", "sWidth":"10%"},
			{
				    // `data` refers to the data for the cell (defined by `mData`, which
				    // defaults to the column being worked with, in this case is the first
				    // Using `row[0]` is equivalent.
				"mRender": function ( data, type, row ) {		
					return '<a id="'+ row.id + '" href="' + root +'/document/show/' + row.id + '">' + data + '</a>'
			    },
			    "mDataProp": "name",
			    "sWidth":"15%",
			    "sTitle":"name"
			},
			{ "mDataProp": "description", "sTitle":"Description", "sWidth":"50%"},
			{
			    // `data` refers to the data for the cell (defined by `mData`, which
			    // defaults to the column being worked with, in this case is the first
			    // Using `row[0]` is equivalent.
			"mRender": function ( data, type, row ) {		
				return '<a id="'+ row.id + '" href="' + root +'/document/download/' + row.id + '">download ' + data + '</a>'
			    },
			    "mDataProp": "fileName",
			    "sWidth":"15%",
			    "sTitle":"content"
			},
			{ "mDataProp": "contentType", "sTitle":"File Type", "sWidth":"10%"},
		]
	} );	
	

	oTable.fnSetFilteringDelay(1000);

}


/*--------------------------------------------------------
END DOCUMENT LIST  SCRIPTS
---------------------------------------------------------*/

/*--------------------------------------------------------
START EXTERNAL SYNONYM LIST  SCRIPTS
---------------------------------------------------------*/

function externalReferenceList(){
	
	$('#externalReferenceList').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-condensed table-hover table-striped" id="externalReferenceTable"></table>' );
	oTable = $('#externalReferenceTable').dataTable( {
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "dataTables",
        "sEmptyTable": "Loading data from server",
        "bAutoWidth": false,
        "aaSorting": [[ 1, "asc" ]],
		"aoColumns": [
			{
				    // `data` refers to the data for the cell (defined by `mData`, which
				    // defaults to the column being worked with, in this case is the first
				    // Using `row[0]` is equivalent.
				"mRender": function ( data, type, row ) {		
					return '<a id="'+ row.id + '" href="' + root +'/externalReference/show/' + row.id + '">' + data + '</a>'
			    },
			    "mDataProp": "name",
			    "sWidth":"50%",
			    "sTitle":"name"
			},
			{
			    // `data` refers to the data for the cell (defined by `mData`, which
			    // defaults to the column being worked with, in this case is the first
			    // Using `row[0]` is equivalent.
			    "mRender": function ( data, type, row ) {
			    	
							return data + '<img class="floatright" src="../images/details_open.png" />'

			    },
			    "mDataProp": "url", 
			    "sWidth":"50%",
			    "sTitle":"Url"
			}
		],
		"fnDrawCallback": function () {
			
			//bind the click handler to the + image within the datatable to show information that is too long for data columns i.e. description/definition


			$('#externalReferenceTable tbody td img').on( 'click', function () {
				var nTr = $(this).parents('tr')[0];
				if ( oTable.fnIsOpen(nTr) )
				{
				/* This row is already open - close it */
				this.src = "../images/details_open.png";
				oTable.fnClose( nTr );
				}
				else
				{
				/* Open this row */
				this.src = "../images/details_close.png";
				oTable.fnOpen( nTr, formatExtSynonymDetails(nTr), 'details' );
				}
			} );
		}
	} );	
	

	oTable.fnSetFilteringDelay(1000);



}

/* Formating function for row details - this is for the description and definition columns.....
* potentially need to add more info from the data elements class
*  */
function formatExtSynonymDetails ( nTr )
{
	var aData = oTable.fnGetData( nTr );
	var attributes = aData.attributes;
	
	var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
	
	$.each(attributes, function( key, value ) {
	sOut += '<tr><td>' + key + '</td><td>' + value + '</td></tr>';
	});
	
	sOut += '</table>';
	 
	return sOut;
}


/*--------------------------------------------------------
END EXTERNAL SYNONYM LIST  SCRIPTS
---------------------------------------------------------*/

/*--------------------------------------------------------
START EXTERNAL REFERENCE LIST  SCRIPTS
---------------------------------------------------------*/

function externalReferenceList(){
	
	$('#externalReferenceList').html( '<table cellpadding="0" cellspacing="0" border="0" class="table table-bordered table-condensed table-hover table-striped" id="externalReferenceTable"></table>' );
	oTable = $('#externalReferenceTable').dataTable( {
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "dataTables",
        "sEmptyTable": "Loading data from server",
        "bAutoWidth": false,
        "aaSorting": [[ 1, "asc" ]],
		"aoColumns": [
			{
				    // `data` refers to the data for the cell (defined by `mData`, which
				    // defaults to the column being worked with, in this case is the first
				    // Using `row[0]` is equivalent.
				"mRender": function ( data, type, row ) {		
					return '<a id="'+ row.id + '" href="' + root +'/externalReference/show/' + row.id + '">' + data + '</a>'
			    },
			    "mDataProp": "name",
			    "sWidth":"50%",
			    "sTitle":"name"
			},
			{
			    // `data` refers to the data for the cell (defined by `mData`, which
			    // defaults to the column being worked with, in this case is the first
			    // Using `row[0]` is equivalent.
			    "mRender": function ( data, type, row ) {
			    	
							return data + '<img class="floatright" src="../images/details_open.png" />'

			    },
			    "mDataProp": "url", 
			    "sWidth":"50%",
			    "sTitle":"Url"
			}
		], 
		"fnInitComplete": function() {
		      $('#externalReferenceTable tbody td img').on('click', function () {
		  		var nTr = $(this).parents('tr')[0];
		  		if ( oTable.fnIsOpen(nTr) )
		  		{
		  		/* This row is already open - close it */
		  		this.src = "../images/details_open.png";
		  		oTable.fnClose( nTr );
		  		}
		  		else
		  		{
		  		/* Open this row */
		  		this.src = "../images/details_close.png";
		  		oTable.fnOpen( nTr, formatExtSynonymDetails(nTr), 'details' );
		  		}
		  	} );
		}
	} );	
	
	oTable.fnSetFilteringDelay(1000);
	
 }

 
	/* Formating function for row details - this is for the description and definition columns.....
	* potentially need to add more info from the data elements class
	*  */
	function formatExtSynonymDetails ( nTr )
	{
		var aData = oTable.fnGetData( nTr );
		var attributes = aData.attributes;
		
		var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
		
		$.each(attributes, function( key, value ) {
		sOut += '<tr><td>' + key + '</td><td>' + value + '</td></tr>';
		});
		
		sOut += '</table>';
		 
		return sOut;
	}

/*--------------------------------------------------------
END EXTERNAL REFERENCE LIST  SCRIPTS
---------------------------------------------------------*/







/*--------------------------------------------------------
START CONCEPTUALDOMAIN  SCRIPTS
---------------------------------------------------------*/


function selectConceptualDomainStuff(selectedValueDomains){
	// create a deferred object
	  var r = $.Deferred();

	//change options to selected for valueDomains, subElements and external Reference
	if(selectedValueDomains.length==1){
			$('select[name="valueDomains"]').find('option[value="'+selectedValueDomains+'"]').attr("selected",true);
	}else if(selectedValueDomains.length>1){
		$.each(selectedValueDomains, function(index, item) {
				$('select[name="valueDomains"]').find('option[value="'+item+'"]').attr("selected",true);
		});
	}
		
	r.resolve();
	return r

}

function conceptualDomainDualListBox(){
	
	//set up dual list box for valueDomains, subElements and external Reference
	
	$('#valueDomains').bootstrapDualListbox({
	    nonselectedlistlabel: 'Available Value Domains',
	    selectedlistlabel: 'Associated Value Domains',
	    preserveselectiononmove: 'moved',
	    moveonselect: false
	});
}

function conceptualDomainForm(selectedValueDomains){
	
	//set up form selecting the data elements that have been included 
	//and when this is done set up the dual list boxes (otherwise it may miss some)
	selectConceptualDomainStuff(selectedValueDomains).done(conceptualDomainDualListBox())

}

/*--------------------------------------------------------
END EDIT CONCEPTUALDOMAIN  SCRIPTS
---------------------------------------------------------*/




/*--------------------------------------------------------
START collection  SCRIPTS
---------------------------------------------------------*/


 function selectCollectionDataElements(mandatoryDataElements, requiredDataElements, optionalDataElements, referenceDataElements){
	
	// create a deferred object
	  var r = $.Deferred();
	
	//change options to selected for DataElements, subElements and external Reference
	if(mandatoryDataElements.length==1){
		$('select[name="mandatoryDataElements"]').find('option[value="'+mandatoryDataElements+'"]').attr("selected",true);
	}else if(mandatoryDataElements.length>1){
		$.each(mandatoryDataElements, function(index, item) {
			$('select[name="mandatoryDataElements"]').find('option[value="'+item+'"]').attr("selected",true);
		});
	}
	
	//change options to selected for DataElements, subElements and external Reference
	if(requiredDataElements.length==1){
		$('select[name="requiredDataElements"]').find('option[value="'+requiredDataElements+'"]').attr("selected",true);
	}else if(requiredDataElements.length>1){
		$.each(requiredDataElements, function(index, item) {
			$('select[name="requiredDataElements"]').find('option[value="'+item+'"]').attr("selected",true);
		});
	}
	
	//change options to selected for DataElements, subElements and external Reference
	if(optionalDataElements.length==1){
		$('select[name="optionalDataElements"]').find('option[value="'+optionalDataElements+'"]').attr("selected",true);
	}else if(optionalDataElements.length>1){
		$.each(optionalDataElements, function(index, item) {
			$('select[name="optionalDataElements"]').find('option[value="'+item+'"]').attr("selected",true);
		});
	}

	
	//change options to selected for DataElements, subElements and external Reference
	if(referenceDataElements.length==1){
		$('select[name="referenceDataElements"]').find('option[value="'+referenceDataElements+'"]').attr("selected",true);
	}else if(referenceDataElements.length>1){
		$.each(referenceDataElements, function(index, item) {
			$('select[name="referenceDataElements"]').find('option[value="'+item+'"]').attr("selected",true);
		});
	}
	
	r.resolve();
	return r
	
}


/*--------------------------------------------------------
END EDIT collection  SCRIPTS
---------------------------------------------------------*/




