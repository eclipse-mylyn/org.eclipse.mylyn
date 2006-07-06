/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests.support;

import org.eclipse.mylar.trac.tests.Constants;
import org.eclipse.mylar.trac.tests.support.XmlRpcServer.Ticket;

/**
 * Initializes Trac repositories to a defined state. This is done once per test
 * run, since cleaning and initializing the repository for each test method
 * would take too long.
 * 
 * @author Steffen Pingel
 */
public class TestFixture {

	public static XmlRpcServer.TestData data1;

	public static XmlRpcServer.TestData initializeRepository1() throws Exception {
		if (data1 == null) {
			XmlRpcServer server = new XmlRpcServer(Constants.TEST_REPOSITORY1_URL,
					Constants.TEST_REPOSITORY1_ADMIN_USERNAME, Constants.TEST_REPOSITORY1_ADMIN_PASSWORD);

			server.ticket().deleteAll();

			server.ticketMilestone("m1").deleteAndCreate();
			Ticket ticket = server.ticket().create("summary1", "description1");
			ticket.update("comment", "milestone", "m1");

			server.ticketMilestone("m2").deleteAndCreate();
			ticket = server.ticket().create("summary2", "description2");
			ticket.update("comment", "milestone", "m2");
			ticket = server.ticket().create("summary3", "description3");
			ticket.update("comment", "milestone", "m2");

			ticket = server.ticket().create("summary4", "description4");

			data1 = server.getData();
		}
		return data1;
	}

	public static void cleanupRepository1() throws Exception {
		if (data1 != null) {
			data1.cleanup();
			data1 = null;
		}
	}

}
