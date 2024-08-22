<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="resourceReadOnly" value="${currentResource.moduleParams.readOnly}"/>
<template:include view="hidden.header"/>

<c:set var="isEmpty" value="true"/>
<section class="">
    <div class="container">
        <div class="row">
            <c:forEach items="${moduleMap.currentList}" var="subchild" begin="${moduleMap.begin}" end="${moduleMap.end}">
                <template:module node="${subchild}" view="${moduleMap.subNodesView}" editable="${moduleMap.editable && !resourceReadOnly}"/>
                <c:set var="isEmpty" value="false"/>
            </c:forEach>


            <c:if test="${not empty moduleMap.emptyListMessage and (renderContext.editMode or moduleMap.forceEmptyListMessageDisplay) and isEmpty}">
                ${moduleMap.emptyListMessage}
            </c:if>
        </div>
    </div>
</section>
<template:include view="hidden.footer"/>
