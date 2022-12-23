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
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.internal.ITreeNode;
import org.eclipse.mylyn.reviews.tasks.core.internal.ReviewResultNode;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskNode;
import org.eclipse.mylyn.reviews.tasks.ui.internal.Images;
import org.eclipse.mylyn.reviews.tasks.ui.internal.ReviewUiUtils;
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
	enum Column implements IColumnSpec<ITreeNode> {
		TASK("Task") {
			@Override
			public String getText(ITreeNode value) {
				return value.getTaskId();
			}
		},
		RESULT(Messages.ReviewSummaryTaskEditorPart_Header_Result) {
			@Override
			public String getText(ITreeNode value) {
				return value.getResult() != null ? value.getResult().name()
						: "";
			}

			@Override
			public Image getImage(ITreeNode value) {
				if (value.getResult() == null)
					return null;
				switch (value.getResult()) {
				case FAIL:
					return Images.REVIEW_RESULT_FAILED.createImage();
				case WARNING:
					return Images.REVIEW_RESULT_WARNING.createImage();
				case PASSED:
					return Images.REVIEW_RESULT_PASSED.createImage();
				case TODO:
					return Images.REVIEW_RESULT_NONE.createImage();
				}
				return null;
			}
		},
		DESCRIPTION("Description") {
			@Override
			public String getText(ITreeNode value) {
				return value.getDescription();
			}
		},
		WHO("Who") {
			@Override
			public String getText(ITreeNode value) {
				return value.getPerson();
			}
		};

		private String title;

		private Column(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public String getText(ITreeNode value) {
			return value != null ? value.toString() : "";
		}

		public Image getImage(ITreeNode value) {
			return null;
		}

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

		TreeViewer reviewResults = createTreeWithColumns(tree);

		reviewResults.setContentProvider(new ReviewResultContentProvider());

		reviewResults.setLabelProvider(new ColumnLabelProvider<ITreeNode>(
				Column.values()));
		reviewResults.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					ITreeNode treeNode = (ITreeNode) ((IStructuredSelection) event
							.getSelection()).getFirstElement();
					if (treeNode instanceof ReviewResultNode) {
						treeNode = treeNode.getParent();
					}
					ITaskProperties task = treeNode.getTask();
					ReviewUiUtils.openTaskInMylyn(task);
				}
			}
		});
		return reviewResults;
	}

	private TreeViewer createTreeWithColumns(Tree tree) {
		TreeViewer reviewResults = new TreeViewer(tree);
		return TreeHelper.createColumns(reviewResults, Column.values());
	}



	private static final class ScopeViewerFilter extends ViewerFilter {
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

		public Object[] getChildren(Object parentElement) {
			return ((ITreeNode) parentElement).getChildren().toArray();
		}

		public Object getParent(Object element) {
			return ((ITreeNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return !((ITreeNode) element).getChildren().isEmpty();
		}

		public void dispose() {
			// nothing to do

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}

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
