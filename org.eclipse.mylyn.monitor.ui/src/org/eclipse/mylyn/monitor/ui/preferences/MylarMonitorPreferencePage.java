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
package org.eclipse.mylar.monitor.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class MylarMonitorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String DESCRIPTION = 
		"Choosing to participate ";
	private IntegerFieldEditor userStudyId;
	
	public MylarMonitorPreferencePage() {
		super();
		setPreferenceStore(MylarMonitorPlugin.getPrefs());	
		setDescription(DESCRIPTION);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout (layout);
		createStatisticsSection(container);
		createUserStudyIdSection(container);
		return container;
	}

	public void init(IWorkbench workbench) {
		// Nothing to init
	}

	private void createStatisticsSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		container.setLayoutData(gridData);
		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);
		Label label = new Label(container, SWT.NULL);
		label.setText("Number of user events since last submission: " + getPreferenceStore().getInt(MylarMonitorPlugin.PREF_NUM_USER_EVENTS));		
		
//		label = new Label(container, SWT.NULL);
//		label.setText("Number of total events: " + MylarMonitorPlugin.getDefault().getTotalNumberEvents());
	}
	
	private void createUserStudyIdSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		container.setLayoutData(gridData);
		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);
		
		userStudyId = new IntegerFieldEditor("", " User ID:", container); // HACK
		userStudyId.setErrorMessage("Your user id must be an integer");
		int uidNum = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarPlugin.USER_ID);
		if (uidNum == 0) uidNum = -1;
		userStudyId.setEmptyStringAllowed(false);
		userStudyId.setStringValue(uidNum + "");
	}
	
	@Override
	public boolean performOk() {
		int uidNum = -1;
      try{
          if(userStudyId.getStringValue() == null ||  userStudyId.getStringValue().equals("")){
              uidNum = -1;
              userStudyId.setStringValue(uidNum + "");
          }
          else{
              uidNum = userStudyId.getIntValue();
          }
          
          if(uidNum <= 0 && uidNum != -1){
              MessageDialog.openError(Display.getDefault().getActiveShell(), "User ID Incorrect", "The user study id must be a posative integer");
              return false;
          }
          if(uidNum != -1 && uidNum % 17 != 1){
              MessageDialog.openError(Display.getDefault().getActiveShell(), "User ID Incorrect", "Your user study id is not valid, please make sure it is correct or get a new id");
              return false;
          }
      }catch(NumberFormatException e){
          MessageDialog.openError(Display.getDefault().getActiveShell(), "User ID Incorrect", "The user study id must be a posative integer");
          return false;
      }
      MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.USER_ID, uidNum);
      return true;
	}
	
	@Override
	public boolean performCancel() {
		userStudyId.setStringValue(MylarPlugin.getDefault().getPreferenceStore().getInt(MylarPlugin.USER_ID)+"");
		return true;
	}
}
