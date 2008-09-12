/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Changes handle from oldHandle to newHandle in Activity Meta Context
 * 
 * @author Rob Elves
 */
public class ChangeActivityHandleOperation extends TaskListModifyOperation {

	private final String oldHandle;

	private final String newHandle;

	public ChangeActivityHandleOperation(String oldHandle, String newHandle) {
		this.oldHandle = oldHandle;
		this.newHandle = newHandle;
	}

	@Override
	protected void operations(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
			InterruptedException {
		if (oldHandle == null || newHandle == null || oldHandle.equals(newHandle)) {
			return;
		}
		try {
			monitor.beginTask("Activity migration", IProgressMonitor.UNKNOWN);
			refactorMetaContextHandles(oldHandle, newHandle);
			TasksUiPlugin.getTaskActivityMonitor().reloadActivityTime();
		} finally {
			monitor.done();
		}
	}

	@SuppressWarnings("restriction")
	private void refactorMetaContextHandles(String oldHandle, String newHandle) {
		ContextCorePlugin.getContextManager().saveActivityMetaContext();
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		InteractionContext newMetaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		for (InteractionEvent event : metaContext.getInteractionHistory()) {
			if (event.getStructureHandle() != null) {
				if (event.getStructureHandle().equals(oldHandle)) {
					event = new InteractionEvent(event.getKind(), event.getStructureKind(), newHandle,
							event.getOriginId(), event.getNavigation(), event.getDelta(),
							event.getInterestContribution(), event.getDate(), event.getEndDate());
				}
			}
			newMetaContext.parseEvent(event);
		}
		ContextCorePlugin.getContextManager().saveActivityMetaContext();
	}

}
