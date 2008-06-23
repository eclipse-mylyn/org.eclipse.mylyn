/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.w3c.dom.Element;

/**
 * @author Steffen Pingel
 */
public class TracTaskListMigrator extends AbstractTaskListMigrator {

	private static final String KEY_TRAC = "Trac";

	private static final String KEY_TRAC_TASK = KEY_TRAC + KEY_TASK;

	private static final String KEY_TRAC_QUERY = KEY_TRAC + KEY_QUERY;

	private static final String KEY_SUPPORTS_SUBTASKS = "SupportsSubtasks";

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public String getTaskElementName() {
		return KEY_TRAC_TASK;
	}

	@Override
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(KEY_TRAC_QUERY);
		return names;
	}

	@Override
	public void migrateQuery(IRepositoryQuery query, Element element) {
		// nothing to do
	}

	@Override
	public void migrateTask(ITask task, Element element) {
		String lastModDate = element.getAttribute(KEY_LAST_MOD_DATE);
		task.setModificationDate(TracUtil.parseDate(lastModDate));
		task.setAttribute(TracRepositoryConnector.TASK_KEY_UPDATE_DATE, lastModDate);
		if (element.hasAttribute(KEY_SUPPORTS_SUBTASKS)) {
			task.setAttribute(TracRepositoryConnector.TASK_KEY_SUPPORTS_SUBTASKS, Boolean.valueOf(
					element.getAttribute(KEY_SUPPORTS_SUBTASKS)).toString());
		}
	}

}
