<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<div id="modalPage">
    <div class="modalBackground">
    </div>
    <div class="modalContainer">
        <div class="modal">
            <div class="modalTop">
                <a href="" target="_parent"><html:img page="/images/buttons/x.gif" alt=""/></a>
            </div>
            <div class="modalBody">

                <h2 style="margin:10px 0 20px 0;text-align:left;">LOGIN <span
                        class="normalWeight">FÜR FOTOGRAFEN</span>
                </h2>
                <%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
                <c:if test="${clientInSession==null}">
                    <div class="g-signin2" data-onsuccess="onSignIn"></div>
                </c:if>
                <%--<tiles:insert attribute="login"/>--%>

            </div>
        </div>
    </div>
</div>