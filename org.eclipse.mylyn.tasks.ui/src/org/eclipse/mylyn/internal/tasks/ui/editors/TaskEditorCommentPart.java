/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jingwen Ou - comment grouping
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.editors.CommentGroupStrategy.CommentGroup;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 * @author Jingwen Ou
 */
public class TaskEditorCommentPart extends AbstractTaskEditorPart {

	private class CommentGroupViewer {

		private final CommentGroup commentGroup;

		private ArrayList<CommentViewer> commentViewers;

		private Section groupSection;

		private boolean renderedInSubSection;

		public CommentGroupViewer(CommentGroup commentGroup) {
			this.commentGroup = commentGroup;
		}

		private Composite createCommentViewers(Composite parent, FormToolkit toolkit) {
			Composite composite = toolkit.createComposite(parent);
			GridLayout contentLayout = new GridLayout();
			contentLayout.marginHeight = 0;
			contentLayout.marginWidth = 0;
			composite.setLayout(contentLayout);

			List<CommentViewer> viewers = getCommentViewers();
			for (CommentViewer commentViewer : viewers) {
				Control control = commentViewer.createControl(composite, toolkit);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
			}
			return composite;
		}

		private Control createControl(final Composite parent, final FormToolkit toolkit) {
			if (renderedInSubSection) {
				return createSection(parent, toolkit);
			} else {
				return createCommentViewers(parent, toolkit);
			}
		}

		private Section createSection(final Composite parent, final FormToolkit toolkit) {
			int style = ExpandableComposite.TWISTIE | ExpandableComposite.SHORT_TITLE_BAR;
//			if (/*commentGroup.hasIncoming() || */expandAllInProgress) {
//				style |= ExpandableComposite.EXPANDED;
//			}

			groupSection = toolkit.createSection(parent, style);
			if (commentGroup.hasIncoming()) {
				groupSection.setBackground(getTaskEditorPage().getAttributeEditorToolkit().getColorIncoming());
			}
			groupSection.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			groupSection.setText(commentGroup.getGroupName() + " (" + commentGroup.getCommentAttributes().size() + ")");

			if (groupSection.isExpanded()) {
				Composite composite = createCommentViewers(groupSection, toolkit);
				groupSection.setClient(composite);
			} else {
				groupSection.addExpansionListener(new ExpansionAdapter() {
					@Override
					public void expansionStateChanged(ExpansionEvent e) {
						if (commentGroup.hasIncoming()) {
							if (e.getState()) {
								groupSection.setBackground(null);
							} else {
								// only decorate background with incoming color when collapsed, otherwise 
								// there is too much decoration in the editor
								groupSection.setBackground(getTaskEditorPage().getAttributeEditorToolkit()
										.getColorIncoming());
							}
						}
						if (groupSection.getClient() == null) {
							try {
								getTaskEditorPage().setReflow(false);
								Composite composite = createCommentViewers(groupSection, toolkit);
								groupSection.setClient(composite);
							} finally {
								getTaskEditorPage().setReflow(true);
							}
							getTaskEditorPage().reflow();
						}
					}
				});
			}

			return groupSection;
		}

		public List<CommentViewer> getCommentViewers() {
			if (commentViewers != null) {
				return commentViewers;
			}

			commentViewers = new ArrayList<CommentViewer>(commentGroup.getCommentAttributes().size());
			for (final TaskAttribute commentAttribute : commentGroup.getCommentAttributes()) {
				CommentViewer commentViewer = new CommentViewer(commentAttribute);
				commentViewers.add(commentViewer);
			}
			return commentViewers;
		}

