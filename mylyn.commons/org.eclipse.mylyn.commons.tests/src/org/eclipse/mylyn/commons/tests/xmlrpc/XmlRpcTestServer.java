/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.xmlrpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

@SuppressWarnings("nls")
public class XmlRpcTestServer {

	private static int port = 8090;

	public static class Server {

		public int identity(int i) {
			return i;
		}

	}

	private static WebServer webServer;

	public static int start() throws Exception {
		if (webServer == null) {
			webServer = new WebServer(port);
			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

			PropertyHandlerMapping phm = new PropertyHandlerMapping();
			phm.addHandler("Test", Server.class);
			xmlRpcServer.setHandlerMapping(phm);

			webServer.start();
		}
		return port;
	}

}
