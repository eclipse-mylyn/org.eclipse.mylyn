/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.w3c.dom.Element;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class BugzillaTaskListMigrator extends AbstractTaskListMigrator {

	private static final String TAG_BUGZILLA_REPORT = "BugzillaReport";

	private static final String KEY_SEVERITY = "bugzilla.severity";

	private static final String KEY_PRODUCT = "bugzilla.product";

	@Override
	public String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(IBugzillaConstants.TAG_BUGZILLA_QUERY);
		names.add(IBugzillaConstants.TAG_BUGZILLA_CUSTOM_QUERY);
		return names;
	}

	@Override
	public String getTaskElementName() {
		return TAG_BUGZILLA_REPORT;
	}

	@Override
	public void migrateQuery(IRepositoryQuery query, Element element) {
		if (element.getNodeName().equals(IBugzillaConstants.TAG_BUGZILLA_CUSTOM_QUERY)) {
			query.setAttribute(IBugzillaConstants.ATTRIBUTE_BUGZILLA_QUERY_CUSTOM, Boolean.TRUE.toString());
		}
	}

	@Override
	public void migrateTask(ITask task, Element element) {
		if (element.hasAttribute(KEY_SEVERITY)) {
			task.setAttribute(KEY_SEVERITY, element.getAttribute(KEY_SEVERITY));
		}
		if (element.hasAttribute(KEY_PRODUCT)) {
			task.setAttribute(KEY_PRODUCT, element.getAttribute(KEY_PRODUCT));
		}
		task.setTaskKey(task.getTaskId());
	}

}
