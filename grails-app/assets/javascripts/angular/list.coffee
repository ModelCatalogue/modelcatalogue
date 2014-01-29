# Doesn't have any required modules because they're loaded by the pathways app. We should be loading
# angular/angular.js
# angular-resource/angular-resource.js
# angular-xeditable/dist/js/xeditable.js
# angular/utils.js

@grailsAppName = 'model_catalogue'
angular.module('list', ["xeditable", "utils"])

.controller('ListCtrl', ['$scope', 'Grails', ($scope, Grails) ->
        $scope.list = Grails.getResource($scope).get {action: 'list'}

        $scope.delete = (item) ->
            Grails.getResource($scope).delete {action: 'delete', id: item.id}
            index = $scope.list.items.indexOf(item)
            $scope.list.items.splice(index, 1)
            #Grails.getResource($scope).get {action: 'show'}
    ])

#.run (editableOptions) ->
#        editableOptions.theme = 'bs3';# bootstrap3 theme. Can be also 'bs2', 'default'