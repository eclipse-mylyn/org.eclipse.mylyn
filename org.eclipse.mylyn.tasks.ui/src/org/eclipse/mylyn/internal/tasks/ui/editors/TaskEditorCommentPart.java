/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.data.RepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskComment;
import org.eclipse.swt.SWT;
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
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskEditorCommentPart extends AbstractTaskEditorPart {

	private class ExpandCompositeListener extends HyperlinkAdapter {

		private final ExpandableComposite expandableComposite;

		public ExpandCompositeListener(ExpandableComposite expandableComposite) {
			this.expandableComposite = expandableComposite;
		}

		@Override
		public void linkActivated(HyperlinkEvent e) {
			EditorUtil.toggleExpandableComposite(!expandableComposite.isExpanded(), expandableComposite);
		}

	}

	private class ExpansionListener extends ExpansionAdapter {

		private static final String KEY_EDITOR = "viewer";

		private final TaskAttribute attribute;

		private final Composite toolBarComposite;

		private final FormToolkit toolkit;

		private final Composite composite;

		public ExpansionListener(FormToolkit toolkit, Composite composite, Composite toolBarComposite,
				TaskAttribute attribute) {
			this.toolkit = toolkit;
			this.composite = composite;
			this.attribute = attribute;
			this.toolBarComposite = toolBarComposite;
		}

		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			toolBarComposite.setVisible(e.getState());
			if (e.getState() && composite.getData(KEY_EDITOR) == null) {
				// create viewer
				TaskAttribute textAttribute = getTaskData().getAttributeMapper().getAssoctiatedAttribute(attribute);
				AbstractAttributeEditor editor = createEditor(textAttribute);
				if (editor != null) {
					editor.createControl(composite, toolkit);
					composite.setData(KEY_EDITOR, editor);

					GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(editor.getControl());

					getTaskEditorPage().getAttributeEditorToolkit().adapt(editor);
					getTaskEditorPage().resetLayout();
				}
			} else if (!e.getState() && composite.getData(KEY_EDITOR) != null) {
				// dispose viewer
				AbstractAttributeEditor editor = (AbstractAttributeEditor) composite.getData(KEY_EDITOR);
				getTaskEditorPage().getAttributeEditorToolkit().dispose(editor);
				editor.getControl().dispose();
				composite.setData(KEY_EDITOR, null);
				getTaskEditorPage().resetLayout();
			}
		}
	}

	private static final int DESCRIPTION_WIDTH = 79 * 7; // 500;

	private static final String LABEL_REPLY = "Reply";

	private Section section;

	private List<ExpandableComposite> commentComposites;

	private List<TaskAttribute> comments;

	private boolean hasIncoming;

	public TaskEditorCommentPart() {
		setPartName("Comments");
	}

	private void initialize() {
		TaskAttribute container = getTaskData().getMappedAttribute(TaskAttribute.CONTAINER_COMMENTS);
		if (container != null) {
			comments = new ArrayList<TaskAttribute>(container.getAttributes().values());
			for (TaskAttribute commentAttribute : comments) {
				if (getAttributeManager().hasIncomingChanges(commentAttribute)) {
					hasIncoming = true;
					break;
				}
			}
		} else {
			comments = Collections.emptyList();
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
			TaskComment taskComment = getTaskData().getAttributeMapper().getTaskComment(commentAttribute);
			ExpandableComposite expandableComposite = toolkit.createExpandableComposite(addCommentsComposite,
					ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
			expandableComposite.setLayout(new GridLayout());
			expandableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

			// always visible
			Composite titleComposite = toolkit.createComposite(expandableComposite);
			expandableComposite.setTextClient(titleComposite);
			RowLayout rowLayout = new RowLayout();
			rowLayout.pack = true;
			rowLayout.marginLeft = 0;
			rowLayout.marginBottom = 0;
			rowLayout.marginTop = 0;
			titleComposite.setLayout(rowLayout);
			titleComposite.setBackground(null);

			ImageHyperlink formHyperlink = createTitleHyperLink(toolkit, titleComposite, taskComment);
			formHyperlink.setFont(expandableComposite.getFont());
			formHyperlink.addHyperlinkListener(new ExpandCompositeListener(expandableComposite));

			// only visible when section is expanded
			Composite buttonComposite = toolkit.createComposite(titleComposite);
			RowLayout buttonCompLayout = new RowLayout();
			buttonCompLayout.marginBottom = 0;
			buttonCompLayout.marginTop = 0;
			buttonComposite.setLayout(buttonCompLayout);
			buttonComposite.setBackground(null);
			buttonComposite.setVisible(expandableComposite.isExpanded());

			createReplyHyperlink(buttonComposite, toolkit, taskComment);

			// HACK: This is necessary
			// due to a bug in SWT's ExpandableComposite.
			// 165803: Expandable bars should expand when clicking anywhere
			// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
			expandableComposite.setData(buttonComposite);
			commentComposites.add(expandableComposite);

			Composite commentComposite = toolkit.createComposite(expandableComposite);
			GridLayout ecLayout = new GridLayout();
			ecLayout.marginHeight = 0;
			ecLayout.marginBottom = 3;
			ecLayout.marginLeft = 15;
			commentComposite.setLayout(ecLayout);
			commentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setClient(commentComposite);
			// code for outline
			//getTaskEditorPage().addSelectableControl(taskComment, expandableComposite);
			expandableComposite.addExpansionListener(new ExpansionListener(toolkit, commentComposite, buttonComposite,
					commentAttribute));

			if (getAttributeManager().hasIncomingChanges(commentAttribute)) {
				expandableComposite.setBackground(getTaskEditorPage().getAttributeEditorToolkit().getColorIncoming());
				// TODO move to construction to improve performance
				EditorUtil.toggleExpandableComposite(true, expandableComposite);
			}
		}

		setSection(toolkit, section);
	}

	private ImageHyperlink createTitleHyperLink(final FormToolkit toolkit, final Composite toolbarComp,
			final TaskComment taskComment) {
		ImageHyperlink formHyperlink = toolkit.createImageHyperlink(toolbarComp, SWT.NONE);
		formHyperlink.setBackground(null);
		formHyperlink.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		RepositoryPerson author = taskComment.getAuthor();
		if (author != null
				&& author.getPersonId().equalsIgnoreCase(getTaskEditorPage().getTaskRepository().getUserName())) {
			formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_ME_NARROW));
		} else {
			formHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.PERSON_NARROW));
		}
		StringBuilder sb = new StringBuilder();
		sb.append(taskComment.getNumber());
		sb.append(": ");
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
			sb.append(getTaskEditorPage().getAttributeEditorToolkit().formatDate(taskComment.getCreationDate()));
		}
		formHyperlink.setText(sb.toString());
		formHyperlink.setEnabled(true);
		formHyperlink.setUnderlined(false);
		return formHyperlink;
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

}
