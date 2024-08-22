<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<jsp:useBean id="random" class="java.util.Random" scope="application" />

<%--Add files used by the webapp--%>
<template:addResources type="css" resources="webapp/${requestScope.webappCssFileName}" media="screen"/>
<script type="application/javascript" src="/modules/quiz-4-jahia/javascript/webapp/${requestScope.webappJsFileName}"></script>

<c:set var="quizId" value="${jcr:getParentOfType(currentNode,'game4nt:quiz').identifier}"/>
<c:set var="_uuid_" value="${currentNode.identifier}"/>
<c:set var="_nodeType_" value="${currentNode.primaryNodeTypeName}"/>
<c:set var="language" value="${currentResource.locale.language}"/>
<c:set var="workspace" value="${renderContext.workspace}"/>
<c:set var="site" value="${renderContext.site.siteKey}"/>
<c:set var="host" value="${url.server}"/>
<c:set var="targetId" value="REACT_Quiz_${fn:replace(random.nextInt(),'-','_')}"/>
<c:set var="isEdit" value="${renderContext.editMode}"/>

<!-- CM view -->
<div id="${targetId}"></div>
<script>
    window.quizUIApp("${targetId}",{
        host:"${host}",
        workspace:"${workspace}",
        scope:"${site}",//site key
        locale:"${language}",
        isEdit:${isEdit},
        quizId:"${quizId}",
        previewTarget:{
            id:"${_uuid_}",
            type:"${_nodeType_}"
        },
        previewCm:true,
        gqlServerUrl:"${host}/modules/graphql",
        contextServerUrl:window.digitalData?window.digitalData.contextServerPublicUrl:undefined,//digitalData is set in live mode only
    });
</script>
