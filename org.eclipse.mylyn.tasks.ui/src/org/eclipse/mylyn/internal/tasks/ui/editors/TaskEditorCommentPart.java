/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
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
 * @author Steffen Pingel
 */
public class TaskEditorCommentPart extends AbstractTaskEditorPart {

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private static final String LABEL_REPLY = "Reply";

	private TaskComment selectedComment;

	private final Section commentsSection;

	private boolean supportsDelete;

	private final List<ExpandableComposite> commentComposites = new ArrayList<ExpandableComposite>();

	public TaskEditorCommentPart(AbstractTaskEditorPage taskEditorPage, Section commentsSection) {
		super(taskEditorPage);
		this.commentsSection = commentsSection;
	}

	@Override
	public void createControl(Composite composite, final FormToolkit toolkit) {
		commentsSection.setText(commentsSection.getText() + " (" + getTaskData().getComments().size() + ")");
		if (getTaskData().getComments().size() == 0) {
			commentsSection.setEnabled(false);
		}

		// Additional (read-only) Comments Area
		final Composite addCommentsComposite = toolkit.createComposite(commentsSection);
		commentsSection.setClient(addCommentsComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		addCommentsComposite.setLayout(addCommentsLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addCommentsComposite);

		boolean foundNew = false;

		for (final TaskComment taskComment : getTaskData().getComments()) {
			final ExpandableComposite expandableComposite = toolkit.createExpandableComposite(addCommentsComposite,
					ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);

			expandableComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

			final Composite toolbarComp = toolkit.createComposite(expandableComposite);
			RowLayout rowLayout = new RowLayout();
			rowLayout.pack = true;
			rowLayout.marginLeft = 0;
			rowLayout.marginBottom = 0;
			rowLayout.marginTop = 0;
			toolbarComp.setLayout(rowLayout);
			toolbarComp.setBackground(null);

			ImageHyperlink formHyperlink = toolkit.createImageHyperlink(toolbarComp, SWT.NONE);
			formHyperlink.setBackground(null);
			formHyperlink.setFont(expandableComposite.getFont());
			formHyperlink.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			if (taskComment.getAuthor() != null
					&& taskComment.getAuthor().equalsIgnoreCase(getTaskRepository().getUserName())) {
				formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_ME_NARROW));
			} else {
				formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_NARROW));
			}

			String authorName = taskComment.getAuthorName();
			String tooltipText = taskComment.getAuthor();
			if (authorName.length() == 0) {
				authorName = taskComment.getAuthor();
				tooltipText = null;
			}

			formHyperlink.setText(taskComment.getNumber() + ": " + authorName + ", "
					+ formatDate(taskComment.getCreated()));

			formHyperlink.setToolTipText(tooltipText);
			formHyperlink.setEnabled(true);
			formHyperlink.setUnderlined(false);

			final Composite toolbarButtonComp = toolkit.createComposite(toolbarComp);
			RowLayout buttonCompLayout = new RowLayout();
			buttonCompLayout.marginBottom = 0;
			buttonCompLayout.marginTop = 0;
			toolbarButtonComp.setLayout(buttonCompLayout);
			toolbarButtonComp.setBackground(null);

			if (supportsDelete()) {
				final ImageHyperlink deleteComment = new ImageHyperlink(toolbarButtonComp, SWT.NULL);
				toolkit.adapt(deleteComment, true, true);
				deleteComment.setImage(TasksUiImages.getImage(TasksUiImages.REMOVE));
				deleteComment.setToolTipText("Remove");

				deleteComment.addHyperlinkListener(new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						getTaskEditorPage().deleteComment(taskComment);
						getTaskEditorPage().submitToRepository();
					}
				});

			}

			final ImageHyperlink replyLink = createReplyHyperlink(toolbarButtonComp, toolkit, taskComment);
			formHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					EditorUtil.toggleExpandableComposite(!expandableComposite.isExpanded(), expandableComposite);
				}

				@Override
				public void linkEntered(HyperlinkEvent e) {
					replyLink.setUnderlined(true);
					super.linkEntered(e);
				}

				@Override
				public void linkExited(HyperlinkEvent e) {
					replyLink.setUnderlined(false);
					super.linkExited(e);
				}
			});

			expandableComposite.setTextClient(toolbarComp);

			toolbarButtonComp.setVisible(expandableComposite.isExpanded());

			// HACK: This is necessary
			// due to a bug in SWT's ExpandableComposite.
			// 165803: Expandable bars should expand when clicking anywhere
			// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
			expandableComposite.setData(toolbarButtonComp);

			expandableComposite.setLayout(new GridLayout());
			expandableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			final Composite ecComposite = toolkit.createComposite(expandableComposite);
			GridLayout ecLayout = new GridLayout();
			ecLayout.marginHeight = 0;
			ecLayout.marginBottom = 3;
			ecLayout.marginLeft = 15;
			ecComposite.setLayout(ecLayout);
			ecComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setClient(ecComposite);
			// code for outline
			commentComposites.add(expandableComposite);
			getTaskEditorPage().addSelectableControl(taskComment, expandableComposite);
			expandableComposite.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent e) {
					toolbarButtonComp.setVisible(expandableComposite.isExpanded());
					TextViewer viewer = null;
					if (e.getState() && expandableComposite.getData("viewer") == null) {
						RichTextAttributeEditor editor = new RichTextAttributeEditor(
								getTaskEditorPage().getAttributeManager(),
								taskComment.getAttribute(RepositoryTaskAttribute.COMMENT_TEXT));
						editor.setDecorationEnabled(false);
						editor.createControl(ecComposite, toolkit);
						viewer = editor.getViewer();
						expandableComposite.setData("viewer", viewer.getTextWidget());
						viewer.getTextWidget().addFocusListener(new FocusListener() {

							public void focusGained(FocusEvent e) {
								selectedComment = taskComment;

							}

							public void focusLost(FocusEvent e) {
								selectedComment = null;
							}
						});

						StyledText styledText = viewer.getTextWidget();
						GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(styledText);
						getTaskEditorPage().resetLayout();
					} else {
						// dispose viewer
						if (expandableComposite.getData("viewer") instanceof StyledText) {
							((StyledText) expandableComposite.getData("viewer")).dispose();
							expandableComposite.setData("viewer", null);
						}
						getTaskEditorPage().resetLayout();
					}
				}
			});

			AbstractTask repositoryTask = getTaskEditorPage().getTask();
			if ((repositoryTask != null && repositoryTask.getLastReadTimeStamp() == null)
					|| getTaskEditorPage().getAttributeManager().getOldTaskData() == null) {
				// hit or lost task data, expose all comments
				EditorUtil.toggleExpandableComposite(true, expandableComposite);
				foundNew = true;
			} else if (getTaskEditorPage().getAttributeManager().isNewComment(taskComment)) {
				// TODO EDITOR getTaskEditorPage().getAttributeEditorManager().decorate(taskAttribute, control)
				expandableComposite.setBackground(getTaskEditorPage().getColorIncoming());
				EditorUtil.toggleExpandableComposite(true, expandableComposite);
				foundNew = true;
			}

		}
		if (foundNew) {
			commentsSection.setExpanded(true);
		} else if (getTaskData().getComments() == null || getTaskData().getComments().size() == 0) {
			commentsSection.setExpanded(false);
		} else if (getTaskEditorPage().getAttributeManager().getTaskData() != null
				&& getTaskEditorPage().getAttributeManager().getOldTaskData() != null) {
			List<TaskComment> newTaskComments = getTaskEditorPage().getAttributeManager().getTaskData().getComments();
			List<TaskComment> oldTaskComments = getTaskEditorPage().getAttributeManager()
					.getOldTaskData()
					.getComments();
			if (newTaskComments == null || oldTaskComments == null) {
				commentsSection.setExpanded(true);
			} else {
				commentsSection.setExpanded(newTaskComments.size() != oldTaskComments.size());
			}
		}

		setControl(addCommentsComposite);
	}

