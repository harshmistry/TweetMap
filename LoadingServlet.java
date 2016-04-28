package com.nyu.tweetmap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoadingServlet
 */
@WebServlet("/LoadingServlet")
public class LoadingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadingServlet() {
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
		if(null != request.getParameter("nextServlet"))
			request.setAttribute("nextServlet",request.getParameter("nextServlet"));
		else if(null!=request.getParameter("nextJsp"))
			request.setAttribute("nextJsp",request.getParameter("nextJsp"));
		request.setAttribute("tagSelected", request.getParameter("tagSelected"));
		System.out.println("tag selected: "+request.getParameter("tagSelected"));
		System.out.println("Next Servlet is : "+request.getParameter("nextServlet"));
		request.getRequestDispatcher("LoadingPage.jsp").forward(request, response);
		
	}

}
