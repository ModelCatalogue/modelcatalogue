<%--
  Created by IntelliJ IDEA.
  User: pwtsui
  Date: 29/04/2014
  Time: 09:35
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Import HEP data</title>
    <style>
        table {
            border-collapse: collapse;
            margin: 5px;
        }

        th, td {
            border: 1px solid #000000;
            padding: 3px 5px;
        }

        th {
            background-color: #aaaaaa;
        }

        .required {
            background-color: #ff0000;
            color: #ffffff;
        }
    </style>
</head>

<body>
<div id="main">

    <g:form action="upload" controller="HEPExcelImporter"  enctype="multipart/form-data" encoding="multipart/form-data">
        <h5>Please select files:</h5>
        <p>
            <input type="file" id="excelFile" name="excelFile" multiple="false" />
            <button id='btnUpload' type="submit" class="btn btn-primary">Upload</button>
        </p>
    </g:form>

    <g:if test="${diList}">
        <h3>Entries with Errors (missing name or data type)</h3>
        <table>
            <thead>
            <tr>
                <th>&nbsp;</th>
                <th>Item Number</th>
                <th>Section</th>
                <th>Sub-Section</th>
                <th>Pathway Category</th>
                <th>Name</th>
                <th>Description</th>
                <th>Data Type (indicative)</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${diList}" var="item" status="i">
                <tr>
                    <td style="text-align: right">${item.index}</td>
                    <td>${item.entry.itemNumber}</td>
                    <td>${item.entry.section}</td>
                    <td>${item.entry.subSection}</td>
                    <td>${item.entry.pathway}</td>
                    <td class="required">${item.entry.name}</td>
                    <td>${item.entry.description}</td>
                    <td class="required">${item.entry.dataType}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
</div>
<g:if test="${flash.error}">
    <div class="alert alert-error"><g:message code="${flash.message}" args="${flash.args}" default="${flash.default}"/></div>
</g:if>
<g:if test="${flash.message}">
    <div class="message" style="display: block">${flash.message}</div>
</g:if>



</body>
</html>