package com.nyu.tweetmap;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class mapServlet
 */
@WebServlet("/mapServlet")
public class mapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public mapServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("map servlet called");
		String tagname = request.getParameter("tagSelected");
		System.out.println("Tag Selected is : "+tagname);
		DynamoDataRetrieval dynamoDataRetrieval = new DynamoDataRetrieval();
		List<TLocation> locationData = dynamoDataRetrieval.getItems(tagname);
		String st = "";
		List<String> items = new ArrayList<String>();
		for (TLocation temp : locationData) {
			st = "{lat:" + temp.getLatitude() + ",lng:"+ temp.getLongitude() + "}";
			items.add(st);
		}
		System.out.println("Size of location is : "+items.size());
		request.setAttribute("locs", items);
		
		request.getRequestDispatcher("map.jsp").forward(request, response);
	}

}
