package org.eclipse.mylyn.gitlab.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.FillWidthLayout;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.editors.CommentGroupStrategy.CommentGroup;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.internal.tasks.ui.editors.UserAttributeEditor;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

	public Control createControl(Composite composite, FormToolkit toolkit) {
	    boolean hasIncomingChanges = getModel().hasIncomingChanges(commentAttribute);
	    getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);
	    int style = ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
		    | ExpandableComposite.COMPACT;
	    if (hasIncomingChanges || (expandAllInProgress && !suppressExpandViewers)) {
		style |= ExpandableComposite.EXPANDED;
	    }
	    composite.setMenu(null);
	    commentComposite = toolkit.createExpandableComposite(composite, style);
	    commentComposite.clientVerticalSpacing = 0;
	    commentComposite.setLayout(new GridLayout());
	    commentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    commentComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

	    buttonComposite = createTitle(commentComposite, toolkit);

	    Composite commentViewerComposite = toolkit.createComposite(commentComposite);
	    commentComposite.setClient(commentViewerComposite);
	    commentViewerComposite
		    .setLayout(new FillWidthLayout(EditorUtil.getLayoutAdvisor(getTaskEditorPage()), 15, 0, 0, 3));

	    commentComposite.addExpansionListener(new ExpansionAdapter() {
		@Override
		public void expansionStateChanged(ExpansionEvent event) {
		    if (commentViewerComposite != null && !commentViewerComposite.isDisposed())
			expandComment(toolkit, commentViewerComposite, event.getState());
		}
	    });
	    if (hasIncomingChanges) {
		commentComposite.setBackground(getTaskEditorPage().getAttributeEditorToolkit().getColorIncoming());
	    }
	    if (commentComposite.isExpanded()) {
		if (commentViewerComposite != null && !commentViewerComposite.isDisposed())
		    expandComment(toolkit, commentViewerComposite, true);
	    }
	    // for outline
	    EditorUtil.setMarker(commentComposite, commentAttribute.getId());
	    return commentComposite;
	}

	@Override
	protected void expandComment(FormToolkit toolkit, Composite composite, boolean expanded) {
	    buttonComposite.setVisible(expanded);
	    if (expanded && composite.getData(KEY_EDITOR) == null) {
		Menu mm0 = composite.getMenu();
		commentViewer = toolkit.createComposite(composite);
		commentViewer.setLayout(new GridLayout(1, false));
		commentViewer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		// Create user image viewer
		boolean showAvatar = Boolean.parseBoolean(getModel().getTaskRepository()
			.getProperty(TaskEditorExtensions.REPOSITORY_PROPERTY_AVATAR_SUPPORT));
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

			userImage.refresh();
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
	    if ("true".equals(systemAttributeValue)) {
		expandCommentHyperlink.setImage(GitlabImages.getImage(GitlabImages.GITLAB));
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

}
