/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.tasks.ui;

import java.io.File;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext;
import org.eclipse.mylyn.internal.tasks.ui.Messages;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;

/**
 * @author Rob Elves
 */
@SuppressWarnings("restriction")
public class ActivityExternalizationParticipant extends AbstractExternalizationParticipant
		implements ITaskActivityListener {

	private boolean isDirty = false;

	private final ExternalizationManager manager;

	private long lastUpdate;

	public ActivityExternalizationParticipant(ExternalizationManager manager) {
		this.manager = manager;
		MonitorUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(event -> {
			if (event.getProperty().equals(MonitorUiPlugin.ACTIVITY_TRACKING_ENABLED)) {
				requestSave();
			}
		});
	}

	@Override
	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		switch (context.getKind()) {
			case SAVE:
				if (ContextCorePlugin.getDefault() != null && MonitorUiPlugin.getDefault().isActivityTrackingEnabled()
						&& ContextCorePlugin.getContextManager() != null) {
					setDirty(false);
					ContextCorePlugin.getContextManager().saveActivityMetaContext();
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
		return Messages.ActivityExternalizationParticipant_Activity_Context;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.ACTIVITY_SCHEDULING_RULE;
	}

	@Override
	public boolean isDirty() {
		return isDirty(false);
	}

	@Override
	public boolean isDirty(boolean full) {
		synchronized (this) {
			return isDirty || full;
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

	@Override
	public void activityReset() {
		// ignore see execute method
	}

	@Override
	public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
		if (System.currentTimeMillis() - lastUpdate > 1000 * 60) {
			requestSave();
		}
	}

	private void requestSave() {
		setDirty(true);
		manager.requestSave();
		lastUpdate = System.currentTimeMillis();
	}

}
