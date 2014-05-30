#= require jsplumb/dist/js/jquery.jsPlumb-1.5.5.js
#= require angular/angular.js
#= require angular-resource/angular-resource.js
#= require angular-xeditable/dist/js/xeditable.js
#= require angular-ui-router/release/angular-ui-router.js
#= require ng-table/ng-table.js

#= require angular/utils/utils.js
#= require angular/pathway-editor/directives.js
#= require angular/pathway-editor/controllers.js
#= require angular/pathway-editor/pathway.services.js

@grailsAppName = 'model_catalogue'
@angularAppName = 'pathway-editor'
angular.module(angularAppName,
	['pathway.services', 'pathway.directives', 'pathway.controllers', "xeditable", "utils", 'ui.router'])

.run (editableOptions, editableThemes) ->
		editableThemes.bs3.inputClass = 'input-sm'
		editableThemes.bs3.buttonsClass = 'btn-sm'
		editableOptions.theme = 'bs3'# bootstrap3 theme. Can be also 'bs2', 'default'

.config ($stateProvider, $urlRouterProvider) ->
	$urlRouterProvider.otherwise "/"

	$stateProvider.state 'empty', {
		url: "/"
		templateUrl: '/' + grailsAppName + '/assets/angular/partials/pathway-editor/properties-empty.html'
	}
	$stateProvider.state 'node', {
		url: "/node/:nodeId",
		templateUrl: '/' + grailsAppName + '/assets/angular/partials/pathway-editor/properties-node.html'
		controller: 'NodePropertiesCtrl'
		onEnter: ($stateParams, ItemSelector, $state) ->
			item = ItemSelector.getSelectedItem()
			if !item || item.type!='node'
				$state.go('empty')
	}
	$stateProvider.state 'link', {
		url: "/link:linkId",
		templateUrl: '/' + grailsAppName + '/assets/angular/partials/pathway-editor/properties-link.html'
		controller: 'LinkPropertiesCtrl'
	}
