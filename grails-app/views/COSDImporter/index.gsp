<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Import COSD data</title>
</head>
<body>
<div id="main">

    <g:form action="upload" controller="COSDImporter"  enctype="multipart/form-data" encoding="multipart/form-data">
        <h5>Please select files:</h5>
        <p>
            <input type="file" id="excelFile" name="excelFile" multiple="false" />
            <button id='btnUpload' type="submit" class="btn btn-primary">Upload</button>
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
