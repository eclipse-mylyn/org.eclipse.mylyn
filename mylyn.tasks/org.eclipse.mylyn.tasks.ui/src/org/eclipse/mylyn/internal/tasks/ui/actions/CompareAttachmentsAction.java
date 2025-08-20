/*******************************************************************************
 * Copyright (c) 2004, 2011 Eugene Kuleshov and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Eugene Kuleshov
 */
public class CompareAttachmentsAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	private ISelection currentSelection;

	public CompareAttachmentsAction() {
		super(Messages.CompareAttachmentsAction_Compare_Attachments);
	}

	protected CompareAttachmentsAction(String text) {
		super(text);
	}

	@Override
	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run(IAction action) {
		if (currentSelection instanceof IStructuredSelection selection) {
			Object[] elements = selection.toArray();
			if (elements.length >= 2) {
				final ITaskAttachment attachment1 = (ITaskAttachment) elements[0];
				final ITaskAttachment attachment2 = (ITaskAttachment) elements[1];

				CompareConfiguration cc = new CompareConfiguration();

				cc.setLeftEditable(false);
				cc.setLeftLabel(attachment1.getFileName());
				cc.setLeftImage(getImage(attachment1));

				cc.setRightEditable(false);
				cc.setRightLabel(attachment2.getFileName());
				cc.setRightImage(getImage(attachment2));

				CompareEditorInput editorInput = new CompareEditorInput(cc) {

					@Override
					public String getTitle() {
						return Messages.CompareAttachmentsAction_Compare__ + attachment1.getFileName() + " - " //$NON-NLS-1$
								+ attachment2.getFileName() + ")"; //$NON-NLS-1$
					}

					@Override
					protected Object prepareInput(IProgressMonitor pm) throws InvocationTargetException {
						CompareItem left = new CompareItem(attachment1);
						CompareItem right = new CompareItem(attachment2);
						return new DiffNode(left, right);
					}

				};

				CompareUI.openCompareEditor(editorInput);
			}
		}
	}

	private static final String[] IMAGE_EXTENSIONS = { ".jpg", ".gif", ".png", ".tiff", ".tif", ".bmp", ".svg" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	private Image getImage(ITaskAttachment attachment) {
		if (AttachmentUtil.isContext(attachment)) {
			return CommonImages.getImage(TasksUiImages.CONTEXT_TRANSFER);
		} else if (attachment.isPatch()) {
			return CommonImages.getImage(TasksUiImages.TASK_ATTACHMENT_PATCH);
		} else {
			String filename = attachment.getFileName();
			if (filename != null) {
				filename = filename.toLowerCase();
				for (String extension : IMAGE_EXTENSIONS) {
					if (filename.endsWith(extension)) {
						return CommonImages.getImage(CommonImages.IMAGE_FILE);
					}
				}
			}
			return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		currentSelection = selection;
	}

	private static class CompareItem implements IStreamContentAccessor, ITypedElement {

		private final ITaskAttachment attachment;

		public CompareItem(ITaskAttachment attachment) {
			this.attachment = attachment;
		}

		@Override
		public InputStream getContents() throws CoreException {
			TaskAttribute attachmentAttribute = attachment.getTaskAttribute();
			if (attachmentAttribute == null) {
				throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						Messages.CompareAttachmentsAction_Failed_to_find_attachment + attachment.getUrl()));
			}
			TaskRepository taskRepository = attachment.getTaskRepository();
			ITask task = attachment.getTask();
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
					.getRepositoryConnector(taskRepository.getConnectorKind());
			AbstractTaskAttachmentHandler handler = connector.getTaskAttachmentHandler();
			return handler.getContent(taskRepository, task, attachmentAttribute, new NullProgressMonitor());
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public String getName() {
			return attachment.getFileName();
		}

		@Override
		public String getType() {
			// ImageMergeViewerCreator - gif,jpg,jpeg,png,bmp,ico,tif,tiff,svg
			// BinaryCompareViewerCreator - class,exe,dll,binary,zip,jar
			// TextMergeViewerCreator - txt
			// PropertiesFileMergeViewerCreator - properties,properties2
			// JavaContentViewerCreator - java,java2"
			// RefactoringDescriptorCompareViewerCreator - refactoring_descriptor
			//
			String filename = attachment.getFileName();
			int n = filename.lastIndexOf('.');
			if (n > -1) {
				return filename.substring(n + 1);
			}
			return ITypedElement.TEXT_TYPE;
		}
	}
}
