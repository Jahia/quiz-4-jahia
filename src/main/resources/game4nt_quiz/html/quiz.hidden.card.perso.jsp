<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="quizQuestion" value="${currentResource.moduleParams.quizQuestion}"/>
<c:set var="imageURL" value="${currentResource.moduleParams.imageURL}"/>
<c:set var="quizReset" value="${currentResource.moduleParams.quizReset}"/>
<c:set var="quizURL" value="${currentResource.moduleParams.quizURL}"/>
<c:set var="quizReleaseDate" value="${currentResource.moduleParams.quizReleaseDate}"/>
<c:set var="quizScore" value="${currentResource.moduleParams.quizScore}"/>
<c:set var="persoResultNode" value="${currentNode.properties['game4:personalizedResultContent'].node}"/>

<div class="col-md-6 col-lg-4 mt-2">
    <div class="card text-white card-has-bg click-col" style="background-image:url('${imageURL}');">
        <div class="card-img-overlay d-flex flex-column quiz">
            <div class="card-body">
                <small class="card-meta mb-2">Quiz</small>
                <h4 class="card-title mt-0 text-white">${quizQuestion}</h4>
<%--                <c:if test="${!empty quizReleaseDate}">--%>
<%--                    <small><i class="far fa-clock"></i> ${quizReleaseDate}</small>--%>
<%--                </c:if>--%>

                <c:choose>
                    <c:when test="${renderContext.editMode}">
                        <fmt:message key="label.persoResultNodeVisibleInPreview"/>...
                    </c:when>
                    <c:otherwise>
                        <c:if test="${!empty quizScore}" >
                            <template:module node="${persoResultNode}" editable="false"/>
                        </c:if>
                    </c:otherwise>
                </c:choose>

            </div>
            <div class="card-footer">
                <c:choose>
                    <c:when test="${!empty quizScore}">
                        <c:if test="${quizReset eq 'true'}" >
                            <p><a class="btn btn-primary btn-block px-3 py-2" href="${quizURL}">
                                <fmt:message key="label.game4_btnTryAgain"/>
                            </a></p>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <p><a class="btn btn-primary btn-block px-3 py-2" href="${quizURL}">
                            <fmt:message key="label.game4_btnStart"/>
                        </a></p>
                    </c:otherwise>
                </c:choose>


            </div>
        </div>
    </div>
</div>

