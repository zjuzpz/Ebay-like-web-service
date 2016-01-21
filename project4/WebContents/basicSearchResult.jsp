<!DOCTYPE html>
<!--Get data from request-->
<%@page import="edu.ucla.cs.cs144.SearchResult" %>
<%
  String q = "";
  int numResultsToSkip = 0, numResultsToReturn = 0;
  int totalPage = 1, currentPage = 1, pageSize = 15;
  try {
	q = request.getAttribute("q").toString();
	numResultsToSkip = Integer.parseInt(request.getAttribute("numResultsToSkip").toString());
	numResultsToReturn = Integer.parseInt(request.getAttribute("numResultsToReturn").toString());
	totalPage = Integer.parseInt(request.getAttribute("totalPage").toString());
	currentPage = Integer.parseInt(request.getAttribute("currentPage").toString());
	pageSize = Integer.parseInt(request.getAttribute("pageSize").toString());
  } catch(NullPointerException e) {
	  response.sendRedirect("/eBay/keywordSearch.html");
  }
  SearchResult[] basicSearchResult = (SearchResult[])request.getAttribute("result");
  if(currentPage > totalPage) {
	  currentPage = totalPage;
  }
  if(currentPage < 1) {
	  currentPage = 1;
  }
  int currentPos = (currentPage - 1) * pageSize;
  int endPos = Math.min((currentPage) * pageSize, basicSearchResult.length); 
%>
<html>
  <head>
    <title>Basic Search Result</title>
  </head>
  <body>
    <h1>Keyword Search</h1>
    <form action="/eBay/search" method="GET">
	  Keyword <input type="text" name="q">
	  <input type="hidden" name="numResultsToSkip" value="0"/>
	  <input type="hidden" name="numResultsToReturn" value="1000"/>	  
	  <input type="submit" value="Search">
	</form>
	<h3>Your search results:</h3>
	<table border=3>
	  <tr> 
		<td>ItemID</td>
		<td>Item Name</td>
      </tr>
	  <% for(int i=currentPos;i<endPos;i++) {
		String id = basicSearchResult[i].getItemId();
		String name = basicSearchResult[i].getName(); %>
		<tr>
	      <td>
		    <a href="/eBay/item?id=<%= id %>"> <%= id %> </a> 
		  </td>
		  <td><%= name %></td>
		<tr/>
	  <% } %>
	</table>
	<% if(currentPage > 1) { %>
	  <a href="/eBay/search?q=<%=q%>&numResultsToSkip=0&numResultsToReturn=1000&currentPage=<%=currentPage-1%>">Previous</a>
	<% }%>
	<% if(currentPage < totalPage) { %>
	  <a href="/eBay/search?q=<%=q%>&numResultsToSkip=0&numResultsToReturn=1000&currentPage=<%=currentPage+1%>">Next</a>
	<% }%>
  </body>
</html>