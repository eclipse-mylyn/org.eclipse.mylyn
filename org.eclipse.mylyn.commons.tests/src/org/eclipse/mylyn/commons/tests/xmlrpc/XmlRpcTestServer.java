/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.xmlrpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

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
