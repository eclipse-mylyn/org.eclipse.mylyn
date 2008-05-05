/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.ScreenshotCreationPage;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobListener;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard to add a new attachment to a task report.
 * 
 * @since 3.0
 * @author Steffen Pingel
 */
public class TaskAttachmentWizard extends Wizard {

	static class ClipboardTaskAttachmentSource extends AbstractTaskAttachmentSource {

		public static boolean isSupportedType(Display display) {
			Clipboard clipboard = new Clipboard(display);
			TransferData[] types = clipboard.getAvailableTypes();
			for (TransferData transferData : types) {
				if (ImageTransfer.getInstance().isSupportedType(transferData)
						|| TextTransfer.getInstance().isSupportedType(transferData)) {
					return true;
				}
			}
			return false;
		}

		private Object contents;

		public ClipboardTaskAttachmentSource() {
			BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), new Runnable() {
				public void run() {
					Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
					contents = clipboard.getContents(ImageTransfer.getInstance());
					if (contents == null) {
						contents = clipboard.getContents(TextTransfer.getInstance());
					}
					clipboard.dispose();
				}
			});
		}

		@Override
		public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
			if (contents instanceof String) {
				return new ByteArrayInputStream(((String) contents).getBytes());
			} else if (contents instanceof ImageData) {
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { (ImageData) contents };
				// TODO create image in memory?
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				loader.save(out, SWT.IMAGE_PNG);
				return new ByteArrayInputStream(out.toByteArray());
			}
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Invalid content type."));
		}

		@Override
		public String getContentType() {
			if (contents instanceof String) {
				return "text/plain";
			} else if (contents instanceof ImageData) {
				return "image/png";
			}
			return "application/octet-stream";
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public long getLength() {
			if (contents instanceof String) {
				return ((String) contents).length();
			}
			return -1;
		}

		@Override
		public String getName() {
			if (contents instanceof String) {
				return "clipboard.txt";
			} else if (contents instanceof ImageData) {
				return "clipboard.png";
			}
			return "";
		}

		@Override
		public boolean isLocal() {
			return true;
		}

	};

	static class ImageSource extends AbstractTaskAttachmentSource {

		private File file;

		private final ScreenshotCreationPage page;

		public ImageSource(ScreenshotCreationPage page) {
			this.page = page;
		}

		@Override
		public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
			try {
				if (file == null || page.isImageDirty()) {
					Image image = page.createImage();
					page.setImageDirty(false);
					try {
						file = File.createTempFile("screenshot", ".png");
						file.deleteOnExit();
						ImageLoader loader = new ImageLoader();
						loader.data = new ImageData[] { image.getImageData() };
						// TODO create image in memory?
						FileOutputStream out = new FileOutputStream(file);
						try {
							loader.save(out, SWT.IMAGE_PNG);
						} finally {
							out.close();
						}
					} finally {
						image.dispose();
					}
				}
				return new FileInputStream(file);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
		}

		@Override
		public String getContentType() {
			return "image/png";
		}

		@Override
		public String getDescription() {
			return "Screenshot";
		}

		@Override
		public long getLength() {
			return (file != null) ? file.length() : -1;
		}

		@Override
		public String getName() {
			return "screenshot.png";
		}

		@Override
		public boolean isLocal() {
			return true;
		}

	}

	public enum Mode {
		DEFAULT, SCREENSHOT
	}

	private static final String DIALOG_SETTINGS_KEY = "AttachmentWizard";

	private final AbstractRepositoryConnector connector;

	private IWizardPage editPage;

	private Mode mode = Mode.DEFAULT;

	private final TaskAttachmentModel model;

	private PreviewAttachmentPage2 previewPage;

	public TaskAttachmentWizard(TaskRepository taskRepository, ITask task, TaskAttribute taskAttachment) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(taskAttachment);
		this.model = new TaskAttachmentModel(taskRepository, task, taskAttachment);
		this.connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(taskRepository.getConnectorKind());
		setMode(Mode.DEFAULT);
		setNeedsProgressMonitor(true);
		setDialogSettings(TasksUiPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS_KEY));
	}

	@Override
	public void addPages() {
		if (model.getSource() == null) {
			if (mode == Mode.SCREENSHOT) {
				ScreenshotCreationPage page = new ScreenshotCreationPage();
				model.setSource(new ImageSource(page));
				addPage(page);
			} else {
				addPage(new InputAttachmentSourcePage2(model));
			}
		}
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(model.getTaskRepository()
				.getConnectorKind());
		editPage = connectorUi.getAttachmentPage(model);
		addPage(editPage);

		previewPage = new PreviewAttachmentPage2(model);
		addPage(previewPage);
	}

	public Mode getMode() {
		return mode;
	}

	public TaskAttachmentModel getModel() {
		return model;
	}

	public AbstractTaskAttachmentSource getSource() {
		return model.getSource();
	}

	private void handleDone(SubmitJob job) {
		if (job.getError() != null) {
			TasksUiInternal.displayStatus(getShell(), "Attachment Failed", job.getError());
		}
	}

	@Override
	public boolean performFinish() {
		SubmitJob job = TasksUiInternal.getJobFactory()
				.createSubmitTaskAttachmentJob(connector, model.getTaskRepository(), model.getTask(),
						model.getSource(), model.getComment(), model.getAttribute());
		final boolean attachContext = model.getAttachContext();
		job.addSubmitJobListener(new SubmitJobListener() {
			@Override
			public void done(SubmitJobEvent event) {
				// ignore
			}

			@Override
			public void taskSubmitted(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
				if (attachContext) {
					monitor.subTask("Attaching context");
					AttachmentUtil.postContext(connector, model.getTaskRepository(), model.getTask(), null, monitor);
				}
			}

			@Override
			public void taskSynchronized(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException {
				// ignore				
			}
		});
		if (previewPage.runInBackground()) {
			runInBackground(job);
			return false;
		} else {
			return runInWizard(job);
		}
	}

	private void runInBackground(final SubmitJob job) {
		getContainer().getShell().setVisible(false);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (job.getError() != null) {
							getContainer().getShell().setVisible(true);
						}
						handleDone(job);
					}
				});
			}
		});
		job.schedule();
	}

	private boolean runInWizard(final SubmitJob job) {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (((SubmitTaskAttachmentJob) job).run(monitor) == Status.CANCEL_STATUS) {
						throw new InterruptedException();
					}
				}
			});
			handleDone(job);
			return job.getError() == null;
		} catch (InvocationTargetException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unexpected error", e));
			return false;
		} catch (InterruptedException e) {
			// canceled
			return false;
		}
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		if (mode == Mode.SCREENSHOT) {
			setWindowTitle("Attach Screenshot");
			setDefaultPageImageDescriptor(CommonImages.BANNER_SCREENSHOT);
		} else {
			setWindowTitle("Add Attachment");
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		}
	}

	public void setSource(AbstractTaskAttachmentSource source) {
		this.model.setSource(source);
	}

}
