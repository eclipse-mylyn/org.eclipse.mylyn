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
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskComment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskEditorCommentPart extends AbstractTaskEditorPart {

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private static final String LABEL_REPLY = "Reply";

	private TaskComment selectedComment;

	private Section section;

	private List<ExpandableComposite> commentComposites;

	private ArrayList<TaskAttribute> comments;

	private boolean hasIncoming;

	public TaskEditorCommentPart() {
		setPartName("Comments");
	}

	private void initialize() {
		TaskAttribute container = getTaskData().getMappedAttribute(TaskAttribute.CONTAINER_COMMENTS);
		comments = new ArrayList<TaskAttribute>(container.getAttributes().values());

		for (TaskAttribute commentAttribute : comments) {
			if (getAttributeManager().hasIncomingChanges(commentAttribute)) {
				hasIncoming = true;
				break;
			}
		}
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		section = createSection(parent, toolkit, hasIncoming);
		section.setText(section.getText() + " (" + comments.size() + ")");

		// Additional (read-only) Comments Area
		final Composite addCommentsComposite = toolkit.createComposite(section);
		section.setClient(addCommentsComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		addCommentsComposite.setLayout(addCommentsLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addCommentsComposite);

		commentComposites = new ArrayList<ExpandableComposite>();
		for (final TaskAttribute commentAttribute : comments) {
			final TaskComment taskComment = getTaskData().getAttributeMapper().getTaskComment(commentAttribute);
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
					&& taskComment.getAuthor().getPersonId().equalsIgnoreCase(
							getTaskEditorPage().getTaskRepository().getUserName())) {
				formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_ME_NARROW));
			} else {
				formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_NARROW));
			}

			String authorName;
			String tooltipText;
			if (taskComment.getAuthor().getName() != null) {
				authorName = taskComment.getAuthor().getName();
				tooltipText = taskComment.getAuthor().getPersonId();
			} else {
				authorName = taskComment.getAuthor().getPersonId();
				tooltipText = null;
			}

			formHyperlink.setText(taskComment.getNumber() + ": " + authorName + ", "
					+ getTaskEditorPage().getAttributeEditorToolkit().formatDate(taskComment.getCreationDate()));
			formHyperlink.setToolTipText(tooltipText);
			formHyperlink.setEnabled(true);
			formHyperlink.setUnderlined(false);

			final Composite toolbarButtonComp = toolkit.createComposite(toolbarComp);
			RowLayout buttonCompLayout = new RowLayout();
			buttonCompLayout.marginBottom = 0;
			buttonCompLayout.marginTop = 0;
			toolbarButtonComp.setLayout(buttonCompLayout);
			toolbarButtonComp.setBackground(null);

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
						viewer = createViewer(ecComposite, toolkit, taskComment.getText());
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

			if (getAttributeManager().hasIncomingChanges(commentAttribute)) {
				expandableComposite.setBackground(getTaskEditorPage().getColorIncoming());
				EditorUtil.toggleExpandableComposite(true, expandableComposite);
			}
		}

		setSection(toolkit, section);
	}

	private RepositoryTextViewer createViewer(Composite parent, FormToolkit toolkit, String text) {
		TaskRepository taskRepository = getTaskEditorPage().getTaskRepository();
		RepositoryTextViewer viewer = new RepositoryTextViewer(taskRepository, parent, SWT.FLAT | SWT.WRAP | SWT.MULTI);

		// NOTE: configuration must be applied before the document is set in order for
		// hyper link coloring to work, the Presenter requires the document object up front
		TextSourceViewerConfiguration viewerConfig = new RepositoryTextViewerConfiguration(taskRepository, false);
		viewer.configure(viewerConfig);

		Document document = new Document(text);
		viewer.setEditable(false);
		viewer.setDocument(document);

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font font = themeManager.getCurrentTheme().getFontRegistry().get(TaskListColorsAndFonts.TASK_EDITOR_FONT);
		viewer.getTextWidget().setFont(font);
		toolkit.adapt(viewer.getTextWidget(), true, true);

		return viewer;
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
//			List<TaskComment> newTaskComments = editorInput.comments;
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
		if (comments.isEmpty()) {
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

			if (section != null) {
				section.setExpanded(true);
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

	public TaskComment getSelectedComment() {
		return selectedComment;
	}

}
