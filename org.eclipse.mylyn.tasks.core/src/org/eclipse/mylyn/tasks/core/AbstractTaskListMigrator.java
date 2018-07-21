/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Set;

import org.w3c.dom.Element;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
@Deprecated
public abstract class AbstractTaskListMigrator {

	public static final String KEY_QUERY = "Query"; //$NON-NLS-1$

	public static final String KEY_TASK = "Task"; //$NON-NLS-1$

	public static final String KEY_LAST_MOD_DATE = "LastModified"; //$NON-NLS-1$

	public abstract String getTaskElementName();

	public abstract Set<String> getQueryElementNames();

	public abstract void migrateQuery(IRepositoryQuery query, Element element);

	public abstract void migrateTask(ITask task, Element element);

	public abstract String getConnectorKind();

}
