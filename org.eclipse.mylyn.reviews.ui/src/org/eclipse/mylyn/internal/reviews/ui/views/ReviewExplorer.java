/*******************************************************************************
 * Copyright (c) 2012 SpringSource, a divison of VMware, Inc.
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.views;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiConstants;
import org.eclipse.mylyn.internal.reviews.ui.providers.ReviewsLabelProvider;
import org.eclipse.mylyn.internal.reviews.ui.providers.TableStyledLabelProvider;
import org.eclipse.mylyn.internal.reviews.ui.providers.TableStyledLabelProvider.TableColumnProvider;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.ui.spi.editor.AbstractReviewTaskEditorPage;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorActivationService;
import org.eclipse.ui.navigator.INavigatorFilterService;

/**
 * @author Miles Parker
 */
public class ReviewExplorer extends CommonNavigator implements ISelectionListener {

	public static final String SHOW_VIEW_LIST = "showViewList";

	public static final String FILTER_FOR_COMMENTS = "filterForComments";

	private static final String TREE_ACTION_GROUP = "tree";

	private static final String FILTER_ACTION_GROUP = "filters";

	private static final String REFRESH_ACTION_GROUP = "refresh";

	private RefreshReviewsAction refreshAction;

	private boolean showList;

	private boolean filterForComments;

	private List<IReview> reviews = Collections.emptyList();

	private TaskEditor currentPart;

	private ReviewsLabelProvider treeLabelProvider;

	private ReviewsLabelProvider flatLabelProvider;

	private TableStyledLabelProvider currentProvider;

	class ShowListAction extends Action {
		public ShowListAction() {
			super("", AS_RADIO_BUTTON); //$NON-NLS-1$
			setText("Show List");
			setDescription("Show comments in a flat list");
			setToolTipText("Show all comments in a flat list (Hide files and patch sets)");
			setImageDescriptor(ReviewsImages.FLAT_LAYOUT);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			if (isChecked()) {
				if (getMemento() != null) {
					getMemento().putBoolean(SHOW_VIEW_LIST, true);
				}
				showList = true;
				updateActivations();
			}
		}
	}

	class ShowTreeAction extends Action {

		public ShowTreeAction() {
			super("", AS_RADIO_BUTTON); //$NON-NLS-1$
			setText("Show Tree");
			setDescription("Show all items in a tree");
			setToolTipText("Show artifacts, files and global comments in a tree");
			setImageDescriptor(ReviewsImages.HIERARCHICAL_LAYOUT);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			if (isChecked()) {
				if (getMemento() != null) {
					getMemento().putBoolean(SHOW_VIEW_LIST, false);
				}
				showList = false;
				updateActivations();
			}
		}
	}

	class RefreshReviewsAction extends Action {

		public RefreshReviewsAction() {
			super("", AS_PUSH_BUTTON); //$NON-NLS-1$
			setText("Refresh");
			setDescription("Refresh Review Items");
			setToolTipText("Refresh Review Items");
			setImageDescriptor(ReviewsImages.REFRESH);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			updatePerservingSelection();
		}
	}

	class FilterNonCommentsReviewsAction extends Action {

		public FilterNonCommentsReviewsAction() {
			super("", AS_CHECK_BOX); //$NON-NLS-1$
			setText("Filter for Comments");
			setDescription("Filter items for comments.");
			setToolTipText("Hide items that don't have comments");
			setImageDescriptor(ReviewsImages.REVIEW_QUOTE);
		}

		/*
		 * @see Action#actionPerformed
		 */
		@Override
		public void run() {
			filterForComments = isChecked();
			if (memento != null) {
				memento.putBoolean(FILTER_FOR_COMMENTS, filterForComments);
			}
			updateActivations();
		}
	}

	@Override
	protected CommonViewer createCommonViewer(Composite parent) {
		flatLabelProvider = new ReviewsLabelProvider.Flat();
		treeLabelProvider = new ReviewsLabelProvider.Tree();
		final CommonViewer viewer = super.createCommonViewer(parent);
		updateTreeViewer(viewer);
		return viewer;
	}

