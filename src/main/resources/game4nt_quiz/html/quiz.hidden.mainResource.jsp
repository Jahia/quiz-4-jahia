<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="random" class="java.util.Random" scope="application" />

<%--Add files used by the webapp--%>
<%--<template:addResources type="css" resources="webapp/2.6c0f60ba.chunk.css" />--%>
<template:addResources type="css" resources="webapp/${requestScope.webappCssFileName}" media="screen"/>
<%--<template:addResources type="javascript" resources="webapp/2.b90ff848.chunk.js" />--%>
<%--<template:addResources type="javascript" resources="webapp/main.dfbbb74d.js" />--%>
<template:addResources type="javascript" resources="webapp/${requestScope.webappJsFileName}"/>

<c:set var="_uuid_" value="${currentNode.identifier}"/>
<c:set var="language" value="${currentResource.locale.language}"/>
<c:set var="workspace" value="${renderContext.workspace}"/>
<c:set var="isEdit" value="${renderContext.editMode}"/>

<c:set var="site" value="${renderContext.site.siteKey}"/>
<c:set var="host" value="${url.server}"/>

<c:set var="targetId" value="REACT_Quiz_${fn:replace(random.nextInt(),'-','_')}"/>

<c:set var="imageNode" value="${currentNode.properties['game4:image'].node}"/>
<c:set var="imageURL" value="${imageNode.getUrl()}"/>


<div class="inner-page">
    <div class="slider-item" style="background-image: url('${imageURL}');">
        <div class="container">
            <div class="row slider-text align-items-center justify-content-center">
                <div class="col-md-8 text-center col-sm-12 pt-5 element-animate">
                    <h1 class="pt-5"><span>Quiz</span></h1>
                    <p class="mb-5 w-75"></p>
                </div>
            </div>
        </div>
    </div>
</div>

<section class="section bg-light">
    <div id="${targetId}" style="max-width: 1300px;margin-left: auto;margin-right: auto"></div>
    <script>
    const quiz_context_${targetId}={
        host:"${host}",
        workspace:"${workspace}",
        isEdit:${isEdit},
        scope:"${site}",//site key
        locale:"${language}",
        quizId:"${_uuid_}",
        <%--filesServerUrl:"${host}/files/${workspace}",--%>
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
</section>