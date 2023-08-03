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

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.identity.core.spi.ProfileImage;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.FillWidthLayout;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.ui.GitlabUiActivator;
import org.eclipse.mylyn.internal.gitlab.core.GitlabRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractReplyToCommentAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.CommentGroupStrategy.CommentGroup;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorRichTextPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.UserAttributeEditor;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class GitlabTaskEditorCommentPart extends TaskEditorCommentPart {
    private class ReplyToCommentAction extends AbstractReplyToCommentAction {
	private final AbstractTaskEditorPage editor;

	private final CommentViewer commentViewerGitlab;

	public ReplyToCommentAction(CommentViewer commentViewer) {
	    super(GitlabTaskEditorCommentPart.this.getTaskEditorPage(), commentViewer.getTaskComment());
	    this.editor = GitlabTaskEditorCommentPart.this.getTaskEditorPage();
	    this.commentViewerGitlab = commentViewer;
	}

	@Override
	protected String getReplyText() {
	    return commentViewerGitlab.getReplyToText();
	}

	protected CommentViewer getCommentViewer() {
	    return commentViewerGitlab;
	}

	@Override
	public void run() {
	    reply(editor, getCommentViewer().getTaskComment(), getReplyText());
	}

	public void dispose() {
	}

	public static void reply(AbstractTaskEditorPage editor, ITaskComment taskComment, String text) {
	    AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(editor.getConnectorKind());
	    String reference = connectorUi.getReplyText(editor.getTaskRepository(), editor.getTask(), taskComment,
		    false);
	    StringBuilder sb = new StringBuilder();
	    sb.append(reference);
	    sb.append("\n"); //$NON-NLS-1$
	    if (text != null) {
		CommentQuoter quoter = new CommentQuoter();
		sb.append(quoter.quote(text));
	    }
	    AbstractTaskEditorPart newCommentPart = editor
		    .getPart(org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage.ID_PART_NEW_COMMENT);
	    if (newCommentPart instanceof TaskEditorRichTextPart) {
		TaskAttribute newCommentAttrib = ((TaskEditorRichTextPart) newCommentPart).getAttribute();
		TaskAttribute newCommentDiscussion = newCommentAttrib.getAttribute("discussions");
		TaskAttribute newCommentNoteable = newCommentAttrib.getAttribute("noteable_id");
		TaskAttribute newCommentNoteID = newCommentAttrib.getAttribute("note_id");
		TaskAttribute commentDiscussion = taskComment.getTaskAttribute().getAttribute("discussions");
		TaskAttribute commentNoteable = taskComment.getTaskAttribute().getAttribute("noteable_id");
		TaskAttribute commentNoteID = taskComment.getTaskAttribute().getAttribute("note_id");
		if (newCommentDiscussion == null) {
		    newCommentDiscussion = newCommentAttrib.createAttribute("discussions");
		}
		if (newCommentNoteable == null) {
		    newCommentNoteable = newCommentAttrib.createAttribute("noteable_id");
		}
		if (newCommentNoteID == null) {
		    newCommentNoteID = newCommentAttrib.createAttribute("note_id");
		}
		newCommentDiscussion.setValue(commentDiscussion.getValue());
		newCommentNoteable.setValue(commentNoteable.getValue());
		newCommentNoteID.setValue(commentNoteID.getValue());
	    }
	    editor.appendTextToNewComment(sb.toString());
	}

    }

    private class ReplyToCommentActionWithMenu extends ReplyToCommentAction implements IMenuCreator {

	public ReplyToCommentActionWithMenu(CommentViewer commentViewer) {
	    super(commentViewer);
	    setMenuCreator(this);
	}

	public Menu getMenu(Control parent) {
	    setCurrentViewer(getCommentViewer());
	    getSelectionProvider().setSelection(new StructuredSelection(getCurrentViewer().getTaskComment()));
	    return getCommentMenu();
	}

	public Menu getMenu(Menu parent) {
	    getSelectionProvider().setSelection(new StructuredSelection(getCommentViewer().getTaskComment()));
	    return getCommentMenu();
	}

    }

    public class GitlabCommentGroupViewer extends CommentGroupViewer {

	private ArrayList<CommentViewer> commentViewers;

	public GitlabCommentGroupViewer(CommentGroup commentGroup) {
	    super(commentGroup);
	}

	@Override
	public List<CommentViewer> getCommentViewers() {
	    if (commentViewers != null) {
		return commentViewers;
	    }

	    commentViewers = new ArrayList<CommentViewer>(commentGroup.getCommentAttributes().size());
	    for (TaskAttribute commentAttribute : commentGroup.getCommentAttributes()) {
		CommentViewer commentViewer = new GitlabCommentViewer(commentAttribute);
		commentViewers.add(commentViewer);
	    }
	    return commentViewers;
	}

    }

    public class GitlabCommentViewer extends CommentViewer {

	ArrayList<GitlabCommentViewer> subViewer = new ArrayList<GitlabCommentViewer>();

	public GitlabCommentViewer(TaskAttribute commentAttribute) {
	    super(commentAttribute);
	}

	@Override
	protected void expandComment(FormToolkit toolkit, Composite composite, boolean expanded) {
	    buttonComposite.setVisible(expanded);
	    if (expanded && composite.getData(KEY_EDITOR) == null) {
		Menu mm0 = composite.getMenu();
		commentViewer = toolkit.createComposite(composite);
		commentViewer.setLayout(new GridLayout(2, false));
		commentViewer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		// Create user image viewer
		boolean showAvatar = Boolean
			.parseBoolean(getModel().getTaskRepository().getProperty(GitlabCoreActivator.AVANTAR));
		if (showAvatar) {
		    String commentAuthor = getTaskData().getAttributeMapper().mapToRepositoryKey(commentAttribute,
			    TaskAttribute.COMMENT_AUTHOR);
		    TaskAttribute userImageAttribute = commentAttribute.getAttribute(commentAuthor);

		    if (userImageAttribute != null) {
			userImageComposite = toolkit.createComposite(commentViewer);
			userImageComposite.setLayout(new GridLayout(1, false));
			GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(userImageComposite);
			toolkit.paintBordersFor(userImageComposite);

			UserAttributeEditor userImage = new UserAttributeEditor(getModel(), userImageAttribute, 30);
			userImage.createControl(userImageComposite, toolkit);
			TaskAttribute avatar_url = userImageAttribute.getAttribute("avatar_url");

			if (avatar_url != null) {
			    GitlabRepositoryConnector gitlabConnector = (GitlabRepositoryConnector) TasksUi
				    .getRepositoryManager()
				    .getRepositoryConnector(userImageAttribute.getTaskData().getConnectorKind());
			    byte[] avatarBytes = gitlabConnector.getAvatarData(avatar_url.getValue());
			    userImage.updateImage(new ProfileImage(avatarBytes, 30, 30, ""));
			}
		    }
		}

		// Create comment text viewer
		TaskAttribute textAttribute = getTaskData().getAttributeMapper()
			.getAssoctiatedAttribute(taskComment.getTaskAttribute());
		commentTextEditor = createAttributeEditor(textAttribute);
		if (commentTextEditor != null) {
		    commentTextEditor.setDecorationEnabled(false);
		    commentTextEditor.createControl(commentViewer, toolkit);
		    GridData commentGridData = new GridData(GridData.FILL_HORIZONTAL);
		    commentGridData.verticalAlignment = GridData.BEGINNING;
		    commentTextEditor.getControl().setLayoutData(commentGridData);

		    commentTextEditor.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			    getTaskEditorPage().selectionChanged(taskComment);
			}
		    });
		    composite.setData(KEY_EDITOR, commentTextEditor);
		    TaskAttribute reply = commentAttribute.getAttribute("reply");
		    if (reply != null) {
			for (TaskAttribute taskAttributeReply : reply.getAttributes().values()) {
			    TaskComment replyComment = new TaskComment(getModel().getTaskRepository(),
				    getModel().getTask(), taskAttributeReply);
			    commentAttribute.getTaskData().getAttributeMapper().updateTaskComment(replyComment,
				    taskAttributeReply);
			    GitlabCommentViewer replyCommentViewer = new GitlabCommentViewer(taskAttributeReply);
			    subViewer.add(replyCommentViewer);
			    replyCommentViewer.createControl(commentViewer, toolkit);
			    GridData subViewerGridData = new GridData(GridData.BEGINNING);
			    subViewerGridData.horizontalSpan = 2;
			    replyCommentViewer.getControl().setLayoutData(subViewerGridData);
			}
		    }
		    getTaskEditorPage().getAttributeEditorToolkit().adapt(commentTextEditor);
		    reflow();
		}
	    } else if (!expanded && composite.getData(KEY_EDITOR) != null) {
		// dispose viewer
		commentTextEditor.getControl().setMenu(null);
		commentTextEditor.getControl().dispose();
		if (userImageComposite != null) {
		    userImageComposite.setMenu(null);
		    userImageComposite.dispose();
		}
		if (commentViewer != null) {
		    for (GitlabCommentViewer subView : subViewer) {
			if (subView != null) {
			    if (subView.commentTextEditor != null && subView.commentTextEditor.getControl() != null
				    && !subView.commentTextEditor.getControl().isDisposed()) {
				subView.commentTextEditor.getControl().setMenu(null);
				subView.commentTextEditor.getControl().dispose();
			    }
			    if (subView.userImageComposite != null && subView.userImageComposite.isDisposed()) {
				subView.userImageComposite.setMenu(null);
				subView.userImageComposite.dispose();
			    }
			    if (subView.commentViewer != null && !subView.commentViewer.isDisposed()) {
				subView.commentViewer.setMenu(null);
				subView.commentViewer.dispose();
			    }
			}
		    }
		    commentViewer.setMenu(null);
		    commentViewer.dispose();
		}
		composite.setData(KEY_EDITOR, null);
		reflow();
	    }
	    if (!suppressSelectionChanged) {
		getTaskEditorPage().selectionChanged(taskComment);
	    }
	}

	@Override
	protected Composite createTitle(ExpandableComposite commentComposite, FormToolkit toolkit) {
	    // always visible
	    Composite titleComposite = toolkit.createComposite(commentComposite);
	    commentComposite.setTextClient(titleComposite);
	    RowLayout rowLayout = new RowLayout();
	    rowLayout.pack = true;
	    rowLayout.marginLeft = 0;
	    rowLayout.marginBottom = 0;
	    rowLayout.marginTop = 0;
	    rowLayout.center = true;
	    titleComposite.setLayout(rowLayout);
	    titleComposite.setBackground(null);

	    ImageHyperlink expandCommentHyperlink = createTitleHyperLink(toolkit, titleComposite, taskComment);
	    expandCommentHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
		@Override
		public void linkActivated(HyperlinkEvent e) {
		    CommonFormUtil.setExpanded(commentComposite, !commentComposite.isExpanded());
		}
	    });
	    TaskAttribute systemAttribute = taskComment.getTaskAttribute().getAttribute("system");
	    String systemAttributeValue = systemAttribute != null ? systemAttribute.getValue() : "false";
	    boolean showCommentIcons = Boolean
		    .parseBoolean(getModel().getTaskRepository().getProperty(GitlabCoreActivator.SHOW_COMMENT_ICONS));
	    if (showCommentIcons) {
		if ("true".equals(systemAttributeValue)) {
		    expandCommentHyperlink.setImage(GitlabUiActivator.getDefault().getImageRegistry().get(GitlabUiActivator.GITLAB_PICTURE_FILE));
		} else {
		    IRepositoryPerson author = taskComment.getAuthor();
		    if (author != null
			    && author.matchesUsername(getTaskEditorPage().getTaskRepository().getUserName())) {
			expandCommentHyperlink.setImage(CommonImages.getImage(CommonImages.PERSON_ME));
		    } else {
			expandCommentHyperlink.setImage(CommonImages.getImage(CommonImages.PERSON));
		    }
		}
	    } else {
		expandCommentHyperlink.setImage(null);
	    }

	    ToolBarManager toolBarManagerTitle = new ToolBarManager(SWT.FLAT);
	    addActionsToToolbarTitle(toolBarManagerTitle, taskComment, this);
	    toolBarManagerTitle.createControl(titleComposite);

	    // only visible when section is expanded
	    Composite buttonComposite = toolkit.createComposite(titleComposite);
	    RowLayout buttonCompLayout = new RowLayout();
	    buttonCompLayout.marginBottom = 0;
	    buttonCompLayout.marginTop = 0;
	    buttonComposite.setLayout(buttonCompLayout);
	    buttonComposite.setBackground(null);
	    buttonComposite.setVisible(commentComposite.isExpanded());

	    ToolBarManager toolBarManagerButton = new ToolBarManager(SWT.FLAT);
	    addActionsToToolbarButton(toolBarManagerButton, taskComment, this);
	    toolBarManagerButton.createControl(buttonComposite);

	    return buttonComposite;
	}

    }

    @Override
    public List<CommentGroupViewer> getCommentGroupViewers() {
	if (commentGroupViewers != null) {
	    return commentGroupViewers;
	}

	// group comments
	List<ITaskComment> comments = new ArrayList<ITaskComment>();
	for (TaskAttribute commentAttribute : this.commentAttributes) {
	    comments.add(convertToTaskComment(getModel(), commentAttribute));
	}
	String currentPersonId = getModel().getTaskRepository().getUserName();
	List<CommentGroup> commentGroups = getCommentGroupStrategy().groupComments(comments, currentPersonId);

	commentGroupViewers = new ArrayList<CommentGroupViewer>(commentGroups.size());
	if (commentGroups.size() > 0) {
	    for (int i = 0; i < commentGroups.size(); i++) {
		GitlabCommentGroupViewer viewer = new GitlabCommentGroupViewer(commentGroups.get(i));
		boolean isLastGroup = i == commentGroups.size() - 1;
		viewer.setRenderedInSubSection(!isLastGroup);
		commentGroupViewers.add(viewer);
	    }
	}
	return commentGroupViewers;
    }

    protected void addActionsToToolbarButton(ToolBarManager toolBarManager, TaskComment taskComment,
	    CommentViewer commentViewer) {
	ReplyToCommentAction replyAction = new ReplyToCommentActionWithMenu(commentViewer);
	replyAction.setImageDescriptor(TasksUiImages.COMMENT_REPLY_SMALL);
	toolBarManager.add(replyAction);
    }

}
