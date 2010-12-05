/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.editors;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.internal.ITreeNode;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskNode;
import org.eclipse.mylyn.reviews.tasks.ui.Images;
import org.eclipse.mylyn.reviews.tasks.ui.ReviewUiUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class ReviewSummaryTaskEditorPart extends AbstractReviewTaskEditorPart {

	public static final String ID_PART_REVIEWSUMMARY = "org.eclipse.mylyn.reviews.ui.editors.parts.reviewsummary"; //$NON-NLS-1$
	private TreeViewer reviewResults;
	private ScopeViewerFilter scopeViewerFilter = new ScopeViewerFilter();

	public ReviewSummaryTaskEditorPart() {
		setPartName(Messages.ReviewSummaryTaskEditorPart_Partname);
	}

	@Override
	public void createControl(final Composite parent, FormToolkit toolkit) {
		Section summarySection = createSection(parent, toolkit,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| ExpandableComposite.EXPANDED);
		summarySection.setLayout(new FillLayout(SWT.HORIZONTAL));
		summarySection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		Composite reviewResultsComposite = toolkit
				.createComposite(summarySection);
		toolkit.paintBordersFor(reviewResultsComposite);
		reviewResultsComposite.setLayout(new GridLayout(1, false));

		reviewResults = createResultsViewer(reviewResultsComposite, toolkit);
		reviewResults.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		try {
			ITreeNode rootNode = getReviewPage().getReviewResults(
					new NullProgressMonitor());
			reviewResults.setInput(new Object[] { rootNode });
			reviewResults.expandAll();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		summarySection.setText("Review Summary ");
		summarySection.setClient(reviewResultsComposite);
		setSection(toolkit, summarySection);
	}

	private TreeViewerColumn createColumn(TreeViewer tree, String columnTitle) {
		TreeViewerColumn column = new TreeViewerColumn(tree, SWT.LEFT);
		column.getColumn().setText(columnTitle);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		return column;
	}

	private TreeViewer createResultsViewer(Composite reviewResultsComposite,
			FormToolkit toolkit) {
		Tree tree = new Tree(reviewResultsComposite, SWT.SINGLE
				| SWT.FULL_SELECTION);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		for (int i = 0; i < tree.getColumnCount(); i++) {
			tree.getColumn(i).pack();
		}
		tree.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);

		TreeViewer reviewResults = new TreeViewer(tree);
		createColumn(reviewResults, "Task");
		createColumn(reviewResults,
				Messages.ReviewSummaryTaskEditorPart_Header_Result);
		createColumn(reviewResults, "Description");
		createColumn(reviewResults, "Who");

		reviewResults.setContentProvider(new ReviewResultContentProvider());

		reviewResults.setLabelProvider(new TableLabelProvider() {
			private static final int COLUMN_TASK_ID = 0;
			private static final int COLUMN_RESULT = 1;
			private static final int COLUMN_DESCRIPTION = 2;
			private static final int COLUMN_WHO = 3;

			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == COLUMN_RESULT) {
					ITreeNode node = (ITreeNode) element;
					if (node.getResult() == null)
						return null;
					switch (node.getResult()) {
					case FAIL:
						return Images.REVIEW_RESULT_FAILED.createImage();
					case WARNING:
						return Images.REVIEW_RESULT_WARNING.createImage();
					case PASSED:
						return Images.REVIEW_RESULT_PASSED.createImage();
					case TODO:
						return Images.REVIEW_RESULT_NONE.createImage();

					}
				}
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {

				ITreeNode node = (ITreeNode) element;
				switch (columnIndex) {
				case COLUMN_TASK_ID:
					return node.getTaskId();
				case COLUMN_RESULT:
					return node.getResult() != null ? node.getResult().name()
							: "";
				case COLUMN_DESCRIPTION:
					return node.getDescription();
				case COLUMN_WHO:
					return node.getPerson();
				default:
					return null;
				}
			}

		});
		reviewResults.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					ITaskProperties task = ((ITreeNode) ((IStructuredSelection) event
							.getSelection()).getFirstElement()).getTask();
					ReviewUiUtils.openTaskInMylyn(task);
				}
			}
		});
		return reviewResults;
	}

	private final class ScopeViewerFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof TaskNode) {
				return ((TaskNode) element).hasReviewSubTasks();
			}
			return true;
		}
	}

	private static class ReviewResultContentProvider implements
			ITreeContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((ITreeNode) parentElement).getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((ITreeNode) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return !((ITreeNode) element).getChildren().isEmpty();
		}

		@Override
		public void dispose() {
			// nothing to do

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			} else if (inputElement instanceof Collection<?>) {
				return ((Collection<?>) inputElement).toArray();
			}
			return new Object[0];
		}
	}

	@Override
	protected void fillToolBar(ToolBarManager manager) {
		Action toggleFiltering = new Action("Filter", SWT.TOGGLE) { //$NON-NLS-1$
			@Override
			public void run() {
				enableFiltering(!isFiltering());
			}
		};
		toggleFiltering.setImageDescriptor(Images.FILTER);
		toggleFiltering.setToolTipText("Toogle Filtering");
		toggleFiltering.setChecked(true);
		enableFiltering(true);
		manager.add(toggleFiltering);
		super.fillToolBar(manager);
	}

	private boolean isFiltering() {
		return reviewResults.getFilters() != null
				&& reviewResults.getFilters().length > 0;
	}

	private void enableFiltering(boolean enable) {
		if (enable) {

			reviewResults.setFilters(new ViewerFilter[] { scopeViewerFilter });
		} else {
			reviewResults.setFilters(new ViewerFilter[0]);
		}
	}
}
