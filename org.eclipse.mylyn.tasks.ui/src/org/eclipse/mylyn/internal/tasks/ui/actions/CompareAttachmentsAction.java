/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.provisional.workbench.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
		super("Compare Attachments");
	}

	protected CompareAttachmentsAction(String text) {
		super(text);
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) currentSelection;
			Object[] elements = selection.toArray();

			final RepositoryAttachment attachment1 = (RepositoryAttachment) elements[0];
			final RepositoryAttachment attachment2 = (RepositoryAttachment) elements[1];

			CompareConfiguration cc = new CompareConfiguration();

			cc.setLeftEditable(false);
			cc.setLeftLabel(attachment1.getFilename());
			cc.setLeftImage(getImage(attachment1));

			cc.setRightEditable(false);
			cc.setRightLabel(attachment2.getFilename());
			cc.setRightImage(getImage(attachment2));

			CompareEditorInput editorInput = new CompareEditorInput(cc) {

				@Override
				public String getTitle() {
					return "Compare (" + attachment1.getFilename() + " - " + attachment2.getFilename() + ")";
				}

				@Override
				protected Object prepareInput(IProgressMonitor pm) throws InvocationTargetException {
					byte[] data1 = downloadAttachment(attachment1, pm);
					byte[] data2 = downloadAttachment(attachment2, pm);
					CompareItem left = new CompareItem(attachment1.getFilename(), data1);
					CompareItem right = new CompareItem(attachment2.getFilename(), data2);
					return new DiffNode(left, right);
				}

				private byte[] downloadAttachment(RepositoryAttachment attachment, IProgressMonitor pm)
						throws InvocationTargetException {
					try {
						TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
								attachment.getRepositoryKind(), attachment.getRepositoryUrl());
						AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
								.getRepositoryConnector(attachment.getRepositoryKind());
						AbstractAttachmentHandler handler = connector.getAttachmentHandler();

						ByteArrayOutputStream os = new ByteArrayOutputStream();
						handler.downloadAttachment(repository, attachment, os, pm);
						return os.toByteArray();
					} catch (CoreException ex) {
						throw new InvocationTargetException(ex);
					}
				}
			};

			CompareUI.openCompareEditor(editorInput);
		}
	}

	private static final String[] IMAGE_EXTENSIONS = { ".jpg", ".gif", ".png", ".tiff", ".tif", ".bmp" };

	private Image getImage(RepositoryAttachment attachment) {
		if (AttachmentUtil.isContext(attachment)) {
			return CommonImages.getImage(TasksUiImages.CONTEXT_TRANSFER);
		} else if (attachment.isPatch()) {
			return CommonImages.getImage(TasksUiImages.TASK_ATTACHMENT_PATCH);
		} else {
			String filename = attachment.getFilename();
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

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}

	class CompareItem implements IStreamContentAccessor, ITypedElement {

		private final String filename;

		private final byte[] data;

		public CompareItem(String filename, byte[] data) {
			this.filename = filename;
			this.data = data;
		}

		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream(data);
		}

		public Image getImage() {
			return null;
		}

		public String getName() {
			return filename;
		}

		public String getType() {
			// ImageMergeViewerCreator - gif,jpg,jpeg,png,bmp,ico,tif,tiff
			// BinaryCompareViewerCreator - class,exe,dll,binary,zip,jar
			// TextMergeViewerCreator - txt
			// PropertiesFileMergeViewerCreator - properties,properties2
			// JavaContentViewerCreator - java,java2"
			// RefactoringDescriptorCompareViewerCreator - refactoring_descriptor
			// 
			int n = filename.lastIndexOf('.');
			if (n > -1) {
				return filename.substring(n + 1);
			}

			return ITypedElement.TEXT_TYPE;
		}
	}
}
