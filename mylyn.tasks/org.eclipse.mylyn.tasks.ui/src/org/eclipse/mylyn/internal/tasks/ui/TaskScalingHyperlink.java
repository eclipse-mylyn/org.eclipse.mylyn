/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Frank Becker
 */
public class TaskScalingHyperlink extends ScalingHyperlink {

	private ITask task;

	public TaskScalingHyperlink(Composite parent, int style) {
		super(parent, style);
		setForeground(CommonColors.HYPERLINK_WIDGET);
		addMouseTrackListener(MOUSE_TRACK_LISTENER);
	}

	public ITask getTask() {
		return task;
	}

	public void setTask(ITask task) {
		this.task = task;
		if (task != null) {
			if ((getStyle() & SWT.SHORT) != 0) {
				setText(task.getTaskKey());
				setToolTipText(task.getTaskKey() + ": " + task.getSummary()); //$NON-NLS-1$
				setStrikeThrough(task.isCompleted());
			} else {
				setText(task.getSummary());
				setToolTipText(""); //$NON-NLS-1$
				setStrikeThrough(false);
			}
		} else {
			setText(""); //$NON-NLS-1$
			setToolTipText(""); //$NON-NLS-1$
			setStrikeThrough(false);
		}
		setUnderlined(false);
	}

}
