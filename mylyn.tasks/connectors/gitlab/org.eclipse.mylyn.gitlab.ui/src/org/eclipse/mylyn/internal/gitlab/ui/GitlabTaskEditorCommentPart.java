/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
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
package org.eclipse.mylyn.internal.gitlab.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.commons.identity.core.spi.ProfileImage;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.ui.GitlabUiActivator;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.editors.CommentGroupStrategy.CommentGroup;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart.CommentGroupViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.UserAttributeEditor;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class GitlabTaskEditorCommentPart extends TaskEditorCommentPart {

    public class GitlabCommentGroupViewer extends CommentGroupViewer {

	public GitlabCommentGroupViewer(CommentGroup commentGroup) {
	    super(commentGroup);
	}

	@Override
	protected CommentViewer createCommentViewer(TaskAttribute commentAttribute) {
	    // TODO Auto-generated method stub
	    return new GitlabCommentViewer(commentAttribute);
	}
    }

    public class GitlabCommentViewer extends CommentViewer {

	private ArrayList<GitlabCommentViewer> subViewer = new ArrayList<GitlabCommentViewer>();

	public GitlabCommentViewer(TaskAttribute commentAttribute) {
	    super(commentAttribute);
	}

	@Override
	protected void createUserImageControl(FormToolkit toolkit, Composite commentViewer,
		TaskAttribute commentAttribute) {
	    boolean showAvatar = Boolean
		    .parseBoolean(getModel().getTaskRepository().getProperty(GitlabCoreActivator.AVANTAR));
	    if (showAvatar) {
		String commentAuthor = getTaskData().getAttributeMapper().mapToRepositoryKey(commentAttribute,
			TaskAttribute.COMMENT_AUTHOR);
		TaskAttribute userImageAttribute = commentAttribute.getAttribute(commentAuthor);

		if (userImageAttribute != null) {
		    Composite userImageComposite = createUserImageComposite(toolkit);

		    UserAttributeEditor userImage = new UserAttributeEditor(getModel(), userImageAttribute, 30);
		    userImage.createControl(userImageComposite, toolkit);
		    TaskAttribute avatarURL = userImageAttribute.getAttribute("avatar_url");

		    if (avatarURL != null) {
			GitlabRepositoryConnector gitlabConnector = (GitlabRepositoryConnector) TasksUi
				.getRepositoryManager()
				.getRepositoryConnector(userImageAttribute.getTaskData().getConnectorKind());
			byte[] avatarBytes = gitlabConnector.getAvatarData(avatarURL.getValue());
			userImage.updateImage(new ProfileImage(avatarBytes, 30, 30, ""));
		    }
		}
	    }
	}

	@Override
	protected void createAdditionalControls(FormToolkit toolkit, Composite commentViewer,
		TaskAttribute commentAttribute) {
	    TaskAttribute reply = commentAttribute.getAttribute("reply");
	    if (reply != null) {
		Composite replayComposite = new Composite(commentViewer, SWT.NONE);
		GridLayout replayLayout = new GridLayout(2, false);
		replayLayout.marginHeight = 0;
		replayLayout.marginWidth = 0;
		replayLayout.horizontalSpacing = 0;
		replayComposite.setLayout(replayLayout);
		replayComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		for (TaskAttribute taskAttributeReply : reply.getAttributes().values()) {
		    TaskComment replyComment = new TaskComment(getModel().getTaskRepository(), getModel().getTask(),
			    taskAttributeReply);
		    commentAttribute.getTaskData().getAttributeMapper().updateTaskComment(replyComment,
			    taskAttributeReply);
		    GitlabCommentViewer replyCommentViewer = new GitlabCommentViewer(taskAttributeReply);
		    subViewer.add(replyCommentViewer);
		    replyCommentViewer.createControl(replayComposite, toolkit);
		    GridData subViewerGridData = new GridData(GridData.BEGINNING);
		    subViewerGridData.horizontalSpan = 2;
		    replyCommentViewer.getControl().setLayoutData(subViewerGridData);
		}
	    }
	}

	@Override
	protected void closeAdditionalControls() {
	    subViewer.forEach(subView -> subView.dispose());
	    subViewer.clear();
	}
    }

    
    public GitlabTaskEditorCommentPart() {
	super();
	setPartName("Activities");
    }

    @Override
    protected CommentGroupViewer createCommentGroupViewer(CommentGroup commentGroup) {
	return new GitlabCommentGroupViewer(commentGroup);
    }

    protected void setTitleImage(ImageHyperlink expandCommentHyperlink, TaskComment taskComment,
	    CommentViewer commentViewer) {
	TaskAttribute systemAttribute = taskComment.getTaskAttribute().getAttribute("system");
	TaskAttribute textAttribute = taskComment.getTaskAttribute().getAttribute(TaskAttribute.COMMENT_TEXT);
	String systemAttributeValue = systemAttribute != null ? systemAttribute.getValue() : "false";
	boolean showCommentIcons = Boolean
		.parseBoolean(getModel().getTaskRepository().getProperty(GitlabCoreActivator.SHOW_ACTIVITY_ICONS));
	if (showCommentIcons) {
	    if (textAttribute != null && (textAttribute.getValue().startsWith("changed due date ")
		    || textAttribute.getValue().startsWith("removed due date "))) {
		expandCommentHyperlink.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_CALENDAR_FILE));
	    } else if (textAttribute != null && (textAttribute.getValue().startsWith("assigned to ")
		    || textAttribute.getValue().startsWith("unassigned "))) {
		expandCommentHyperlink.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PERSON_FILE));
	    } else if (textAttribute != null && textAttribute.getValue().startsWith("changed ")) {
		expandCommentHyperlink.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PENCIL_FILE));
	    } else if (textAttribute != null && textAttribute.getValue().startsWith("unlocked ")) {
		expandCommentHyperlink.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_UNLOCK_FILE));
	    } else if (textAttribute != null && textAttribute.getValue().startsWith("locked ")) {
		expandCommentHyperlink.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_LOCK_FILE));
	    } else if ("true".equals(systemAttributeValue)) {
		expandCommentHyperlink.setImage(
			GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PICTURE_FILE));
	    } else {
		IRepositoryPerson author = taskComment.getAuthor();
		if (author != null && author.matchesUsername(getTaskEditorPage().getTaskRepository().getUserName())) {
		    expandCommentHyperlink.setImage(CommonImages.getImage(CommonImages.PERSON_ME_SMALL));
		} else {
		    expandCommentHyperlink.setImage(CommonImages.getImage(CommonImages.PERSON));
		}
	    }
	} else {
	    expandCommentHyperlink.setImage(null);
	}
    }

    protected void setUserImage(UserAttributeEditor userImage, TaskAttribute commentAttribute,
	    CommentViewer commentViewer) {
	String commentAuthor = getTaskData().getAttributeMapper().mapToRepositoryKey(commentAttribute,
		TaskAttribute.COMMENT_AUTHOR);

	TaskAttribute userImageAttribute = commentAttribute.getAttribute(commentAuthor);
	TaskAttribute avatarUrl = userImageAttribute.getAttribute("avatar_url");

	if (avatarUrl != null) {
	    GitlabRepositoryConnector gitlabConnector = (GitlabRepositoryConnector) TasksUi.getRepositoryManager()
		    .getRepositoryConnector(userImageAttribute.getTaskData().getConnectorKind());
	    byte[] avatarBytes = gitlabConnector.getAvatarData(avatarUrl.getValue());
	    userImage.updateImage(new ProfileImage(avatarBytes, 30, 30, ""));
	}
    }

}
