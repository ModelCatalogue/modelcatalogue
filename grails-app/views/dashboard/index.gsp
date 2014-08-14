<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="layout" content="main"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Model Catalogue - Dashboard</title>




</head>
<body>
    <h2>Dashboard</h2>
  	<div id="dashboard">
	  	<div class="dashboard-page dashboard-page-enabled" id="dashboard-options">
			<div class="options-box">
				<div class="dashboard_option" id="pathways">
					<img src="../images/dashboard/Pathways_colour.png"/>
					<span>Pathway Models</span>
				</div>
			<div class="dashboard_option" id="forms">
				<img src="../images/dashboard/Forms_colour.png"/>
				<span>Form Models</span>
			</div>
            <!--
			<div class="dashboard_option" id="deployments">
				<img src="images/dashboard/Deployment_colour.png"/>
				<span>Deployment Models</span>
			</div>
			<div class="dashboard_option" id="projects">
				<img src="images/dashboard/Projects_colour.png"/>
				<span>Projects</span>
			</div>
			-->
			<div id="metadata">
				<img src="../images/dashboard/Advanced_colour.png"/>
				<span>Metadata Curation</span>
			</div>
            <!--
			<div class="dashboard_option" id="profile">
				<img src="images/dashboard/Profile_colour.png"/>
				<span>My Profile</span>
			</div>
			-->
            </div>
		</div>


	  	<div class="dashboard-page dashboard-page-disabled" id="dashboard-pathways">
	  		<div class="dashboard-wrapper">
		  		<h2>Pathway Models</h2>
		  		<p>You currently have ${draftPathways.size()} pathway(s) in a 'draft' state, and you have
					previously finalised ${finalizedPathways.size()} pathway model(s). Click a model below to edit or
					view, or start creating a new pathway model by clicking the button below...
				</p>

				<table class="table table-bordered">
					<thead>
		  				<tr>
		  					<th>'Draft' Pathway Models</th>
		  					<th>Finalised Pathway Models</th>
		  				</tr>
		  			</thead>
		  			<tbody>
		  				<tr>
		  					<td>
			  					<ul>
			  						<g:each var="pathway" in="${draftPathways}">
                                        <li><g:link controller="pathway" action="show" id="${pathway.id}">${pathway.name}</g:link> </li>
									</g:each>
			  					</ul>
			  				</td>
		  					<td>
			  					<ul>
			  						<g:each var="pathway" in="${finalizedPathways}">
                                        <li><g:link controller="pathway" action="show" id="${pathway.id}">${pathway.name}</g:link> </li>
									</g:each>
			  					</ul>
			  				</td>
		  				</tr>
		  			</tbody>
		  		</table>
	  			<button id="dashCreatePathway" class="btn btn-large btn-primary" data-toggle="modal" data-target="#createPathwayModal"><i class="fa fa-plus"></i>&nbsp;Create a new Pathway Model</button>

		  		<button class="btn btn-large btn-info"><i class="fa fa-long-arrow-left"></i></i>&nbsp;Return to the Dashboard</button>
	  		</div>
	  	</div>
	  	<div class="dashboard-page dashboard-page-disabled" id="dashboard-forms">
	  		<div class="dashboard-wrapper">
	  	 		<h2>Forms</h2>
		  		<p>You currently have ${draftForms.size()} form(s) in a 'draft' state, and you have
					previously finalised ${finalizedForms.size()} form model(s). Click a model below to edit or
					view, or start creating a new form model by clicking the button below...
				</p>

				<table class="table table-bordered">
					<thead>
		  				<tr>
		  					<th>'Draft' Form Models</th>
		  					<th>Finalised Form Models</th>
		  				</tr>
		  			</thead>
		  			<tbody>
		  				<tr>
		  					<td>
			  					<ul>
			  						<g:each var="form" in="${draftForms}">
                                        <li><g:link controller="formDesign" action="show" id="${form.id}">${form.name}</g:link> </li>
									</g:each>
			  					</ul>
			  				</td>
		  					<td>
			  					<ul>
			  						<g:each var="form" in="${finalizedForms}">
                                        <li><g:link controller="formDesign" action="show" id="${form.id}">${form.name}</g:link> </li>
									</g:each>
			  					</ul>
			  				</td>
		  				</tr>
		  			</tbody>
		  		</table>
                <button id="dashCreateForm" class="btn btn-large btn-primary" data-toggle="modal" data-target="createFormModal"><i class="fa fa-plus"></i></i>&nbsp;Create a new Form Model</button>
		  		<button class="dashboard-return btn btn-large btn-info"><i class="fa fa-long-arrow-left"></i>&nbsp;Return to the Dashboard</button>
	  		</div>
	  	</div>
        <div class="dashboard-page dashboard-page-disabled" id="dashboard-deployments">
	  		<div class="dashboard-wrapper">
		  		<h2>Deployment Models</h2>
		  		<br/><br/>
		  		<h3>Under construction!</h3>
		  		<p>This feature is not yet finished! In time, users will be able
					to model databases, schemas and tables, services and data-feeds,
					and physical locations. Please try again soon!</p>

		  		<button class="dashboard-return btn btn-default"><i class="fa fa-long-arrow-left"></i>&nbsp;Return to the Dashboard</button>
	  		</div>
	  	</div>
	  	<div class="dashboard-page dashboard-page-disabled" id="dashboard-projects">
	  		<div class="dashboard-wrapper">
		  		<h2>Projects</h2>
		  		<br/><br/>
		  		<h3>Under construction!</h3>
		  		<p>This feature is not yet finished! In time, this will be the
					place to come to organise collaborations, and keep track of
					user-created and automatically-generated artefacts. Please try
					again soon!</p>
		  		<button class="dashboard-return btn btn-default"><i class="fa fa-long-arrow-left"></i>&nbsp;Return to the Dashboard</button>
	  		</div>
	  	</div>
	  	<div class="dashboard-page dashboard-page-disabled" id="dashboard-metadata">
	  		<div class="dashboard-wrapper">
		  		<h2>Metadata Curation</h2>
                <h3>Under construction!</h3>
		  		<button class="dashboard-return btn btn-default"><<i class="fa fa-long-arrow-left"></i>&nbsp;Return to the Dashboard</button>
	  		</div>
	  	</div>
	  	<div class="dashboard-page dashboard-page-disabled" id="dashboard-profile">
	  		<div class="dashboard-wrapper">
		  		<h2>Profile</h2>
		  		<br/><br/>
		  		<h3>Under construction!</h3>
				<p>This feature is not yet finished! This will be the place
					where you change your user profile. Please try again soon!</p>
				<button class="dashboard-return btn btn-default"><i class="fa fa-long-arrow-left"></i>&nbsp;Return to the Dashboard</button>
	  		</div>
	  	</div>
	</div><!-- End div dashboard -->
    <asset:javascript src="jquery/dist/jquery.js"/>
    <asset:javascript src="jquery-ui/ui/jquery-ui.js"/>
    <asset:javascript src="main/dashboard.js"/>

</body>

</html>