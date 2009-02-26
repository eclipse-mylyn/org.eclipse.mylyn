/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskComparator.SortByIndex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

	public TaskCompareDialog(IShellProvider parentShell, TaskComparator taskComparator) {
		super(parentShell.getShell());
		SortByIndex[] values = SortByIndex.values();
		propertyText = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			propertyText[i] = values[i].getLabel();
		}
		this.taskComparator = taskComparator;
		setTitle(Messages.TaskCompareDialog_Sorting);
	}

	protected void createDialogStartArea(Composite parent) {
		Label sortByLabel = new Label(parent, SWT.NULL);
		sortByLabel.setText(Messages.TaskCompareDialog_SortOrder);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		sortByLabel.setLayoutData(data);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		initializeDialogUnits(composite);

		Composite prioritiesArea = new Composite(composite, SWT.NULL);
		prioritiesArea.setLayout(new GridLayout(3, false));

		createDialogStartArea(prioritiesArea);

		ascendingButtons = new Button[2];
		descendingButtons = new Button[2];
		priorityCombos = new Combo[2];

		for (int i = 0; i < 2; i++) {
			final int index = i;
			Label numberLabel = new Label(prioritiesArea, SWT.NULL);
			numberLabel.setText("" + (i + 1) + "."); //$NON-NLS-1$ //$NON-NLS-2$
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
			if (i < priorityCombos.length - 1) {
				priorityCombos[i].addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int oldSelectionDirection = 1;
						if (descendingButtons[index].getSelection()) {
							oldSelectionDirection = -1;
						}
						ArrayList<String> oldSelectionList = new ArrayList<String>(
								Arrays.asList(priorityCombos[index].getItems()));
						oldSelectionList.removeAll(Arrays.asList(priorityCombos[index + 1].getItems()));
						if (oldSelectionList.size() != 1) {
							return;
						}
						String oldSelection = oldSelectionList.get(0);
						String newSelection = priorityCombos[index].getItem(priorityCombos[index].getSelectionIndex());
						if (oldSelection.equals(newSelection)) {
							return;
						}
						for (int j = index + 1; j < priorityCombos.length; j++) {
							int newSelectionIndex = priorityCombos[j].indexOf(newSelection);
							//this combo's current selection is equal to newSelection
							if (priorityCombos[j].getSelectionIndex() == newSelectionIndex) {
								priorityCombos[j].remove(newSelection);
								int insertionPoint = -1
										- Arrays.binarySearch(priorityCombos[j].getItems(), oldSelection,
												columnComparator);
								if (insertionPoint >= 0 && insertionPoint <= priorityCombos[j].getItemCount()) {
									priorityCombos[j].add(oldSelection, insertionPoint);
								} else {
									priorityCombos[j].add(oldSelection);
								}
								priorityCombos[j].select(priorityCombos[j].indexOf(oldSelection));
								ascendingButtons[index].setSelection(ascendingButtons[j].getSelection());
								descendingButtons[index].setSelection(descendingButtons[j].getSelection());
								ascendingButtons[j].setSelection(oldSelectionDirection == 1);
								descendingButtons[j].setSelection(oldSelectionDirection == -1);
							}
							//this combo contains newSelection
							else if (newSelectionIndex >= 0) {
								String currentText = priorityCombos[j].getText();
								priorityCombos[j].remove(newSelection);
								int insertionPoint = -1
										- Arrays.binarySearch(priorityCombos[j].getItems(), oldSelection,
												columnComparator);
								if (insertionPoint >= 0 && insertionPoint <= priorityCombos[j].getItemCount()) {
									priorityCombos[j].add(oldSelection, insertionPoint);
									priorityCombos[j].select(priorityCombos[j].indexOf(currentText));
								} else {
									priorityCombos[j].add(oldSelection);
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
		int a[] = new int[2];
		int b[] = new int[2];
		a[0] = taskComparator.getSortByIndex().ordinal();
		a[1] = taskComparator.getSortByIndex2().ordinal();
		b[0] = taskComparator.getSortDirection();
		b[1] = taskComparator.getSortDirection2();
		updateUI(a, b);
		Dialog.applyDialogFont(composite);
		return composite;
	}

	@Override
	protected void okPressed() {
		if (isDirty()) {
			taskComparator.setSortByIndex(SortByIndex.valueOfLabel(priorityCombos[0].getText()));
			taskComparator.setSortByIndex2(SortByIndex.valueOfLabel(priorityCombos[1].getText()));
			if (descendingButtons[0].getSelection()) {
				taskComparator.setSortDirection(-1);
			} else {
				taskComparator.setSortDirection(1);
			}
			if (descendingButtons[1].getSelection()) {
				taskComparator.setSortDirection2(-1);
			} else {
				taskComparator.setSortDirection2(1);
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

	protected void updateUI(int[] priorities, int[] directions) {
		ArrayList<String> availablePriorities = new ArrayList<String>(Arrays.asList(propertyText));

		for (int i = 0; i < priorityCombos.length; i++) {
			priorityCombos[i].removeAll();
			for (int j = 0; j < availablePriorities.size(); j++) {
				priorityCombos[i].add(availablePriorities.get(j));
			}
			priorityCombos[i].select(priorityCombos[i].indexOf(propertyText[priorities[i]]));
			availablePriorities.remove(propertyText[priorities[i]]);
			ascendingButtons[i].setSelection(directions[i] == 1);
			descendingButtons[i].setSelection(directions[i] == -1);
		}
	}

}
