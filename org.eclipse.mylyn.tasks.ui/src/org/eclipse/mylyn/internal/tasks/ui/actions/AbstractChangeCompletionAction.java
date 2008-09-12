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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 */
public class AbstractChangeCompletionAction extends Action {

	protected boolean shouldEnable(List<IRepositoryElement> selectedElements) {
		boolean allLocalTasks = true;
		for (IRepositoryElement abstractTaskContainer : selectedElements) {
			if (!(abstractTaskContainer instanceof ITask)) {
				allLocalTasks = false;
				break;
			} else if (!((AbstractTask) abstractTaskContainer).isLocal()) {
				allLocalTasks = false;
				break;
			}
		}
		return allLocalTasks;
	}

	protected String generateMessage(List<AbstractTask> toComplete, String status) {
		String message = "Mark selected local tasks " + status + "?\n\n";
		int i = 0;
		for (ITask task : toComplete) {
			i++;
			if (i < 20) {
				message += "    ";
				if (task.getTaskKey() != null) {
					message += task.getTaskKey() + ": ";
				}
				message += task.getSummary() + "\n";
			} else {
				message += "...";
				break;
			}
		}
		return message;
	}
}
