<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<div id="header">

    <tiles:insert attribute="hallo"/>
    <div id="tabnavigation">
        <tiles:insert attribute="tabs"/>
    </div>
    <html:link action="/index"><html:img imageName="logo" page="/images/logo.gif" alt="logo"/></html:link>
</div>
