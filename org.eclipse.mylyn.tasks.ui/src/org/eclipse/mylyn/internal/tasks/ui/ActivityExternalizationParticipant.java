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
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationParticipant;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskActivityAdapter;

/**
 * @author Rob Elves
 */
public class ActivityExternalizationParticipant extends TaskActivityAdapter implements IExternalizationParticipant {

	private boolean isDirty = false;

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

	public String getDescription() {
		return "Activity Context";
	}

	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.ACTIVITY_SCHEDULING_RULE;
	}

	public boolean isDirty() {
		synchronized (this) {
			return isDirty;
		}
	}

	@Override
	public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
		setDirty(true);
	}

	private void setDirty(boolean dirty) {
		synchronized (this) {
			isDirty = dirty;
		}
	}

}
