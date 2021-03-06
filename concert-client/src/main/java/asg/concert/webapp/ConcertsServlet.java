package asg.concert.webapp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import asg.concert.webapp.util.AuthUtil;

import java.io.IOException;

public class ConcertsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AuthUtil.setSignedInStatus(req);

        req.getRequestDispatcher("/WEB-INF/jsp/concerts.jsp").forward(req, resp);

    }
}
