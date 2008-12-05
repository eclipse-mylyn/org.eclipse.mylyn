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

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.Date;

import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotification;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Rob Elves
 */
public class TaskListNotificationQueryIncoming extends TaskListNotification {

	public TaskListNotificationQueryIncoming(ITask task) {
		super(task);
	}

	@Override
	public String getDescription() {
		return task.getSummary();
	}

	@Override
	public int compareTo(AbstractNotification anotherNotification) throws ClassCastException {
		if (!(anotherNotification != null)) {
			throw new ClassCastException("A ITaskListNotification object expected."); //$NON-NLS-1$
		}
		Date anotherDate = (anotherNotification).getDate();
		if (date != null && anotherDate != null) {
			return date.compareTo(anotherDate);
		} else if (date == null) {
			return -1;
		} else {
			return 1;
		}
	}
}