		public boolean isExpanded() {
			if (groupSection != null) {
				return groupSection.isExpanded();
			}

			if (commentViewers != null) {
				for (CommentViewer commentViewer : commentViewers) {
					if (commentViewer.isExpanded()) {
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * Returns true if this group and all comments in it are expanded.
		 */
		public boolean isFullyExpanded() {
			if (groupSection != null && !groupSection.isExpanded()) {
				return false;
			}
			if (commentViewers != null) {
				for (CommentViewer commentViewer : commentViewers) {
					if (!commentViewer.isExpanded()) {
						return false;
					}
				}
				return true;
			}
			return false;
		}

		public boolean isRenderedInSubSection() {
			return renderedInSubSection;
		}

		/**
		 * Expands this group and all comments in it.
		 */
		public void setExpanded(boolean expanded) {
			if (groupSection != null && groupSection.isExpanded() != expanded) {
				EditorUtil.toggleExpandableComposite(expanded, groupSection);
			}

			if (commentViewers != null) {
				for (CommentViewer commentViewer : commentViewers) {
					commentViewer.setExpanded(expanded);
				}
			}
		}

		public void setRenderedInSubSection(boolean renderedInSubSection) {
			this.renderedInSubSection = renderedInSubSection;
		}

		//		private void createToolBar(final FormToolkit toolkit) {
//		if (section == null) {
//			return;
//		}
//
//		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
//
//		Action collapseAllAction = new Action("") {
//			@Override
//			public void run() {
//				toggleSection(section, false);
//			}
//		};
//		collapseAllAction.setImageDescriptor(CommonImages.COLLAPSE_ALL_SMALL);
//		collapseAllAction.setToolTipText("Collapse All Current Comments");
//		toolBarManager.add(collapseAllAction);
//
//		Action expandAllAction = new Action("") {
//			@Override
//			public void run() {
//				toggleSection(section, true);
//			}
//		};
//		expandAllAction.setImageDescriptor(CommonImages.EXPAND_ALL_SMALL);
//		expandAllAction.setToolTipText("Expand All Current Comments");
//		toolBarManager.add(expandAllAction);
//
//		Composite toolbarComposite = toolkit.createComposite(section);
//		toolbarComposite.setBackground(null);
//		RowLayout rowLayout = new RowLayout();
//		rowLayout.marginTop = 0;
//		rowLayout.marginBottom = 0;
//		rowLayout.marginLeft = 0;
//		rowLayout.marginRight = 0;
//		toolbarComposite.setLayout(rowLayout);
//
//		toolBarManager.createControl(toolbarComposite);
//		section.setTextClient(toolbarComposite);
//	}

	}

	private class CommentViewer {

		private Composite buttonComposite;

		private final TaskAttribute commentAttribute;

		private ExpandableComposite commentComposite;

		private final TaskComment taskComment;

		public CommentViewer(TaskAttribute commentAttribute) {
			this.commentAttribute = commentAttribute;
			this.taskComment = new TaskComment(getModel().getTaskRepository(), getModel().getTask(), commentAttribute);
		}

		public Control createControl(Composite composite, final FormToolkit toolkit) {
			boolean hasIncomingChanges = getModel().hasIncomingChanges(commentAttribute);
			getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);
			int style = ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
					| ExpandableComposite.COMPACT;
			if (hasIncomingChanges || expandAllInProgress) {
				style |= ExpandableComposite.EXPANDED;
			}
			commentComposite = toolkit.createExpandableComposite(composite, style);
			commentComposite.setLayout(new GridLayout());
			commentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			commentComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

			buttonComposite = createTitle(commentComposite, toolkit);

			final Composite commentTextComposite = toolkit.createComposite(commentComposite);
			commentComposite.setClient(commentTextComposite);
			commentTextComposite.setLayout(new FillWidthLayout(EditorUtil.getLayoutAdvisor(getTaskEditorPage()), 15, 0,
					0, 3));
			commentComposite.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					expandComment(toolkit, commentTextComposite, event.getState());
				}
			});
			if (hasIncomingChanges) {
				commentComposite.setBackground(getTaskEditorPage().getAttributeEditorToolkit().getColorIncoming());
			}
			if (commentComposite.isExpanded()) {
				expandComment(toolkit, commentTextComposite, true);
			}

			// for outline
			EditorUtil.setMarker(commentComposite, commentAttribute.getId());
			return commentComposite;
		}

		private ImageHyperlink createReplyHyperlink(Composite composite, FormToolkit toolkit,
				final ITaskComment taskComment) {
			final ImageHyperlink replyLink = new ImageHyperlink(composite, SWT.NULL);
			toolkit.adapt(replyLink, false, false);
			replyLink.setImage(CommonImages.getImage(TasksUiImages.COMMENT_REPLY));
			replyLink.setToolTipText(LABEL_REPLY);
			// no need for the background - transparency will take care of it
			replyLink.setBackground(null);
			// replyLink.setBackground(section.getTitleBarGradientBackground());
			replyLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					AbstractReplyToCommentAction.reply(getTaskEditorPage(), taskComment, taskComment.getText());
				}

				@Override
				public void linkEntered(HyperlinkEvent e) {
					replyLink.setUnderlined(true);
				}

