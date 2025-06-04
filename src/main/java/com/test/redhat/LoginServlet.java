package com.test.redhat;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet{
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get the values from the form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Now you can use the username and password for authentication or other purposes
        // For example, you might check them against a database
        // ...

        // Example: Print the values (for demonstration purposes)
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Send a response back to the client (e.g., redirect to a success page)
        // response.sendRedirect("welcome.jsp");
    }

}
