
<%@ page import="uk.co.mdc.pathways.Pathway" %>
<!DOCTYPE html>
<html>
	<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'pathway.label', default: 'Pathway')}" />
		<title>All pathways</title>
		<parameter name="name" value="Pathways" />
	</head>
	<body>
        <g:set var="grailsParams" value="${params.collect{ it.key + '=\'' + it.value + '\''}.join('; ')}" />
		<div class="box" ng-app="list" ng-init="${grailsParams}; grailsAppName='model_catalogue'">
			<div id="list-pathways" class="content scaffold-list" role="main" >
				<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
				</g:if>
				<div id="pathwayList" ng-controller="ListCtrl" class="ng-cloak">
                    <table>
                        <thead>
                        <th><g:message code="pathway.name" /></th>
                        <th><g:message code="pathway.description" /></th>
                        <th><g:message code="pathway.userVersion" /></th>
                        <th><g:message code="pathway.isDraft" /></th>
                        <th>actions</td>
                        </thead>
                        <tbody>
                            <tr ng-repeat="item in list">
                                <td><a href="/model_catalogue/pathways/{{item.id}}">{{item.name}}</a></td>
                                <td>{{item.description}}</td>
                                <td>{{item.userVersion}}</td>
                                <td>{{item.isDraft}}</td>
                                <td><div confirm-delete on-confirm="delete(item)"></div></td>
                            </tr>
                        </tbody>
                    </table>
				</div>
                <button class="btn btn-large btn-primary" data-toggle="modal" data-target="#createPathwayModal"><i class="fa fa-plus"></i>&nbsp;Create a new pathway</button>
			</div>
            <script type="text/ng-template" id="templates/deleteConfirmation.html">
                <div style="width: 300px;">
                    <div ng-hide="isDeleting" >
                        <button type="button" class="btn btn-danger btn-xs" ng-click="startDelete()"><i class="fa fa-trash-o"></i> Delete</button>
                    </div>
                    <div ng-show="isDeleting">
                        <button type="button" class="btn btn-default btn-xs" ng-click="cancel()">Abort deletion</button>
                        <button type="button" class="btn btn-danger btn-xs"  ng-click="confirm()"><i class="fa fa-trash-o"></i> Confirm</button>
                    </div>
                </div>
            </script>
		</div>
	</body>
</html>
