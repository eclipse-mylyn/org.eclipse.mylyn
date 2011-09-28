/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Adapts the active task to the active context.
 * 
 * @author Steffen Pingel
 */
public class ContextTasksAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { IInteractionContext.class };

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes")
	Class adapterType) {
		if (adapterType == IInteractionContext.class) {
			if (adaptableObject == TasksUi.getTaskActivityManager().getActiveTask()) {
				return ContextCore.getContextManager().getActiveContext();
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
