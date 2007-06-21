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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.w3c.dom.Element;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskListFactory extends AbstractTaskListFactory {

	private static final String KEY_SEVERITY = "bugzilla.severity";

	private static final String KEY_PRODUCT = "bugzilla.product";

	private static final String TAG_BUGZILLA = "Bugzilla";

	private static final String TAG_BUGZILLA_QUERY = TAG_BUGZILLA + KEY_QUERY;

	private static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustom" + KEY_QUERY;

	private static final String TAG_BUGZILLA_REPORT = "BugzillaReport";

	@Override
	public String getTaskElementName() {
		return TAG_BUGZILLA_REPORT;
	}
	
	@Override
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(TAG_BUGZILLA_QUERY);
		names.add(TAG_BUGZILLA_CUSTOM_QUERY);
		return names;
	}
	
	@Override
	public boolean canCreate(AbstractRepositoryQuery category) {
		return category instanceof BugzillaRepositoryQuery;
	}

	@Override
	public boolean canCreate(AbstractTask task) {
		return task instanceof BugzillaTask;
	}
	
	@Override
	public String getQueryElementName(AbstractRepositoryQuery query) {
		if (query instanceof BugzillaRepositoryQuery) {
			if (((BugzillaRepositoryQuery) query).isCustomQuery()) {
				return TAG_BUGZILLA_CUSTOM_QUERY;
			} else {
				return TAG_BUGZILLA_QUERY;
			}
		}
		return null;
	}

	@Override
	public AbstractRepositoryQuery createQuery(String repositoryUrl, String queryString, String label, Element element) throws TaskExternalizationException {
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(repositoryUrl, queryString, label);
		if (element.getNodeName().equals(TAG_BUGZILLA_CUSTOM_QUERY)) {
			query.setCustomQuery(true);
		}
		return query;
	}
	
	@Override
	public void setAdditionalAttributes(AbstractTask task, Element element) {
		element.setAttribute(KEY_SEVERITY, ((BugzillaTask) task).getSeverity());
		element.setAttribute(KEY_PRODUCT, ((BugzillaTask) task).getProduct());
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element) throws TaskExternalizationException {
		BugzillaTask task = new BugzillaTask(repositoryUrl, taskId, summary);
		if (element.hasAttribute(KEY_SEVERITY)) {
			task.setSeverity(element.getAttribute(KEY_SEVERITY));
		}
		if (element.hasAttribute(KEY_PRODUCT)) {
			task.setProduct(element.getAttribute(KEY_PRODUCT));
		}
		return task;
	}
}
