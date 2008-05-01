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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.ScreenshotCreationPage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.swt.SWT;
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
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class TaskAttachmentWizard extends Wizard {

	static class ClipboardSource extends AbstractTaskAttachmentSource {

		private Object contents;

		public ClipboardSource() {
			Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
			contents = clipboard.getContents(ImageTransfer.getInstance());
			if (contents == null) {
				contents = clipboard.getContents(TextTransfer.getInstance());
			}
			clipboard.dispose();
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
			return "";
		}

		@Override
		public String getDescription() {
			return "Clipboard";
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

	};

	static class FileSource extends AbstractTaskAttachmentSource {

		private final File file;

		public FileSource(File file) {
			this.file = file;
		}

		@Override
		public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
		}

		@Override
		public String getContentType() {
			return null;
		}

		@Override
		public String getDescription() {
			return getName();
		}

		@Override
		public long getLength() {
			return file.length();
		}

		@Override
		public String getName() {
			return file.getName();
		}

		@Override
		public boolean isLocal() {
			return true;
		}

	}

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

	private Mode mode = Mode.DEFAULT;

	private final TaskAttachmentModel model;

	private IWizardPage editPage;

	public TaskAttachmentWizard(TaskRepository taskRepository, AbstractTask task, TaskAttribute taskAttachment) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(taskAttachment);
		this.model = new TaskAttachmentModel(taskRepository, task, taskAttachment);
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
	}

	public TaskAttachmentModel getModel() {
		return model;
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == editPage) {
			PreviewAttachmentPage2 previewPage = new PreviewAttachmentPage2(model);
			previewPage.setWizard(this);
			return previewPage;
		}
		return super.getNextPage(page);
	}

	public AbstractTaskAttachmentSource getSource() {
		return model.getSource();
	}

//	private void handleSubmitError(final CoreException exception) {
//		if (exception.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
//			if (TasksUiUtil.openEditRepositoryWizard(taskRepository) == Window.OK) {
//				// performFinish();
//			}
//		} else {
//			TasksUiInternal.displayStatus("Attachment failed", exception.getStatus());
//		}
//	}

	@Override
	public boolean performFinish() {
//		attachPage.populateAttachment();
//		final String path = inputPage.getAbsoluteAttachmentPath();
		// upload the attachment
//		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
//				taskRepository.getConnectorKind());
//		final AbstractAttachmentHandler attachmentHandler = connector.getAttachmentHandler();
//		if (attachmentHandler == null) {
//			return false;
//		}
//
////		final boolean attachContext = attachPage.getAttachContext();
//
//		final SubmitTaskAttachmentJob job = new SubmitTaskAttachmentJob(connector, taskRepository, taskAttachment);
//		try {
//			getContainer().run(true, true, new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//					job.run(monitor);
//				}
//			});
//		} catch (InvocationTargetException e) {
//			if (e.getCause() instanceof CoreException) {
//				handleSubmitError((CoreException) e.getCause());
//			} else {
//				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Attachment failure", e));
//			}
//			return false;
//		} catch (InterruptedException e) {
//			// cancelled
//			return false;
//		}

		return true;
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
