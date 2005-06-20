/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.java.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Shawn Minto
 *
 */
public class MylarPreferenceWizardPage extends WizardPage {

	Button removeInPlace;
	boolean inPlace = true;
	
	Button setMylarEditorDefault;
	boolean mylarEditorDefault = true;
	
	Button turnOnAutoFolding;
	boolean autoFolding = true;
	
	Button addMylarActiveWorkingSet;
	boolean workingSet = true;
	
	protected MylarPreferenceWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	public void createControl(Composite parent) {

		Composite buttonComposite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		buttonComposite.setLayout(layout);
		
		removeInPlace = new Button(buttonComposite, SWT.CHECK);
		GridData gd = new GridData();
		removeInPlace.setLayoutData(gd);
		removeInPlace.setSelection(true);
		removeInPlace.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				inPlace = removeInPlace.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about this event
			}
		});
		
		Label label = new Label(buttonComposite, SWT.NONE);
		label.setText("Remove the mylar filter from the in-place outline view");
		gd = new GridData();
		label.setLayoutData(gd);
		
		setMylarEditorDefault = new Button(buttonComposite, SWT.CHECK);
		gd = new GridData();
		setMylarEditorDefault.setLayoutData(gd);
		setMylarEditorDefault.setSelection(true);
		setMylarEditorDefault.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				mylarEditorDefault = setMylarEditorDefault.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about this event
			}
		});
		
		label = new Label(buttonComposite, SWT.NONE);
		label.setText("Set the Mylar editor to be the default java editor");
		gd = new GridData();
		label.setLayoutData(gd);
		
		turnOnAutoFolding = new Button(buttonComposite, SWT.CHECK);
		gd = new GridData();
		turnOnAutoFolding.setLayoutData(gd);
		turnOnAutoFolding.setSelection(true);
		turnOnAutoFolding.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				autoFolding = turnOnAutoFolding.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about this event
			}
		});
		
		label = new Label(buttonComposite, SWT.NONE);
		label.setText("Turn automatic folding on");
		gd = new GridData();
		label.setLayoutData(gd);

		addMylarActiveWorkingSet = new Button(buttonComposite, SWT.CHECK);
		gd = new GridData();
		addMylarActiveWorkingSet.setSelection(true);
		addMylarActiveWorkingSet.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				workingSet = addMylarActiveWorkingSet.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about this event
			}
		});
		
		label = new Label(buttonComposite, SWT.NONE);
		label.setText("Add the Mylar working set");
		gd = new GridData();
		label.setLayoutData(gd);
		setControl(buttonComposite);
	}

	public boolean isAutoFolding() {
		return autoFolding;
	}

	public boolean isInPlace() {
		return inPlace;
	}

	public boolean isMylarEditorDefault() {
		return mylarEditorDefault;
	}

	public boolean isWorkingSet() {
		return workingSet;
	}
}
