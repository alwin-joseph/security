/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.jakarta.tck.security.test;

import static ee.jakarta.tck.security.test.Utils.notNull;
import static jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

import java.io.IOException;

import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.credential.CallerOnlyCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Test Servlet that prints out the name of the authenticated caller and whether
 * this caller is in any of the roles {foo, bar, kaz}
 */
@DeclareRoles({"foo", "bar", "kaz"})
@WebServlet("/servlet")
public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private SecurityContext securityContext;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.getWriter().write("This is a servlet \n");

        String name = request.getParameter("name");

        if (notNull(name)) {

            AuthenticationStatus status = securityContext.authenticate(
                    request, response,
                    withParams()
                            .credential(
                                    new CallerOnlyCredential(name)));

            response.getWriter().write("Authenticated with status: " + status.name() + "\n");
        }

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        response.getWriter().write("web username: " + webName + "\n");

        response.getWriter().write("web user has role \"foo\": " + request.isUserInRole("foo") + "\n");
        response.getWriter().write("web user has role \"bar\": " + request.isUserInRole("bar") + "\n");
        response.getWriter().write("web user has role \"kaz\": " + request.isUserInRole("kaz") + "\n");

        String contextName = null;
        if (securityContext.getCallerPrincipal() != null) {
            contextName = securityContext.getCallerPrincipal().getName();
        }

        response.getWriter().write("context username: " + contextName + "\n");

        response.getWriter().write("context user has role \"foo\": " + securityContext.isCallerInRole("foo") + "\n");
        response.getWriter().write("context user has role \"bar\": " + securityContext.isCallerInRole("bar") + "\n");
        response.getWriter().write("context user has role \"kaz\": " + securityContext.isCallerInRole("kaz") + "\n");

        response.getWriter().write("has access to /protectedServlet: " + securityContext.hasAccessToWebResource("/protectedServlet") + "\n");

    }

}