	void updateTreeViewer(CommonViewer viewer) {
		for (TreeColumn column : viewer.getTree().getColumns()) {
			column.dispose();
		}
		if (isShowColumns()) {
			//Artifact top-level
			Tree treeTable = viewer.getTree();

			treeTable.setHeaderVisible(true);
			treeTable.setLinesVisible(false);
			treeTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			currentProvider = null;
			if (isFlat()) {
				currentProvider = flatLabelProvider;
			} else {
				currentProvider = treeLabelProvider;
			}
			if (viewer.getLabelProvider() != currentProvider) {
				viewer.setLabelProvider(currentProvider);
			}
			updateTable(viewer);
		}
	}

	void updateTable(TreeViewer viewer) {
		TableLayout layout = new TableLayout();
		viewer.getTree().setLayout(layout);
		ColumnViewerToolTipSupport.enableFor(viewer);
		for (TableColumnProvider columnProvider : currentProvider.getColumnProviders()) {
			updateColumn(viewer, columnProvider);
		}
	}

	void updateColumn(TreeViewer viewer, final TableColumnProvider provider) {
		TreeColumn column = null;
		TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
		final DelegatingStyledCellLabelProvider styledLabelProvider = new DelegatingStyledCellLabelProvider(provider) {
			@Override
			public String getToolTipText(Object element) {
				//For some reason tooltips are not delegated..
				return provider.getToolTipText(element);
			};
		};
		viewerColumn.setLabelProvider(styledLabelProvider);
		column = viewerColumn.getColumn();
		column.setText(provider.getTitle());
		if (!provider.isFillAvailable()) {
			column.setWidth(provider.getMinimumSize());
		} else {
			int width = viewer.getTree().getClientArea().width;
			if (!viewer.getTree().getVerticalBar().isVisible()) {
				width -= viewer.getTree().getVerticalBar().getSize().x;
			}
			int allWidths = 0;
			for (TableColumnProvider provider2 : currentProvider.getColumnProviders()) {
				if (provider2 != provider) {
					allWidths += provider2.getMinimumSize();
				}
			}
			column.setWidth((width - allWidths));
		}
		TableLayout layout = (TableLayout) viewer.getTree().getLayout();
		layout.addColumnData(new ColumnWeightData(provider.getWeight(), provider.getMinimumSize()));
	}

