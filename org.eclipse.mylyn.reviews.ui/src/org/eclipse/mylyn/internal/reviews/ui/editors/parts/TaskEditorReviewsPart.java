/*******************************************************************************
 * Copyright (c) 2015, 2016, Landon Butterworth and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Blaine Lewis & Landon Butterworth - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.editors.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.mylyn.commons.ui.TableSorter;
import org.eclipse.mylyn.internal.reviews.ui.ReviewColumnLabelProvider;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListToolTip;
import org.eclipse.mylyn.reviews.internal.core.TaskReviewsMappingsStore;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Blaine Lewis
 * @author Landon Butterworth
 */
@SuppressWarnings("restriction")
public class TaskEditorReviewsPart extends AbstractTaskEditorPart {

	private static final class ReviewColumnSorter extends TableSorter {
		private final ReviewColumnLabelProvider labelProvider;

		public ReviewColumnSorter(ReviewColumnLabelProvider labelProvider) {
			this.labelProvider = labelProvider;
		}

		@Override
		public int compare(TableViewer viewer, Object e1, Object e2, int columnIndex) {
			int cat1 = category(e1);
			int cat2 = category(e2);

			if (cat1 != cat2) {
				return cat1 - cat2;
			}

			String name1 = labelProvider.getSortString(e1, columnIndex);
			String name2 = labelProvider.getSortString(e2, columnIndex);

			if (name1 == null) {
				name1 = "";//$NON-NLS-1$
			}
			if (name2 == null) {
				name2 = "";//$NON-NLS-1$
			}

			// use the comparator to compare the strings
			return getComparator().compare(name1, name2);
		}
	}

	private Composite reviewsComposite;

	protected Section section;

	private Table reviewsTable;

	private static final String[] REVIEWS_COLUMNS = { Messages.TaskEditorReviewsPart_DescriptionString,
			Messages.TaskEditorReviewsPart_Branch, Messages.TaskEditorReviewsPart_CodeReviewString,
			Messages.TaskEditorReviewsPart_VerifiedString, Messages.TaskEditorReviewsPart_Status };

	private static final int[] REVIEWS_COLUMNS_WIDTH = { 550, 90, 30, 30, 90, 0 };

	private TableViewer reviewsViewer;

	private List<TaskReview> reviewContainers;

	private Collection<ITask> reviews;

	private final TaskReviewsMappingsStore taskReviewStore;

	private final TaskList taskList = TasksUiPlugin.getTaskList();

	private TaskListToolTip toolTip;

	public TaskEditorReviewsPart() {
		taskReviewStore = ReviewsUiPlugin.getDefault().getTaskReviewsMappingStore();
		setPartName(Messages.TaskEditorReviewsPart_ReviewsString);
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {

		if (taskReviewStore != null) {
			Collection<String> reviewUrls = taskReviewStore.getReviewUrls(getTaskEditorPage().getTask().getUrl());
			getReviewsFromUrls(reviewUrls);
		}

		if (reviews == null || reviews.size() == 0) {
			//Don't build component because there are no reviews
			return;
		}

		reviewContainers = new ArrayList<TaskReview>();
		populateReviews(reviewContainers);

		section = createSection(parent, toolkit, true);
		section.setText(section.getText());

		createContents(toolkit, section);

		setSection(toolkit, section);
	}

	private void getReviewsFromUrls(Collection<String> reviewUrls) {
		reviews = new ArrayList<ITask>();

		for (String reviewUrl : reviewUrls) {

			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getConnectorForRepositoryTaskUrl(reviewUrl);

			if (connector == null) {
				continue;
			}

			String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(reviewUrl);

			String reviewId = connector.getTaskIdFromTaskUrl(reviewUrl);

			if (repositoryUrl == null || reviewId == null) {
				continue;
			}

			ITask review = taskList.getTask(repositoryUrl, reviewId);

			if (review != null) {
				reviews.add(review);
			}
		}
	}

	private void createReviewsTable(FormToolkit toolkit, final Composite composite) {
		reviewsTable = toolkit.createTable(reviewsComposite, SWT.MULTI | SWT.FULL_SELECTION);

		reviewsTable.setLinesVisible(true);
		reviewsTable.setHeaderVisible(true);
		reviewsTable.setLayout(new GridLayout());

		GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, false)
				.hint(500, SWT.DEFAULT)
				.applyTo(reviewsTable);

		reviewsTable.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		reviewsViewer = new TableViewer(reviewsTable);

		for (int i = 0; i < REVIEWS_COLUMNS.length; i++) {
			final TableColumn column = new TableColumn(reviewsTable, SWT.LEFT, i);
			column.setText(REVIEWS_COLUMNS[i]);
			column.setWidth(REVIEWS_COLUMNS_WIDTH[i]);
			column.setMoveable(true);
			column.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int direction = reviewsTable.getSortDirection();
					if (reviewsTable.getSortColumn() == column && direction != SWT.NONE) {
						direction = (direction == SWT.DOWN) ? SWT.UP : SWT.NONE;
					} else {
						direction = SWT.DOWN;
					}

					reviewsTable.setSortDirection(direction);
					if (direction == SWT.NONE) {
						reviewsTable.setSortColumn(null);
					} else {
						reviewsTable.setSortColumn(column);
					}
					reviewsViewer.refresh();
				}
			});
		}

		reviewsViewer.setUseHashlookup(true);
		reviewsViewer.setColumnProperties(REVIEWS_COLUMNS);
		ColumnViewerToolTipSupport.enableFor(reviewsViewer, ToolTip.NO_RECREATE);

		reviewsViewer.setContentProvider(new ArrayContentProvider());
		ReviewColumnLabelProvider labelProvider = new ReviewColumnLabelProvider();
		reviewsViewer.setLabelProvider(labelProvider);
		reviewsViewer.setComparator(new ReviewColumnSorter(labelProvider));

		reviewsViewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				openReview(event);
			}

		});

		reviewsViewer.setInput(reviewContainers.toArray());

		toolTip = new TaskListToolTip(reviewsTable);

		getTaskEditorPage().reflow();
	}

	private void populateReviews(List<TaskReview> reviewContainers) {
		for (ITask review : reviews) {
			reviewContainers.add(new TaskReview(review));
		}
	}

	private void createContents(final FormToolkit toolkit, final Section section) {

		reviewsComposite = toolkit.createComposite(section);
		section.setClient(reviewsComposite);
		reviewsComposite.setLayout(EditorUtil.createSectionClientLayout());

		getTaskEditorPage().registerDefaultDropListener(section);

		createReviewsTable(toolkit, reviewsComposite);

		toolkit.paintBordersFor(reviewsComposite);
	}

	public boolean isReviewsSectionExpanded() {
		return section != null && section.isExpanded();
	}

	protected void openReview(OpenEvent event) {
		List<TaskReview> reviewsToOpen = new ArrayList<TaskReview>();

		StructuredSelection selection = (StructuredSelection) event.getSelection();

		List<?> items = selection.toList();
		for (Object item : items) {
			if (item instanceof TaskReview) {
				reviewsToOpen.add((TaskReview) item);
			}
		}

		if (reviewsToOpen.isEmpty()) {
			return;
		}

		for (TaskReview openThis : reviewsToOpen) {
			TasksUiUtil.openTask(openThis.getUrl());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (toolTip != null) {
			toolTip.dispose();
		}
	}
}
