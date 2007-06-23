/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard to add a new attachment to a task report.
 * 
 * @author Jeff Pound
 */
public class NewAttachmentWizard extends Wizard {

	private static final String DIALOG_SETTINGS_KEY = "AttachmentWizard";

	protected static final String CLIPBOARD_FILENAME = "clipboard.txt";

	private LocalAttachment attachment;

	private InputAttachmentSourcePage inputPage;

	private NewAttachmentPage attachPage;

	private NewAttachmentWizardDialog dialog;

	private boolean hasNewDialogSettings;

	private TaskRepository repository;

	private AbstractTask task;

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task) {
		super();
		this.task = task;
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle("Add Attachment");
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		attachment = new LocalAttachment();
		attachment.setFilePath("");
		inputPage = new InputAttachmentSourcePage(this);

		IDialogSettings workbenchSettings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		if (section == null) {
			hasNewDialogSettings = true;
		} else {
			hasNewDialogSettings = false;
			setDialogSettings(section);
		}
	}

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task, File attachFile) {
		this(repository, task);
		attachment.setFilePath(attachFile.getAbsolutePath());
	}

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task, String attachContents) {
		this(repository, task);
		inputPage.setUseClipboard(true);
		inputPage.setClipboardContents(attachContents);
		attachment.setFilePath(InputAttachmentSourcePage.CLIPBOARD_LABEL);
	}

	@Override
	public boolean performFinish() {
		/* TODO jpound - support non-text in clipboard */
		attachPage.populateAttachment();
		final String path = inputPage.getAbsoluteAttachmentPath();

		// Save the dialog settings
		if (hasNewDialogSettings) {
			IDialogSettings workbenchSettings = TasksUiPlugin.getDefault().getDialogSettings();
			IDialogSettings section = workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
			section = workbenchSettings.addNewSection(DIALOG_SETTINGS_KEY);
			setDialogSettings(section);
		}

		// upload the attachment
		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		final AbstractAttachmentHandler attachmentHandler = connector.getAttachmentHandler();
		if (attachmentHandler == null) {
			return false;
		}

		final boolean attachContext = attachPage.getAttachContext();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					monitor.beginTask("Attaching file...", 2);
					task.setSubmitting(true);
					task.setSynchronizationState(RepositoryTaskSyncState.OUTGOING);					

					if (InputAttachmentSourcePage.CLIPBOARD_LABEL.equals(path)) {
						String contents = inputPage.getClipboardContents();
						if (contents == null) {
							throw new InvocationTargetException(new CoreException(new RepositoryStatus(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, RepositoryStatus.ERROR_INTERNAL, "Clipboard is empty", null)));
						}
						attachment.setContent(contents.getBytes());
						attachment.setFilename(CLIPBOARD_FILENAME);
					} else {
						File file = new File(path);
						attachment.setFile(file);
						attachment.setFilename(file.getName());
					}
					
					attachmentHandler.uploadAttachment(repository, task, attachment, attachment.getComment(), 
							new SubProgressMonitor(monitor, 1));

					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}

					if (attachContext && connector.getAttachmentHandler() != null) {
						connector.getAttachmentHandler().attachContext(repository, task, "", new SubProgressMonitor(monitor, 1));
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}

			}

		};

		try {
			getContainer().run(true, true, op);

			TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, false, new JobChangeAdapter() {
				@Override
				public void done(final IJobChangeEvent event) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (event.getResult().getException() != null) {

								MessageDialog.openError(Display.getDefault().getActiveShell(),
										ITasksUiConstants.TITLE_DIALOG, event.getResult().getMessage());

							}
							forceRefreshInplace(task);
						}
					});
				}
			});

		} catch (InvocationTargetException e1) {
			task.setSubmitting(false);
			task.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
			if (e1.getCause() != null && e1.getCause() instanceof CoreException) {
				handleSubmitError((CoreException) e1.getCause());

			} else {
				StatusManager.fail(e1, "Attachment failure", true);
			}
			return false;
		} catch (InterruptedException e1) {
			task.setSubmitting(false);
			task.setSynchronizationState(RepositoryTaskSyncState.SYNCHRONIZED);
		}

		return true;
	}

	/**
	 * If task is open, force inplace refresh Must be called from UI thread.
	 */
	public static boolean forceRefreshInplace(AbstractTask task) {
		if (task instanceof AbstractTask) {
			String handleTarget = task.getHandleIdentifier();
			for (TaskEditor editor : TasksUiUtil.getActiveRepositoryTaskEditors()) {
				if (editor.getEditorInput() instanceof AbstractRepositoryTaskEditorInput) {
					AbstractRepositoryTaskEditorInput input = (AbstractRepositoryTaskEditorInput) editor.getEditorInput();
					if (input.getTaskData() != null) {
						String handle = RepositoryTaskHandleUtil.getHandle(input.getTaskData().getRepositoryUrl(),
								input.getTaskData().getId());
						if (handle.equals(handleTarget)) {
							editor.refreshEditorContents();
							editor.getEditorSite().getPage().activate(editor);
							return true;
						}
					}
				} else if (editor.getEditorInput() instanceof TaskEditorInput) {
					TaskEditorInput input = (TaskEditorInput) editor.getEditorInput();
					if (input.getTask().getHandleIdentifier().equals(handleTarget)) {
						editor.refreshEditorContents();
						editor.getEditorSite().getPage().activate(editor);
						return true;
					}
				}
			}
		}
		return false;
	}

	private void handleSubmitError(final CoreException exception) {
		if (exception.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
			if (TasksUiUtil.openEditRepositoryWizard(repository) == MessageDialog.OK) {
				// performFinish();
			}
		} else {
			StatusManager.displayStatus("Attachment failed", exception.getStatus());
		}
	}

	protected boolean hasContext() {
		return ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier());
	}

	@Override
	public boolean canFinish() {
		return attachPage.isPageComplete();
	}

	@Override
	public void addPages() {
		super.addPages();
		if ("".equals(attachment.getFilePath())) {
			addPage(inputPage);
		}
		addPage((attachPage = new NewAttachmentPage(attachment)));
	}

	public LocalAttachment getAttachment() {
		return attachment;
	}

	protected String getFilePath() {
		return inputPage.getAttachmentName();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == inputPage) {
			attachPage.setFilePath(inputPage.getAttachmentName());
		}
		return super.getNextPage(page);
	}

	public void showPage(IWizardPage page) {
		dialog.showPage(page);
	}

	public void setDialog(NewAttachmentWizardDialog dialog) {
		this.dialog = dialog;
	}

	public String getClipboardContents() {
		return inputPage.getClipboardContents();
	}

	@Override
	public boolean needsPreviousAndNextButtons() {
		return true;
	}
}
