<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="random" class="java.util.Random" scope="application" />

<%--Add files used by the webapp--%>
<template:addResources type="css" resources="webapp/${requestScope.webappCssFileName}" media="screen"/>
<template:addResources type="javascript" resources="webapp/${requestScope.webappJsFileName}"/>

<c:set var="_uuid_" value="${currentNode.identifier}"/>
<c:set var="language" value="${currentResource.locale.language}"/>
<c:set var="workspace" value="${renderContext.workspace}"/>
<c:set var="isEdit" value="${renderContext.editMode}"/>

<c:set var="site" value="${renderContext.site.siteKey}"/>
<c:set var="host" value="${url.server}"/>

<c:set var="targetId" value="REACT_Quiz_${fn:replace(random.nextInt(),'-','_')}"/>

<div id="${targetId}"></div>

<script>
    const quiz_context_${targetId}={
        host:"${host}",
        workspace:"${workspace}",
        isEdit:${isEdit},
        scope:"${site}",//site key
        locale:"${language}",
        quizId:"${_uuid_}",
        gqlServerUrl:"${host}/modules/graphql",
        contextServerUrl:window.digitalData?window.digitalData.contextServerPublicUrl:undefined,//digitalData is set in live mode only
    };

    window.addEventListener("DOMContentLoaded", (event) => {
        //in case if edit mode slow down the load waiting for the jahia GWT UI was setup,
        // otherwise the react app failed (maybe loosing his position as the DOM is updated by the jahia UI at the same time)
        <c:choose>
            <c:when test="${isEdit}" >
            setTimeout(() => {
                window.quizUIApp("${targetId}",quiz_context_${targetId});
            },500);
            </c:when>
            <c:otherwise>
            window.quizUIApp("${targetId}",quiz_context_${targetId});
            </c:otherwise>
        </c:choose>
    });
</script>
