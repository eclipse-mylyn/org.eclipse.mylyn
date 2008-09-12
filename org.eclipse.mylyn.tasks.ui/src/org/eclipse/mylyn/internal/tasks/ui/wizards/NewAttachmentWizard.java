/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.ScreenshotCreationPage;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard to add a new attachment to a task report.
 * 
 * @author Jeff Pound
 */
/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public class NewAttachmentWizard extends Wizard {

	private static final String DIALOG_SETTINGS_KEY = "AttachmentWizard";

	protected static final String CLIPBOARD_FILENAME = "clipboard.txt";

	protected static final String SCREENSHOT_FILENAME = "screenshot.jpg";

	private LocalAttachment attachment;

	private final InputAttachmentSourcePage inputPage;

	private NewAttachmentPage attachPage;

	private NewAttachmentWizardDialog dialog;

	private ScreenshotCreationPage shotPage;

	private boolean hasNewDialogSettings;

	private final TaskRepository repository;

	private final AbstractTask task;

	private final boolean screenshotMode;

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task, boolean screenshotMode) {
		this.task = task;
		this.repository = repository;
		this.screenshotMode = screenshotMode;

		if (screenshotMode) {
			setWindowTitle("Attach Screenshot");
			setDefaultPageImageDescriptor(CommonImages.BANNER_SCREENSHOT);
		} else {
			setWindowTitle("Add Attachment");
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		}

		inputPage = new InputAttachmentSourcePage(this);
		if (screenshotMode) {
			shotPage = new ScreenshotAttachmentPage();
			attachment = new ImageAttachment(shotPage);
		} else {
			attachment = new LocalAttachment();
		}
		attachment.setFilePath("");
		setNeedsProgressMonitor(true);
		IDialogSettings workbenchSettings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		if (section == null) {
			hasNewDialogSettings = true;
		} else {
			hasNewDialogSettings = false;
			setDialogSettings(section);
		}
	}

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task) {
		this(repository, task, false);
	}

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task, File attachFile) {
		this(repository, task, false);
		attachment.setFilePath(attachFile.getAbsolutePath());
	}

	public NewAttachmentWizard(TaskRepository repository, AbstractTask task, String attachContents) {
		this(repository, task, false);
		inputPage.setUseClipboard(true);
		inputPage.setClipboardContents(attachContents);
		attachment.setFilePath(InputAttachmentSourcePage.CLIPBOARD_LABEL);
	}

	@Override
	public void dispose() {
		// Ensures the temporary screenshot image is deleted
		if (attachment != null && attachment instanceof ImageAttachment) {
			((ImageAttachment) attachment).clearImageFile();
		}
		super.dispose();
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
		final AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(repository.getConnectorKind());
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
					task.setSynchronizationState(SynchronizationState.OUTGOING);

					if (screenshotMode || InputAttachmentSourcePage.SCREENSHOT_LABEL.equals(path)) {
						((ImageAttachment) attachment).ensureImageFileWasCreated();
					} else if (InputAttachmentSourcePage.CLIPBOARD_LABEL.equals(path)) {
						String contents = inputPage.getClipboardContents();
						if (contents == null) {
							throw new InvocationTargetException(new CoreException(new RepositoryStatus(IStatus.ERROR,
									TasksUiPlugin.ID_PLUGIN, RepositoryStatus.ERROR_INTERNAL, "Clipboard is empty",
									null)));
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
						AttachmentUtil.attachContext(connector.getAttachmentHandler(), repository, task, "",
								new SubProgressMonitor(monitor, 1));
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

			TasksUiInternal.synchronizeTask(connector, task, false, new JobChangeAdapter() {
				@Override
				public void done(final IJobChangeEvent event) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (event.getResult().getException() != null) {

								MessageDialog.openError(Display.getDefault().getActiveShell(), "Task Attachment",
										event.getResult().getMessage());

							}
							forceRefreshInplace(task);
						}
					});
				}
			});

		} catch (InvocationTargetException e1) {
			task.setSubmitting(false);
			task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
			if (e1.getCause() != null && e1.getCause() instanceof CoreException) {
				handleSubmitError((CoreException) e1.getCause());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Attachment failure", e1));
			}
			return false;
		} catch (InterruptedException e1) {
			task.setSubmitting(false);
			task.setSynchronizationState(SynchronizationState.SYNCHRONIZED);
		}

		return true;
	}

	/**
	 * If task is open, force inplace refresh Must be called from UI thread.
	 */
	public static boolean forceRefreshInplace(ITask task) {
		if (task != null) {
			String handleTarget = task.getHandleIdentifier();
			for (TaskEditor editor : TasksUiInternal.getActiveRepositoryTaskEditors()) {
				if (editor.getEditorInput() instanceof AbstractRepositoryTaskEditorInput) {
					AbstractRepositoryTaskEditorInput input = (AbstractRepositoryTaskEditorInput) editor.getEditorInput();
					if (input.getTaskData() != null) {
						String handle = RepositoryTaskHandleUtil.getHandle(input.getTaskData().getRepositoryUrl(),
								input.getTaskData().getTaskId());
						if (handle.equals(handleTarget)) {
							editor.refreshPages();
							editor.getEditorSite().getPage().activate(editor);
							return true;
						}
					}
				} else if (editor.getEditorInput() instanceof TaskEditorInput) {
					TaskEditorInput input = (TaskEditorInput) editor.getEditorInput();
					if (input.getTask().getHandleIdentifier().equals(handleTarget)) {
						editor.refreshPages();
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
			if (TasksUiUtil.openEditRepositoryWizard(repository) == Window.OK) {
				// performFinish();
			}
		} else {
			TasksUiInternal.displayStatus("Attachment failed", exception.getStatus());
		}
	}

	protected boolean hasContext() {
		return ContextCore.getContextManager().hasContext(task.getHandleIdentifier());
	}

	@Override
	public boolean canFinish() {
		if (screenshotMode) {
			return shotPage.isPageComplete() && attachPage.isPageComplete();
		} else {
			return attachPage.isPageComplete();
		}
	}

	@Override
	public void addPages() {
		super.addPages();
		if (screenshotMode) {
			addPage(shotPage);
			addPage((attachPage = new NewAttachmentPage(attachment)));
		} else {
			if ("".equals(attachment.getFilePath())) {
				addPage(inputPage);
			}
			addPage((attachPage = new NewAttachmentPage(attachment)));
//			addPage((shotPage = new ScreenshotAttachmentPage(attachment)));
		}
		// XXX wizard needs refactoring: bug 193156 
		attachPage.setSupportsDescription(!"jira".equals(task.getConnectorKind()));
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
