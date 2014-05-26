package org.simbasecurity.manager.service.manager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.common.config.SystemConfiguration;

public class SimbaLocationManagerService extends HttpServlet {

	private static final long serialVersionUID = 5630573260820907232L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append(SystemConfiguration.getSimbaWebURL());
    }
}
