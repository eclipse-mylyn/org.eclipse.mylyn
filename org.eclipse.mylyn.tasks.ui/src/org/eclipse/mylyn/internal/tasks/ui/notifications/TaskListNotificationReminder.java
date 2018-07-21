/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListNotificationReminder extends TaskListNotification {

	private final static String ID_EVENT_TASK_DUE = "org.eclipse.mylyn.tasks.ui.events.TaskDue"; //$NON-NLS-1$

	public TaskListNotificationReminder(AbstractTask task) {
		super(ID_EVENT_TASK_DUE, task);
	}

	@Override
	public Image getNotificationKindImage() {
		return CommonImages.getImage(CommonImages.OVERLAY_DATE_DUE);
	}
}
