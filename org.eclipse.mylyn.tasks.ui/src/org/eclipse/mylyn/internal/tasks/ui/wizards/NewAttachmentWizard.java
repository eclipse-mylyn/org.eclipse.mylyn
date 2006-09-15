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

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.LocalAttachment;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard to add a new attachment to a task report.
 * 
 * @author Jeff Pound
 */
public class NewAttachmentWizard extends Wizard {

	private static final String DIALOG_SETTINGS_KEY = "AttachmentWizard";

	private LocalAttachment attachment;

	private InputAttachmentSourcePage inputPage;

	private NewAttachmentPage attachPage;

	private NewAttachmentWizardDialog dialog;

	private boolean hasNewDialogSettings;

	private TaskRepository repository;

	private AbstractRepositoryTask task;

	public NewAttachmentWizard(TaskRepository repository, AbstractRepositoryTask task) {
		super();
		this.task = task;
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle("Add Attachment");
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY);
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

	public NewAttachmentWizard(TaskRepository repository, AbstractRepositoryTask task, File attachFile) {
		this(repository, task);
		attachment.setFilePath(attachFile.getAbsolutePath());
	}

	public NewAttachmentWizard(TaskRepository repository, AbstractRepositoryTask task, String attachContents) {
		this(repository, task);
		inputPage.setUseClipboard(true);
		inputPage.setClipboardContents(attachContents);
		attachment.setFilePath(InputAttachmentSourcePage.CLIPBOARD_LABEL);
	}

	@Override
	public boolean performFinish() {
		/* TODO jpound - support non-text in clipboard */
		attachPage.populateAttachment();
		String path = inputPage.getAbsoluteAttachmentPath();
		if (InputAttachmentSourcePage.CLIPBOARD_LABEL.equals(path)) {
			// write temporary file
			String contents = inputPage.getClipboardContents();
			if (contents == null) {
				// TODO Handle error
			}

			File file = new File(TasksUiPlugin.getDefault().getDefaultDataDirectory()
					+ System.getProperty("file.separator").charAt(0) + "Clipboard-attachment");
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(contents);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Handle error
			}
			path = file.getAbsolutePath();
			attachment.setDeleteAfterUpload(true);
		}
		attachment.setFilePath(path);

		// Save the dialog settings
		if (hasNewDialogSettings) {
			IDialogSettings workbenchSettings = TasksUiPlugin.getDefault().getDialogSettings();
			IDialogSettings section = workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
			section = workbenchSettings.addNewSection(DIALOG_SETTINGS_KEY);
			setDialogSettings(section);
		}

		// upload the attachment
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());
		final IAttachmentHandler attachmentHandler = connector.getAttachmentHandler();
		if (attachmentHandler == null) {
			return false;
		}
		final boolean attachContext = attachPage.getAttachContext();

		Job submitJob = new Job("Submitting attachment") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
						.getRepositoryConnector(repository.getKind());
				try {
					attachmentHandler.uploadAttachment(repository, task, attachment.getComment(), attachment
							.getDescription(), new File(attachment.getFilePath()), attachment.getContentType(),
							attachment.isPatch(), TasksUiPlugin.getDefault().getProxySettings());

					if (attachment.getDeleteAfterUpload()) {
						File file = new File(attachment.getFilePath());
						if (!file.delete()) {
							// TODO: Handle bad clean up
						}
					}

					if (attachContext) {
						connector.attachContext(repository, (AbstractRepositoryTask) task, "", TasksUiPlugin.getDefault().getProxySettings());
						// attachContext sets outgoing state but we want to recieve incoming
						// on synchronization. This could result in lost edits so need to 
						// review the whole attachment interaction.
						task.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
					}

				} catch (final CoreException e) {
					if (e.getStatus().getCode() == Status.INFO) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								WebBrowserDialog.openAcceptAgreement(null, "Upload Error", e.getStatus().getMessage(),
										e.getStatus().getException().getMessage());
							}
						});
					} else if (e.getStatus().getCode() == Status.ERROR) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(null, "Upload Error", e.getStatus().getMessage());
							}
						});
					}
				}
				
				TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, false, new JobChangeAdapter() {
					public void done(final IJobChangeEvent event) {
						if (event.getResult().getException() != null) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(Display.getDefault().getActiveShell(),
											TasksUiPlugin.TITLE_DIALOG, event.getResult().getMessage());
								}
							});
						}
					}
				});
				return Status.OK_STATUS;
			}
		};

		submitJob.schedule();
		return true;
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

	public IWizardPage getNextPage(IWizardPage page) {
		attachPage.setFilePath(inputPage.getAttachmentName());
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

	public boolean needsPreviousAndNextButtons() {
		return true;
	}
}
