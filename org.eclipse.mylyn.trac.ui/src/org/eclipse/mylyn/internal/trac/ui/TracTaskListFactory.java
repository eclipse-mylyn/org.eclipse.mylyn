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

package org.eclipse.mylyn.internal.trac.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.w3c.dom.Element;

/**
 * @author Steffen Pingel
 * @author Mik Kersten
 */
public class TracTaskListFactory extends AbstractTaskListFactory {

	private static final String KEY_TRAC = "Trac";

	private static final String KEY_TRAC_TASK = KEY_TRAC + AbstractTaskListFactory.KEY_TASK;

	private static final String KEY_TRAC_QUERY = KEY_TRAC + AbstractTaskListFactory.KEY_QUERY;
	
	public String getTaskElementName() {
		return KEY_TRAC_TASK;
	}
	
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(KEY_TRAC_QUERY);
		return names;
	}

	public boolean canCreate(AbstractTask task) {
		return task instanceof TracTask;
	}
	
	@Override
	public boolean canCreate(AbstractRepositoryQuery category) {
		return category instanceof TracRepositoryQuery;
	}
	
	@Override
	public String getQueryElementName(AbstractRepositoryQuery query) {
		return query instanceof TracRepositoryQuery ? KEY_TRAC_QUERY : "";
	}
	
	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element) throws TaskExternalizationException {
		return new TracTask(repositoryUrl, taskId, summary);
	}

	@Override
	public AbstractRepositoryQuery createQuery(String repositoryUrl, String queryString, String label, Element element) throws TaskExternalizationException {
		return new TracRepositoryQuery(repositoryUrl, queryString, label);
	}
}