	@Override
	protected CommonViewer createCommonViewerObject(Composite parent) {
		return new CommonViewer(getViewId(), parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	private Collection<Object> matchingElements(ITreeContentProvider provider, Object parent,
			Collection<Object> toMatch, boolean prune) {
		HashSet<String> matchingLabels = new HashSet<String>();
		for (Object object : toMatch) {
			String label = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(object);
			matchingLabels.add(label);
		}
		return matchingLabelElements(provider, parent, matchingLabels, prune);
	}

	private Collection<Object> matchingLabelElements(ITreeContentProvider provider, Object parent,
			Collection<String> toMatch, boolean prune) {
		HashSet<Object> matches = new HashSet<Object>();
		String parentLabel = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(parent);
		if (toMatch.contains(parentLabel)) {
			matches.add(parent);
		}
		Object[] children = provider.getElements(parent);
		for (Object object : children) {
			String childLabel = ((ILabelProvider) getCommonViewer().getLabelProvider()).getText(object);
			if (!prune || toMatch.contains(childLabel)) {
				matches.addAll(matchingLabelElements(provider, object, toMatch, prune));
			}
		}
		return matches;
	}

	public void refreshView() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updatePerservingSelection();
			}
		});
	}

	/**
	 * @see org.eclipse.ui.navigator.CommonNavigator#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite aParent) {
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager manager = actionBars.getToolBarManager();

		if (isSupportsListTree()) {
			showList = false;
			if (getMemento() != null) {
				Boolean value = getMemento().getBoolean(SHOW_VIEW_LIST);
				if (value != null) {
					showList = value;
				}
			}
			ShowTreeAction showTreeAction = new ShowTreeAction();
			showTreeAction.setChecked(!showList);
			ShowListAction showListAction = new ShowListAction();
			showListAction.setChecked(showList);
			manager.add(new Separator(TREE_ACTION_GROUP));
			manager.add(new Separator("presentation")); //$NON-NLS-1$
			manager.appendToGroup("presentation", showTreeAction); //$NON-NLS-1$
			manager.appendToGroup("presentation", showListAction); //$NON-NLS-1$
		}
		filterForComments = false;
		if (getMemento() != null) {
			Boolean value = getMemento().getBoolean(FILTER_FOR_COMMENTS);
			if (value != null) {
				filterForComments = value;
			}
		}
		manager.add(new Separator(FILTER_ACTION_GROUP));
		manager.appendToGroup(FILTER_ACTION_GROUP, new FilterNonCommentsReviewsAction());
		manager.add(new Separator("Separator"));

		super.createPartControl(aParent);

		manager.add(new Separator(REFRESH_ACTION_GROUP));
		refreshAction = new RefreshReviewsAction();
		refreshAction.setEnabled(false);
		manager.appendToGroup(REFRESH_ACTION_GROUP, refreshAction);

		updateActivations();

		getViewSite().getPage().addPartListener(new IPartListener() {

			public void partOpened(IWorkbenchPart part) {
			}

			public void partDeactivated(IWorkbenchPart part) {
			}

			public void partClosed(IWorkbenchPart part) {
			}

			public void partBroughtToTop(IWorkbenchPart part) {
			}

			public void partActivated(IWorkbenchPart part) {
				if (part == ReviewExplorer.this) {
					activated();
				}
			}
		});

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			handleActivePage(activePage);
		} else {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(new IPageListener() {
				public void pageOpened(IWorkbenchPage page) {
				}

				public void pageClosed(IWorkbenchPage page) {
				}

				public void pageActivated(IWorkbenchPage page) {
					handleActivePage(page);
				}
			});
		}
		update();
	}

	private void handleActivePage(IWorkbenchPage page) {
		if (page != null) {
			IEditorPart activeEditor = page.getActiveEditor();
			if (activeEditor instanceof TaskEditor) {
				currentPart = (TaskEditor) activeEditor;
			}
			page.addPartListener(new IPartListener() {

				public void partOpened(IWorkbenchPart part) {
					// ignore

				}

				public void partDeactivated(IWorkbenchPart part) {
					// ignore

				}

				public void partClosed(IWorkbenchPart part) {
					if (part == currentPart) {
						clear();
					}
				}

				public void partBroughtToTop(IWorkbenchPart part) {
					// ignore

				}

				public void partActivated(IWorkbenchPart part) {
					// ignore

				}
			});
			activated();
		}
	}

	protected void updateContentDescription() {
		String title = "(No Selection)";
		Object input = getCommonViewer().getInput();
		if (input != null && currentPart != null) {
			ITask task = currentPart.getTaskEditorInput().getTask();
			title = "Change " + task.getTaskId() + ": " + task.getSummary();
		}
		setContentDescription(title);
	}

	/* (non-Javadoc)
	 * Method declared on ISelectionListener.
	 * Notify the current page that the selection has changed.
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		List<IReview> lastReviews = reviews;
		if (part instanceof TaskEditor) {
			TaskEditor editor = (TaskEditor) part;
			IFormPage page = editor.getActivePageInstance();
			if (page instanceof AbstractReviewTaskEditorPage) {
				IReview review = ((AbstractReviewTaskEditorPage) page).getReview();
				if (review != null) {
					reviews = Collections.singletonList(review);
				} else {
					reviews = Collections.emptyList();
				}
				currentPart = (TaskEditor) part;

				if (!reviews.equals(lastReviews)) {
					update();
				}
			}
		}
	}

	private void update() {
		getCommonViewer().setInput(reviews);
		refreshAction.setEnabled(!reviews.isEmpty());
		updateContentDescription();
		getCommonViewer().refresh();
	}

	protected void updatePerservingSelection() {
		//Because we don't necessarily have the same backing EMF objects, we need to test on equality. We'd also like to restore selection even if the tree structure changes. In this case, using labels will be most consistent with user expectations..
		Object[] priorExpanded = getCommonViewer().getExpandedElements();
		Object[] priorSelection = new Object[] {};
		if (getCommonViewer().getSelection() instanceof IStructuredSelection) {
			priorSelection = ((IStructuredSelection) getCommonViewer().getSelection()).toArray();
		}
		getCommonViewer().getControl().setRedraw(false);
		update();
		Collection<Object> newExpanded = matchingElements(
				(ITreeContentProvider) getCommonViewer().getContentProvider(), reviews,
				new HashSet<Object>(Arrays.asList(priorExpanded)), true);
		Collection<Object> newSelection = matchingElements(
				(ITreeContentProvider) getCommonViewer().getContentProvider(), reviews,
				new HashSet<Object>(Arrays.asList(priorSelection)), false);
		getCommonViewer().setExpandedElements(newExpanded.toArray());
		getCommonViewer().setSelection(new StructuredSelection(newSelection.toArray()), true);
		getCommonViewer().getControl().setRedraw(true);
		getCommonViewer().getControl().redraw();
	}

	private void clear() {
		reviews = Collections.emptyList();
		currentPart = null;
		update();
	}

	/**
	 * @see org.eclipse.ui.navigator.CommonNavigator#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento aMemento) {
		super.saveState(aMemento);
		if (isSupportsListTree()) {
			aMemento.putBoolean(SHOW_VIEW_LIST, showList);
			aMemento.putBoolean(FILTER_ACTION_GROUP, filterForComments);
		}
	}

	/**
	 * @see org.eclipse.ui.navigator.CommonNavigator#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		site.getPage().addPostSelectionListener(this);
		super.init(site, memento);
	}

	/* (non-Javadoc)
	 * Method declared on IWorkbenchPart.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removePostSelectionListener(this);
		//Don't hang on to references
		flatLabelProvider.doDispose();
		treeLabelProvider.doDispose();
		currentPart = null;
	}

	protected void refreshAll() {
		refreshView();
	}

	public boolean isFlat() {
		return isSupportsListTree() && showList;
	}

	protected String getTreeContentId() {
		return ReviewsUiConstants.REVIEW_CONTENT_ID;
	}

	protected String getListContentId() {
		return ReviewsUiConstants.REVIEW_FLAT_CONTENT_ID;
	}

	protected String getViewId() {
		return ReviewsUiConstants.REVIEW_EXPLORER_ID;
	}

	protected final boolean isSupportsListTree() {
		return getListContentId() != null;
	}

	protected boolean isShowColumns() {
		return true;
	}

	public boolean isFilterForComments() {
		return filterForComments;
	}

	protected void updateActivations() {
		INavigatorActivationService activationService = getCommonViewer().getNavigatorContentService()
				.getActivationService();
		if (!isSupportsListTree()) {
			activationService.activateExtensions(new String[] { getTreeContentId() }, false);
		} else {
			if (isFlat()) {
				activationService.deactivateExtensions(new String[] { getTreeContentId() }, false);
				activationService.activateExtensions(new String[] { getListContentId() }, false);
			} else {
				activationService.deactivateExtensions(new String[] { getListContentId() }, false);
				activationService.activateExtensions(new String[] { getTreeContentId() }, false);
			}
		}
		INavigatorFilterService filterService = getCommonViewer().getNavigatorContentService().getFilterService();
		ICommonFilterDescriptor[] visibleDescriptors = filterService.getVisibleFilterDescriptors();
		boolean commentFilterActive = false;
		for (ICommonFilterDescriptor descriptor : visibleDescriptors) {
			if (descriptor.getId().equals(ReviewsUiConstants.REVIEW_FILTER_FOR_COMMENTS)
					&& filterService.isActive(descriptor.getId())) {
				commentFilterActive = true;
				break;
			}
		}
		boolean filtersModified = false;
		if (isFilterForComments() && !commentFilterActive) {
			filterService.setActiveFilterIds(new String[] { ReviewsUiConstants.REVIEW_FILTER_FOR_COMMENTS });
			filtersModified = true;
		} else if (!isFilterForComments() && commentFilterActive) {
			filterService.setActiveFilterIds(new String[] {});
			filtersModified = true;
		}
		if (filtersModified) {
			filterService.persistFilterActivationState();
			ViewerFilter[] visibleFilters = filterService.getVisibleFilters(true);
			getCommonViewer().setFilters(visibleFilters);
		}

		updateTreeViewer(getCommonViewer());
		getCommonViewer().refresh();
	}

	protected void activated() {
		if (currentPart instanceof IEditorPart) {
			selectionChanged(currentPart, StructuredSelection.EMPTY);
		}
	}

	public IWorkbenchPart getCurrentPart() {
		return currentPart;
	}
}