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

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.tasks.core.getAllCategories;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_SEVERITY = "bugzilla.severity";

	private static final String KEY_PRODUCT = "bugzilla.product";

	private static final String TAG_BUGZILLA = "Bugzilla";

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
	public AbstractRepositoryQuery readQuery(Node node, getAllCategories taskList) throws TaskExternalizationException {
		Element element = (Element) node;
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(element.getAttribute(KEY_REPOSITORY_URL), element
				.getAttribute(KEY_QUERY_STRING), element.getAttribute(KEY_NAME));
		if (node.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			query.setCustomQuery(true);
		}
		if (element.getAttribute(KEY_LAST_REFRESH) != null && !element.getAttribute(KEY_LAST_REFRESH).equals("")) {
			query.setLastRefreshTimeStamp(element.getAttribute(KEY_LAST_REFRESH));
		}
		return query;
	}

	@Override
	public Element createTaskElement(AbstractTask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		node.setAttribute(KEY_SEVERITY, ((BugzillaTask) task).getSeverity());
		node.setAttribute(KEY_PRODUCT, ((BugzillaTask) task).getProduct());
		return node;
	}

	@Override
	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof BugzillaRepositoryQuery;
	}

	@Override
	public boolean canCreateElementFor(AbstractTask task) {
		return task instanceof BugzillaTask;
	}

	@Override
	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element, getAllCategories taskList,
			AbstractTaskListElement category, AbstractTask parent) throws TaskExternalizationException {
		BugzillaTask task = new BugzillaTask(repositoryUrl, taskId, summary);
		if (element.hasAttribute(KEY_SEVERITY)) {
			task.setSeverity(element.getAttribute(KEY_SEVERITY));
		}
		if (element.hasAttribute(KEY_PRODUCT)) {
			task.setProduct(element.getAttribute(KEY_PRODUCT));
		}
		return task;
	}

	@Override
	public String getTaskTagName() {
		return TAG_BUGZILLA_REPORT;
	}

}