//	private boolean shouldExpandCommentSection() {
//		for (final TaskComment taskComment : taskData.getComments()) {
//			if ((repositoryTask != null && repositoryTask.getLastReadTimeStamp() == null)
//					|| editorInput.getOldTaskData() == null) {
//				return true;
//			} else if (isNewComment(taskComment)) {
//				return true;
//			}
//		}
//
//		if (taskData.getComments() == null || taskData.getComments().size() == 0) {
//			return false;
//		} else if (editorInput.getTaskData() != null && editorInput.getOldTaskData() != null) {
//			List<TaskComment> newTaskComments = editorInput.getTaskData().getComments();
//			List<TaskComment> oldTaskComments = editorInput.getOldTaskData().getComments();
//			if (newTaskComments == null || oldTaskComments == null) {
//				return true;
//			} else if (newTaskComments.size() != oldTaskComments.size()) {
//				return true;
//			}
//		}
//		return false;
//	}

	// TODO EDITOR merge with AbstractReplyToAction
	private ImageHyperlink createReplyHyperlink(Composite composite, FormToolkit toolkit, final TaskComment taskComment) {
		final ImageHyperlink replyLink = new ImageHyperlink(composite, SWT.NULL);
		toolkit.adapt(replyLink, true, true);
		replyLink.setImage(TasksUiImages.getImage(TasksUiImages.REPLY));
		replyLink.setToolTipText(LABEL_REPLY);
		// no need for the background - transparency will take care of it
		replyLink.setBackground(null);
		// replyLink.setBackground(section.getTitleBarGradientBackground());
		replyLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(" (In reply to comment #" + taskComment.getNumber() + ")\n");
				CommentQuoter quoter = new CommentQuoter();
				strBuilder.append(quoter.quote(taskComment.getText()));
				getTaskEditorPage().appendTextToNewComment(strBuilder.toString());
			}
		});

		return replyLink;
	}

	@Override
	protected void fillToolBar(ToolBarManager barManager) {
		if (getTaskData().getComments().isEmpty()) {
			return;
		}

		Action collapseAllAction = new Action("") {
			@Override
			public void run() {
				hideAllComments();
			}
		};
		collapseAllAction.setImageDescriptor(TasksUiImages.COLLAPSE_ALL);
		collapseAllAction.setToolTipText("Collapse All Comments");
		barManager.add(collapseAllAction);

		Action expandAllAction = new Action("") {
			@Override
			public void run() {
				expandAllComments();
			}
		};
		expandAllAction.setImageDescriptor(TasksUiImages.EXPAND_ALL);
		expandAllAction.setToolTipText("Expand All Comments");
		barManager.add(expandAllAction);

//		ImageHyperlink collapseAllHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
//		collapseAllHyperlink.setToolTipText("Collapse All Comments");
//		getManagedForm().getToolkit().adapt(collapseAllHyperlink, true, true);
//		collapseAllHyperlink.setBackground(null);
//		collapseAllHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.COLLAPSE_ALL));
//		collapseAllHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				hideAllComments();
//			}
//		});

//		ImageHyperlink expandAllHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
//		expandAllHyperlink.setToolTipText("Expand All Comments");
//		getManagedForm().getToolkit().adapt(expandAllHyperlink, true, true);
//		expandAllHyperlink.setBackground(null);
//		expandAllHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.EXPAND_ALL));
//		expandAllHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
//					public void run() {
//						revealAllComments();
//					}
//				});
//			}
//		});
//		commentsSection.setTextClient(commentsSectionClient);
	}

	private void hideAllComments() {
		try {
			getTaskEditorPage().setReflow(false);

			for (ExpandableComposite composite : commentComposites) {
				if (composite.isDisposed()) {
					continue;
				}

				if (composite.isExpanded()) {
					EditorUtil.toggleExpandableComposite(false, composite);
				}

//			Composite comp = composite.getParent();
//			while (comp != null && !comp.isDisposed()) {
//				if (comp instanceof ExpandableComposite && !comp.isDisposed()) {
//					ExpandableComposite ex = (ExpandableComposite) comp;
//					setExpandableCompositeState(false, ex);
//
//					// HACK: This is necessary
//					// due to a bug in SWT's ExpandableComposite.
//					// 165803: Expandable bars should expand when clicking anywhere
//					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
//					if (ex.getData() != null && ex.getData() instanceof Composite) {
//						((Composite) ex.getData()).setVisible(false);
//					}
//
//					break;
//				}
//				comp = comp.getParent();
//			}
			}

//		if (commentsSection != null) {
//			commentsSection.setExpanded(false);
//		}

		} finally {
			getTaskEditorPage().setReflow(true);
		}
	}

	private void expandAllComments() {
		try {
			getTaskEditorPage().setReflow(false);

			if (commentsSection != null) {
				commentsSection.setExpanded(true);
			}
			for (ExpandableComposite composite : commentComposites) {
				if (composite.isDisposed()) {
					continue;
				}
				if (!composite.isExpanded()) {
					EditorUtil.toggleExpandableComposite(true, composite);
				}
//			Composite comp = composite.getParent();
//			while (comp != null && !comp.isDisposed()) {
//				if (comp instanceof ExpandableComposite && !comp.isDisposed()) {
//					ExpandableComposite ex = (ExpandableComposite) comp;
//					setExpandableCompositeState(true, ex);
//
//					// HACK: This is necessary
//					// due to a bug in SWT's ExpandableComposite.
//					// 165803: Expandable bars should expand when clicking
//					// anywhere
//					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
//					if (ex.getData() != null && ex.getData() instanceof Composite) {
//						((Composite) ex.getData()).setVisible(true);
//					}
//
//					break;
//				}
//				comp = comp.getParent();
//			}
			}
		} finally {
			getTaskEditorPage().setReflow(true);
		}
	}

	public void setSupportsDelete(boolean supportsDelete) {
		this.supportsDelete = supportsDelete;
	}

	private boolean supportsDelete() {
		return supportsDelete;
	}

	public TaskComment getSelectedComment() {
		return selectedComment;
	}

	// TODO EDITOR where should this go?
	public String formatDate(String dateString) {
		return dateString;
	}

}
