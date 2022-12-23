/*******************************************************************************
 * Copyright (c) 2004, 2011 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion;
import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion.SortKey;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author FrankBecker
 */
public class TaskCompareDialog extends SelectionDialog {

	private Combo[] priorityCombos;

	private Button[] ascendingButtons;

	private Button[] descendingButtons;

	private final String[] propertyText;

	private boolean dirty = false;

	private final TaskComparator taskComparator;

	private String selectedValueLastCombo;

	public TaskCompareDialog(IShellProvider parentShell, TaskComparator taskComparator) {
		super(parentShell.getShell());
		SortCriterion.SortKey[] values = SortCriterion.SortKey.values();
		propertyText = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			propertyText[i] = values[i].getLabel();
		}
		this.taskComparator = taskComparator;
		setTitle(Messages.TaskCompareDialog_Sorting);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		initializeDialogUnits(composite);
		Control control = createContentArea(composite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(control);
		Dialog.applyDialogFont(parent);
		return composite;
	}

	protected Control createContentArea(Composite parent) {
		Group prioritiesArea = new Group(parent, SWT.NONE);
		prioritiesArea.setLayout(new GridLayout(3, false));
		prioritiesArea.setText(Messages.TaskCompareDialog_Tasks);

		Label label = new Label(prioritiesArea, SWT.WRAP);
		label.setText(Messages.TaskCompareDialog_Presentation_warning);
		label.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());

		ascendingButtons = new Button[SortCriterion.kindCount];
		descendingButtons = new Button[SortCriterion.kindCount];
		priorityCombos = new Combo[SortCriterion.kindCount];

