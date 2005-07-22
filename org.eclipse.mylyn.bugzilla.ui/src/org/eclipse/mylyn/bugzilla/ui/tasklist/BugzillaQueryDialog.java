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
package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.bugzilla.ui.query.GetQueryDialog;
import org.eclipse.mylar.bugzilla.ui.query.SaveQueryDialog;
import org.eclipse.mylar.bugzilla.ui.search.BugzillaSearchPage;
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
	private String name = "";
	private BugzillaSearchOptionPage searchOptionPage;
	private String startingUrl = null;
	
	public BugzillaQueryDialog(Shell parentShell) {
		super(parentShell);
		searchOptionPage = new BugzillaSearchOptionPage();
		
	}
	
	public BugzillaQueryDialog(Shell parentShell, String startingUrl, String name) {
		super(parentShell);
		searchOptionPage = new BugzillaSearchOptionPage();
		this.startingUrl = startingUrl;
		this.name = name;
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
		
		searchOptionPage.setVisible(true);
		Control c = super.createContents(parent);
		if(startingUrl != null){
			try{
				searchOptionPage.updateDefaults(startingUrl);
			} catch (UnsupportedEncodingException e){
				// ignore, should never get this
			}
		}
		return c;
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
		InputDialog getNameDialog = new InputDialog(Display.getCurrent().getActiveShell(), "Bugzilla Query Category Name", "Please enter a name for the bugzilla query category",name, new IInputValidator(){

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
		
		public BugzillaSearchOptionPage() {
			
			preselectedStatusValues = new String[0];
			
			scontainer = new ISearchPageContainer() {
				public ISelection getSelection() {
					// TODO Auto-generated method stub
					return null;
				}

				public IRunnableContext getRunnableContext() {
					return null;
				}

				public void setPerformActionEnabled(boolean state) {
					Button ok = BugzillaQueryDialog.this.getButton(Dialog.OK);
					if (ok != null)
						ok.setEnabled(state);
				}

				public int getSelectedScope() {
					return 0;
				}

				public void setSelectedScope(int scope) {
				}

				public boolean hasValidScope() {
					return true;
				}

				public IWorkingSet[] getSelectedWorkingSets() {
					return null;
				}

				public void setSelectedWorkingSets(IWorkingSet[] workingSets) {
				}
			};
		}
		
		public void updateDefaults(String startingUrl) throws UnsupportedEncodingException{
//			String serverName = startingUrl.substring(0, startingUrl.indexOf("?"));
			startingUrl = startingUrl.substring(startingUrl.indexOf("?") + 1);
			String[] options = startingUrl.split("&");
			for(String option: options){
				String key = option.substring(0, option.indexOf("="));
				String value = URLDecoder.decode(option.substring(option.indexOf("=") + 1), "UTF-8");
				if (key == null)
					continue;
				
				if (key.equals("short_desc")) {
					summaryPattern.setText(value);
				} else if(key.equals("short_desc_type")){
					if(value.equals("allwordssubstr"))
						value = "all words";
					else if(value.equals("anywordssubstr"))
						value = "any word";
					int index = 0;
					for(String item: summaryOperation.getItems()){
						if(item.compareTo(value) == 0)
							break;
						index++;
					}
					if(index < summaryOperation.getItemCount()){
						summaryOperation.select(index);
					}
				} else if(key.equals("product")){
					String [] sel = product.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					product.setSelection(selList.toArray(sel));
				} else if(key.equals("component")){
					String [] sel = component.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					component.setSelection(selList.toArray(sel));
				} else if(key.equals("version")){
					String [] sel = version.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					version.setSelection(selList.toArray(sel));
				} else if(key.equals("target_milestone")){ //XXX
					String [] sel = target.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					target.setSelection(selList.toArray(sel));
				} else if(key.equals("version")){
					String [] sel = version.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					version.setSelection(selList.toArray(sel));
				} else if(key.equals("long_desc_type")){
					if(value.equals("allwordssubstr"))
						value = "all words";
					else if(value.equals("anywordssubstr"))
						value = "any word";
					int index = 0;
					for(String item: commentOperation.getItems()){
						if(item.compareTo(value) == 0)
							break;
						index++;
					}
					if(index < commentOperation.getItemCount()){
						commentOperation.select(index);
					}
				} else if(key.equals("long_desc")){
					commentPattern.setText(value);
				} else if(key.equals("bug_status")){
					String [] sel = status.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					status.setSelection(selList.toArray(sel));
				} else if(key.equals("resolution")){
					String [] sel = resolution.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					resolution.setSelection(selList.toArray(sel));
				} else if(key.equals("bug_severity")){
					String [] sel = severity.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					severity.setSelection(selList.toArray(sel));
				} else if(key.equals("priority")){
					String [] sel = priority.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					priority.setSelection(selList.toArray(sel));
				} else if(key.equals("ref_platform")){
					String [] sel = hardware.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					hardware.setSelection(selList.toArray(sel));
				} else if(key.equals("op_sys")){
					String [] sel = os.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					os.setSelection(selList.toArray(sel));
				} else if(key.equals("emailassigned_to1")){ // HACK: email buttons assumed to be in same position
					if(value.equals("1"))
						emailButton[0].setSelection(true);
					else
						emailButton[0].setSelection(false);
				} else if(key.equals("emailreporter1")){ // HACK: email buttons assumed to be in same position
					if(value.equals("1"))
						emailButton[1].setSelection(true);
					else
						emailButton[1].setSelection(false);
				} else if(key.equals("emailcc1") ){ // HACK: email buttons assumed to be in same position
					if(value.equals("1"))
						emailButton[2].setSelection(true);
					else
						emailButton[2].setSelection(false);
				} else if(key.equals("emaillongdesc1")){ // HACK: email buttons assumed to be in same position
					if(value.equals("1"))
						emailButton[3].setSelection(true);
					else
						emailButton[3].setSelection(false);
				} else if(key.equals("emailtype1")){
					int index = 0;
					for(String item: emailOperation.getItems()){
						if(item.compareTo(value) == 0)
							break;
						index++;
					}
					if(index < emailOperation.getItemCount()){
						emailOperation.select(index);
					}
				} else if(key.equals("email1")){
					emailPattern.setText(value);
				} else if(key.equals("changedin")){
					daysText.setText(value);
				}
			}
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
