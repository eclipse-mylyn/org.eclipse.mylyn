/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.model.TracComment;
import org.eclipse.mylyn.internal.trac.core.model.TracComponent;
import org.eclipse.mylyn.internal.trac.core.model.TracMilestone;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracRepositoryInfo;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSeverity;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketField;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketType;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;

/**
 * Defines the requirements for classes that provide remote access to Trac repositories.
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
				return "Web"; //$NON-NLS-1$
			case XML_RPC:
				return "XML-RPC"; //$NON-NLS-1$ 
			default:
				return null;
			}
		}

	}

	public static final String CHARSET = "UTF-8"; //$NON-NLS-1$

	public static final String TIME_ZONE = "UTC"; //$NON-NLS-1$

	public static final String LOGIN_URL = "/login"; //$NON-NLS-1$

	public static final String QUERY_URL = "/query?format=tab"; //$NON-NLS-1$

	public static final String TICKET_URL = "/ticket/"; //$NON-NLS-1$

	public static final String NEW_TICKET_URL = "/newticket"; //$NON-NLS-1$

	public static final String CUSTOM_QUERY_URL = "/query"; //$NON-NLS-1$

	public static final String TICKET_ATTACHMENT_URL = "/attachment/ticket/"; //$NON-NLS-1$

	public static final String DEFAULT_USERNAME = "anonymous"; //$NON-NLS-1$

	public static final String WIKI_URL = "/wiki/"; //$NON-NLS-1$

	public static final String REPORT_URL = "/report/"; //$NON-NLS-1$

	public static final String CHANGESET_URL = "/changeset/"; //$NON-NLS-1$

	public static final String REVISION_LOG_URL = "/log/"; //$NON-NLS-1$

	public static final String MILESTONE_URL = "/milestone/"; //$NON-NLS-1$

	public static final String BROWSER_URL = "/browser/"; //$NON-NLS-1$

	public static final String ATTACHMENT_URL = "/attachment/ticket/"; //$NON-NLS-1$

	/**
	 * Gets ticket with <code>id</code> from repository.
	 * 
	 * @param id
	 *            the id of the ticket to get
	 * @param monitor
	 *            TODO
	 * @return the ticket
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	TracTicket getTicket(int id, IProgressMonitor monitor) throws TracException;

	/**
	 * Returns the access type.
	 */
	Version getAccessMode();

	/**
	 * Returns the repository url.
	 */
	String getUrl();

	/**
	 * Queries tickets from repository. All found tickets are added to <code>result</code>.
	 * 
	 * @param query
	 *            the search criteria
	 * @param result
	 *            the list of found tickets
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	void search(TracSearch query, List<TracTicket> result, IProgressMonitor monitor) throws TracException;

	/**
	 * Queries ticket id from repository. All found tickets are added to <code>result</code>.
	 * 
	 * @param query
	 *            the search criteria
	 * @param result
	 *            the list of found tickets
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	void searchForTicketIds(TracSearch query, List<Integer> result, IProgressMonitor monitor) throws TracException;

	/**
	 * Validates the repository connection.
	 * 
	 * @return information about the repository
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	TracRepositoryInfo validate(IProgressMonitor monitor) throws TracException;

	/**
	 * Returns true, if the repository details are cached. If this method returns true, invoking
	 * <tt>updateAttributes(monitor, false)</tt> will return without opening a connection.
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

	TracTicketField getTicketFieldByName(String tracKey);

	TracMilestone[] getMilestones();

	TracPriority[] getPriorities();

	TracSeverity[] getSeverities();

	TracTicketResolution[] getTicketResolutions();

	TracTicketStatus[] getTicketStatus();

	TracTicketType[] getTicketTypes();

	TracVersion[] getVersions();

	InputStream getAttachmentData(int ticketId, String filename, IProgressMonitor monitor) throws TracException;

	void putAttachmentData(int ticketId, String name, String description, InputStream source, IProgressMonitor monitor,
			boolean replace) throws TracException;

	void deleteAttachment(int ticketId, String filename, IProgressMonitor monitor) throws TracException;

	/**
	 * @return the id of the created ticket
	 */
	int createTicket(TracTicket ticket, IProgressMonitor monitor) throws TracException;

	void updateTicket(TracTicket ticket, String comment, IProgressMonitor monitor) throws TracException;

	/**
	 * Sets a reference to the cached repository attributes.
	 * 
	 * @param data
	 *            cached repository attributes
	 */
	void setData(TracClientData data);

	Set<Integer> getChangedTickets(Date since, IProgressMonitor monitor) throws TracException;

	Date getTicketLastChanged(Integer id, IProgressMonitor monitor) throws TracException;

	void deleteTicket(int ticketId, IProgressMonitor monitor) throws TracException;

	List<TracComment> getComments(int id, IProgressMonitor monitor) throws TracException;

}
