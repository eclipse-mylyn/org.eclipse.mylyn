/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Set;

import org.w3c.dom.Element;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractTaskListMigrator {

	public static final String KEY_QUERY = "Query";

	public static final String KEY_TASK = "Task";

	public abstract String getTaskElementName();

	public abstract Set<String> getQueryElementNames();

	public abstract void migrateQuery(IRepositoryQuery query, Element element);

	public abstract void migrateTask(ITask task, Element element);

	public abstract String getConnectorKind();

}
