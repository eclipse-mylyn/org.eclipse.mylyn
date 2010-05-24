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
package org.eclipse.mylyn.reviews.ui.editors;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.ReviewSubTask;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class ReviewSummaryTaskEditorPart extends AbstractTaskEditorPart {
	public static final String ID_PART_REVIEWSUMMARY = "org.eclipse.mylyn.reviews.ui.editors.parts.reviewsummary"; //$NON-NLS-1$

	public ReviewSummaryTaskEditorPart() {
		setPartName(Messages.ReviewSummaryTaskEditorPart_Partname);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;

		Section summarySection = createSection(parent, toolkit,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| ExpandableComposite.EXPANDED);
		summarySection.setLayout(new FillLayout(SWT.HORIZONTAL));
		summarySection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		summarySection.setText(Messages.ReviewSummaryTaskEditorPart_Partname);
		Composite reviewResultsComposite = toolkit
				.createComposite(summarySection);
		reviewResultsComposite.setLayout(new GridLayout(1, false));
		TableViewer reviewResults = createResultsTableViewer(reviewResultsComposite);
		reviewResults.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		summarySection.setClient(reviewResultsComposite);

	}

	private TableViewerColumn createColumn(TableViewer parent,
			String columnTitle) {
		TableViewerColumn column = new TableViewerColumn(parent, SWT.LEFT);
		column.getColumn().setText(columnTitle);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		return column;
	}

	private TableViewer createResultsTableViewer(
			Composite reviewResultsComposite) {
		TableViewer reviewResults = new TableViewer(reviewResultsComposite);
		reviewResults.getTable().setHeaderVisible(true);
		createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_ReviewId);

		createColumn(reviewResults,
				Messages.ReviewSummaryTaskEditorPart_Header_Patch);
		createColumn(reviewResults,
				Messages.ReviewSummaryTaskEditorPart_Header_Author);
		createColumn(reviewResults,
				Messages.ReviewSummaryTaskEditorPart_Header_Reviewer);
		createColumn(reviewResults,
				Messages.ReviewSummaryTaskEditorPart_Header_Result);

		createColumn(reviewResults, Messages.ReviewSummaryTaskEditorPart_Header_Comment);

		reviewResults.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof ITaskContainer) {
					ITaskContainer taskContainer = (ITaskContainer) inputElement;
					List<ReviewSubTask> reviewSubTasks = ReviewsUtil
							.getReviewSubTasksFor(taskContainer, TasksUi
									.getTaskDataManager(), TasksUi
									.getRepositoryModel(),
									new NullProgressMonitor());

					return reviewSubTasks
							.toArray(new ReviewSubTask[reviewSubTasks.size()]);
				}
				return null;
			}
		});

		reviewResults.setLabelProvider(new TableLabelProvider() {
			private static final int COLUMN_ID = 0;
			private static final int COLUMN_PATCHFILE = 1;
			private static final int COLUMN_AUTHOR = 2;
			private static final int COLUMN_REVIEWER = 3;
			private static final int COLUMN_RESULT = 4;
			private static final int COLUMN_COMMENT = 5;

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == COLUMN_RESULT) {
					ReviewSubTask subtask = (ReviewSubTask) element;
					switch (subtask.getResult()) {
					case FAILED:
						return Images.REVIEW_RESULT_FAILED.createImage();
					case WARNING:
						return Images.REVIEW_RESULT_WARNING.createImage();
					case PASSED:
						return Images.REVIEW_RESULT_PASSED.createImage();
					case NONE:
						return Images.REVIEW_RESULT_NONE.createImage();

					}
				}
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {

				ReviewSubTask subtask = (ReviewSubTask) element;
				switch (columnIndex) {
				case COLUMN_ID:
					return subtask.getTask().getTaskId();
				case COLUMN_PATCHFILE:
					return subtask.getPatchDescription();
				case COLUMN_AUTHOR:
					return subtask.getAuthor();
				case COLUMN_REVIEWER:
					return subtask.getReviewer();
				case COLUMN_RESULT:
					return subtask.getResult().getName();
				case COLUMN_COMMENT:
					return subtask.getComment();
				default:
					return null;
				}
			}
		});
		reviewResults.setInput(getModel().getTask());
		reviewResults.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					ITask task = ((ReviewSubTask) ((IStructuredSelection) event
							.getSelection()).getFirstElement()).getTask();
					TasksUiUtil.openTask(task);
				}
			}
		});
		return reviewResults;
	}

}
