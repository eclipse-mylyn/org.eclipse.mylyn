/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.resources.preferences.MylarResourcesPreferenceInitializer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Mik Kersten
 */
public class MylarResourcesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Table ignoreTable;

	private Button addButton;

	private Button removeButton;

	public void init(IWorkbench workbench) {
		// ignore
	}
	
	@Override
	protected Control createContents(Composite parent) {
		createExcludesTable(parent);

		return parent;
	}

	private void createExcludesTable(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Resource Monitoring Exclusions");
		GridLayout layout = new GridLayout(1, false);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(layout);
		
		Composite composite = new Composite(group, SWT.NULL);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l1 = new Label(composite, SWT.NULL);
		l1.setText("Matching file or directory names will not be added automatically to the context");
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		l1.setLayoutData(data);

		ignoreTable = new Table(composite, SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		// gd.widthHint = convertWidthInCharsToPixels(30);
		data.heightHint = 60;
		ignoreTable.setLayoutData(data);
		ignoreTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				handleSelection();
			}
		});

		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("Add...");
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				addIgnore();
			}
		});

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				removeIgnore();
			}
		});
		fillTable(MylarResourcesPreferenceInitializer.getExcludedResourcePatterns());
		Dialog.applyDialogFont(group);
		setButtonLayoutData(addButton);
		setButtonLayoutData(removeButton);
	}

	/**
	 * Do anything necessary because the OK button has been pressed.
	 * 
	 * @return whether it is okay to close the preference page
	 */
	@Override
	public boolean performOk() {
		Set<String> patterns = new HashSet<String>();
		TableItem[] items = ignoreTable.getItems();
		for (int i = 0; i < items.length; i++) {
			patterns.add(items[i].getText());
		}
		MylarResourcesPreferenceInitializer.setExcludedResourcePatterns(patterns);
		return true;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		ignoreTable.removeAll();
		MylarResourcesPreferenceInitializer.restoreDefaultExcludedResourcePatterns();
		fillTable(MylarResourcesPreferenceInitializer.getExcludedResourcePatterns());
	}

	/**
	 * @param ignore
	 */
	private void fillTable(Set<String> ignored) {
		for (String pattern : ignored) {
			TableItem item = new TableItem(ignoreTable, SWT.NONE);
			item.setText(pattern);
		}
	}

	private void addIgnore() {
		InputDialog dialog = new InputDialog(getShell(), "Add Ignored Resource",
				"Enter pattern (* = any string)", null, null); // 
		dialog.open();
		if (dialog.getReturnCode() != Window.OK)
			return;
		String pattern = dialog.getValue();
		if (pattern.equals(""))return; //$NON-NLS-1$
		TableItem item = new TableItem(ignoreTable, SWT.NONE);
		item.setText(pattern);
		item.setChecked(true);
	}

	private void removeIgnore() {
		int[] selection = ignoreTable.getSelectionIndices();
		ignoreTable.remove(selection);
	}

	private void handleSelection() {
		if (ignoreTable.getSelectionCount() > 0) {
			removeButton.setEnabled(true);
		} else {
			removeButton.setEnabled(false);
		}
	}

}
