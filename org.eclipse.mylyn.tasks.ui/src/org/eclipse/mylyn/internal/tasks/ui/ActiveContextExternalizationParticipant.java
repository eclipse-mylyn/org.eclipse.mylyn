/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext;
import org.eclipse.mylyn.monitor.ui.IUserAttentionListener;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * This externalization participant only handles saving the active context periodically. No snapshots are taken and task
 * activation and deactivation control the load and final write of the context in InteractionContextManager.
 * 
 * @author Shawn Minto
 */
@SuppressWarnings("restriction")
public class ActiveContextExternalizationParticipant extends AbstractExternalizationParticipant implements
		ITaskActivityListener, IUserAttentionListener {
	private boolean isDirty = false;

	private final ExternalizationManager manager;

	private long lastUpdate;

	private IInteractionContext currentlyActiveContext;

	private final AbstractContextListener listener = new AbstractContextListener() {
		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
			case ACTIVATED:
				currentlyActiveContext = event.getContext();
				break;
			case DEACTIVATED:
				currentlyActiveContext = null;
				setDirty(false);
				break;
			}
		}
	};

	public ActiveContextExternalizationParticipant(ExternalizationManager manager) {
		this.manager = manager;
	}

	public void registerListeners() {
		ContextCore.getContextManager().addListener(listener);
		TasksUi.getTaskActivityManager().addActivityListener(this);
		(MonitorUiPlugin.getDefault().getActivityContextManager()).addListener(this);
		currentlyActiveContext = ContextCore.getContextManager().getActiveContext();
	}

	// currently not called since no way to remove a participant
	public void dispose() {
		ContextCore.getContextManager().removeListener(listener);
		TasksUi.getTaskActivityManager().removeActivityListener(this);
		if (MonitorUiPlugin.getDefault().getActivityContextManager() != null) {
			(MonitorUiPlugin.getDefault().getActivityContextManager()).removeListener(this);
		}
	}

	@Override
	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		switch (context.getKind()) {
		case SAVE:
			if (shouldWriteContext()) {
				setDirty(false);
				ContextCorePlugin.getContextManager().saveContext(currentlyActiveContext);
			}
			break;
		case LOAD:
			// ignore loads since we will do this synchronously with task activation
			break;
		case SNAPSHOT:
			// ignore snapshots
			break;
		}
	}

	@Override
	public String getDescription() {
		return Messages.ActiveContextExternalizationParticipant_Active_Task_Context;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.ACTIVE_CONTEXT_SCHEDULING_RULE;
	}

	@Override
	public boolean isDirty() {
		return isDirty(false);
	}

	@Override
	public boolean isDirty(boolean full) {
		synchronized (this) {
			return isDirty || (full && shouldWriteContext());
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

	public void elapsedTimeUpdated(ITask task, long newElapsedTime) {
		if (System.currentTimeMillis() - lastUpdate > 1000 * 60 * 3) {
			// TODO TYR TO CHECK IF IT IS DIRTY AND IT EXISTS
			setDirty(shouldWriteContext());
			if (isDirty()) {
				manager.requestSave();
			}
			lastUpdate = System.currentTimeMillis();
		}
	}

	private boolean shouldWriteContext() {
		if (ContextCorePlugin.getContextManager() != null && currentlyActiveContext != null
				&& currentlyActiveContext.getAllElements().size() > 0) {
			// we could add a check here for whether there were changes to the context
			return true;
		}
		return false;
	}

	public void activityReset() {
		// ignore
	}

	public void userAttentionGained() {
		// ignore
	}

	public void userAttentionLost() {
		setDirty(shouldWriteContext());
		if (isDirty()) {
			manager.requestSave();
		}
		lastUpdate = System.currentTimeMillis();
	}

}
