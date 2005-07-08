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
package org.eclipse.mylar.tasks.ui.views;

import java.io.UnsupportedEncodingException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.bugzilla.core.search.BugzillaSearchPage;
import org.eclipse.mylar.bugzilla.ui.query.GetQueryDialog;
import org.eclipse.mylar.bugzilla.ui.query.SaveQueryDialog;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Shawn Minto
 */
public class BugzillaQueryDialog extends Dialog {

	private String url;
	private String name;
	private BugzillaSearchOptionPage searchOptionPage;
	
	public BugzillaQueryDialog(Shell parentShell) {
		super(parentShell);
		searchOptionPage = new BugzillaSearchOptionPage();
		
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		searchOptionPage.createControl(parent);
		searchOptionPage.setVisible(true); // called to initialize the values
		return super.createContents(parent);
	}

	@Override
	protected void okPressed(){
		url = searchOptionPage.getSearchURL();
		if(url == null || url.equals("")){
			/*
			 * Should never get here. Every implementation of the Java platform is required
			 * to support the standard charset "UTF-8"
			 */
			return;
		}
		InputDialog getNameDialog = new InputDialog(Display.getCurrent().getActiveShell(), "Bugzilla Query Category Name", "Please enter a name for the bugzilla query category","", new IInputValidator(){

			public String isValid(String newText) {
				if(newText != null && !newText.equals("")){
					return null;
				} else {
					return "You must enter a name for the category";
				}
			}
				
		});
		getNameDialog.setBlockOnOpen(true);
		if(getNameDialog.open() == InputDialog.OK){
			name = getNameDialog.getValue();
			
			super.okPressed();
		} else {
			super.cancelPressed();
		}
	}
	
	private class BugzillaSearchOptionPage extends BugzillaSearchPage{
		
		public BugzillaSearchOptionPage(){
			scontainer = new ISearchPageContainer(){
				public ISelection getSelection() {
					// TODO Auto-generated method stub
					return null;
				}

				public IRunnableContext getRunnableContext() {
					return null;
				}

				public void setPerformActionEnabled(boolean state) {
					Button ok = BugzillaQueryDialog.this.getButton(Dialog.OK);
					if(ok != null)
						ok.setEnabled(state);
				}

				public int getSelectedScope() {
					return 0;
				}

				public void setSelectedScope(int scope) {}

				public boolean hasValidScope() {
					return true;
				}

				public IWorkingSet[] getSelectedWorkingSets() {
					return null;
				}

				public void setSelectedWorkingSets(IWorkingSet[] workingSets) {}
			};
		}
		
		public String getSearchURL() {
			try{
				if(rememberedQuery){
					return getQueryURL(new StringBuffer(input.getQueryParameters(selIndex)));
				} else {
					return getQueryURL(getQueryParameters());
				}
			} catch (UnsupportedEncodingException e){
				/*
				 * Do nothing. Every implementation of the Java platform is required
				 * to support the standard charset "UTF-8"
				 */
			}
			return "";
		}
		
		@Override
		protected Control createSaveQuery(Composite control) {
			GridLayout layout;
			GridData gd;

			Group group = new Group(control, SWT.NONE);
			layout = new GridLayout(3, false);
			group.setLayout(layout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gd = new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
			gd.horizontalSpan = 2;
			group.setLayoutData(gd);
			
			loadButton = new Button(group, SWT.PUSH | SWT.LEFT);
			loadButton.setText("Saved Queries...");
			loadButton.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					GetQueryDialog qd = new GetQueryDialog(getShell(),
							"Saved Queries", input){
						@Override
						protected void createButtonsForButtonBar(Composite parent) {
							super.createButtonsForButtonBar(parent);
							Button okButton = super.getButton(IDialogConstants.OK_ID);
							if(okButton != null)
								okButton.setText("Select");
						}
					};
					
					if (qd.open() == InputDialog.OK) {
						selIndex = qd.getSelected();
						if (selIndex != -1) {
							rememberedQuery = true;
						}
					} else {
						rememberedQuery = false;
					}
				}
			});
			loadButton.setEnabled(true);
			loadButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
			
			saveButton = new Button(group, SWT.PUSH | SWT.LEFT);
			saveButton.setText("Remember...");
			saveButton.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					SaveQueryDialog qd = new SaveQueryDialog(getShell(),
							"Remember Query");
					if (qd.open() == InputDialog.OK) {
						String qName = qd.getText();
						if (qName != null && qName.compareTo("") != 0) {
							try {
								input.add(getQueryParameters().toString(), qName, summaryPattern.getText());
							}
							catch (UnsupportedEncodingException e) {
								/*
								 * Do nothing. Every implementation of the Java platform is required
								 * to support the standard charset "UTF-8"
								 */
							}
						}
					}
				}
			});
			saveButton.setEnabled(true);
			saveButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
			
			return group;
		}
	}
}
