<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="random" class="java.util.Random" scope="application" />

<%--Add files used by the webapp--%>
<template:addResources type="css" resources="webapp/${requestScope.webappCssFileName}" media="screen"/>
<script type="application/javascript" src="/modules/game-4-jcustomer-components-quiz/javascript/webapp/${requestScope.webappJsFileName}"></script>

<c:set var="_uuid_" value="${currentNode.identifier}"/>
<c:set var="_nodeType_" value="${currentNode.primaryNodeTypeName}"/>
<c:set var="language" value="${currentResource.locale.language}"/>
<c:set var="workspace" value="${renderContext.workspace}"/>
<c:set var="isEdit" value="${renderContext.editMode}"/>
<c:set var="site" value="${renderContext.site.siteKey}"/>
<c:set var="host" value="${url.server}"/>
<c:set var="targetId" value="REACT_Quiz_${fn:replace(random.nextInt(),'-','_')}"/>

<!-- CM view -->
<div id="${targetId}"></div>
<script>
    window.quizUIApp("${targetId}",{
        host:"${host}",
        workspace:"${workspace}",
        isEdit:${isEdit},
        scope:"${site}",//site key
        locale:"${language}",
        quizId:"${_uuid_}",
        previewTarget:{
            id:"${_uuid_}",
            type:"${_nodeType_}"
        },
        previewCm:true,
        gqlServerUrl:"${host}/modules/graphql",
        contextServerUrl:window.digitalData?window.digitalData.contextServerPublicUrl:undefined,//digitalData is set in live mode only
    });
</script>
