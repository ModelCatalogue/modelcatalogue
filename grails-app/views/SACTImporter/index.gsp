<%--
  Created by IntelliJ IDEA.
  User: sus_avi
  Date: 05/06/2014
  Time: 15:13
--%>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Import SACT data dictionary</title>
</head>
<body>
<div id="main">

    <g:form action="upload" controller="SACTImporter"  enctype="multipart/form-data" encoding="multipart/form-data">
        <h5>Please select the XSD SACT file:</h5>
        <p>
            <input type="file" id="xsdSACTFile" name="xsdSACTFile" multiple="false" />
        </p>
        <h5>Please select the XSD CommonTypes SACT file:</h5>
        <p>
            <input type="file" id="xsdSACTTypesFile" name="xsdSACTTypesFile" multiple="false" />
        </p>
        <p>
            <button id='btnUpload' type="submit" class="btn btn-primary">Upload SACT xsd files </button>
        </p>
    </g:form>
</div>
<g:if test="${flash.error}">
    <div class="alert alert-error"><g:message code="${flash.message}" args="${flash.args}" default="${flash.default}"/></div>
</g:if>
<g:if test="${flash.message}">
    <div class="message" style="display: block">${flash.message}</div>
</g:if>

</body>
</html>
