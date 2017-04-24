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

<!--
  ~ Copyright 2011 Simba Open Source
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
  -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
Design by Free CSS Templates
http://www.freecsstemplates.org
Released for free under a Creative Commons Attribution 2.5 License

Name       : Passageway  
Description: A two-column, fixed-width design with dark color scheme.
Version    : 1.0
Released   : 20100418

-->
<%@page import="org.simbasecurity.client.configuration.SimbaConfiguration"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="keywords" content="" />
<meta name="description" content="" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>SIMBA Zoo</title>
<link href="../css/style.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="../jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="../jquery/jquery.gallerax-0.2.js"></script>
</head>
<body>
<div id="wrapper">
	<div id="header">
		<div id="logo">
			<h1><a href="#">SIMBA Zoo</a></h1>
			<p>Passageway design by <a href="http://www.freecsstemplates.org/">Free CSS Templates</a></p>
		</div>
	</div>
	<div id="page">
		<div id="page-bgtop">
			<div id="page-bgbtm">
				<div id="content">
					<div>
						<div id="gallery">
							<div id="gallery-background"><img src="../images/steak.png" alt="" width="600" height="340" class="output" /></div>
							<div id="gallery-bgthumb">
								<ul class="thumbnails">
									<li><img src="../images/steak.png" title="" alt="" width="128" height="88" /></li>
									<li><img src="../images/vegetable.png" title="" alt="" width="100" height="75" /></li>
									<li><img src="../images/coffee.jpg" title="" alt="" width="100" height="75" /></li>
									<li><img src="../images/tomato.png" title="" alt="" width="100" height="75" /></li>
								</ul>
								<br class="clear" />
							</div>
						</div>
						<script type="text/javascript">

						$('#gallery').gallerax({
							outputSelector: 		'.output',					// Output selector
							thumbnailsSelector:		'.thumbnails li img',		// Thumbnails selector
							fade: 					'fast',						// Transition speed (fast)
							navNextSelector:		'.nav li a.navNext',		// 'Next' selector
							navPreviousSelector:	'.nav li a.navPrevious'		// 'Previous' selector
						});

					</script>
						<!-- end -->
					</div>
					<div class="post">
						<h2 class="title"><a href="#">Feeding time @ SIMBA Zoo </a></h2>
						<p class="meta"><span class="date">December 1, 2011</span><span class="posted">Posted by <a href="#">Ground Keeper</a></span></p>
						<div style="clear: both;">&nbsp;</div>
						<div class="entry">
							<p>Beware when feeding the animals !  Although you can access this page, feeding them is still dangerous.<br><br><br><br></p>							
						</div>
					</div>
					<div style="clear: both;">&nbsp;</div>
				</div>
				<!-- end #content -->
				<div id="sidebar">
					<ul>
						<li>
							<div id="search" >
								<form method="get" action="#">
									<div>
										<input type="text" name="s" id="search-text" value="" />
										<input type="submit" id="search-submit" value="GO" />
									</div>
								</form>
							</div>
							<div style="clear: both;">&nbsp;</div>
						</li>
						<li>
							<h2>Actions</h2>
							<p>Here you find all the actions that can be performed in the Zoo.  Please pick one, if you have the rights, the action will be executed. Otherwise an error message is shown.</p>
						</li>
						<li>
							<h2>Possible actions</h2>
							<ul>
								<li><a href="main.jsp">Back to animals</a></li>
								<li><a href=<%=SimbaConfiguration.getSimbaLogoutURL("/simba-zoo") %>>Logout</a></li>
							</ul>
						</li>						
					</ul>
				</div>
				<!-- end #sidebar -->
				<div style="clear: both;">&nbsp;</div>
			</div>
		</div>
	</div>
	<!-- end #page -->
</div>
<div id="footer">
	<p>Copyright (c) 2008 Sitename.com. All rights reserved. Design by <a href="http://www.freecsstemplates.org/">Free CSS Templates</a>.</p>
</div>
<!-- end #footer -->
</body>
</html>
