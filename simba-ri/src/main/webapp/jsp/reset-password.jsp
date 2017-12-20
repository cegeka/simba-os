<%--
  ~ Copyright 2013-2017 Simba Open Source
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
  ~
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.simbasecurity.messages.Message"/>
<html>
<head>
    <title><fmt:message key="reset.password.title"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <link href="../css/style.css" rel="stylesheet" type="text/css"/>
</head>
<body onload="document.resetPasswordForm.email.focus()">
<div id="container">
    <div id="header">
        <h2>Simba</h2>

        <h3><fmt:message key="reset.password.header"/></h3>
    </div>
    <div id="content">
        <%
            String resetSuccessfull = request.getParameter("resetSuccessfull");
        %>
        <script type="text/javascript">
            function toggleButton(ref,bttnID){
                document.getElementById(bttnID).disabled= ((ref.value === null));
            }
        </script>
        <form name="resetPasswordForm" id="resetPassword" method="post" action="/simba/http/simba-reset-pwd">
            <p>
                <label for="email"><fmt:message key="email"/></label>
                <span class="input"><input type="email" name="email" id="email" class="text" onkeyup="toggleButton(this, 'bttnsubmit')"/></span>
            </p>
            <p>
                <input type="submit" id="bttnsubmit" value="<fmt:message key="reset.password.button"/>" class="button-submit" disabled/>
            </p>
        </form>
        <% if (resetSuccessfull != null) { %>
        <p id="successMessage" class="p-top-space">
            <fmt:message key="reset.password.successfull"/>
        </p>
        <% } %>
    </div>
</div>
</body>
</html>