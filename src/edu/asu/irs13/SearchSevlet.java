package edu.asu.irs13;


import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import edu.asu.irs13.GUIHandler;
import edu.asu.irs13.PageRankMain;
import edu.asu.irs13.SnippetsGenerator;

import java.util.ArrayList;
import java.util.List;
/**
 * Servlet implementation class SearchSevlet
 */
@WebServlet("/SearchSevlet")
public class SearchSevlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchSevlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    List<String> snippList = new ArrayList<String>();
    List<String> titleList = new ArrayList<String>();
    List<Integer> docIdList = new ArrayList<Integer>();
    List<String> docLinkList = new ArrayList<String>();
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public static int count = 0 ;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		titleList.clear();
		snippList.clear();
		docLinkList.clear();
		String query = "admissions";
		if(request.getParameter("input")!=null){
			query=request.getParameter("input");
		}
		count++;
		boolean flag = true;
		System.out.println(query);
		GUIHandler handler = new GUIHandler(query,true,count);
		//retreiving  snippets
		snippList = handler.processRequest(count);
		titleList =SnippetsGenerator.titleList;
		
		System.out.println(GUIHandler.top10);
		IndexReader r = IndexReader.open(FSDirectory.open(new File("index")));
		for(int i=0;i<10;i++){
			Document d = r.document(GUIHandler.top10.get(i));
			String url = "http://"+d.getFieldable("path").stringValue(); // the 'path' field of the Document object holds the URL
			url=url.replace("%%", "/");
			
			docLinkList.add(url);
			
		}
		//storing the results in request attribute
		request.setAttribute("query", query); 
	    request.setAttribute("snippList", snippList); 
	    request.setAttribute("titleList", titleList);
	    request.setAttribute("docIdList", GUIHandler.top10);
	    
	    request.setAttribute("docLinkList", docLinkList);
	    request.getRequestDispatcher("/Test.jsp").forward(request, response);
		//String[] qArray = new String[1];
		//main.main(qArray);
	    
	}

}
