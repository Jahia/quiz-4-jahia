<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="qjexp" uri="http://www.jahia.org/qjexp" %>

<template:addResources type="css" resources="cards.css"/>
<c:set var="title" value="${fn:escapeXml(currentNode.displayableName)}"/>
<c:set var="subtitle" value="${fn:escapeXml(currentNode.properties['game4:subtitle'])}"/>
<c:set var="quizQuestion" value="${title} - ${subtitle}"/>
<c:set var="quizReset" value="${currentNode.properties['game4:reset'].boolean}"/>

<c:set var="imageNode" value="${currentNode.properties['game4:image'].node}"/>
<template:addCacheDependency node="${imageNode}"/>

<c:catch var ="getUrlException">
    <c:set var="imageURL" value="${imageNode.getUrl(['width:768'])}"/>
</c:catch>
<c:if test = "${getUrlException != null}">
    <c:set var="imageURL" value="${imageNode.getUrl()}"/>
</c:if>

<c:url var="quizURL" value="${currentNode.url}"/>
<c:set var="site" value="${renderContext.site.siteKey}"/>
<c:set var="quizKey" value="${currentNode.properties['game4:quizKey']}"/>
<c:set var="quizScorePropertyName" value="quiz-score-${quizKey}"/>
<c:set var="language" value="${currentResource.locale.language}"/>
<c:set var="quizEventProps"
       value="${qjexp:searchQuizScore(pageContext.request,site,quizKey,quizScorePropertyName,language)}"/>

<c:set var="persoResultNode" value="${currentNode.properties['game4:personalizedResultContent'].node}"/>

<c:choose>
    <c:when test="${!empty persoResultNode}">
        <template:include view="hidden.card.perso">
            <template:param name="quizQuestion" value="${quizQuestion}"/>
            <template:param name="imageURL" value="${imageURL}"/>
            <template:param name="quizReset" value="${quizReset}"/>
            <template:param name="quizURL" value="${quizURL}"/>
            <template:param name="quizReleaseDate" value="${quizEventProps['quizReleaseDate']}"/>
            <template:param name="quizScore" value="${quizEventProps['quizScore']}"/>
        </template:include>
    </c:when>
    <c:otherwise>
        <template:include view="hidden.card.score">
            <template:param name="quizQuestion" value="${quizQuestion}"/>
            <template:param name="imageURL" value="${imageURL}"/>
            <template:param name="quizReset" value="${quizReset}"/>
            <template:param name="quizURL" value="${quizURL}"/>
            <template:param name="quizReleaseDate" value="${quizEventProps['quizReleaseDate']}"/>
            <template:param name="quizScore" value="${quizEventProps['quizScore']}"/>
        </template:include>
    </c:otherwise>
</c:choose>
