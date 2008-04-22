/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.SubmitTaskAttachmentJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachment;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
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

		@Override
		public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
			String content = getContent();
			if (content != null) {
				return new ByteArrayInputStream(content.getBytes());
			}
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Invalid content type."));
		}

		protected String getContent() {
			Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
			Object o = clipboard.getContents(TextTransfer.getInstance());
			clipboard.dispose();
			if (o instanceof String) {
				return (String) o;
			}
			return null;
		}

		@Override
		public String getContentType() {
			return "text/plain";
		}

		@Override
		public long getLength() {
			String content = getContent();
			if (content != null) {
				return content.length();
			}
			return -1;
		}

		@Override
		public String getName() {
			return "clipboard.txt";
		}

		@Override
		public boolean isLocal() {
			return true;
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

		private final IImageCreator imageCreator;

		public ImageSource(IImageCreator imageCreator) {
			this.imageCreator = imageCreator;
		}

		@Override
		public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
			// TODO check dirty status
			Image image = imageCreator.createImage();
			try {
				file = File.createTempFile("screenshot", ".jpg");
				file.deleteOnExit();
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { image.getImageData() };
				// create image in memory?
				loader.save(new FileOutputStream(file), SWT.IMAGE_JPEG);
				return new FileInputStream(file);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			} finally {
				image.dispose();
			}
		}

		@Override
		public String getContentType() {
			return "image/jpeg";
		}

		@Override
		public long getLength() {
			return (file != null) ? file.length() : -1;
		}

		@Override
		public String getName() {
			return "screenshot.jpg";
		}

		@Override
		public boolean isLocal() {
			return true;
		}

	}

	public enum Mode {
		DEFAULT, SCREENSHOT
	}

	static class StringSource extends ClipboardSource {

		private final String content;

		public StringSource(String content) {
			this.content = content;
		}

		@Override
		protected String getContent() {
			return content;
		}

	}

	private static final String DIALOG_SETTINGS_KEY = "AttachmentWizard";

	private Mode mode = Mode.DEFAULT;

	private AbstractTaskAttachmentSource source;

	private final TaskAttachment taskAttachment;

	private final TaskRepository taskRepository;

	public TaskAttachmentWizard(TaskRepository taskRepository, TaskAttachment taskAttachment) {
		this.taskRepository = taskRepository;
		this.taskAttachment = taskAttachment;

		if (mode == Mode.SCREENSHOT) {
			setWindowTitle("Attach Screenshot");
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_SCREENSHOT);
		} else {
			setWindowTitle("Add Attachment");
			setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		}

		setNeedsProgressMonitor(true);
		setDialogSettings(TasksUiPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS_KEY));
	}

	@Override
	public void addPages() {
		if (getSource() == null) {
			if (mode == Mode.SCREENSHOT) {
				ScreenshotAttachmentPage shotPage = new ScreenshotAttachmentPage();
				setSource(new ImageSource(shotPage));
				addPage(shotPage);
			} else {
				InputAttachmentSourcePage inputPage = new InputAttachmentSourcePage(null);
				inputPage.setTaskAttachment(taskAttachment);
				addPage(inputPage);
			}
		}

		addPage(createEditPage());
	}

	protected TaskAttachmentPage createEditPage() {
		return new TaskAttachmentPage(taskAttachment);
	}

	public TaskAttachment getAttachment() {
		return taskAttachment;
	}

	public AbstractTaskAttachmentSource getSource() {
		return source;
	}

	private void handleSubmitError(final CoreException exception) {
		if (exception.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
			if (TasksUiUtil.openEditRepositoryWizard(taskRepository) == Window.OK) {
				// performFinish();
			}
		} else {
			StatusHandler.displayStatus("Attachment failed", exception.getStatus());
		}
	}

	@Override
	public boolean performFinish() {
//		attachPage.populateAttachment();
//		final String path = inputPage.getAbsoluteAttachmentPath();
		// upload the attachment
		final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		final AbstractAttachmentHandler attachmentHandler = connector.getAttachmentHandler();
		if (attachmentHandler == null) {
			return false;
		}

//		final boolean attachContext = attachPage.getAttachContext();

		final SubmitTaskAttachmentJob job = new SubmitTaskAttachmentJob(connector, taskRepository, taskAttachment);
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					job.run(monitor);
				}
			});
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				handleSubmitError((CoreException) e.getCause());
			} else {
				StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Attachment failure", e));
			}
			return false;
		} catch (InterruptedException e) {
			// cancelled
			return false;
		}

		return true;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setSource(AbstractTaskAttachmentSource source) {
		this.source = source;
	}

}
