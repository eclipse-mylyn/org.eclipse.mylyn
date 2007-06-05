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

import java.io.InputStream;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylar.internal.trac.core.model.TracComponent;
import org.eclipse.mylar.internal.trac.core.model.TracMilestone;
import org.eclipse.mylar.internal.trac.core.model.TracPriority;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;
import org.eclipse.mylar.internal.trac.core.model.TracSeverity;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracTicketField;
import org.eclipse.mylar.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.core.model.TracTicketType;
import org.eclipse.mylar.internal.trac.core.model.TracVersion;

/**
 * Defines the requirements for classes that provide remote access to Trac
 * repositories.
 * 
 * @author Steffen Pingel
 */
public interface ITracClient {

	public enum Version {
		XML_RPC, TRAC_0_9;

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
				return "Web (Trac 0.9 or 0.10)";
			case XML_RPC:
				return "XML-RPC Plugin (Rev. " + TracXmlRpcClient.REQUIRED_REVISION + ")";
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

	public static final String NEW_TICKET_URL = "/newticket";
	
	public static final String CUSTOM_QUERY_URL = "/query";

	public static final String TICKET_ATTACHMENT_URL = "/attachment/ticket/";

	public static final String DEFAULT_USERNAME = "anonymous";

	public static final String WIKI_URL = "/wiki/";

	public static final String REPORT_URL = "/report/";

	public static final String CHANGESET_URL = "/changeset/";

	public static final String REVISION_LOG_URL = "/log/";

	public static final String MILESTONE_URL = "/milestone/";

	public static final String BROWSER_URL = "/browser/";
	
	public static final String ATTACHMENT_URL = "/attachment/ticket/";

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

	/**
	 * Returns true, if the repository details are cached. If this method
	 * returns true, invoking <tt>updateAttributes(monitor, false)</tt> will
	 * return without opening a connection.
	 * 
	 * @see #updateAttributes(IProgressMonitor, boolean) 
	 */
	boolean hasAttributes();

	/**
	 * Updates cached repository details: milestones, versions etc.
	 * 
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	void updateAttributes(IProgressMonitor monitor, boolean force) throws TracException;

	TracComponent[] getComponents();

	TracTicketField[] getTicketFields();
	
	TracMilestone[] getMilestones();

	TracPriority[] getPriorities();

	TracSeverity[] getSeverities();

	TracTicketResolution[] getTicketResolutions();

	TracTicketStatus[] getTicketStatus();

	TracTicketType[] getTicketTypes();

	TracVersion[] getVersions();

	InputStream getAttachmentData(int ticketId, String filename) throws TracException;

	void putAttachmentData(int ticketId, String name, String description, InputStream source) throws TracException;

	void deleteAttachment(int ticketId, String filename) throws TracException;

	/**
	 * @return the id of the created ticket
	 */
	int createTicket(TracTicket ticket) throws TracException;

	void updateTicket(TracTicket ticket, String comment) throws TracException;

	/**
	 * Sets a reference to the cached repository attributes.
	 * 
	 * @param data
	 *            cached repository attributes
	 */
	void setData(TracClientData data);

	Set<Integer> getChangedTickets(Date since) throws TracException;

	void setProxy(Proxy proxy);

}
