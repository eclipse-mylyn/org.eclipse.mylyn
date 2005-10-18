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

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * Page to get a mylar user study id for the user
 * @author Shawn Minto
 */
public class GetNewUserIdPage extends WizardPage {
	
	private static final String SELECT_BELOW = "<Select Below>";
	private Text firstName;
    private Text lastName;
	private Text emailAddress;
    private Button anonymous;
//    private Hyperlink terms;
    private Button getNewUid;
    private Button getExistingUid;
	
    private String first;
    private String last;
    private String email;
    private boolean anon;
    private boolean hasValidated = false;
//    private String termsMessage = "<html><head>testing</head><p>This is a test </p></html>";
    
    private String jobFunction = SELECT_BELOW;
    private String companySize = SELECT_BELOW;
    private String companyFunction = SELECT_BELOW;
    
    private UsageSubmissionWizard wizard;
    private boolean performUpload;
    
    /**
     * Constructor
     */
	public GetNewUserIdPage(UsageSubmissionWizard wizard, boolean performUpload) {
		super("Statistics Wizard");
		this.performUpload = performUpload;
		setTitle("Get Mylar Feedback User ID");
		setDescription(
			"Before starting the Mylar user study you must get a study ID by filling out the following form.\n" +
			"If you already have an ID please fill out the information again to retrieve it.");
        this.wizard = wizard;
	} 

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;   	
		createBrowserSection(container);
        createAnonymousSection(container);
        createNamesSection(container);
//        createTermsSection(container);        
        createJobDetailSection(container);
        createUserIdButtons(container);
		setControl(container); 
	}    
	
	private void createBrowserSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
		Browser browser = new Browser(parent, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 200;
        gd.widthHint = 600;
        browser.setLayoutData(gd);
        
        Path path = new Path(MylarMonitorPlugin.getDefault().getStudyParameters().getFormsConsent());
        URL url = Platform.find(MylarMonitorPlugin.getDefault().getBundle(), path);
        try {
        	URL localURL = Platform.asLocalURL(url);
        	 browser.setUrl(localURL.toString());
        } catch (IOException e) {
        	browser.setText("Error: Ethics form could not be located");
        }
	}
    
	private void createAnonymousSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        
		anonymous = new Button(container, SWT.CHECK);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		anonymous.setLayoutData(gd);
		anonymous.setSelection(false);
		anonymous.setText("Anonymous (you must still provide your name and email for consent purposes)");
		anonymous.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget instanceof Button) {
					Button b = (Button) e.widget;
					anon = b.getSelection();
					updateEnablement();
//					boolean edit = !anon;
//					firstName.setEditable(edit);
//					lastName.setEditable(edit);
//					emailAddress.setEditable(edit);
					GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about default selection
			}
		});
	}
	
	private void createNamesSection(Composite parent) {
		Composite names = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(6, true);
        layout.verticalSpacing = 9;
        layout.horizontalSpacing = 4;
        names.setLayout(layout);
        
		Label label = new Label(names, SWT.NULL);
		label.setText("First Name:");		
		
		firstName= new Text(names, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
		firstName.setLayoutData(gd);
		firstName.setEditable(true);
        firstName.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                first = firstName.getText();
                updateEnablement();
                GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
            }
        });
		
        label = new Label(names, SWT.NULL);
        label.setText("Last Name:");
      
        lastName= new Text(names, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        lastName.setLayoutData(gd);
        lastName.setEditable(true);
        lastName.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                last = lastName.getText();
                updateEnablement();
                GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
            }
        });
        
        
        label = new Label(names, SWT.NONE);
        label.setText("Email Address:");
       
        emailAddress= new Text(names, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
        gd.horizontalSpan = 5;
        emailAddress.setLayoutData(gd);
        emailAddress.setEditable(true);
        emailAddress.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                email = emailAddress.getText();
                updateEnablement();
                GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
            }
        });
	}
	
