<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

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

                <tiles:insert attribute="login"/>
                <%--<form action="/j_security_check" method="POST">
                    <table class="form">
                        <tr>
                            <td class="formLead">Username</td>
                            <td>
                                <input type="text" name="j_username" value=""/>
                            </td>
                        </tr>
                        <tr>
                            <td class="formLead">Passwort</td>
                            <td>
                                <input type="password" name="j_password">
                            </td>
                        </tr>
                        <tr>
                            <td class="formLead"></td>
                            <td class="checkbox">
                                <input type="checkbox" name="j_rememberme" value="true"> Remember me
                            </td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <input class="left" name="loginPh" type="image"
                                       src="/images/buttons/bt_login_de.gif"
                                       title="Login f�r aktive User"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">&nbsp;</td>
                        </tr>

                    </table>
                </form>
                --%>

            </div>
        </div>
    </div>
</div>