				@Override
				public void linkExited(HyperlinkEvent e) {
					replyLink.setUnderlined(false);
				}

			});
			return replyLink;
		}

		private Composite createTitle(final ExpandableComposite commentComposite, final FormToolkit toolkit) {
			// always visible
			Composite titleComposite = toolkit.createComposite(commentComposite);
			commentComposite.setTextClient(titleComposite);
			RowLayout rowLayout = new RowLayout();
			rowLayout.pack = true;
			rowLayout.marginLeft = 0;
			rowLayout.marginBottom = 0;
			rowLayout.marginTop = 0;
			titleComposite.setLayout(rowLayout);
			titleComposite.setBackground(null);

			ImageHyperlink expandCommentHyperlink = createTitleHyperLink(toolkit, titleComposite, taskComment);
			expandCommentHyperlink.setFont(commentComposite.getFont());
			expandCommentHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					EditorUtil.toggleExpandableComposite(!commentComposite.isExpanded(), commentComposite);
				}
			});

			// only visible when section is expanded
			final Composite buttonComposite = toolkit.createComposite(titleComposite);
			RowLayout buttonCompLayout = new RowLayout();
			buttonCompLayout.marginBottom = 0;
			buttonCompLayout.marginTop = 0;
			buttonComposite.setLayout(buttonCompLayout);
			buttonComposite.setBackground(null);
			buttonComposite.setVisible(commentComposite.isExpanded());

			createReplyHyperlink(buttonComposite, toolkit, taskComment);
			return buttonComposite;
		}

		private ImageHyperlink createTitleHyperLink(final FormToolkit toolkit, final Composite toolbarComp,
				final ITaskComment taskComment) {
			ImageHyperlink formHyperlink = toolkit.createImageHyperlink(toolbarComp, SWT.NONE);
			formHyperlink.setBackground(null);
			formHyperlink.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			IRepositoryPerson author = taskComment.getAuthor();
			if (author != null
					&& author.getPersonId().equalsIgnoreCase(getTaskEditorPage().getTaskRepository().getUserName())) {
				formHyperlink.setImage(CommonImages.getImage(CommonImages.PERSON_ME_NARROW));
			} else {
				formHyperlink.setImage(CommonImages.getImage(CommonImages.PERSON_NARROW));
			}
			StringBuilder sb = new StringBuilder();
			if (taskComment.getNumber() >= 0) {
				sb.append(taskComment.getNumber());
				sb.append(": ");
			}
			if (author != null) {
				if (author.getName() != null) {
					sb.append(author.getName());
					formHyperlink.setToolTipText(author.getPersonId());
				} else {
					sb.append(author.getPersonId());
				}
			}
			if (taskComment.getCreationDate() != null) {
				sb.append(", ");
				sb.append(EditorUtil.formatDateTime(taskComment.getCreationDate()));
			}
			formHyperlink.setText(sb.toString());
			formHyperlink.setEnabled(true);
			formHyperlink.setUnderlined(false);
			return formHyperlink;
		}

		private void expandComment(FormToolkit toolkit, Composite composite, boolean expanded) {
			buttonComposite.setVisible(expanded);
			if (expanded && composite.getData(KEY_EDITOR) == null) {
				// create viewer
				TaskAttribute textAttribute = getTaskData().getAttributeMapper().getAssoctiatedAttribute(
						taskComment.getTaskAttribute());
				AbstractAttributeEditor editor = createAttributeEditor(textAttribute);
				if (editor != null) {
					editor.setDecorationEnabled(false);
					editor.createControl(composite, toolkit);
					editor.getControl().addMouseListener(new MouseAdapter() {
						@Override
						public void mouseDown(MouseEvent e) {
							getTaskEditorPage().selectionChanged(taskComment);
						}
					});
					composite.setData(KEY_EDITOR, editor);

					getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
					getTaskEditorPage().reflow();
				}
			} else if (!expanded && composite.getData(KEY_EDITOR) != null) {
				// dispose viewer
				AbstractAttributeEditor editor = (AbstractAttributeEditor) composite.getData(KEY_EDITOR);
				editor.getControl().setMenu(null);
				editor.getControl().dispose();
				composite.setData(KEY_EDITOR, null);
				getTaskEditorPage().reflow();
			}
			getTaskEditorPage().selectionChanged(taskComment);
		}

		public boolean isExpanded() {
			return commentComposite != null && commentComposite.isExpanded();
		}

		public void setExpanded(boolean expanded) {
			if (commentComposite != null && commentComposite.isExpanded() != expanded) {
				EditorUtil.toggleExpandableComposite(expanded, commentComposite);
			}
		}

	}

	/** Expandable composites are indented by 6 pixels by default. */
	private static final int INDENT = -6;

	private static final String KEY_EDITOR = "viewer";

	private static final String LABEL_REPLY = "Reply";

	private List<TaskAttribute> commentAttributes;

	private CommentGroupStrategy commentGroupStrategy;

	private List<CommentGroupViewer> commentGroupViewers;

	private boolean expandAllInProgress;

	private boolean hasIncoming;

	protected Section section;

	public TaskEditorCommentPart() {
		setPartName("Comments");
	}

	private void collapseAllComments() {
		try {
			getTaskEditorPage().setReflow(false);

			boolean collapsed = false;
			List<CommentGroupViewer> viewers = getCommentGroupViewers();
			for (int i = 0; i < viewers.size(); i++) {
				if (viewers.get(i).isExpanded()) {
					viewers.get(i).setExpanded(false);
					collapsed = viewers.get(i).isRenderedInSubSection();
					break;
				}
			}

			if (!collapsed && section != null) {
				EditorUtil.toggleExpandableComposite(false, section);
			}
		} finally {
			getTaskEditorPage().setReflow(true);
		}
		getTaskEditorPage().reflow();
	}

	private TaskComment convertToTaskComment(TaskDataModel taskDataModel, TaskAttribute commentAttribute) {
		TaskComment taskComment = new TaskComment(taskDataModel.getTaskRepository(), taskDataModel.getTask(),
				commentAttribute);
		taskDataModel.getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);
		return taskComment;
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		section = createSection(parent, toolkit, hasIncoming);
		section.setText(section.getText() + " (" + commentAttributes.size() + ")");

		if (commentAttributes.isEmpty()) {
			section.setEnabled(false);
		} else {
			if (hasIncoming) {
				expandSection(toolkit, section);
			} else {
				section.addExpansionListener(new ExpansionAdapter() {
					@Override
					public void expansionStateChanged(ExpansionEvent event) {
						if (section.getClient() == null) {
							try {
								getTaskEditorPage().setReflow(false);
								expandSection(toolkit, section);
							} finally {
								getTaskEditorPage().setReflow(true);
							}
							getTaskEditorPage().reflow();
						}
					}
				});
			}
		}
		setSection(toolkit, section);
	}

	private void expandAllComments() {
		try {
			expandAllInProgress = true;
			getTaskEditorPage().setReflow(false);

			if (section != null) {
				// the expandAllInProgress flag will ensure that comments in top-level groups have been 
				// expanded, no need to expand groups explicitly
				boolean expandGroups = section.getClient() != null;

				EditorUtil.toggleExpandableComposite(true, section);

				if (expandGroups) {
					List<CommentGroupViewer> viewers = getCommentGroupViewers();
					for (int i = viewers.size() - 1; i >= 0; i--) {
						if (!viewers.get(i).isFullyExpanded()) {
							viewers.get(i).setExpanded(true);
							break;
						}
					}
				}
			}
		} finally {
			expandAllInProgress = false;
			getTaskEditorPage().setReflow(true);
		}
		getTaskEditorPage().reflow();
	}

	private void expandSection(final FormToolkit toolkit, final Section section) {
		Composite composite = toolkit.createComposite(section);
		section.setClient(composite);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		composite.setLayout(layout);

		List<CommentGroupViewer> viewers = getCommentGroupViewers();
		for (CommentGroupViewer viewer : viewers) {
			Control control = viewer.createControl(composite, toolkit);
			if (viewer.isRenderedInSubSection()) {
				// align twistie of sub-section with section
				GridDataFactory.fillDefaults().grab(true, false).indent(2 * INDENT, 0).applyTo(control);
			} else {
				GridDataFactory.fillDefaults().grab(true, false).indent(INDENT, 0).applyTo(control);
			}
		}
	}

	@Override
	protected void fillToolBar(ToolBarManager barManager) {
		if (commentAttributes.isEmpty()) {
			return;
		}

		Action collapseAllAction = new Action("") {
			@Override
			public void run() {
				collapseAllComments();
			}
		};
		collapseAllAction.setImageDescriptor(CommonImages.COLLAPSE_ALL_SMALL);
		collapseAllAction.setToolTipText("Collapse Comments");
		barManager.add(collapseAllAction);

		Action expandAllAction = new Action("") {
			@Override
			public void run() {
				expandAllComments();
			}
		};
		expandAllAction.setImageDescriptor(CommonImages.EXPAND_ALL_SMALL);
		expandAllAction.setToolTipText("Expand Comments");
		barManager.add(expandAllAction);
	}

	private CommentGroupStrategy getCommentGroupStrategy() {
		if (commentGroupStrategy == null) {
			commentGroupStrategy = new CommentGroupStrategy() {
				@Override
				protected boolean hasIncomingChanges(ITaskComment taskComment) {
					return getModel().hasIncomingChanges(taskComment.getTaskAttribute());
				}
			};
		}
		return commentGroupStrategy;
	}

	private List<CommentGroupViewer> getCommentGroupViewers() {
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
				CommentGroupViewer viewer = new CommentGroupViewer(commentGroups.get(i));
				boolean isLastGroup = i == commentGroups.size() - 1;
				viewer.setRenderedInSubSection(!isLastGroup);
				commentGroupViewers.add(viewer);
			}
		}
		return commentGroupViewers;
	}

	private void initialize() {
		commentAttributes = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(),
				TaskAttribute.TYPE_COMMENT);
		if (commentAttributes.size() > 0) {
			for (TaskAttribute commentAttribute : commentAttributes) {
				if (getModel().hasIncomingChanges(commentAttribute)) {
					hasIncoming = true;
					break;
				}
			}
		}
	}

}
