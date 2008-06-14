/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;

/**
 * @author Rob Elves
 */
public class ActivityExternalizationParticipant extends AbstractExternalizationParticipant implements
		ITaskActivityListener {

	private boolean isDirty = false;

	private final ExternalizationManager manager;

	private long lastUpdate;

	public ActivityExternalizationParticipant(ExternalizationManager manager) {
		this.manager = manager;
	}

	@SuppressWarnings("restriction")
	@Override
	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		switch (context.getKind()) {
		case SAVE:
			if (ContextCorePlugin.getDefault() != null && ContextCorePlugin.getContextManager() != null) {
				ContextCorePlugin.getContextManager().saveActivityMetaContext();
				setDirty(false);
			}
			break;
		case LOAD:
			ContextCorePlugin.getContextManager().loadActivityMetaContext();
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

	public void setDirty(boolean dirty) {
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
	public void load(File sourceFile, IProgressMonitor monitor) throws CoreException {
		// ignore see execute method
	}

	@Override
	public void save(File targetFile, IProgressMonitor monitor) throws CoreException {
		// ignore see execute method
	}

	public void activityReset() {
		// ignore see execute method
	}

	public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
		if (System.currentTimeMillis() - lastUpdate > 1000 * 60) {
			setDirty(true);
			manager.requestSave();
			lastUpdate = System.currentTimeMillis();
		}
	}

}
