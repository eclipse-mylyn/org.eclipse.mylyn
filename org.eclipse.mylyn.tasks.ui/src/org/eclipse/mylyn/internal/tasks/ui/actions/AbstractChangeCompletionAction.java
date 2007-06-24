/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Mik Kersten
 */
public class AbstractChangeCompletionAction extends Action {

	protected boolean shouldEnable(List<AbstractTaskContainer> selectedElements) {
		boolean allLocalTasks = true;
		for (AbstractTaskContainer abstractTaskContainer : selectedElements) {
			if (!(abstractTaskContainer instanceof AbstractTask)) {
				allLocalTasks = false;
				break;
			} else if (!((AbstractTask)abstractTaskContainer).isLocal()) {
				allLocalTasks = false;
				break;
			}
		}
		return allLocalTasks;
	}
	
	protected String generateMessage(List<AbstractTask> toComplete, String status) {
		String message = "Mark selected local tasks " + status + "?\n\n";
		int i = 0;
		for (AbstractTask task : toComplete) {
			i++;
			if (i < 20) {
				message += "    ";
				if (task.getTaskKey() != null) {
					message +=  task.getTaskKey() + ": ";
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
