<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>

<style>
    .half .text, .half .image {
        width: 50%; }
    @media (max-width: 991.98px) {
        .half .text, .half .image {
            width: 100%; } }

    .half .text {
        padding: 7%; }

    .half .image {
        background-size: cover;
        background-position: center center; }
    @media (max-width: 991.98px) {
        .half .image {
            height: 350px; } }
    .half .image div {
        width: 100%;
        height: 100%;
    }

    .half .image .image-display {
        background-size: cover;
        background-position: center center;
    }
</style>

<c:set var="title" value="${fn:escapeXml(currentNode.displayableName)}"/>
<c:set var="description" value="${currentNode.properties['game4:description'].string}"/>

<c:set var="imageNode" value="${currentNode.properties['game4:image'].node}"/>
<template:addCacheDependency node="${imageNode}"/>

<c:catch var ="getUrlException">
    <c:set var="imageURL" value="${imageNode.getUrl(['width:768'])}"/>
</c:catch>
<c:if test = "${getUrlException != null}">
    <c:set var="imageURL" value="${imageNode.getUrl()}"/>
</c:if>

<c:url var="quiz" value="${currentNode.url}"/>

<div class="half d-lg-flex d-block">
    <div class="image ${imagePosition} ${renderContext.editMode == true?' position-relative':''}">
        <div class="image-display" style="background-image: url('${imageURL}');"></div>
    </div>
    <div class="text text-center">
        <h3 class="mb-4">${title}</h3>
        <p class="mb-5">
            ${description}
        </p>
        <p><a class="btn btn-primary btn-sm px-3 py-2" href="${quiz}"><fmt:message key="label.read_more"/></a></p>
    </div>
</div>
