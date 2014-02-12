
#= require jsplumb/dist/js/jquery.jsPlumb-1.5.5.js
#= require angular/angular.js
#= require angular-resource/angular-resource.js
#= require angular-xeditable/dist/js/xeditable.js
#= require angular/utils/utils.js
#= require angular/pathway-editor/directives.js
#= require angular/pathway-editor/controllers.js
#= require angular/pathway-editor/pathway.services.js

@grailsAppName = 'model_catalogue'
@angularAppName = 'pathway-editor'
angular.module(angularAppName, ['pathway.services', 'pathway.directives', 'pathway.controllers', "xeditable", "utils"])

.run (editableOptions, editableThemes) ->
	editableThemes.bs3.inputClass = 'input-sm'
	editableThemes.bs3.buttonsClass = 'btn-sm'
	editableOptions.theme = 'bs3' # bootstrap3 theme. Can be also 'bs2', 'default'

