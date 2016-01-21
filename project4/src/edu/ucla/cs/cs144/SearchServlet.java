package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
		String q = request.getParameter("q");
		String numResultsToSkip = request.getParameter("numResultsToSkip");
		if(numResultsToSkip == null) {
			numResultsToSkip = "0";
		}
		String numResultsToReturn = request.getParameter("numResultsToReturn");
		if(numResultsToReturn == null) {
			numResultsToReturn = "1000";
		}
		String currentPage = request.getParameter("currentPage");
		if(currentPage == null) {
			currentPage = "1";
		}
		SearchResult[] res = AuctionSearchClient.basicSearch(q, Integer.parseInt(numResultsToSkip), Integer.parseInt(numResultsToReturn));
		int pageSize = 15;
		int totalRecords = res.length;
		int totalPage = totalRecords % pageSize == 0 ? totalRecords / pageSize : totalRecords / pageSize + 1;
		request.setAttribute("q", q);
		request.setAttribute("numResultsToSkip", numResultsToSkip);
		request.setAttribute("numResultsToReturn", numResultsToReturn);
		request.setAttribute("result", res);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("pageSize", pageSize);
		request.getRequestDispatcher("/basicSearchResult.jsp").forward(request, response);
    }
}
