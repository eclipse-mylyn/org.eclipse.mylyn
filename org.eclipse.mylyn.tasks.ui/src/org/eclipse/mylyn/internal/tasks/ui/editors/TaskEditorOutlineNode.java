/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;

/**
 * A node for the tree in {@link TaskEditorOutlinePage}.
 * 
 * @author Steffen Pingel
 */
public class TaskEditorOutlineNode {

	public static final String LABEL_COMMENTS = Messages.TaskEditorOutlineNode_Comments;

	public static final String LABEL_DESCRIPTION = Messages.TaskEditorOutlineNode_Description;

	public static final String LABEL_NEW_COMMENT = Messages.TaskEditorOutlineNode_New_Comment;

	public static final String LABEL_ATTACHMENTS = Messages.TaskEditorOutlineNode_Attachments;

	public static final String LABEL_ATTRIBUTES = Messages.TaskEditorOutlineNode_Attributes;

	public static final String LABEL_RELATED_TASKS = Messages.TaskEditorOutlineNode_Related_Tasks;

	private static TaskEditorOutlineNode createNode(TaskData taskData, String attributeId, String label) {
		TaskAttribute taskAttribute = taskData.getRoot().getMappedAttribute(attributeId);
		if (taskAttribute != null) {
			if (label == null) {
				label = taskAttribute.getValue();
			}
			return new TaskEditorOutlineNode(label, taskAttribute);
		}
		return null;
	}

	private static TaskEditorOutlineNode createNode(TaskAttribute taskAttribute) {
		String type = taskAttribute.getMetaData().getType();
		if (TaskAttribute.TYPE_COMMENT.equals(type)) {
			ITaskComment taskComment = TasksUiPlugin.getRepositoryModel().createTaskComment(taskAttribute);
			if (taskComment != null) {
				taskAttribute.getTaskData().getAttributeMapper().updateTaskComment(taskComment, taskAttribute);
				StringBuilder sb = new StringBuilder();
				sb.append(taskComment.getNumber());
				sb.append(": "); //$NON-NLS-1$
				IRepositoryPerson author = taskComment.getAuthor();
				if (author != null) {
					sb.append(author.toString());
				}
				Date creationDate = taskComment.getCreationDate();
				if (creationDate != null) {
					sb.append(" ("); //$NON-NLS-1$
					sb.append(EditorUtil.formatDateTime(creationDate));
					sb.append(")"); //$NON-NLS-1$
				}
				TaskEditorOutlineNode node = new TaskEditorOutlineNode(sb.toString(), taskAttribute);
				node.setTaskComment(taskComment);
				return node;
			}
		} else if (TaskAttribute.TYPE_ATTACHMENT.equals(type)) {
			ITaskAttachment taskAttachment = TasksUiPlugin.getRepositoryModel().createTaskAttachment(taskAttribute);
			if (taskAttachment != null) {
				taskAttribute.getTaskData().getAttributeMapper().updateTaskAttachment(taskAttachment, taskAttribute);
				StringBuilder sb = new StringBuilder();
				sb.append(taskAttribute.getTaskData().getAttributeMapper().getValueLabel(taskAttribute));
				sb.append(": "); //$NON-NLS-1$
				if (AttachmentUtil.isContext(taskAttachment)) {
					sb.append(Messages.AttachmentTableLabelProvider_Task_Context);
				} else if (taskAttachment.isPatch()) {
					sb.append(Messages.AttachmentTableLabelProvider_Patch);
				} else {
					sb.append(taskAttachment.getFileName());
				}
				TaskEditorOutlineNode node = new TaskEditorOutlineNode(sb.toString(), taskAttribute);
				node.setTaskAttachment(taskAttachment);
				return node;
			}
		} else {
			String label = taskAttribute.getTaskData().getAttributeMapper().getLabel(taskAttribute);
			if (label.endsWith(":")) { //$NON-NLS-1$
				label = label.substring(0, label.length() - 1);
			}
			return new TaskEditorOutlineNode(label, taskAttribute);
		}
		return null;
	}

