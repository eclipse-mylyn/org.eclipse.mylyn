/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TaskSearchPage;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskActivationHistory;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskDetailLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListFilteredTree;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.WorkingSetLabelComparator;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.IWorkingSetEditWizard;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Willian Mitsuda
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class TaskSelectionDialog extends FilteredItemsSelectionDialog {

	private Button openInBrowserCheck;

	private static final String TASK_SELECTION_DIALOG_SECTION = "TaskSelectionDialogSection";

	private static final String OPEN_IN_BROWSER_SETTING = "OpenInBrowser";

	private static final String SHOW_COMPLETED_TASKS_SETTING = "ShowCompletedTasks";

	private static final String IS_USING_WINDOW_WORKING_SET_SETTING = "IsUsingWindowWorkingSet";

	private static final String WORKING_SET_NAME_SETTING = "WorkingSetName";

	private boolean openInBrowser;

	public boolean getOpenInBrowser() {
		return openInBrowser;
	}

	public void setOpenInBrowser(boolean openInBrowser) {
		this.openInBrowser = openInBrowser;
	}

	private boolean insertInHistory;

	public void setInsertInHistory(boolean insertInHistory) {
		this.insertInHistory = insertInHistory;
	}

	public boolean getInsertInHistory() {
		return insertInHistory;
	}

	/**
	 * Integrates {@link FilteredItemsSelectionDialog} history management with Mylyn's task list activation history
	 * <p>
	 * Due to {@link SelectionHistory} use of memento-based history storage, many methods are overridden
	 */
	private class TaskSelectionHistory extends SelectionHistory {

		/**
		 * Mylyn's task activation history
		 */
		private final TaskActivationHistory history = TasksUiPlugin.getTaskListManager().getTaskActivationHistory();

		@Override
		public synchronized void accessed(Object object) {
			if (insertInHistory) {
				history.addTask((AbstractTask) object);
			}
		}

		@Override
		public synchronized boolean contains(Object object) {
			return history.containsTask((AbstractTask) object);
		}

		@Override
		public synchronized boolean remove(Object object) {
			return history.removeTask((AbstractTask) object);
		}

		@Override
		public synchronized boolean isEmpty() {
			return !history.hasPrevious();
		}

		@Override
		public void load(IMemento memento) {
			// do nothing because tasklist history handles this
		}

		@Override
		public void save(IMemento memento) {
			// do nothing because tasklist history handles this
		}

		@Override
		protected Object restoreItemFromMemento(IMemento memento) {
			// do nothing because tasklist history handles this
			return null;
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento) {
			// do nothing because tasklist history handles this
		}

		@Override
		public synchronized Object[] getHistoryItems() {
			return history.getPreviousTasks().toArray();
		}
	}

	/**
	 * Caches the window working set
	 */
	private final IWorkingSet windowWorkingSet;

	/**
	 * Set of filtered working sets
	 */
	private IWorkingSet selectedWorkingSet;

	/**
	 * Refilters if the current working set content has changed
	 */
	private final IPropertyChangeListener workingSetListener = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE)) {
				if (event.getNewValue().equals(selectedWorkingSet)) {
					applyFilter();
				}
			}
		}

	};

	private final TaskElementLabelProvider labelProvider;

	public TaskSelectionDialog(Shell parent) {
		super(parent);
		setSelectionHistory(new TaskSelectionHistory());

		labelProvider = new TaskElementLabelProvider(false);

		setListLabelProvider(labelProvider);

//		setListLabelProvider(new DecoratingLabelProvider(labelProvider, PlatformUI.getWorkbench()
//				.getDecoratorManager()
//				.getLabelDecorator()));
		setDetailsLabelProvider(new TaskDetailLabelProvider());
		setSeparatorLabel(TaskListView.LABEL_VIEW + " matches");

		// If there is a text selection, use it as the initial filter
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();
		if (selection instanceof ITextSelection) {
			// Get only get first line
			String text = ((ITextSelection) selection).getText();
			int n = text.indexOf('\n');
			if (n > -1) {
				text.substring(0, n);
			}
			setInitialPattern(text);
		}

		windowWorkingSet = window.getActivePage().getAggregateWorkingSet();
		selectedWorkingSet = windowWorkingSet;

		PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(workingSetListener);
	}

	private boolean showExtendedOpeningOptions;

	public void setShowExtendedOpeningOptions(boolean showExtendedOpeningOptions) {
		this.showExtendedOpeningOptions = showExtendedOpeningOptions;
	}

	public boolean getShowExtendedOpeningOptions() {
		return showExtendedOpeningOptions;
	}

	private final ShowCompletedTasksAction showCompletedTasksAction = new ShowCompletedTasksAction();

	@Override
	protected void fillViewMenu(IMenuManager menuManager) {
		super.fillViewMenu(menuManager);
		menuManager.add(showCompletedTasksAction);
		menuManager.add(new Separator());

		// Fill existing tasks working sets
		menuManager.add(new SelectWorkingSetAction());
		final DeselectWorkingSetAction deselectAction = new DeselectWorkingSetAction();
		menuManager.add(deselectAction);
		final EditWorkingSetAction editAction = new EditWorkingSetAction();
		menuManager.add(editAction);
		menuManager.add(new Separator("lruActions"));
		final FilterWorkingSetAction windowWorkingSetAction = new FilterWorkingSetAction(windowWorkingSet, 1);
		menuManager.add(windowWorkingSetAction);

		menuManager.addMenuListener(new IMenuListener() {

			private final List<ActionContributionItem> lruActions = new ArrayList<ActionContributionItem>();

			public void menuAboutToShow(IMenuManager manager) {
				deselectAction.setEnabled(selectedWorkingSet != null);
				editAction.setEnabled(selectedWorkingSet != null && selectedWorkingSet.isEditable());

				// Remove previous LRU actions
				for (ActionContributionItem action : lruActions) {
					manager.remove(action);
				}
				lruActions.clear();

				// Adds actual LRU actions
				IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getRecentWorkingSets();
				Arrays.sort(workingSets, new WorkingSetLabelComparator());
				int count = 2;
				for (IWorkingSet workingSet : workingSets) {
					if (workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
						IAction action = new FilterWorkingSetAction(workingSet, count++);
						if (workingSet.equals(selectedWorkingSet)) {
							action.setChecked(true);
						}
						ActionContributionItem ci = new ActionContributionItem(action);
						lruActions.add(ci);
						manager.appendToGroup("lruActions", ci);
					}
				}
				windowWorkingSetAction.setChecked(windowWorkingSet.equals(selectedWorkingSet));
			}

		});
	}

	/**
	 * All working set filter changes should be made through this method; ensures proper history handling and triggers
	 * refiltering
	 */
	private void setSelectedWorkingSet(IWorkingSet workingSet) {
		selectedWorkingSet = workingSet;
		if (workingSet != null) {
			PlatformUI.getWorkbench().getWorkingSetManager().addRecentWorkingSet(workingSet);
		}
		applyFilter();
	}

	private boolean showCompletedTasks;

	private class ShowCompletedTasksAction extends Action {

		public ShowCompletedTasksAction() {
			super("Show &Completed Tasks", IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			showCompletedTasks = isChecked();
			applyFilter();
		}

	}

	private class SelectWorkingSetAction extends Action {

		public SelectWorkingSetAction() {
			super("Select &Working Set...", IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			IWorkingSetSelectionDialog dlg = PlatformUI.getWorkbench()
					.getWorkingSetManager()
					.createWorkingSetSelectionDialog(getShell(), false,
							new String[] { TaskWorkingSetUpdater.ID_TASK_WORKING_SET });
			if (selectedWorkingSet != null) {
				dlg.setSelection(new IWorkingSet[] { selectedWorkingSet });
			}
			if (dlg.open() == Window.OK) {
				IWorkingSet[] selection = dlg.getSelection();
				if (selection.length == 0) {
					setSelectedWorkingSet(null);
				} else {
					setSelectedWorkingSet(selection[0]);
				}
			}
		}
	}

	private class DeselectWorkingSetAction extends Action {

		public DeselectWorkingSetAction() {
			super("&Deselect Working Set", IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			setSelectedWorkingSet(null);
		}
	}

	private class EditWorkingSetAction extends Action {

		public EditWorkingSetAction() {
			super("&Edit Active Working Set...", IAction.AS_PUSH_BUTTON);
		}

		@Override
		public void run() {
			IWorkingSetEditWizard wizard = PlatformUI.getWorkbench().getWorkingSetManager().createWorkingSetEditWizard(
					selectedWorkingSet);
			if (wizard != null) {
				WizardDialog dlg = new WizardDialog(getShell(), wizard);
				dlg.open();
			}
		}
	}

	private class FilterWorkingSetAction extends Action {

		private final IWorkingSet workingSet;

		public FilterWorkingSetAction(IWorkingSet workingSet, int shortcutKeyNumber) {
			super("", IAction.AS_RADIO_BUTTON);
			this.workingSet = workingSet;
			if (shortcutKeyNumber >= 1 && shortcutKeyNumber <= 9) {
				setText("&" + String.valueOf(shortcutKeyNumber) + " " + workingSet.getLabel());
			} else {
				setText(workingSet.getLabel());
			}
			setImageDescriptor(workingSet.getImageDescriptor());
		}

		@Override
		public void run() {
			setSelectedWorkingSet(workingSet);
		}
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		if (!showExtendedOpeningOptions) {
			return null;
		}

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 5).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().create());

		openInBrowserCheck = new Button(composite, SWT.CHECK);
		openInBrowserCheck.setText("Open with &Browser");
		openInBrowserCheck.setSelection(openInBrowser);

		ImageHyperlink openHyperlink = new ImageHyperlink(composite, SWT.NONE);
		openHyperlink.setText(TaskListFilteredTree.LABEL_SEARCH);
		openHyperlink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		openHyperlink.setUnderlined(true);
		openHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				getShell().close();
				new SearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), TaskSearchPage.ID).open();
			}

		});

		return composite;
	}

	@Override
	public boolean close() {
		PlatformUI.getWorkbench().getWorkingSetManager().removePropertyChangeListener(workingSetListener);
		if (openInBrowserCheck != null) {
			openInBrowser = openInBrowserCheck.getSelection();
		}
		return super.close();
	}

	private class TasksFilter extends ItemsFilter {

		private final boolean showCompletedTasks;

		/**
		 * Stores the task containers from selected working set; empty, which can come from no working set selection or
		 * working set with no task containers selected, means no filtering
		 */
		private final Set<AbstractTaskContainer> elements;

		private Set<AbstractTask> allTasksFromWorkingSets;

		public TasksFilter(boolean showCompletedTasks, IWorkingSet selectedWorkingSet) {
			super(new SearchPattern());
			// Little hack to force always a match inside any part of task text
			patternMatcher.setPattern("*" + patternMatcher.getPattern());
			this.showCompletedTasks = showCompletedTasks;

			elements = new HashSet<AbstractTaskContainer>();
			if (selectedWorkingSet != null) {
				for (IAdaptable adaptable : selectedWorkingSet.getElements()) {
					AbstractTaskContainer container = (AbstractTaskContainer) adaptable.getAdapter(AbstractTaskContainer.class);
					if (container != null) {
						elements.add(container);
					}
				}
			}
		}

		@Override
		public boolean isSubFilter(ItemsFilter filter) {
			if (!super.isSubFilter(filter)) {
				return false;
			}
			if (filter instanceof TasksFilter) {
				TasksFilter tasksFilter = (TasksFilter) filter;
				if (!showCompletedTasks && tasksFilter.showCompletedTasks) {
					return false;
				}
				if (elements.isEmpty()) {
					return true;
				}
				if (tasksFilter.elements.isEmpty()) {
					return false;
				}
				return elements.containsAll(tasksFilter.elements);
			}
			return true;
		}

		@Override
		public boolean equalsFilter(ItemsFilter filter) {
			if (!super.equalsFilter(filter)) {
				return false;
			}
			if (filter instanceof TasksFilter) {
				TasksFilter tasksFilter = (TasksFilter) filter;
				if (showCompletedTasks != tasksFilter.showCompletedTasks) {
					return false;
				}
				return elements.equals(tasksFilter.elements);
			}
			return true;
		}

		@Override
		public boolean isConsistentItem(Object item) {
			return item instanceof AbstractTask;
		}

		@Override
		public boolean matchItem(Object item) {
			if (!(item instanceof AbstractTask)) {
				return false;
			}
			if (!showCompletedTasks && ((AbstractTask) item).isCompleted()) {
				return false;
			}
			if (!elements.isEmpty()) {
				if (allTasksFromWorkingSets == null) {
					populateTasksFromWorkingSets();
				}
				if (!allTasksFromWorkingSets.contains(item)) {
					return false;
				}
			}
			return matches(labelProvider.getText(item));
		}

		private void populateTasksFromWorkingSets() {
			allTasksFromWorkingSets = new HashSet<AbstractTask>(1000);
			for (AbstractTaskContainer container : elements) {
				allTasksFromWorkingSets.addAll(container.getChildren());
			}
		}
	}

	@Override
	protected ItemsFilter createFilter() {
		return new TasksFilter(showCompletedTasks, selectedWorkingSet);
	}

	/**
	 * Caches all tasks; populated at first access
	 */
	private Set<AbstractTask> allTasks;

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {
		progressMonitor.beginTask("Search for tasks", 100);

		if (allTasks == null) {
			allTasks = new HashSet<AbstractTask>();
			TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
			allTasks.addAll(taskList.getAllTasks());
		}
		progressMonitor.worked(10);

		SubProgressMonitor subMonitor = new SubProgressMonitor(progressMonitor, 90);
		subMonitor.beginTask("Scanning tasks", allTasks.size());
		for (AbstractTask task : allTasks) {
			contentProvider.add(task, itemsFilter);
			subMonitor.worked(1);
		}
		subMonitor.done();

		progressMonitor.done();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(TASK_SELECTION_DIALOG_SECTION);
		if (section == null) {
			section = settings.addNewSection(TASK_SELECTION_DIALOG_SECTION);
			section.put(OPEN_IN_BROWSER_SETTING, false);
			section.put(SHOW_COMPLETED_TASKS_SETTING, true);
			section.put(IS_USING_WINDOW_WORKING_SET_SETTING, true);
			section.put(WORKING_SET_NAME_SETTING, "");
		}
		return section;
	}

	@Override
	protected void restoreDialog(IDialogSettings settings) {
		openInBrowser = settings.getBoolean(OPEN_IN_BROWSER_SETTING);
		showCompletedTasks = settings.getBoolean(SHOW_COMPLETED_TASKS_SETTING);
		showCompletedTasksAction.setChecked(showCompletedTasks);
		boolean isUsingWindowWorkingSet = settings.getBoolean(IS_USING_WINDOW_WORKING_SET_SETTING);
		if (isUsingWindowWorkingSet) {
			selectedWorkingSet = windowWorkingSet;
		} else {
			String workingSetName = settings.get(WORKING_SET_NAME_SETTING);
			if (workingSetName != null) {
				selectedWorkingSet = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(workingSetName);
			}
		}
		super.restoreDialog(settings);
	}

	@Override
	protected void storeDialog(IDialogSettings settings) {
		settings.put(OPEN_IN_BROWSER_SETTING, openInBrowser);
		settings.put(SHOW_COMPLETED_TASKS_SETTING, showCompletedTasks);
		settings.put(IS_USING_WINDOW_WORKING_SET_SETTING, selectedWorkingSet == windowWorkingSet);
		if (selectedWorkingSet == null) {
			settings.put(WORKING_SET_NAME_SETTING, "");
		} else {
			settings.put(WORKING_SET_NAME_SETTING, selectedWorkingSet.getName());
		}
		super.storeDialog(settings);
	}

	@Override
	public String getElementName(Object item) {
		return labelProvider.getText(item);
	}

	/**
	 * Sort tasks by summary
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Comparator getItemsComparator() {
		return new Comparator() {

			public int compare(Object o1, Object o2) {
				return labelProvider.getText(o1).compareTo(labelProvider.getText(o2));
			}

		};
	}

	@Override
	protected IStatus validateItem(Object item) {
		if (item instanceof AbstractTask) {
			return Status.OK_STATUS;
		}
		return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Selected item is not a task");
	}

}
