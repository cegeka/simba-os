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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.simbasecurity.messages.Message"/>
<html>
<head>
    <title><fmt:message key="access.denied.title"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <link href="../css/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="container">
    <div id="header">
        <h2>Simba</h2>

        <h3><fmt:message key="access.denied.header"/></h3>
    </div>
    <div id="content">
        <p><fmt:message key="access.denied.message"/></p>
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