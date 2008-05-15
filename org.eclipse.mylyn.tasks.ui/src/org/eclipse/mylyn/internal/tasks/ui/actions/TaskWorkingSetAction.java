/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.AbstractWorkingSetDialogCOPY;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetEditWizard;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.WorkingSetComparator;
import org.eclipse.ui.internal.dialogs.WorkingSetFilter;
import org.eclipse.ui.internal.dialogs.WorkingSetLabelProvider;

/**
 * Derived from SelectWorkingSetsAction
 * 
 * @author Leo Dos Santos
 * @author Mik Kersten
 */
public class TaskWorkingSetAction extends Action implements IMenuCreator {

	public static final String LABEL_SETS_NONE = "All";

	public static String TASK_WORKING_SET_TEXT_LABEL = "Select and Edit Working Sets";

	private Menu dropDownMenu = null;

	public TaskWorkingSetAction() {
		super();
		setText("Sets");
		setToolTipText(TASK_WORKING_SET_TEXT_LABEL);
		setImageDescriptor(TasksUiImages.TASK_WORKING_SET);
		setEnabled(true);
		setMenuCreator(this);
	}

	public void dispose() {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	@SuppressWarnings("unchecked")
	private void addActionsToMenu() {
		IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();

		if (doTaskWorkingSetsExist()) {
			ActionContributionItem itemAll = new ActionContributionItem(new ToggleAllWorkingSetsAction(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()));
//			ActionContributionItem itemNone = new ActionContributionItem(new ToggleNoWorkingSetsAction());

			List<IWorkingSet> sortedWorkingSets = Arrays.asList(workingSets);
			Collections.sort(sortedWorkingSets, new WorkingSetComparator());

			Iterator<IWorkingSet> iter = sortedWorkingSets.iterator();
			while (iter.hasNext()) {
				IWorkingSet workingSet = iter.next();
				if (workingSet != null
						&& workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
					ActionContributionItem itemSet = new ActionContributionItem(new ToggleWorkingSetAction(workingSet));
					itemSet.fill(dropDownMenu, -1);
				}
			}

			Separator separator = new Separator();
			separator.fill(dropDownMenu, -1);
			itemAll.fill(dropDownMenu, -1);
		}

		ActionContributionItem editItem = new ActionContributionItem(new ManageWorkingSetsAction());
		editItem.fill(dropDownMenu, -1);
	}

	private boolean doTaskWorkingSetsExist() {
		IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
		for (IWorkingSet workingSet : workingSets) {
			if (workingSet != null && workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void run() {
		String[] ids = new String[1];
		ids[0] = TaskWorkingSetUpdater.ID_TASK_WORKING_SET;
		ConfigureWindowWorkingSetsDialog dialog = new ConfigureWindowWorkingSetsDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow(), ids);
		dialog.open();
	}

	public void run(IAction action) {
		this.run();
	}

	public void run(IWorkingSet editWorkingSet) {
		IWorkingSetManager manager = WorkbenchPlugin.getDefault().getWorkingSetManager();
		IWorkingSetEditWizard wizard = manager.createWorkingSetEditWizard(editWorkingSet);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);

//		dialog.create();
		dialog.open();
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
//				IWorkbenchHelpContextIds.WORKING_SET_EDIT_WIZARD);
//		if (dialog.open() == Window.OK) {
//			editWorkingSet = wizard.getSelection();
//			availableWorkingSetsChanged();
//			// make sure ok button is enabled when the selected working set
//			// is edited. Fixes bug 33386.
//			updateButtonAvailability();
//		}
//		editedWorkingSets.put(editWorkingSet, originalWorkingSet);
	}

	private class ManageWorkingSetsAction extends Action {
		ManageWorkingSetsAction() {
			super(WorkbenchMessages.Edit);
		}

		@Override
		public void run() {
			TaskWorkingSetAction.this.run(this);
		}
	}

	// TODO: remove?
	protected class ToggleEnableAllSetsAction extends Action {

		ToggleEnableAllSetsAction() {
			super("Deselect All", IAction.AS_CHECK_BOX);
//			setImageDescriptor(TasksUiImages.TASK_WORKING_SET);
//			setChecked(!areAllTaskWorkingSetsEnabled());
		}

		@Override
		public void runWithEvent(Event event) {
			Set<IWorkingSet> newList = new HashSet<IWorkingSet>(Arrays.asList(TaskWorkingSetUpdater.getEnabledSets()));

			Set<IWorkingSet> tempList = new HashSet<IWorkingSet>();
			Iterator<IWorkingSet> iter = newList.iterator();
			while (iter.hasNext()) {
				IWorkingSet workingSet = iter.next();
				if (workingSet != null
						&& workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
					tempList.add(workingSet);
				}
			}
			newList.removeAll(tempList);

			if (isChecked()) {
				IWorkingSet[] allWorkingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
				for (IWorkingSet workingSet : allWorkingSets) {
					if (workingSet != null
							&& workingSet.getId().equalsIgnoreCase(TaskWorkingSetUpdater.ID_TASK_WORKING_SET)) {
						newList.add(workingSet);
					}
				}
			}

			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setWorkingSets(
					newList.toArray(new IWorkingSet[newList.size()]));
		}

	}

	class ConfigureWindowWorkingSetsDialog extends AbstractWorkingSetDialogCOPY {

		private final static int SIZING_SELECTION_WIDGET_HEIGHT = 200;

		private final static int SIZING_SELECTION_WIDGET_WIDTH = 50;

		private final IWorkbenchWindow window;

		private CheckboxTableViewer viewer;

		private Set<String> taskWorkingSetIds;

		protected ConfigureWindowWorkingSetsDialog(IWorkbenchWindow window, String[] workingSetIds) {
			super(window.getShell(), workingSetIds, true);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			this.window = window;
			//setTitle(WorkbenchMessages.WorkingSetSelectionDialog_title_multiSelect);
			setTitle(TASK_WORKING_SET_TEXT_LABEL);
			setMessage(WorkbenchMessages.WorkingSetSelectionDialog_message_multiSelect);

			if (workingSetIds == null || workingSetIds.length == 0) {
				taskWorkingSetIds = null;
			} else {
				taskWorkingSetIds = new HashSet<String>();
				for (String id : workingSetIds) {
					taskWorkingSetIds.add(id);
				}
			}
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			initializeDialogUnits(parent);

			Composite composite = (Composite) super.createDialogArea(parent);

			Composite viewerComposite = new Composite(composite, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = layout.marginWidth = 0;
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			viewerComposite.setLayout(layout);

			GridData data = new GridData(GridData.FILL_BOTH);
			data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
			data.widthHint = SIZING_SELECTION_WIDGET_WIDTH + 300; // fudge?  I like fudge.
			viewerComposite.setLayoutData(data);

			viewer = CheckboxTableViewer.newCheckList(viewerComposite, SWT.BORDER);
			viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
			viewer.setLabelProvider(new WorkingSetLabelProvider());
			viewer.setContentProvider(new ArrayContentProvider());
			viewer.addFilter(new WorkingSetFilter(taskWorkingSetIds));
			viewer.setInput(window.getWorkbench().getWorkingSetManager().getWorkingSets());

			viewer.setCheckedElements(window.getActivePage().getWorkingSets());

			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					handleSelectionChanged();
				}
			});

			data = new GridData(GridData.FILL_BOTH);
			data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
			data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;

			viewer.getControl().setLayoutData(data);
			addModifyButtons(viewerComposite);

			addSelectionButtons(composite);

			availableWorkingSetsChanged();

			Dialog.applyDialogFont(composite);

			return composite;
		}

		@Override
		protected void okPressed() {
			Set<IWorkingSet> newList = new HashSet<IWorkingSet>(Arrays.asList(TaskWorkingSetUpdater.getEnabledSets()));
			Set<IWorkingSet> tempList = new HashSet<IWorkingSet>();
			for (IWorkingSet workingSet : newList) {
				for (String id : taskWorkingSetIds) {
					if (workingSet.getId().equalsIgnoreCase(id)) {
						tempList.add(workingSet);
					}
				}
			}
			newList.removeAll(tempList);

			Object[] selection = viewer.getCheckedElements();
			IWorkingSet[] setsToEnable = new IWorkingSet[selection.length];
			System.arraycopy(selection, 0, setsToEnable, 0, selection.length);
			newList.addAll(new HashSet<IWorkingSet>(Arrays.asList(setsToEnable)));

			window.getActivePage().setWorkingSets(newList.toArray(new IWorkingSet[newList.size()]));
			super.okPressed();
		}

		@Override
		protected List<?> getSelectedWorkingSets() {
			ISelection selection = viewer.getSelection();
			if (selection instanceof IStructuredSelection) {
				return ((IStructuredSelection) selection).toList();
			}
			return null;
		}

		@Override
		protected void availableWorkingSetsChanged() {
			viewer.setInput(window.getWorkbench().getWorkingSetManager().getWorkingSets());
			super.availableWorkingSetsChanged();
		}

		/**
		 * Called when the selection has changed.
		 */
		void handleSelectionChanged() {
			updateButtonAvailability();
		}

		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
		}

		@Override
		protected void selectAllSets() {
			viewer.setCheckedElements(window.getWorkbench().getWorkingSetManager().getWorkingSets());
			updateButtonAvailability();
		}

		@Override
		protected void deselectAllSets() {
			viewer.setCheckedElements(new Object[0]);
			updateButtonAvailability();
		}
	}
}
