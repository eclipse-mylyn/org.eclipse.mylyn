/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;

/**
 * @author Rob Elves
 */
public class ActivityExternalizationParticipant extends AbstractExternalizationParticipant implements
		ITaskActivityListener {

	private boolean isDirty = false;

	@Override
	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		switch (context.getKind()) {
		case SAVE:
			ContextCore.getContextManager().saveActivityContext();
			setDirty(false);
			break;
		case LOAD:
			break;
		case SNAPSHOT:
			break;
		}
	}

	@Override
	public String getDescription() {
		return "Activity Context";
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.ACTIVITY_SCHEDULING_RULE;
	}

	@Override
	public boolean isDirty() {
		synchronized (this) {
			return isDirty;
		}
	}

	private void setDirty(boolean dirty) {
		synchronized (this) {
			isDirty = dirty;
		}
	}

	@Override
	public String getFileName() {
		// ignore
		return null;
	}

	@Override
	public void load(String rootPath, IProgressMonitor monitor) throws CoreException {
		// ignore

	}

	@Override
	public void save(String rootPath, IProgressMonitor monitor) throws CoreException {
		ContextCore.getContextManager().saveActivityContext();
		setDirty(false);
	}

	public void activityReset() {
		// ignore

	}

	public void preTaskActivated(ITask task) {
		// ignore

	}

	public void preTaskDeactivated(ITask task) {
		// ignore

	}

	public void taskActivated(ITask task) {
		// ignore

	}

	public void taskDeactivated(ITask task) {
		// ignore

	}

	public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
		setDirty(true);
	}

}
