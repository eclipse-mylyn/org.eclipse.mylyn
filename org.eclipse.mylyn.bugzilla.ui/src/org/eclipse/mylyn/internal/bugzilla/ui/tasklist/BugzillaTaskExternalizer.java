/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskExternalizationException;
import org.eclipse.mylar.tasks.core.TaskList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String STATUS_RESO = "RESO";

	private static final String STATUS_NEW = "NEW";

	private static final String TAG_BUGZILLA = "Bugzilla";
	
	private static final String TAG_BUGZILLA_QUERY_HIT = TAG_BUGZILLA + KEY_QUERY_HIT;

	private static final String TAG_BUGZILLA_QUERY = TAG_BUGZILLA + KEY_QUERY;

	private static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustom" + KEY_QUERY;

	private static final String TAG_BUGZILLA_REPORT = "BugzillaReport";

	@Override
	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		if (query instanceof BugzillaRepositoryQuery) {
			if (((BugzillaRepositoryQuery) query).isCustomQuery()) {
				return TAG_BUGZILLA_CUSTOM_QUERY;
			} else {
				return TAG_BUGZILLA_QUERY;
			}
		}
		return "";
	}

	@Override
	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY) || node.getNodeName().equals(TAG_BUGZILLA_QUERY);
	}

	@Override
	public AbstractRepositoryQuery readQuery(Node node, TaskList taskList) throws TaskExternalizationException {
		Element element = (Element) node;
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(element.getAttribute(KEY_REPOSITORY_URL), element
				.getAttribute(KEY_QUERY_STRING), element.getAttribute(KEY_NAME), element
				.getAttribute(KEY_QUERY_MAX_HITS), taskList);
		if (node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			query.setCustomQuery(true);
		}
		if (element.getAttribute(KEY_LAST_REFRESH) != null && !element.getAttribute(KEY_LAST_REFRESH).equals("")) {
			query.setLastRefreshTimeStamp(element.getAttribute(KEY_LAST_REFRESH));
		}
		return query;
	}

	@Override
	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof BugzillaRepositoryQuery;
	}

	@Override
	public boolean canCreateElementFor(ITask task) {
		return task instanceof BugzillaTask;
	}

	@Override
	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	@Override
	public ITask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {
		BugzillaTask task = new BugzillaTask(repositoryUrl, taskId, summary, false);
		return task;
	}

	@Override
	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	@Override
	public AbstractQueryHit createQueryHit(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList, AbstractRepositoryQuery query)
			throws TaskExternalizationException {
		String status = STATUS_NEW;
		if (element.hasAttribute(KEY_COMPLETE)) {
			status = element.getAttribute(KEY_COMPLETE);
			if (status.equals(VAL_TRUE)) {
				status = STATUS_RESO;
			}
		}
		BugzillaQueryHit hit = new BugzillaQueryHit(taskList, summary, "", repositoryUrl, taskId, null, status);
		return hit;
	}

	@Override
	public String getTaskTagName() {
		return TAG_BUGZILLA_REPORT;
	}

	@Override
	public String getQueryHitTagName() {
		return TAG_BUGZILLA_QUERY_HIT;
	}
}
