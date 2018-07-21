/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart.CommentGroupViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart.CommentViewer;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Adds support for finding text to the task editor.
 * 
 * @author Jingwen Ou
 * @author Lily Guo
 * @author Sam Davis
 */
public class TaskEditorFindSupport {

	private Action toggleFindAction;

	private static final Color HIGHLIGHTER_YELLOW = new Color(Display.getDefault(), 255, 238, 99);

	private static final Color ERROR_NO_RESULT = new Color(Display.getDefault(), 255, 150, 150);

	private final List<StyledText> styledTexts = new ArrayList<StyledText>();

	private final List<CommentGroupViewer> commentGroupViewers = new ArrayList<CommentGroupViewer>();

	private final AbstractTaskEditorPage taskEditorPage;;

	public TaskEditorFindSupport(AbstractTaskEditorPage page) {
		Assert.isNotNull(page);
		this.taskEditorPage = page;
	}

	public void toggleFind() {
		if (toggleFindAction != null) {
			toggleFindAction.setChecked(!toggleFindAction.isChecked());
			toggleFindAction.run();
		}
	}

	public void addFindAction(IToolBarManager toolBarManager) {
		if (toggleFindAction != null && toggleFindAction.isChecked()) {
			ControlContribution findTextboxControl = new ControlContribution(Messages.TaskEditorFindSupport_Find) {
				@Override
				protected Control createControl(Composite parent) {
					FormToolkit toolkit = taskEditorPage.getEditor().getHeaderForm().getToolkit();
					final Composite findComposite = toolkit.createComposite(parent);

					GridLayout findLayout = new GridLayout();
					findLayout.marginHeight = 4;
					findComposite.setLayout(findLayout);
					findComposite.setBackground(null);

					final Text findText = toolkit.createText(findComposite, "", SWT.FLAT); //$NON-NLS-1$
					findText.setLayoutData(new GridData(100, SWT.DEFAULT));
					findText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
					findText.setFocus();
					toolkit.adapt(findText, false, false);

					findText.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent e) {
							if (findText.getText().equals("")) { //$NON-NLS-1$
								clearSearchResults();
								findText.setBackground(null);
							}
						}
					});

					findText.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetDefaultSelected(SelectionEvent event) {
							searchTaskEditor(findText);
						}
					});
					toolkit.paintBordersFor(findComposite);
					return findComposite;
				}

			};
			toolBarManager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, findTextboxControl);
		}

		if (toggleFindAction == null) {
			toggleFindAction = new Action("", SWT.TOGGLE) { //$NON-NLS-1$
				@Override
				public void run() {
					if (!this.isChecked()) {
						clearSearchResults();
					}
					taskEditorPage.getEditor().updateHeaderToolBar();
				}

			};
			toggleFindAction.setImageDescriptor(CommonImages.FIND);
			toggleFindAction.setToolTipText(Messages.TaskEditorFindSupport_Find);
		}
		toolBarManager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, toggleFindAction);
	}

	protected void searchTaskEditor(final Text findBox) {
		try {
			taskEditorPage.setReflow(false);
			findBox.setBackground(null);
			if (findBox.getText().equals("")) { //$NON-NLS-1$
				return;
			}
			clearSearchResults();
			String searchString = findBox.getText().toLowerCase();
			for (IFormPart part : taskEditorPage.getManagedForm().getParts()) {
				if (!(part instanceof AbstractTaskEditorPart)) {
					continue;
				}
				Control control = ((AbstractTaskEditorPart) part).getControl();
				if (part instanceof TaskEditorSummaryPart) {
					if (contains(taskEditorPage.getModel().getTaskData(), TaskAttribute.SUMMARY, searchString)) {
						gatherStyledTexts(control, styledTexts);
					}
				} else if (part instanceof TaskEditorPlanningPart) {
					RichTextEditor noteEditor = ((TaskEditorPlanningPart) part).getPlanningPart().getNoteEditor();
					if (noteEditor != null && noteEditor.getText() != null
							&& noteEditor.getText().toLowerCase().contains(searchString)) {
						gatherStyledTexts(control, styledTexts);
					}
				} else if (part instanceof TaskEditorDescriptionPart) {
					if (contains(taskEditorPage.getModel().getTaskData(), TaskAttribute.DESCRIPTION, searchString)) {
						gatherStyledTexts(control, styledTexts);
					}
				} else if (part instanceof TaskEditorCommentPart) {
					commentGroupViewers.clear();
					commentGroupViewers.addAll(((TaskEditorCommentPart) part).getCommentGroupViewers());
					searchCommentPart(searchString, (TaskEditorCommentPart) part, commentGroupViewers, styledTexts);
				}
			}

			for (StyledText styledText : styledTexts) {
				highlightMatches(searchString, styledText);
			}
			if (styledTexts.isEmpty()) {
				findBox.setBackground(ERROR_NO_RESULT);
			}
		} finally {
			taskEditorPage.setReflow(true);
		}
		taskEditorPage.reflow();
		findBox.setFocus();
	}

	protected static boolean contains(TaskData taskData, String attributeId, String searchString) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(attributeId);
		if (attribute != null) {
			return attribute.getValue().toLowerCase().contains(searchString);
		}
		return false;
	}

	private void searchCommentPart(final String searchString, final TaskEditorCommentPart part,
			List<CommentGroupViewer> commentGroupViewers, final List<StyledText> styledTexts) {
		TaskData taskData = taskEditorPage.getModel().getTaskData();
		List<TaskAttribute> commentAttributes = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_COMMENT);

		if (!anyCommentContains(commentAttributes, searchString)) {
			return;
		}

		if (!part.isCommentSectionExpanded()) {
			try {
				part.setReflow(false);
				part.expandAllComments(false);
			} finally {
				part.setReflow(true);
			}
		}

		int end = commentAttributes.size();
		boolean expandMatchingGroup = true;
		for (int i = commentGroupViewers.size() - 1; i >= 0; i--) {
			final CommentGroupViewer group = commentGroupViewers.get(i);
			List<CommentViewer> commentViewers = group.getCommentViewers();
			int start = end - commentViewers.size();
			List<TaskAttribute> groupAttributes = commentAttributes.subList(start, end);
			if (expandMatchingGroup && anyCommentContains(groupAttributes, searchString)) {
				if (!group.isExpanded()) {
					try {
						part.setReflow(false);
						group.setExpanded(true);
					} finally {
						part.setReflow(true);
					}
				}
				// once we've seen a matching group, don't expand any more groups
				expandMatchingGroup = false;
			}
			final List<CommentViewer> matchingViewers = searchComments(groupAttributes, commentViewers, searchString);
			if (!group.isRenderedInSubSection() || group.isExpanded()) {
				try {
					part.setReflow(false);
					gatherStyledTexts(matchingViewers, styledTexts);
				} finally {
					part.setReflow(true);
				}
				group.clearSectionHyperlink();
			} else if (!matchingViewers.isEmpty()) {
				addShowMoreLink(group, matchingViewers, part, searchString, styledTexts);
			} else {
				group.clearSectionHyperlink();
			}
			end = start;
		}
	}

	protected void addShowMoreLink(final CommentGroupViewer group, final List<CommentViewer> matchingViewers,
			final TaskEditorCommentPart part, final String searchString, final List<StyledText> styledTexts) {
		HyperlinkAdapter listener = new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				List<StyledText> commentStyledTexts = new ArrayList<StyledText>();
				try {
					taskEditorPage.setReflow(false);
					part.setReflow(false);
					group.setExpanded(true);
					gatherStyledTexts(matchingViewers, commentStyledTexts);
				} finally {
					taskEditorPage.setReflow(true);
					part.setReflow(true);
				}
				for (StyledText styledText : commentStyledTexts) {
					highlightMatches(searchString, styledText);
					styledTexts.add(styledText);
				}
				group.clearSectionHyperlink();
				taskEditorPage.reflow();
			}
		};
		group.createSectionHyperlink(
				NLS.bind(Messages.TaskEditorFindSupport_Show_X_more_results, matchingViewers.size()), listener);
	}

	private static boolean anyCommentContains(List<TaskAttribute> commentAttributes, String text) {
		for (TaskAttribute commentAttribute : commentAttributes) {
			if (commentContains(commentAttribute, text)) {
				return true;
			}
		}
		return false;
	}

	private static boolean commentContains(TaskAttribute commentAttribute, String searchString) {
		TaskAttribute attribute = commentAttribute.getMappedAttribute(TaskAttribute.COMMENT_TEXT);
		return attribute.getValue().toLowerCase().contains(searchString);
	}

	private static List<CommentViewer> searchComments(List<TaskAttribute> commentAttributes,
			List<CommentViewer> commentViewers, String searchString) {
		List<CommentViewer> matchingViewers = new ArrayList<TaskEditorCommentPart.CommentViewer>();
		for (int i = 0; i < commentViewers.size(); i++) {
			CommentViewer viewer = commentViewers.get(i);
			if (commentContains(commentAttributes.get(i), searchString)) {
				matchingViewers.add(viewer);
			}
		}
		return matchingViewers;
	}

	protected static void gatherStyledTexts(List<CommentViewer> commentViewers, List<StyledText> styledTexts) {
		for (CommentViewer viewer : commentViewers) {
			try {
				ExpandableComposite composite = (ExpandableComposite) viewer.getControl();
				viewer.suppressSelectionChanged(true);
				if (composite != null && !composite.isExpanded()) {
					CommonFormUtil.setExpanded(composite, true);
				}
				gatherStyledTextsInComposite(composite, styledTexts);
			} finally {
				viewer.suppressSelectionChanged(false);
			}
		}
	}

	private static void gatherStyledTexts(Control control, List<StyledText> result) {
		if (control instanceof ExpandableComposite) {
			ExpandableComposite composite = (ExpandableComposite) control;
			if (!composite.isExpanded()) {
				CommonFormUtil.setExpanded(composite, true);
			}
			gatherStyledTextsInComposite(composite, result);
		} else if (control instanceof Composite) {
			gatherStyledTextsInComposite((Composite) control, result);
		}
	}

	private static void gatherStyledTextsInComposite(Composite composite, List<StyledText> result) {
		if (composite != null && !composite.isDisposed()) {
			for (Control child : composite.getChildren()) {
				if (child instanceof StyledText) {
					result.add((StyledText) child);
				} else if (child instanceof Composite) {
					gatherStyledTextsInComposite((Composite) child, result);
				}
			}
		}
	}

	private static void highlightMatches(String searchString, StyledText styledText) {
		String text = styledText.getText().toLowerCase();
		for (int index = 0; index < text.length(); index += searchString.length()) {
			index = text.indexOf(searchString, index);
			if (index == -1) {
				break;
			}
			styledText.setStyleRange(new StyleRange(index, searchString.length(), null, HIGHLIGHTER_YELLOW));
		}
	}

	private void clearSearchResults() {
		for (StyledText oldText : styledTexts) {
			List<StyleRange> otherRanges = new ArrayList<StyleRange>();
			if (!oldText.isDisposed()) {
				for (StyleRange styleRange : oldText.getStyleRanges()) {
					if (styleRange.background == null || !styleRange.background.equals(HIGHLIGHTER_YELLOW)) {
						otherRanges.add(styleRange); // preserve ranges that aren't from highlighting search results
					}
				}
				oldText.setStyleRanges(otherRanges.toArray(new StyleRange[otherRanges.size()]));
			}
		}
		styledTexts.clear();
		for (CommentGroupViewer group : commentGroupViewers) {
			group.clearSectionHyperlink();
		}
	}

}
