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
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * Custom filters are used so that the "Find:" filter can 'see through'
 * any filters that may have been applied.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskListFilter {

	public abstract boolean select(Object parent, Object element);

	public boolean shouldAlwaysShow(Object parent, AbstractTask task, boolean exposeSubTasks) {
		return task.isActive();
	} 
}
