/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests.support;

import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;

/**
 * @author Steffen Pingel
 */
public class TracTestUtil {

	public static TracTicket createTicket(ITracClient client, String summary) throws Exception {
		TracTicket ticket = new TracTicket();
		ticket.putBuiltinValue(Key.SUMMARY, summary);
		ticket.putBuiltinValue(Key.DESCRIPTION, "");
		int id = client.createTicket(ticket);
		return client.getTicket(id);
	}
	
}
