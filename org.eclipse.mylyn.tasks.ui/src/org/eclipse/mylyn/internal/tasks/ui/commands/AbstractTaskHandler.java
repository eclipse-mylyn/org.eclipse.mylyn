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

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractTaskHandler extends AbstractHandler {

	protected boolean recurse;

	public AbstractTaskHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object[] items = ((IStructuredSelection) selection).toArray();
			for (Object item : items) {
				process(event, item, recurse);
			}
		}
		return null;
	}

	private void process(ExecutionEvent event, Object item, boolean recurse) throws ExecutionException {
		if (item instanceof ITask) {
			execute(event, (ITask) item);
		}
		if (item instanceof ITaskContainer && (recurse || !(item instanceof AbstractTask))) {
			execute(event, (ITaskContainer) item);
		}
	}

	protected void execute(ExecutionEvent event, ITaskContainer item) throws ExecutionException {
		for (ITask task : item.getChildren()) {
			process(event, task, true);
		}
	}

	protected void execute(ExecutionEvent event, ITask task) throws ExecutionException {
	}

}
