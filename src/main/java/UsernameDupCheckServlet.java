package pac1;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pac1.func.Util;

/**
 * Servlet implementation class UsernameDupCheckServlet
 */
@WebServlet("/UsernameDupCheckServlet")
public class UsernameDupCheckServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String JSP_PATH = "account_icon_switch.jsp";

		request.setAttribute("result", Util.existsUser(Util.sanitizing(request.getParameter("userName"))));
		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
		rd.forward(request, response);
	}

}
