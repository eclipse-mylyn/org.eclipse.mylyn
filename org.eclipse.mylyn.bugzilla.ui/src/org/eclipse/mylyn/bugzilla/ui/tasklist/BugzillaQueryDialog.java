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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.tasklist.TaskRepository;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;

/**
 * @author Shawn Minto
 */
public class BugzillaQueryDialog extends Dialog {

	private String url;

	private String name = "";

	private BugzillaSearchOptionPage searchOptionPage;

	private String startingUrl = null;

	private String maxHits;

	private Text queryText;

	private Button customButton;

	private boolean isCustom = false;

	private boolean isNew = false;

	private boolean enabled = true;

	private String title;

	public BugzillaQueryDialog(Shell parentShell) {
		super(parentShell);
		isNew = true;
		isCustom = false;
		searchOptionPage = new BugzillaSearchOptionPage();
		title = "New Bugzilla Query";
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	public BugzillaQueryDialog(Shell parentShell, String repositoryUrl, String startingUrl, String name, String maxHits) {
		super(parentShell);
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(BugzillaPlugin.REPOSITORY_KIND, repositoryUrl);
		searchOptionPage = new BugzillaSearchOptionPage();
		searchOptionPage.setRepository(repository);
		this.startingUrl = startingUrl;
		this.maxHits = maxHits;
		this.name = name;
		isNew = false;
		title = "Edit Bugzilla Query";
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getMaxHits() {
		return maxHits;
	}

	public boolean isCustom() {
		return isCustom;
	}

	@Override
	protected Control createContents(Composite parent) {
		searchOptionPage.createControl(parent);
		searchOptionPage.setVisible(true);

		if (isNew) {
			
			Group custom = new Group(parent, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			custom.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			custom.setLayoutData(gd);
			
//			Group custom = new Group(parent, SWT.NONE);
//			GridLayout gl = new GridLayout(2, false);
//			custom.setLayout(gl);

			customButton = new Button(custom, SWT.CHECK);
			customButton.setText("Custom Query");
//			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//			customButton.setLayoutData(gd);

//			Label l = new Label(custom, SWT.NONE);
//
//			l = new Label(custom, SWT.NONE);
//			l.setText("Query URL");

			queryText = new Text(custom, SWT.BORDER | SWT.SINGLE);
			if (startingUrl != null)
				queryText.setText(startingUrl);
		}

		if (isNew) {
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = 450;
			queryText.setLayoutData(gd);

			customButton.setSelection(!enabled);
			queryText.setEnabled(!enabled);
			searchOptionPage.setControlsEnabled(enabled);

			customButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					enabled = !enabled;
					searchOptionPage.setControlsEnabled(enabled);
					queryText.setEnabled(!enabled);
					isCustom = customButton.getSelection();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		
		Control control = super.createContents(parent);
		if (startingUrl != null) {
			try {
				searchOptionPage.updateDefaults(startingUrl, maxHits);
			} catch (UnsupportedEncodingException e) {
				// ignore, should never get this
			}
		}

		return control;
	}

	@Override
	protected void okPressed() {
		TaskRepository repository = searchOptionPage.getRepository();
		if (repository == null) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
			return;
		}
		if (repository == null) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), IBugzillaConstants.TITLE_MESSAGE_DIALOG,
					TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
			return;
		}

		if (customButton != null && customButton.getSelection()) {
			url = queryText.getText();
		} else {
			url = searchOptionPage.getSearchURL(repository);
		}
		if (url == null || url.equals("")) {
			// Should never get here. Every implementation of the Java platform
			// is required to support the standard charset "UTF-8"
			return;
		}
		maxHits = searchOptionPage.getMaxHits();
		InputDialog getNameDialog = new InputDialog(Display.getCurrent().getActiveShell(),
				"Bugzilla Query Category Name", "Please enter a name for the bugzilla query category", name,
				new IInputValidator() {

					public String isValid(String newText) {
						if (newText != null && !newText.equals("")) {
							return null;
						} else {
							return "You must enter a name for the category";
						}
					}

				});
		getNameDialog.setBlockOnOpen(true);
		if (getNameDialog.open() == InputDialog.OK) {
			name = getNameDialog.getValue();
			super.okPressed();
		} else {
			super.cancelPressed();
		}
	}

	private class BugzillaSearchOptionPage extends BugzillaSearchPage {

		public BugzillaSearchOptionPage() {

			// preselectedStatusValues = new String[0];

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

		/** 
		 * TODO: get rid of this?
		 */
		public void updateDefaults(String startingUrl, String maxHits) throws UnsupportedEncodingException {
			// String serverName = startingUrl.substring(0,
			// startingUrl.indexOf("?"));
			startingUrl = startingUrl.substring(startingUrl.indexOf("?") + 1);
			String[] options = startingUrl.split("&");
			for (String option : options) {
				String key = option.substring(0, option.indexOf("="));
				String value = URLDecoder.decode(option.substring(option.indexOf("=") + 1), "UTF-8");
				if (key == null)
					continue;

				if (key.equals("short_desc")) {
					summaryPattern.setText(value);
				} else if (key.equals("short_desc_type")) {
					if (value.equals("allwordssubstr"))
						value = "all words";
					else if (value.equals("anywordssubstr"))
						value = "any word";
					int index = 0;
					for (String item : summaryOperation.getItems()) {
						if (item.compareTo(value) == 0)
							break;
						index++;
					}
					if (index < summaryOperation.getItemCount()) {
						summaryOperation.select(index);
					}
				} else if (key.equals("product")) {
					String[] sel = product.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					product.setSelection(selList.toArray(sel));
				} else if (key.equals("component")) {
					String[] sel = component.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					component.setSelection(selList.toArray(sel));
				} else if (key.equals("version")) {
					String[] sel = version.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					version.setSelection(selList.toArray(sel));
				} else if (key.equals("target_milestone")) { // XXX
					String[] sel = target.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					target.setSelection(selList.toArray(sel));
				} else if (key.equals("version")) {
					String[] sel = version.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					version.setSelection(selList.toArray(sel));
				} else if (key.equals("long_desc_type")) {
					if (value.equals("allwordssubstr"))
						value = "all words";
					else if (value.equals("anywordssubstr"))
						value = "any word";
					int index = 0;
					for (String item : commentOperation.getItems()) {
						if (item.compareTo(value) == 0)
							break;
						index++;
					}
					if (index < commentOperation.getItemCount()) {
						commentOperation.select(index);
					}
				} else if (key.equals("long_desc")) {
					commentPattern.setText(value);
				} else if (key.equals("bug_status")) {
					String[] sel = status.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					status.setSelection(selList.toArray(sel));
				} else if (key.equals("resolution")) {
					String[] sel = resolution.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					resolution.setSelection(selList.toArray(sel));
				} else if (key.equals("bug_severity")) {
					String[] sel = severity.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					severity.setSelection(selList.toArray(sel));
				} else if (key.equals("priority")) {
					String[] sel = priority.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					priority.setSelection(selList.toArray(sel));
				} else if (key.equals("ref_platform")) {
					String[] sel = hardware.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					hardware.setSelection(selList.toArray(sel));
				} else if (key.equals("op_sys")) {
					String[] sel = os.getSelection();
					List<String> selList = Arrays.asList(sel);
					selList = new ArrayList<String>(selList);
					selList.add(value);
					sel = new String[selList.size()];
					os.setSelection(selList.toArray(sel));
				} else if (key.equals("emailassigned_to1")) { // HACK: email
																// buttons
																// assumed to be
																// in same
																// position
					if (value.equals("1"))
						emailButton[0].setSelection(true);
					else
						emailButton[0].setSelection(false);
				} else if (key.equals("emailreporter1")) { // HACK: email
															// buttons assumed
															// to be in same
															// position
					if (value.equals("1"))
						emailButton[1].setSelection(true);
					else
						emailButton[1].setSelection(false);
				} else if (key.equals("emailcc1")) { // HACK: email buttons
														// assumed to be in same
														// position
					if (value.equals("1"))
						emailButton[2].setSelection(true);
					else
						emailButton[2].setSelection(false);
				} else if (key.equals("emaillongdesc1")) { // HACK: email
															// buttons assumed
															// to be in same
															// position
					if (value.equals("1"))
						emailButton[3].setSelection(true);
					else
						emailButton[3].setSelection(false);
				} else if (key.equals("emailtype1")) {
					int index = 0;
					for (String item : emailOperation.getItems()) {
						if (item.compareTo(value) == 0)
							break;
						index++;
					}
					if (index < emailOperation.getItemCount()) {
						emailOperation.select(index);
					}
				} else if (key.equals("email1")) {
					emailPattern.setText(value);
				} else if (key.equals("changedin")) {
					daysText.setText(value);
				}
			}
			this.maxHits = maxHits;
			maxHitsText.setText(maxHits);
		}

		public void setControlsEnabled(boolean enabled) {
			summaryOperation.setEnabled(enabled);
			repositoryCombo.setEnabled(enabled);
			product.setEnabled(enabled);
			os.setEnabled(enabled);
			hardware.setEnabled(enabled);
			priority.setEnabled(enabled);
			severity.setEnabled(enabled);
			resolution.setEnabled(enabled);
			status.setEnabled(enabled);
			commentOperation.setEnabled(enabled);
			commentPattern.setEnabled(enabled);
			component.setEnabled(enabled);
			version.setEnabled(enabled);
			target.setEnabled(enabled);
			emailOperation.setEnabled(enabled);
			emailPattern.setEnabled(enabled);
			for (Button b : emailButton) {
				b.setEnabled(enabled);
			}
			// saveButton.setEnabled(enabled);
			// loadButton.setEnabled(enabled);
			updateButton.setEnabled(enabled);
			summaryPattern.setEnabled(enabled);
			daysText.setEnabled(enabled);

		}

		public String getSearchURL(TaskRepository repository) {
			try {
				if (rememberedQuery) {
					return getQueryURL(repository, new StringBuffer(input.getQueryParameters(selIndex)));
				} else {
					return getQueryURL(repository, getQueryParameters());
				}
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
			return "";
		}
	}

	public TaskRepository getRepository() {
		return searchOptionPage.getRepository();
	}
}
