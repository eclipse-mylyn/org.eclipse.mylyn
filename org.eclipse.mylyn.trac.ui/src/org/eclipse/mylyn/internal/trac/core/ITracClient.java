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

package org.eclipse.mylar.internal.trac.core;

import java.util.List;

import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracTicket;

/**
 * Defines the requirements for classes that provide remote access to Trac
 * repositories.
 * 
 * @author Steffen Pingel
 */
public interface ITracClient {

	public enum Version {
		TRAC_0_9, XML_RPC;

		public static Version fromVersion(String version) {
			try {
				return Version.valueOf(version);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

		@Override
		public String toString() {
			switch (this) {
			case TRAC_0_9:
				return "Trac 0.9 and higher";
			case XML_RPC:
				return "XML-RPC Plugin (Rev. 826)";
			default:
				return null;
			}
		}

	}

	public static final String CHARSET = "UTF-8";

	public static final String TIME_ZONE = "UTC";

	public static final String LOGIN_URL = "/login";

	public static final String QUERY_URL = "/query?format=tab";

	public static final String TICKET_URL = "/ticket/";

	/**
	 * Gets ticket with <code>id</code> from repository.
	 * 
	 * @param id
	 *            the id of the ticket to get
	 * @return the ticket
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	TracTicket getTicket(int id) throws TracException;

	/**
	 * Returns the access type.
	 */
	Version getVersion();

	/**
	 * Queries tickets from repository. All found tickets are added to
	 * <code>result</code>.
	 * 
	 * @param query
	 *            the search criteria
	 * @param result
	 *            the list of found tickets
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	void search(TracSearch query, List<TracTicket> result) throws TracException;

	/**
	 * Validates the repository connection.
	 * 
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	void validate() throws TracException;

}
