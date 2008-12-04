/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
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
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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

		private byte[] data;

		public static boolean isSupportedType(Display display) {
			Clipboard clipboard = new Clipboard(display);
			TransferData[] types = clipboard.getAvailableTypes();
			for (TransferData transferData : types) {
				List<Transfer> transfers = getTransfers();
				for (Transfer transfer : transfers) {
					if (transfer.isSupportedType(transferData)) {
						return true;
					}
				}
			}
			return false;
		}

		private static List<Transfer> transfers;

		private static List<Transfer> getTransfers() {
			if (transfers != null) {
				return transfers;
			}

			transfers = new ArrayList<Transfer>();
			try {
				Class<?> clazz = Class.forName("org.eclipse.swt.dnd.ImageTransfer"); //$NON-NLS-1$
				Method method = clazz.getMethod("getInstance"); //$NON-NLS-1$
				if (method != null) {
					transfers.add((Transfer) method.invoke(null));
				}
			} catch (Exception e) {
				// ignore
			} catch (LinkageError e) {
				// ignore
			}
			transfers.add(TextTransfer.getInstance());
			return transfers;
		}

		private Object contents;

		public ClipboardTaskAttachmentSource() {
			BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), new Runnable() {
				public void run() {
					Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
					List<Transfer> transfers = getTransfers();
					for (Transfer transfer : transfers) {
						contents = clipboard.getContents(transfer);
						if (contents != null) {
							break;
						}
					}
					clipboard.dispose();
				}
			});
		}

		@Override
		public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
			byte[] bytes = getData();
			if (bytes != null) {
				return new ByteArrayInputStream(data);
			}
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Invalid content type.")); //$NON-NLS-1$
		}

		@Override
		public String getContentType() {
			if (contents instanceof String) {
				return "text/plain"; //$NON-NLS-1$
			} else if (contents instanceof ImageData) {
				return "image/png"; //$NON-NLS-1$
			}
			return "application/octet-stream"; //$NON-NLS-1$
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public long getLength() {
			byte[] bytes = getData();
			return (bytes != null) ? bytes.length : -1;
		}

		private byte[] getData() {
			if (data == null) {
				if (contents instanceof String) {
					data = ((String) contents).getBytes();
				} else if (contents instanceof ImageData) {
					ImageLoader loader = new ImageLoader();
					loader.data = new ImageData[] { (ImageData) contents };
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					loader.save(out, SWT.IMAGE_PNG);
					data = out.toByteArray();
				}
			}
			return data;
		}

		@Override
		public String getName() {
			if (contents instanceof String) {
				return "clipboard.txt"; //$NON-NLS-1$
			} else if (contents instanceof ImageData) {
				return "clipboard.png"; //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
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
						file = File.createTempFile("screenshot", ".png"); //$NON-NLS-1$ //$NON-NLS-2$
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
			return "image/png"; //$NON-NLS-1$
		}

		@Override
		public String getDescription() {
			return Messages.TaskAttachmentWizard_Screenshot;
		}

		@Override
		public long getLength() {
			return (file != null) ? file.length() : -1;
		}

		@Override
		public String getName() {
			return "screenshot.png"; //$NON-NLS-1$
		}

		@Override
		public boolean isLocal() {
			return true;
		}

	}

	public enum Mode {
		DEFAULT, SCREENSHOT
	}

	private static final String DIALOG_SETTINGS_KEY = "AttachmentWizard"; //$NON-NLS-1$

	private final AbstractRepositoryConnector connector;

	private IWizardPage editPage;

	private Mode mode = Mode.DEFAULT;

	private final TaskAttachmentModel model;

	private AttachmentPreviewPage previewPage;

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
				addPage(new AttachmentSourcePage(model));
			}
		}
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(model.getTaskRepository()
				.getConnectorKind());
		editPage = connectorUi.getTaskAttachmentPage(model);
		addPage(editPage);

		previewPage = new AttachmentPreviewPage(model);
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
		if (job.getStatus() != null) {
			TasksUiInternal.displayStatus(Messages.TaskAttachmentWizard_Attachment_Failed, job.getStatus());
		}
	}

	@Override
	public boolean canFinish() {
		// InputAttachmentSourcePage relies on getNextPage() being called, do not allow wizard to finish on first page
		if (getContainer() != null && getContainer().getCurrentPage() instanceof AttachmentSourcePage) {
			return false;
		}
		return super.canFinish();
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
					monitor.subTask(Messages.TaskAttachmentWizard_Attaching_context);
					AttachmentUtil.postContext(connector, model.getTaskRepository(), model.getTask(), null, null,
							monitor);
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
						if (job.getStatus() != null) {
							getContainer().getShell().setVisible(true);
						}
						handleDone(job);
						if (job.getStatus() == null) {
							getContainer().getShell().close();
						}
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
			return job.getStatus() == null;
		} catch (InvocationTargetException e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unexpected error", e)); //$NON-NLS-1$
			return false;
		} catch (InterruptedException e) {
			// canceled
			return false;
		}
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		if (mode == Mode.SCREENSHOT) {
			setWindowTitle(Messages.TaskAttachmentWizard_Attach_Screenshot);
			setDefaultPageImageDescriptor(CommonImages.BANNER_SCREENSHOT);
		} else {
			setWindowTitle(Messages.TaskAttachmentWizard_Add_Attachment);
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		}
	}

	public void setSource(AbstractTaskAttachmentSource source) {
		this.model.setSource(source);
	}

}
