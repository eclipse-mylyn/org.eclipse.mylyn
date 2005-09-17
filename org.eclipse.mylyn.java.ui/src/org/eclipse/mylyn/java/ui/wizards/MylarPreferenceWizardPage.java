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
import org.eclipse.swt.browser.Browser;
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

	private static final  String AUTO_FOLDING = "Turn interest-based automatic Java editor folding on";
	private static final  String AUTO_CLOSE = "Close all editors automatically on task deactivation";
	private static final  String WORKING_SET = "Add the \"active task context\" working set";
	private static final  String DEFAULT_EDITOR = "Set the Mylar editor to be the default for .java " +
		"(enables interest-based content assist)";
	
	Button setMylarEditorDefault;
	boolean mylarEditorDefault = true;
	
	Button turnOnAutoFolding;
	boolean autoFolding = true;
	
	Button addMylarActiveWorkingSet;
	boolean workingSet = true;
	
	Button closeEditorsOnDeactivation;
	boolean closeEditors = true;
	
	private String htmlDocs;
	
	protected MylarPreferenceWizardPage(String pageName, String htmlDocs) {
		super(pageName);
		this.htmlDocs = htmlDocs;
		setTitle(pageName);
		setDescription(
			"Configures Mylar preferences to the recommended defaults. To alter these \n" +
			"go to the Mylar preference page or re-invoke this wizard via the \"New\" menu.");
	}

	public void createControl(Composite parent) {

		Composite containerComposite = new Composite(parent, SWT.NULL);
		containerComposite.setLayout(new GridLayout());
		
		Composite buttonComposite = new Composite(containerComposite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		buttonComposite.setLayout(layout);
		
		setMylarEditorDefault = new Button(buttonComposite, SWT.CHECK);
		GridData gd = new GridData();
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
		
		Label label = new Label(buttonComposite, SWT.NONE);
		label.setText(DEFAULT_EDITOR);
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
		label.setText(AUTO_FOLDING);
		gd = new GridData();
		label.setLayoutData(gd);

		closeEditorsOnDeactivation = new Button(buttonComposite, SWT.CHECK);
		gd = new GridData();
		closeEditorsOnDeactivation.setLayoutData(gd);
		closeEditorsOnDeactivation.setSelection(true);
		closeEditorsOnDeactivation.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				closeEditors = closeEditorsOnDeactivation.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about this event
			}
		});
		
		label = new Label(buttonComposite, SWT.NONE);
		label.setText(AUTO_CLOSE);
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
		label.setText(WORKING_SET);
		gd = new GridData();
		label.setLayoutData(gd);
		setControl(buttonComposite);
		
		Label spacer = new Label(buttonComposite, SWT.NONE);
		spacer.setText(" ");
		spacer = new Label(buttonComposite, SWT.NONE);
		spacer = new Label(buttonComposite, SWT.NONE);
		spacer.setText(" ");
		
		Composite browserComposite = new Composite(containerComposite, SWT.NULL);
		browserComposite.setLayout(new GridLayout());
		
		Browser browser = new Browser(browserComposite, SWT.NONE);
		browser.setText(htmlDocs);
        GridData browserLayout = new GridData(GridData.FILL_HORIZONTAL);
        browserLayout.heightHint = 100;
        browserLayout.widthHint = 600;
        browser.setLayoutData(browserLayout);
		
		setControl(containerComposite);
	}

	public boolean isAutoFolding() {
		return autoFolding;
	}
	
	public boolean closeEditors(){
		return closeEditors;
	}

//	public boolean isInPlace() {
//		return inPlace;
//	}

	public boolean isMylarEditorDefault() {
		return mylarEditorDefault;
	}

	public boolean isWorkingSet() {
		return workingSet;
	}
}
