/*******************************************************************************
 * Copyright (c) 2010, 2015 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Frank Becker
 */
public class TaskEditorOutlineNodeLabelProvider extends LabelProvider {

	private static final String[] IMAGE_EXTENSIONS = { "jpg", "gif", "png", "tiff", "tif", "bmp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$	@Override

	@Override
	public Image getImage(Object element) {
		if (element instanceof TaskEditorOutlineNode) {
			TaskEditorOutlineNode node = (TaskEditorOutlineNode) element;
			if (TaskEditorOutlineNode.LABEL_COMMENTS.equals(node.getLabel())
					|| TaskEditorOutlineNode.LABEL_NEW_COMMENT.equals(node.getLabel())) {
				return CommonImages.getImage(TasksUiImages.COMMENT);
			}
			if (TaskEditorOutlineNode.LABEL_DESCRIPTION.equals(node.getLabel())) {
				return CommonImages.getImage(TasksUiImages.TASK_NOTES);
			} else if (node.getTaskComment() != null) {
				IRepositoryPerson author = node.getTaskComment().getAuthor();
				TaskAttribute taskAttribute = node.getData();
				String repositoryUrl = taskAttribute.getTaskData().getRepositoryUrl();
				String connectorKind = null;
				TaskRepository taskRepository = null;
				if (repositoryUrl != null) {
					connectorKind = taskAttribute.getTaskData().getConnectorKind();
					if (connectorKind != null) {
						taskRepository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);
					}
				}
				if (taskRepository != null && author != null && author.matchesUsername(taskRepository.getUserName())) {
					return CommonImages.getImage(CommonImages.PERSON_ME);
				} else {
					return CommonImages.getImage(CommonImages.PERSON);
				}
			} else if (node.getTaskAttachment() != null) {
				ITaskAttachment attachment = node.getTaskAttachment();
				if (AttachmentUtil.isContext(attachment)) {
					return CommonImages.getImage(TasksUiImages.CONTEXT_TRANSFER);
				} else if (attachment.isPatch()) {
					return CommonImages.getImage(TasksUiImages.TASK_ATTACHMENT_PATCH);
				} else {
					String filename = attachment.getFileName();
					if (filename != null) {
						int dotIndex = filename.lastIndexOf('.');
						if (dotIndex != -1) {
							String fileType = filename.substring(dotIndex + 1);
							for (String element2 : IMAGE_EXTENSIONS) {
								if (element2.equalsIgnoreCase(fileType)) {
									return CommonImages.getImage(CommonImages.IMAGE_FILE);
								}
							}
						}
					}
					return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FILE);
				}
			} else if (node.getParent() == null) {
				return CommonImages.getImage(TasksUiImages.TASK);
			} else if (TaskEditorOutlineNode.LABEL_RELATED_TASKS.equals(node.getLabel())
					|| TaskEditorOutlineNode.LABEL_ATTRIBUTES.equals(node.getLabel())) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
			return CommonImages.getImage(TasksUiImages.TASK);
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof TaskEditorOutlineNode) {
			TaskEditorOutlineNode node = (TaskEditorOutlineNode) element;
			if (TaskEditorOutlineNode.LABEL_COMMENTS.equals(node.getLabel())
					|| TaskEditorOutlineNode.LABEL_NEW_COMMENT.equals(node.getLabel())
					|| TaskEditorOutlineNode.LABEL_DESCRIPTION.equals(node.getLabel())) {
				return node.getLabel();
			}
			if (node.getData() != null && node.getTaskAttachment() == null && node.getTaskComment() == null) {
				// regular attribute
				TaskAttribute taskAttribute = node.getData();
				String label = taskAttribute.getTaskData().getAttributeMapper().getLabel(taskAttribute);
				if (!label.endsWith(":")) { //$NON-NLS-1$
					label += ":"; //$NON-NLS-1$
				}
				return label + " " + taskAttribute.getTaskData().getAttributeMapper().getValueLabel(taskAttribute); //$NON-NLS-1$
			}
			return node.getLabel();
		}
		return super.getText(element);
	}

}
