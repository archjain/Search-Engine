<%@ page import="java.io.*,java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<html>
<head>
<title>Search UI</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" >
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<style>
.container {
   
   
    padding: 10px 10px 10px 10px;
}
body{
padding-top: 10px;

}
</style>
</head>



<body>
</br>
<b>Result Sequence:</b><br> 
<b>Docid Doclink</b></br>
<b>Html Title</b><br>
<b>Body content</b></br>
<form action="SearchSevlet" method="post">
<input type = "text" class="form-control" name="input" placeholder="Type Query Here" required/> <input type = "submit" class="btn-primary" />
</form>
<h3><span class="label label-default"><c:out value="${query}" /></span></h3>


<c:forEach items="${snippList}" var="snippList" varStatus="status">
   
     <div class="container" >
     Doc Id: <b> ${docIdList[status.index]}</b>     <a href=${docLinkList[status.index]}>${docLinkList[status.index]}</a><br/>
     <b>${titleList[status.index]}</b></br>   ${snippList}
     </div>
      
    
</c:forEach>





</body>
</html>