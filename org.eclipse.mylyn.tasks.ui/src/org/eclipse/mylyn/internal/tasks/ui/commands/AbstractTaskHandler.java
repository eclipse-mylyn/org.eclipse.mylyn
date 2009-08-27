/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskHandler extends AbstractHandler {

	protected boolean recurse;

	protected boolean singleTask;

	public AbstractTaskHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null || selection.isEmpty()) {
			selection = HandlerUtil.getCurrentSelection(event);
		}
		boolean processed = process(event, selection);
		if (!processed) {
			// fall back to processing task currently visible in the editor
			IWorkbenchPart part = HandlerUtil.getActivePart(event);
			if (part instanceof TaskEditor) {
				selection = new StructuredSelection(((TaskEditor) part).getTaskEditorInput().getTask());
				processed = process(event, selection);
			}
		}
		return null;
	}

	private boolean process(ExecutionEvent event, ISelection selection) throws ExecutionException {
		boolean processed = false;
		if (selection instanceof IStructuredSelection) {
			Object[] items = ((IStructuredSelection) selection).toArray();
			if (singleTask) {
				if (items.length == 1 && items[0] instanceof ITask) {
					processed |= process(event, items[0], false);
				}
			} else {
				for (Object item : items) {
					processed |= process(event, item, recurse);
				}
			}
		}
		return processed;
	}

	private boolean process(ExecutionEvent event, Object item, boolean recurse) throws ExecutionException {
		if (!(item instanceof IRepositoryElement)) {
			item = Platform.getAdapterManager().getAdapter(item, ITask.class);
		}
		if (item instanceof ITask) {
			execute(event, (ITask) item);
			return true;
		}
		if (item instanceof ITaskContainer && (recurse || !(item instanceof AbstractTask))) {
			execute(event, (ITaskContainer) item);
			return true;
		}
		return false;
	}

	protected void execute(ExecutionEvent event, ITaskContainer item) throws ExecutionException {
		for (ITask task : item.getChildren()) {
			process(event, task, true);
		}
	}

	protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
	}

}
