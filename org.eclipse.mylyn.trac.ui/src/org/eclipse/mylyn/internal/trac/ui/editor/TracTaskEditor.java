/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractTaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.InvalidTicketException;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditor extends AbstractRepositoryTaskEditor {

	private static final String SUBMIT_JOB_LABEL = "Submitting to Trac repository";

	private TracRepositoryConnector connector;

	public TracTaskEditor(FormEditor editor) {
		super(editor);
	}

	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof RepositoryTaskEditorInput))
			return;

		editorInput = (AbstractTaskEditorInput) input;
		repository = editorInput.getRepository();
		connector = (TracRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());

		setSite(site);
		setInput(input);

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(editorInput.getRepositoryTaskData());

		isDirty = false;
		updateEditorTitle();
	}

	@Override
	protected void submitToRepository() {
		if (isDirty()) {
			this.doSave(new NullProgressMonitor());
		}
		updateTask();
		submitButton.setEnabled(false);
		showBusy(true);

		final TracTicket ticket;
		try {
			ticket = TracRepositoryConnector.getTracTicket(repository, getRepositoryTaskData());
		} catch (InvalidTicketException e) {
			TracUiPlugin.handleTracException(e);
			return;
		}
		final String comment = getNewCommentText();
		final AbstractRepositoryTask task = (AbstractRepositoryTask) TasksUiPlugin.getTaskListManager().getTaskList()
				.getTask(AbstractRepositoryTask.getHandle(repository.getUrl(), getRepositoryTaskData().getId()));
		final boolean attachContext = getAttachContext();

		JobChangeAdapter listener = new JobChangeAdapter() {
			public void done(final IJobChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (event.getJob().getResult().isOK()) {
							if (attachContext) {
								attachContext();
							}
							close();
						} else {
							// TracUiPlugin.handleTracException(event.getResult());
							submitButton.setEnabled(true);
							TracTaskEditor.this.showBusy(false);
						}
					}
				});
			}
		};

		Job submitJob = new Job(SUBMIT_JOB_LABEL) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ITracClient server = connector.getClientManager().getRepository(repository);
					server.updateTicket(ticket, comment);
					if (task != null) {
						// XXX: HACK TO AVOID OVERWRITE WARNING
						task.setTaskData(null);
						TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
					}
					return Status.OK_STATUS;
				} catch (Exception e) {
					return TracCorePlugin.toStatus(e);
				}
			}

		};

		submitJob.addJobChangeListener(listener);
		submitJob.schedule();

	}

	@Override
	protected void validateInput() {
	}

	private void attachContext() {
		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), getRepositoryTaskData().getId());
		final AbstractRepositoryTask modifiedTask = (AbstractRepositoryTask) TasksUiPlugin.getTaskListManager()
				.getTaskList().getTask(handle);

		IProgressService ps = PlatformUI.getWorkbench().getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) {
					try {
						connector.attachContext(repository, modifiedTask, "");
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "Failed to attach task context.\n\n" + e.getMessage(), true);
					}
				}
			});
		} catch (InvocationTargetException e) {
			MylarStatusHandler.fail(e.getCause(), "Failed to attach task context.\n\n" + e.getMessage(), true);
		} catch (InterruptedException ignore) {
		}
	}

}