	public static TaskEditorOutlineNode parse(TaskData taskData, boolean includeAttributes) {
		TaskEditorOutlineNode rootNode = createNode(taskData, TaskAttribute.SUMMARY, null);
		if (rootNode == null) {
			rootNode = new TaskEditorOutlineNode(Messages.TaskEditorOutlineNode_Task_ + taskData.getTaskId());
		}
		if (includeAttributes) {
			final TaskList taskList = TasksUiPlugin.getTaskList();
			TaskEditorOutlineNode relatedTasksNode = new TaskEditorOutlineNode(LABEL_RELATED_TASKS);
			rootNode.addChild(relatedTasksNode);
			AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(taskData.getConnectorKind());
			Collection<TaskRelation> relations = connector.getTaskRelations(taskData);
			TaskRepositoryManager manager = TasksUiPlugin.getRepositoryManager();
			TaskRepository taskRepository = manager.getRepository(taskData.getConnectorKind(),
					taskData.getRepositoryUrl());
			if (relations != null) {
				for (TaskRelation taskRelation : relations) {
					ITask task = taskList.getTask(taskData.getRepositoryUrl(), taskRelation.getTaskId());
					String label;
					if (task != null) {
						if (task.getTaskKey() != null) {
							label = NLS.bind(Messages.TaskEditorOutlineNode_TaskRelation_Label,
									new Object[] { task.getTaskKey(), task.getSummary() });
						} else {
							label = task.getSummary();
						}
					} else {
						label = NLS.bind(Messages.TaskEditorOutlineNode_TaskRelation_Label,
								new Object[] { taskRelation.getTaskId(), Messages.TaskEditorOutlineNode_unknown_Label });
					}
					TaskEditorOutlineNode childNode = new TaskEditorOutlineNode(label);

					childNode.setTaskRelation(taskRelation);
					childNode.setTaskRepository(taskRepository);
					relatedTasksNode.addChild(childNode);
				}
			}

			TaskEditorOutlineNode attributesNode = new TaskEditorOutlineNode(LABEL_ATTRIBUTES);
			rootNode.addChild(attributesNode);
			Map<String, TaskAttribute> attributes = taskData.getRoot().getAttributes();
			for (TaskAttribute attribute : attributes.values()) {
				if (TaskAttribute.KIND_DEFAULT.equals(attribute.getMetaData().getKind())) {
					TaskEditorOutlineNode node = createNode(attribute);
					if (node != null) {
						attributesNode.addChild(node);
					}
				}
			}
		}
		addNode(rootNode, taskData, TaskAttribute.DESCRIPTION, LABEL_DESCRIPTION);
		List<TaskAttribute> attachments = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT);
		if (attachments.size() > 0) {
			TaskEditorOutlineNode attachmentNode = new TaskEditorOutlineNode(LABEL_ATTACHMENTS);
			rootNode.addChild(attachmentNode);
			for (TaskAttribute attachmentAttribute : attachments) {
				TaskEditorOutlineNode node = createNode(attachmentAttribute);
				if (node != null) {
					attachmentNode.addChild(node);
				}
			}
		}
		List<TaskAttribute> comments = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_COMMENT);
		if (comments.size() > 0) {
			TaskEditorOutlineNode commentsNode = new TaskEditorOutlineNode(LABEL_COMMENTS);
			rootNode.addChild(commentsNode);
			for (TaskAttribute commentAttribute : comments) {
				TaskEditorOutlineNode node = createNode(commentAttribute);
				if (node != null) {
					commentsNode.addChild(node);
				}
			}
		}
		addNode(rootNode, taskData, TaskAttribute.COMMENT_NEW, LABEL_NEW_COMMENT);
		return rootNode;
	}

	private static TaskEditorOutlineNode addNode(TaskEditorOutlineNode parentNode, TaskData taskData,
			String attributeId, String label) {
		TaskEditorOutlineNode node = createNode(taskData, attributeId, label);
		if (node != null) {
			parentNode.addChild(node);
		}
		return node;
	}

	private List<TaskEditorOutlineNode> children;

	private final String label;

	/** The parent of this node or null if it is the bug report */
	private TaskEditorOutlineNode parent;

	private final TaskAttribute taskAttribute;

	private ITaskComment taskComment;

	private ITaskAttachment taskAttachment;

	private TaskRelation taskRelation;

	private TaskRepository taskRepository;

	public TaskEditorOutlineNode(String label) {
		this(label, null);
	}

	public TaskEditorOutlineNode(String label, TaskAttribute taskAttribute) {
		this.label = label;
		this.taskAttribute = taskAttribute;
	}

	public void addChild(TaskEditorOutlineNode node) {
		Assert.isNotNull(node);
		if (children == null) {
			children = new ArrayList<TaskEditorOutlineNode>();
		}
		node.parent = this;
		children.add(node);
	}

	public TaskEditorOutlineNode getChild(String label) {
		if (children != null) {
			for (TaskEditorOutlineNode child : children) {
				if (child.getLabel().equals(label)) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * @return <code>true</code> if the given object is another node representing the same piece of data in the editor.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof TaskEditorOutlineNode) {
			TaskEditorOutlineNode node = (TaskEditorOutlineNode) o;
			return getLabel().equals(node.getLabel());
		}
		return false;
	}

	public TaskEditorOutlineNode[] getChildren() {
		return (children == null) ? new TaskEditorOutlineNode[0]
				: children.toArray(new TaskEditorOutlineNode[children.size()]);
	}

	public ITaskComment getTaskComment() {
		return taskComment;
	}

	public void setTaskComment(ITaskComment taskComment) {
		this.taskComment = taskComment;
	}

	public TaskAttribute getData() {
		return taskAttribute;
	}

	public String getLabel() {
		return label;
	}

	public TaskEditorOutlineNode getParent() {
		return parent;
	}

	@Override
	public int hashCode() {
		return getLabel().hashCode();
	}

	@Override
	public String toString() {
		return getLabel();
	}

	public ITaskAttachment getTaskAttachment() {
		return taskAttachment;
	}

	public void setTaskAttachment(ITaskAttachment taskAttachment) {
		this.taskAttachment = taskAttachment;
	}

	public TaskRelation getTaskRelation() {
		return taskRelation;
	}

	public void setTaskRelation(TaskRelation taskRelation) {
		this.taskRelation = taskRelation;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

}
