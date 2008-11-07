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

	private static final String KEY_EDITOR = "viewer";

	private static final String LABEL_REPLY = "Reply";

	/** Expandable composites are indented by 6 pixels by default. */
	private static final int INDENT = -6;

	protected Section section;

	protected List<ExpandableComposite> commentComposites;

	private List<TaskAttribute> comments;

	private boolean hasIncoming;

	private CommentGroupStrategy commentGroupStrategy;

	private List<Section> subSections;

	private boolean expandAllInProgress;

	public TaskEditorCommentPart() {
		setPartName("Comments");
	}

	protected void expandComment(FormToolkit toolkit, Composite composite, Composite toolBarComposite,
			final TaskComment taskComment, boolean expanded) {
		toolBarComposite.setVisible(expanded);
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

	private void initialize() {
		comments = getTaskData().getAttributeMapper().getAttributesByType(getTaskData(), TaskAttribute.TYPE_COMMENT);
		if (comments.size() > 0) {
			for (TaskAttribute commentAttribute : comments) {
				if (getModel().hasIncomingChanges(commentAttribute)) {
					hasIncoming = true;
					break;
				}
			}
		}
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();

		section = createSection(parent, toolkit, hasIncoming);
		section.setText(section.getText() + " (" + comments.size() + ")");

		if (comments.isEmpty()) {
			section.setEnabled(false);
		} else {
			if (hasIncoming) {
				commentComposites = new ArrayList<ExpandableComposite>();
				expandSection(toolkit, section, comments);
			} else {
				section.addExpansionListener(new ExpansionAdapter() {
					@Override
					public void expansionStateChanged(ExpansionEvent event) {
						if (commentComposites == null) {
							try {
								getTaskEditorPage().setReflow(false);
								commentComposites = new ArrayList<ExpandableComposite>();
								expandSection(toolkit, section, comments);
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

	protected void expandSection(final FormToolkit toolkit, final Section section, List<TaskAttribute> commentAttributes) {
		Composite composite = toolkit.createComposite(section);
		section.setClient(composite);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		composite.setLayout(layout);

		// group comments
		List<ITaskComment> comments = new ArrayList<ITaskComment>();
		for (TaskAttribute commentAttribute : commentAttributes) {
			comments.add(convertToTaskComment(getModel(), commentAttribute));
		}
		String currentPersonId = getModel().getTaskRepository().getUserName();
		List<CommentGroup> commentGroups = getCommentGroupStrategy().groupComments(comments, currentPersonId);
		if (commentGroups.size() > 0) {
			subSections = new ArrayList<Section>();
			for (int i = 0; i < commentGroups.size() - 1; i++) {
				createSubSection(toolkit, composite, commentGroups.get(i));
			}
			// last group is not rendered as subsection
			CommentGroup lastGroup = commentGroups.get(commentGroups.size() - 1);
			addComments(toolkit, composite, lastGroup.getCommentAttributes());
		}
	}

	protected void addComments(final FormToolkit toolkit, final Composite composite, final List<TaskAttribute> comments) {
		for (final TaskAttribute commentAttribute : comments) {
			boolean hasIncomingChanges = getModel().hasIncomingChanges(commentAttribute);
			final TaskComment taskComment = new TaskComment(getModel().getTaskRepository(), getModel().getTask(),
					commentAttribute);
			getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);
			int style = ExpandableComposite.TREE_NODE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
					| ExpandableComposite.COMPACT;
			if (hasIncomingChanges || expandAllInProgress) {
				style |= ExpandableComposite.EXPANDED;
			}
			final ExpandableComposite commentComposite = toolkit.createExpandableComposite(composite, style);
			commentComposite.setLayout(new GridLayout());
			commentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			commentComposite.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			commentComposites.add(commentComposite);

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

			final Composite commentTextComposite = toolkit.createComposite(commentComposite);
			commentComposite.setClient(commentTextComposite);
			commentTextComposite.setLayout(new FillWidthLayout(EditorUtil.getLayoutAdvisor(getTaskEditorPage()), 15, 0,
					0, 3));
			//commentTextComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if (composite.getParent().getParent().getParent() instanceof Section) {
				GridDataFactory.fillDefaults().grab(true, false).applyTo(commentComposite);
			} else {
				GridDataFactory.fillDefaults().grab(true, false).indent(INDENT, 0).applyTo(commentComposite);
			}
			commentComposite.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					expandComment(toolkit, commentTextComposite, buttonComposite, taskComment, event.getState());
				}
			});
			if (hasIncomingChanges) {
				commentComposite.setBackground(getTaskEditorPage().getAttributeEditorToolkit().getColorIncoming());
			}
			if (commentComposite.isEnabled()) {
				expandComment(toolkit, commentTextComposite, buttonComposite, taskComment, true);
			}

			// for outline
			EditorUtil.setMarker(commentComposite, commentAttribute.getId());
		}
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

	protected ImageHyperlink createReplyHyperlink(Composite composite, FormToolkit toolkit,
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
		collapseAllAction.setImageDescriptor(CommonImages.COLLAPSE_ALL_SMALL);
		collapseAllAction.setToolTipText("Collapse All Comments");
		barManager.add(collapseAllAction);

		Action expandAllAction = new Action("") {
			@Override
			public void run() {
				expandAllComments();
			}
		};
		expandAllAction.setImageDescriptor(CommonImages.EXPAND_ALL_SMALL);
		expandAllAction.setToolTipText("Expand All Comments");
		barManager.add(expandAllAction);
	}

	private void hideAllComments() {
		if (commentComposites != null) {
			try {
				getTaskEditorPage().setReflow(false);

				for (ExpandableComposite composite : commentComposites) {
					if (composite.isDisposed()) {
						continue;
					}

					if (composite.isExpanded()) {
						EditorUtil.toggleExpandableComposite(false, composite);
					}
				}
			} finally {
				getTaskEditorPage().setReflow(true);
			}
			getTaskEditorPage().reflow();
		}
	}

	protected void expandAllComments() {
		try {
			expandAllInProgress = true;
			getTaskEditorPage().setReflow(false);

			if (section != null) {
				EditorUtil.toggleExpandableComposite(true, section);
			}

			if (subSections != null) {
				// first toggle on all subSections
				if (section != null) {
					EditorUtil.toggleExpandableComposite(true, section);
				}

				for (Section subSection : subSections) {
					if (subSection.isDisposed()) {
						continue;
					}
					EditorUtil.toggleExpandableComposite(true, subSection);
				}
			}

			for (ExpandableComposite composite : commentComposites) {
				if (composite.isDisposed()) {
					continue;
				}
				if (!composite.isExpanded()) {
					EditorUtil.toggleExpandableComposite(true, composite);
				}
			}
		} finally {
			expandAllInProgress = false;
			getTaskEditorPage().setReflow(true);
		}
		getTaskEditorPage().reflow();
	}

//	private static void toggleChildren(Composite composite, boolean expended) {
//		for (Control child : composite.getChildren()) {
//			if (child instanceof ExpandableComposite && !child.isDisposed()) {
//				EditorUtil.toggleExpandableComposite(expended, (ExpandableComposite) child);
//			}
//			if (child instanceof Composite) {
//				toggleChildren((Composite) child, expended);
//			}
//		}
//	}

	private TaskComment convertToTaskComment(TaskDataModel taskDataModel, TaskAttribute commentAttribute) {
		TaskComment taskComment = new TaskComment(taskDataModel.getTaskRepository(), taskDataModel.getTask(),
				commentAttribute);
		taskDataModel.getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);
		return taskComment;
	}

//	private void createCurrentSubsectionToolBar(final FormToolkit toolkit, final Section section) {
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

	private void createSubSection(final FormToolkit toolkit, final Composite parent, final CommentGroup commentGroup) {
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.SHORT_TITLE_BAR;
		if (/*commentGroup.hasIncoming() || */expandAllInProgress) {
			style |= ExpandableComposite.EXPANDED;
		}

		final Section groupSection = toolkit.createSection(parent, style);
		if (commentGroup.hasIncoming()) {
			groupSection.setBackground(getTaskEditorPage().getAttributeEditorToolkit().getColorIncoming());
		}
		groupSection.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridDataFactory.fillDefaults().grab(true, false).indent(2 * INDENT, 0).applyTo(groupSection);
		groupSection.setText(commentGroup.getGroupName() + " (" + commentGroup.getCommentAttributes().size() + ")");

		// create tool bar only for Current section
//		if (commentGroup.getGroupName().equals("Current")) {
//			createCurrentSubsectionToolBar(toolkit, groupSection);
//		}

		// only Current subsection will be expanded by default
		if (groupSection.isExpanded()) {
			expandSubSection(toolkit, commentGroup, groupSection);
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
							expandSubSection(toolkit, commentGroup, groupSection);
						} finally {
							getTaskEditorPage().setReflow(true);
						}
						getTaskEditorPage().reflow();
					}
				}
			});
		}

		subSections.add(groupSection);
	}

	private void expandSubSection(final FormToolkit toolkit, CommentGroup commentGroup, Section subSection) {
		Composite composite = toolkit.createComposite(subSection);
		subSection.setClient(composite);
		GridLayout contentLayout = new GridLayout();
		contentLayout.marginHeight = 0;
		contentLayout.marginWidth = 0;
		composite.setLayout(contentLayout);

		addComments(toolkit, composite, commentGroup.getCommentAttributes());
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

//	private void toggleSection(Section section, boolean expended) {
//		try {
//			getTaskEditorPage().setReflow(false);
//
//			if (expended && !section.isDisposed()) {
//				EditorUtil.toggleExpandableComposite(true, section);
//			}
//
//			toggleChildren(section, expended);
//		} finally {
//			getTaskEditorPage().setReflow(true);
//		}
//		getTaskEditorPage().reflow();
//	}

}
