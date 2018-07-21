/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.ui.Messages;
import org.eclipse.mylyn.internal.tasks.ui.TaskListModifyOperation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Changes a set of old handles to a set of new handles in the Activity Meta Context.
 *
 * @author Rob Elves
 */
public class ChangeActivityHandleOperation extends TaskListModifyOperation {

	private final Map<String, String> handles;

	public ChangeActivityHandleOperation(String oldHandle, String newHandle) {
		this(Collections.singletonMap(oldHandle, newHandle));
	}

	public ChangeActivityHandleOperation(Map<String, String> handles) {
		this.handles = handles;
	}

	@Override
	protected void operations(IProgressMonitor monitor)
			throws CoreException, InvocationTargetException, InterruptedException {
		Map<String, String> changedHandles = handles.entrySet()
				.stream()
				.filter(e -> !e.getKey().equals(e.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		if (changedHandles.isEmpty()) {
			return;
		}
		try {
			refactorMetaContextHandles(changedHandles, monitor);
			TasksUiPlugin.getTaskActivityMonitor().reloadActivityTime();
		} finally {
			monitor.done();
		}
	}

	@SuppressWarnings("restriction")
	private void refactorMetaContextHandles(Map<String, String> oldToNewHandles, IProgressMonitor monitor) {
		ContextCorePlugin.getContextManager().saveActivityMetaContext();
		InteractionContext metaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		ContextCorePlugin.getContextManager().resetActivityMetaContext();
		InteractionContext newMetaContext = ContextCorePlugin.getContextManager().getActivityMetaContext();
		List<InteractionEvent> interactionHistory = metaContext.getInteractionHistory();
		monitor.beginTask(Messages.ChangeActivityHandleOperation_Activity_migration, interactionHistory.size());
		for (InteractionEvent event : interactionHistory) {
			if (event.getStructureHandle() != null) {
				String newHandle = oldToNewHandles.get(event.getStructureHandle());
				if (newHandle != null) {
					event = new InteractionEvent(event.getKind(), event.getStructureKind(), newHandle,
							event.getOriginId(), event.getNavigation(), event.getDelta(),
							event.getInterestContribution(), event.getDate(), event.getEndDate());
				}
			}
			newMetaContext.parseEvent(event);
			monitor.worked(1);
		}
		ContextCorePlugin.getContextManager().saveActivityMetaContext();
	}

}