//	private void createTermsSection(Composite parent) {
//		terms = new Hyperlink(parent, SWT.None);
//		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//        terms.setLayoutData(gd);
//        terms.setText("Terms of Early Release");
//        terms.setUnderlined(true);
//        terms.setForeground(new Color(Display.getDefault(), 0, 0, 200));
//        
//        terms.addHyperlinkListener(new IHyperlinkListener(){
//            public void linkEntered(HyperlinkEvent e) {
//            	// don't care about this event
//            }
//            public void linkExited(HyperlinkEvent e) {
//            	// don't care about this event
//            }
//            public void linkActivated(HyperlinkEvent e) {
//                termsMessage = "http://www.cs.ubc.ca/~mylar/";
//                WebBrowserDialog.openAcceptAgreement(null, "Terms of Early Release", "", termsMessage, true);
//            }
//        
//        });
//	}	
	
	private void createJobDetailSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        
        Label l = new Label(container , SWT.NULL);
        l.setText("Job Function:");
        final Combo jobFunctionCombo = new Combo(container,SWT.DROP_DOWN);
		jobFunctionCombo.setText(jobFunction);
		jobFunctionCombo.add("Application Developer");
		jobFunctionCombo.add("QA/Testing");
		jobFunctionCombo.add("Program Director");
		jobFunctionCombo.add("CIO/CTO");
		jobFunctionCombo.add("VP Development Systems Integrator");
		jobFunctionCombo.add("Application Architect");
		jobFunctionCombo.add("Project Manager");
		jobFunctionCombo.add("Student");
		jobFunctionCombo.add("Faculty");
		jobFunctionCombo.add("Business");
		jobFunctionCombo.add("Analyst");
		jobFunctionCombo.add("Database Administrator");
		jobFunctionCombo.add("Other");
		jobFunctionCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				jobFunction = jobFunctionCombo.getText();	
				updateEnablement();
			}
		});
		
		l = new Label(container , SWT.NULL);
        l.setText("Company Size:");
		final Combo companySizecombo = new Combo(container,SWT.DROP_DOWN);
		companySizecombo.setText(companySize);
		companySizecombo.add("Individual");
		companySizecombo.add("<50");
		companySizecombo.add("50-100");
		companySizecombo.add("100-500");
		companySizecombo.add("500-1000");
		companySizecombo.add("1000-2500");
		companySizecombo.add(">2500");
		companySizecombo.addSelectionListener(new SelectionAdapter() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				companySize = companySizecombo.getText();
				updateEnablement();
			}
		});
		
		l = new Label(container , SWT.NULL);
        l.setText("Company Buisness");
		final Combo companyBuisnesscombo = new Combo(container,SWT.DROP_DOWN);
		companyBuisnesscombo.setText(companyFunction);
		companyBuisnesscombo.add("Financial service/insurance");
		companyBuisnesscombo.add("Energy");
		companyBuisnesscombo.add("Government");
		companyBuisnesscombo.add("Hardware Manufacturer");
		companyBuisnesscombo.add("Networking");
		companyBuisnesscombo.add("Pharmaceutical/Medical");
		companyBuisnesscombo.add("Automotive");
		companyBuisnesscombo.add("Software Manufacturer");
		companyBuisnesscombo.add("Communications");
		companyBuisnesscombo.add("Transportation");
		companyBuisnesscombo.add("Retail");
		companyBuisnesscombo.add("Utilities");
		companyBuisnesscombo.add("Other Manufacturing");
		companyBuisnesscombo.add("Academic/Education");
		companyBuisnesscombo.addSelectionListener(new SelectionAdapter() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				companyFunction = companyBuisnesscombo.getText();
				updateEnablement();
			}
		});
	}
	
	private void createUserIdButtons(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;   
		container.setLayout(layout);
		
		Label l = new Label(container, SWT.NONE);
		l.setText("To get a new ID you must complete the 3 survey questions above.");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		l.setLayoutData(gd);
				
		container = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;   
		container.setLayout(layout);
		
		getNewUid = new Button(container, SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		getNewUid.setLayoutData(gd);
		getNewUid.setSelection(false);
		getNewUid.setText("I agree, get me a new user ID");
		getNewUid.addSelectionListener(new SelectionListener(){
            public void widgetSelected(SelectionEvent e) {
                if(e.widget instanceof Button){
                    if(hasAllFields(false)){
                        if(wizard.getNewUid(first, last, email, anon, jobFunction, companySize, companyFunction) != -1){
                            if(wizard.getUploadPage() != null) 
                                wizard.getUploadPage().updateUid();
                            hasValidated = true;
                            MessageDialog.openInformation(Display.getDefault().getActiveShell(),"Mylar User Study ID", "Your mylar user study id is: " + wizard.getUid() + "\n Please record this number if you are using multiple copies of eclipse so that you do not have to register again.");
                        }
                    } else {
                    	MessageDialog.openError( Display.getDefault().getActiveShell(), 
    							"Incomplete Form Input", "Please complete all of the fields.");
                    }
                    GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            	// don't care about default selected
            }
        });
        
        getExistingUid = new Button(container, SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		getExistingUid.setLayoutData(gd);
		getExistingUid.setSelection(false);
		getExistingUid.setText("Get my existing user ID");
		getExistingUid.addSelectionListener(new SelectionListener(){
            public void widgetSelected(SelectionEvent e) {
                if(e.widget instanceof Button){
                    if(hasAllFields(true)){
                        if(wizard.getExistingUid(first, last, email, anon) != -1){
                            if(wizard.getUploadPage() != null) 
                                wizard.getUploadPage().updateUid();
                            hasValidated = true;
                            MessageDialog.openInformation(Display.getDefault().getActiveShell(),
                            		"Mylar Feedback User ID", "Your mylar feedback id is: " + wizard.getUid() + "\n Please record this number if you are using multiple copies of eclipse so that you do not have to register again.");
                        }
                    } else {
                    	MessageDialog.openError( Display.getDefault().getActiveShell(), 
    							"Incomplete Form Input", "Please complete all of the fields.");
                    }
                    GetNewUserIdPage.this.setPageComplete(GetNewUserIdPage.this.isPageComplete());
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            	// don't care about default selected
            }
        });
		updateEnablement();
	}
	
	
	private void updateEnablement() {
		boolean nameFilled = (!firstName.getText().equals("") && !lastName.getText().equals("") && !emailAddress.getText().equals("")) || anon;
		if(nameFilled){
			getExistingUid.setEnabled(true);
			boolean jobFilled = !jobFunction.equals(SELECT_BELOW) 
    		&& !companyFunction.equals(SELECT_BELOW)
    		&& !companySize.equals(SELECT_BELOW);
			if(jobFilled){
				getNewUid.setEnabled(true);	
			} else {
				getNewUid.setEnabled(false);				
			}
		} else {
			getExistingUid.setEnabled(false);
			getNewUid.setEnabled(false);
		}
	}
	
    public boolean hasAllFields(boolean existing){
    	boolean nameFilled = !firstName.getText().equals("") && !lastName.getText().equals("") && !emailAddress.getText().equals("");
        
    	if(!existing){
	    	boolean jobFilled = !jobFunction.equals(SELECT_BELOW) 
	    		&& !companyFunction.equals(SELECT_BELOW)
	    		&& !companySize.equals(SELECT_BELOW);
	    	return (jobFilled && nameFilled);
    	} else {
    		return nameFilled || anon;
    	}
    }
    
    @Override
    public boolean isPageComplete(){
        if(hasAllFields(true) && hasValidated)
            return true;
        else return false;
    }
    
    @Override
    public IWizardPage getNextPage(){
        if(isPageComplete() && performUpload)
            wizard.addPage(wizard.getUploadPage());
        
        return super.getNextPage();
        
    }

    public boolean isAnonymous() {
        return anon;
    }

    public String getEmailAddress() {
        return email;
    }

    public String getFirstName() {
        return first;
    }

    public String getLastName() {
        return last;
    }
}
