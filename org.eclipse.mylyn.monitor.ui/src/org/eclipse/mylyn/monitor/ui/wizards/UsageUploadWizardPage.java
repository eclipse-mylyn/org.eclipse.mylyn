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
package org.eclipse.mylar.monitor.ui.wizards;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Page to upload the file to the server
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class UsageUploadWizardPage extends WizardPage {

    /** A text box to hold the address of the server */
	private Text serverAddrText;

    /** A text box to hold the location of the usage statistics file */
	private Text usageFileText;
	
//	/** A text box to hold the location of the log file */
//	private Text logFileText;
	
    /** A text file to show the id of the user */
	private Text idText;
	    
    private UsageSubmissionWizard wizard;
    
    /**
     * Constructor
     */
	public UsageUploadWizardPage(UsageSubmissionWizard wizard) {
		super("Usage Statistics Submission Wizard");
		setTitle("Statistics Upload");
		setDescription(
				"The files listed below will be uploaded. Information about program elements that you "
				+ "worked with is obfuscated to ensure privacy.");
		this.wizard = wizard;
	}

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		
		Label label = new Label(parent, SWT.NULL);
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		label.setText(MylarMonitorPlugin.getDefault().getCustomizedByMessage());
		
		label = new Label(container, SWT.NULL);
		label.setText("Upload URL:");		
		
		serverAddrText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		serverAddrText.setLayoutData(gd);
		serverAddrText.setEditable(false);
		serverAddrText.setText(MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl());
		
		label = new Label(container, SWT.NULL);
		label.setText("Usage file location:");
		
		usageFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		usageFileText.setLayoutData(gd);
		usageFileText.setEditable(false);
		
		usageFileText.setText(wizard.getMonitorFileName());
		
//		label = new Label(container, SWT.NULL);
//		label.setText("Log file location:");
		
//		logFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		logFileText.setLayoutData(gd);
//		logFileText.setEditable(false);
//		
//		logFileText.setText(wizard.getLogFileName());
		
		label = new Label(container, SWT.NULL);
		label.setText("User study ID:");
		
		idText  = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		idText.setLayoutData(gd);
		idText.setEditable(false);
		idText.setText(wizard.getUid() + "");
	
//	add back in if we want to view the file in a dialog
//        Button b = new Button(container, SWT.PUSH);
//        gd = new GridData(GridData.FILL_HORIZONTAL);
//        b.setLayoutData(gd);
//        b.setText("View File");
//        b.addSelectionListener(new SelectionListener(){
//
//            public void widgetSelected(SelectionEvent e) {
//                String filename = wizard.getStatisticFileName();
//                File file = new File(filename);
//                try {
//                    FileDisplayDialog.openShowFile(null, filename, "", file);
//                }catch(FileNotFoundException fnfe){
//                	MylarPlugin.log(this.getClass().toString(), fnfe);
//                }
//            }
//
//            public void widgetDefaultSelected(SelectionEvent e) {
//            	// don't care about default selected
//            }
//            
//        });
        
		setControl(container);
	}
    
	@Override
    public IWizardPage getNextPage(){
        return null;
    }

    public void updateUid() {
        if(idText != null && !idText.isDisposed())
            idText.setText(wizard.getUid() + "");
    }
}
