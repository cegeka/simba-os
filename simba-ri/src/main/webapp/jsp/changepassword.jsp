<%--
  ~ Copyright 2013 Simba Open Source
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.simbasecurity.messages.Message"/>


<html>
<head>
    <title><fmt:message key="changepassword.title"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <link href="../css/style.css" rel="stylesheet" type="text/css"/>
</head>
<body onload="document.changePasswordForm.password.focus()">
<div id="container">
    <div id="header">
        <h2>Simba</h2>

        <h3>
            <fmt:message key="changepassword.header" />               
        </h3>
    </div>
    <div id="content">
       <%
            String token = request.getParameter("loginToken");			
		%>
		
		<form name="changePasswordForm" id="changepassword" method="post" action="/simba/http/simba-change-pwd?SimbaAction=SimbaChangePasswordAction" autocomplete="off">
            <input id="username" type="hidden" name="username" value="${param['username']}"/>

            <p>
                <label for="password"><fmt:message key="password"/></label>
					<span class="input"><input type="password" name="password"
                                               id="password" class="text" /></span>
            </p>

            <p>
                <label for="newpassword"><fmt:message key="changepassword.newpassword"/></label>
					<span class="input"><input type="password" name="newpassword"
                                               id="newpassword" value="${param['newpassword']}" class="text"/></span>
            </p>

            <p>
                <label for="newpasswordconfirmation"><fmt:message key="changepassword.newpasswordconfirmation"/></label>
					<span class="input"><input type="password" name="newpasswordconfirmation"
                                               id="newpasswordconfirmation" value="${param['newpasswordconfirmation']}"
                                               class="text"/></span>
            </p>
            <c:if test="${param['errorMessage'] != null}">
                <p class="error" id="errorMessage"><fmt:message key="error.${param['errorMessage']}"/></p>
            </c:if>
            <input type="submit" id="submit" value="<fmt:message key="changepassword.button"/>"
                   style="cursor:pointer; cursor:hand;"/>
            <%
			if (token != null) {
			%>
				<input type="hidden" name="loginToken" value="<%=token %>" />
			<%
			}
			%>       
        </form>
    </div>
</div>
<div id="blocks">
    <span id="block1" class="block"></span>
    <span id="block2" class="block"></span>
    <span id="block3" class="block"></span>
    <span id="block4" class="block"></span>
    <span id="block5" class="block"></span>
</div>
</body>
</html>