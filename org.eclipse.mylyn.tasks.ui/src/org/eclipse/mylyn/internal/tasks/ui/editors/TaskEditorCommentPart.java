/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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

	private TaskComment selectedComment;

	private final Section commentsSection;

	private boolean supportsDelete;

	private final List<ExpandableComposite> commentComposites = new ArrayList<ExpandableComposite>();

	private final RepositoryTaskEditorInput editorInput;

	private final AbstractTask repositoryTask;

	// TODO EDITOR remove editorInput and repositoryTask
	public TaskEditorCommentPart(AbstractTaskEditorPage taskEditorPage, Section commentsSection,
			RepositoryTaskEditorInput editorInput, AbstractTask repositoryTask) {
		super(taskEditorPage);
		this.commentsSection = commentsSection;
		this.editorInput = editorInput;
		this.repositoryTask = repositoryTask;
	}

	@Override
	public void createControl(Composite composite, FormToolkit toolkit) {
		commentsSection.setText(commentsSection.getText() + " (" + getTaskData().getComments().size() + ")");
		if (getTaskData().getComments().size() > 0) {
			commentsSection.setEnabled(true);

			final Composite commentsSectionClient = toolkit.createComposite(commentsSection);
			RowLayout rowLayout = new RowLayout();
			rowLayout.pack = true;
			rowLayout.marginLeft = 0;
			rowLayout.marginBottom = 0;
			rowLayout.marginTop = 0;
			commentsSectionClient.setLayout(rowLayout);
			commentsSectionClient.setBackground(null);

			ImageHyperlink collapseAllHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
			collapseAllHyperlink.setToolTipText("Collapse All Comments");
			toolkit.adapt(collapseAllHyperlink, true, true);
			collapseAllHyperlink.setBackground(null);
			collapseAllHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.COLLAPSE_ALL));
			collapseAllHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					hideAllComments();
				}
			});

			ImageHyperlink expandAllHyperlink = new ImageHyperlink(commentsSectionClient, SWT.NONE);
			expandAllHyperlink.setToolTipText("Expand All Comments");
			toolkit.adapt(expandAllHyperlink, true, true);
			expandAllHyperlink.setBackground(null);
			expandAllHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.EXPAND_ALL));
			expandAllHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
						public void run() {
							revealAllComments();
						}
					});
				}
			});
			commentsSection.setTextClient(commentsSectionClient);
		} else {
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
					+ getTaskEditorPage().formatDate(taskComment.getCreated()));

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

			final ImageHyperlink replyLink = getTaskEditorPage().createReplyHyperlink(taskComment.getNumber(), toolbarButtonComp,
					taskComment.getText());

			formHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					toggleExpandableComposite(!expandableComposite.isExpanded(), expandableComposite);
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
						// FIXME EDITOR
						viewer = getTaskEditorPage().addTextViewer(getTaskRepository(), ecComposite,
								taskComment.getText().trim(), SWT.MULTI | SWT.WRAP);
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

			if ((repositoryTask != null && repositoryTask.getLastReadTimeStamp() == null)
					|| editorInput.getOldTaskData() == null) {
				// hit or lost task data, expose all comments
				toggleExpandableComposite(true, expandableComposite);
				foundNew = true;
			} else if (getTaskEditorPage().getAttributeEditorManager().isNewComment(taskComment)) {
				// TODO EDITOR getTaskEditorPage().getAttributeEditorManager().decorate(taskAttribute, control)
				expandableComposite.setBackground(getTaskEditorPage().getAttributeEditorManager().getColorIncoming());
				toggleExpandableComposite(true, expandableComposite);
				foundNew = true;
			}

		}
		if (foundNew) {
			commentsSection.setExpanded(true);
		} else if (getTaskData().getComments() == null || getTaskData().getComments().size() == 0) {
			commentsSection.setExpanded(false);
		} else if (editorInput.getTaskData() != null && editorInput.getOldTaskData() != null) {
			List<TaskComment> newTaskComments = editorInput.getTaskData().getComments();
			List<TaskComment> oldTaskComments = editorInput.getOldTaskData().getComments();
			if (newTaskComments == null || oldTaskComments == null) {
				commentsSection.setExpanded(true);
			} else {
				commentsSection.setExpanded(newTaskComments.size() != oldTaskComments.size());
			}
		}
		
		setControl(addCommentsComposite);
	}

	private void hideAllComments() {
		try {
			getTaskEditorPage().setReflow(false);

			for (ExpandableComposite composite : commentComposites) {
				if (composite.isDisposed()) {
					continue;
				}

				if (composite.isExpanded()) {
					toggleExpandableComposite(false, composite);
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

	private void revealAllComments() {
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
					toggleExpandableComposite(true, composite);
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

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 */
	// TODO EDITOR move to utility class?
	private void toggleExpandableComposite(boolean expanded, ExpandableComposite comp) {
		if (comp.isExpanded() != expanded) {
			Method method = null;
			try {
				method = comp.getClass().getDeclaredMethod("programmaticToggleState");
				method.setAccessible(true);
				method.invoke(comp);
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public TaskComment getSelectedComment() {
		return selectedComment;
	}

}