		for (int i = 0; i < SortCriterion.kindCount; i++) {
			final int index = i;
			Label numberLabel = new Label(prioritiesArea, SWT.NONE);
			if (i == 0) {
				numberLabel.setText(Messages.TaskCompareDialog_Sort_by);
			} else {
				numberLabel.setText(Messages.TaskCompareDialog_Then_by);
			}
//			numberLabel.setText("" + (i + 1) + "."); //$NON-NLS-1$ //$NON-NLS-2$
			priorityCombos[i] = new Combo(prioritiesArea, SWT.READ_ONLY);
			priorityCombos[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Composite directionGroup = new Composite(prioritiesArea, SWT.NONE);
			directionGroup.setLayout(new GridLayout(2, false));
			ascendingButtons[i] = new Button(directionGroup, SWT.RADIO);
			ascendingButtons[i].setText(Messages.TaskCompareDialog_Ascending);
			ascendingButtons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					markDirty();
				}
			});
			descendingButtons[i] = new Button(directionGroup, SWT.RADIO);
			descendingButtons[i].setText(Messages.TaskCompareDialog_Descending);
			descendingButtons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					markDirty();
				}
			});
			if (i < priorityCombos.length) {
				priorityCombos[i].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
//						int oldSelectionDirection = 1;
//						if (descendingButtons[index].getSelection()) {
//							oldSelectionDirection = -1;
//						}
						ArrayList<String> oldSelectionList = new ArrayList<String>(
								Arrays.asList(priorityCombos[index].getItems()));
						String oldSelection;
						if (index + 1 == SortCriterion.kindCount) {
							oldSelection = selectedValueLastCombo;
						} else {
							oldSelectionList.removeAll(Arrays.asList(priorityCombos[index + 1].getItems()));
							oldSelection = propertyText[SortKey.NONE.ordinal()];
							if (oldSelectionList.size() == 1) {
								oldSelection = oldSelectionList.get(0);
							}
						}
						String newSelection = priorityCombos[index].getItem(priorityCombos[index].getSelectionIndex());
						if (oldSelection.equals(newSelection)) {
							return;
						}
						if (index + 1 == SortCriterion.kindCount) {
							selectedValueLastCombo = newSelection;
						}
						if (oldSelection.equals(propertyText[SortKey.NONE.ordinal()])) {
							ascendingButtons[index].setEnabled(true);
							descendingButtons[index].setEnabled(true);
							if (index + 1 < SortCriterion.kindCount) {
								priorityCombos[index + 1].setEnabled(true);
								ArrayList<String> availablePriorities = new ArrayList<String>(
										Arrays.asList(priorityCombos[index].getItems()));
								availablePriorities.remove(newSelection);
								for (int k = index + 1; k < SortCriterion.kindCount; k++) {
									priorityCombos[k].removeAll();
									for (int j = 0; j < availablePriorities.size(); j++) {
										priorityCombos[k].add(availablePriorities.get(j));
									}
									priorityCombos[k]
											.select(priorityCombos[k].indexOf(propertyText[SortKey.NONE.ordinal()]));
								}
							}
						} else if (newSelection.equals(propertyText[SortKey.NONE.ordinal()])) {
							ascendingButtons[index].setEnabled(false);
							descendingButtons[index].setEnabled(false);
							if (index + 1 < SortCriterion.kindCount) {
								ArrayList<String> availablePriorities = new ArrayList<String>(
										Arrays.asList(priorityCombos[index].getItems()));
								for (int k = index + 1; k < SortCriterion.kindCount; k++) {
									priorityCombos[k].setEnabled(true);
									priorityCombos[k].removeAll();
									for (int j = 0; j < availablePriorities.size(); j++) {
										priorityCombos[k].add(availablePriorities.get(j));
									}
									priorityCombos[k]
											.select(priorityCombos[k].indexOf(propertyText[SortKey.NONE.ordinal()]));
									priorityCombos[k].setEnabled(false);
									ascendingButtons[k].setEnabled(false);
									descendingButtons[k].setEnabled(false);
								}
							}
						} else {
							for (int j = index + 1; j < priorityCombos.length; j++) {
								int newSelectionIndex = priorityCombos[j].indexOf(newSelection);
								//this combo's current selection is equal to newSelection
								if (priorityCombos[j].getSelectionIndex() == newSelectionIndex) {
									priorityCombos[j].remove(newSelection);
									int insertionPoint = -1 - Arrays.binarySearch(priorityCombos[j].getItems(),
											oldSelection, columnComparator);
									if (insertionPoint >= 0 && insertionPoint <= priorityCombos[j].getItemCount()) {
										priorityCombos[j].add(oldSelection, insertionPoint);
									} else {
										priorityCombos[j].add(oldSelection);
									}
									priorityCombos[j].select(priorityCombos[j].indexOf(oldSelection));
// remove the comment if you want to move the current ascending/descending
//									ascendingButtons[index].setSelection(ascendingButtons[j].getSelection());
//									descendingButtons[index].setSelection(descendingButtons[j].getSelection());
//									ascendingButtons[j].setSelection(oldSelectionDirection == 1);
//									descendingButtons[j].setSelection(oldSelectionDirection == -1);
								}
								//this combo contains newSelection
								else if (newSelectionIndex >= 0) {
									String currentText = priorityCombos[j].getText();
									priorityCombos[j].remove(newSelection);
									int insertionPoint = -1 - Arrays.binarySearch(priorityCombos[j].getItems(),
											oldSelection, columnComparator);
									if (insertionPoint >= 0 && insertionPoint <= priorityCombos[j].getItemCount()) {
										priorityCombos[j].add(oldSelection, insertionPoint);
										priorityCombos[j].select(priorityCombos[j].indexOf(currentText));
									} else {
										priorityCombos[j].add(oldSelection);
									}
								}
							}
						}
						markDirty();
					}
				});
			} else {
				priorityCombos[i].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						markDirty();
					}
				});
			}

		}
		updateUI();
		return prioritiesArea;
	}

	@Override
	protected void okPressed() {
		if (isDirty()) {
			for (int i = 0; i < SortCriterion.kindCount; i++) {
				SortCriterion keyEntries = taskComparator.getSortCriterion(i);
				keyEntries.setKey(SortKey.valueOfLabel(priorityCombos[i].getText()));
				if (descendingButtons[i].getSelection()) {
					keyEntries.setDirection(-1);
				} else {
					keyEntries.setDirection(1);
				}
			}
		}
		super.okPressed();
	}

	protected boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty flag to true.
	 */
	protected void markDirty() {
		dirty = true;
	}

	private final Comparator<String> columnComparator = new Comparator<String>() {
		public int compare(String arg0, String arg1) {
			int index0 = -1;
			int index1 = -1;
			for (int i = 0; i < propertyText.length; i++) {
				if (propertyText[i].equals(arg0)) {
					index0 = i;
				}
				if (propertyText[i].equals(arg1)) {
					index1 = i;
				}
			}
			return index0 - index1;
		}
	};

	protected void updateUI() {
		ArrayList<String> availablePriorities = new ArrayList<String>(Arrays.asList(propertyText));
		for (int i = 0; i < TaskComparator.CRITERIA_COUNT; i++) {
			SortCriterion criterion = taskComparator.getSortCriterion(i);
			priorityCombos[i].removeAll();
			for (int j = 0; j < availablePriorities.size(); j++) {
				priorityCombos[i].add(availablePriorities.get(j));
			}
			priorityCombos[i].select(priorityCombos[i].indexOf(propertyText[criterion.getKey().ordinal()]));
			ascendingButtons[i].setSelection(criterion.getDirection() == 1);
			descendingButtons[i].setSelection(criterion.getDirection() == -1);
			if (i == TaskComparator.CRITERIA_COUNT - 1) {
				selectedValueLastCombo = propertyText[criterion.getKey().ordinal()];
			}
			if (criterion.getKey() != SortKey.NONE) {
				availablePriorities.remove(propertyText[criterion.getKey().ordinal()]);
			} else {
				ascendingButtons[i].setEnabled(false);
				descendingButtons[i].setEnabled(false);
				for (int k = i + 1; k < TaskComparator.CRITERIA_COUNT; k++) {
					for (int j = 0; j < availablePriorities.size(); j++) {
						priorityCombos[k].add(availablePriorities.get(j));
					}
					priorityCombos[k].select(priorityCombos[k].indexOf(propertyText[SortKey.NONE.ordinal()]));
					if (k == TaskComparator.CRITERIA_COUNT - 1) {
						selectedValueLastCombo = propertyText[SortKey.NONE.ordinal()];
					}
					ascendingButtons[k].setSelection(taskComparator.getSortCriterion(k).getDirection() == 1);
					descendingButtons[k].setSelection(taskComparator.getSortCriterion(k).getDirection() == -1);
					priorityCombos[k].setEnabled(false);
					ascendingButtons[k].setEnabled(false);
					descendingButtons[k].setEnabled(false);
				}
				break;
			}
		}
	}

}
