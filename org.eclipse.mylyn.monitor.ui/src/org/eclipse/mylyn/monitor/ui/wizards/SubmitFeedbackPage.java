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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * Page to submit feedback for the user study
 * @author Shawn Minto
 */
public class SubmitFeedbackPage extends WizardPage {

	private Text firstName;
    private Text lastName;
	private Text emailAddress;
    private Text feedback;
    private Text uid;
    private Button totallyAnonymous;
    private Button identifiedUserStudyId;
    private Button identifiedNameEmail;
        
    private String first;
    private String last;
    private String email;
    private String feed;
    private int id;
    private boolean anon;
    private boolean named;
    
    private UserStudySubmissionWizard uploadWizard;
    
    /**
     * Constructor
     */
	public SubmitFeedbackPage(UserStudySubmissionWizard uploadWizard) {
		super("Feedback Wizard");
		setTitle("Submit feedback for Mylar User Study");
		setDescription(QuestionnaireWizardPage.FEEDBACK_REQUEST);
        this.uploadWizard = uploadWizard;
	}

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

        Composite container2 = null;

        if(uploadWizard == null){
            container2 = new Composite(container, SWT.NULL);
            layout = new GridLayout(2, false);
            container2.setLayout(layout);
            
            container2 = addSubmissionStyleControls(container2);
        }else{
            container2= new Composite(container, SWT.NULL);
            layout = new GridLayout(2, true);
            container2.setLayout(layout);
        }
               
       
        Label label = new Label(container2, SWT.NONE);
        //HACK used to make the feedback column a nice width
        label.setText("Feedback:                                                            ");
        GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);
        
        feedback = new Text(container2, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = 2;
        gd.verticalSpan = 40;
        feedback.setLayoutData(gd);
        feedback.setEditable(true);
        feedback.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                feed = feedback.getText();
                SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
            }
        });

		setControl(container);
	}
   
    private Composite addSubmissionStyleControls(Composite container) {
        totallyAnonymous = new Button(container, SWT.RADIO);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        totallyAnonymous.setLayoutData(gd);
        totallyAnonymous.setSelection(false);
        totallyAnonymous.addSelectionListener(new SelectionListener(){
            public void widgetSelected(SelectionEvent e) {
                if(e.widget == totallyAnonymous){
                    anon = true;
                    named = false;
                    boolean edit = !anon;
                    firstName.setEditable(edit);
                    lastName.setEditable(edit);
                    emailAddress.setEditable(edit);
                    SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            	// don't care about default selection
            }
        });
        
        Group anonCont = new Group(container, SWT.SHADOW_ETCHED_IN);
        GridLayout layout = new GridLayout(1, true);
        layout.verticalSpacing = 9;
        layout.horizontalSpacing = 4;
        anonCont.setLayout(layout);
        anonCont.setText("Anonymous");
        
        identifiedUserStudyId = new Button(container, SWT.RADIO);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        identifiedUserStudyId.setLayoutData(gd);
        identifiedUserStudyId.setSelection(false);
        identifiedUserStudyId.addSelectionListener(new SelectionListener(){
            public void widgetSelected(SelectionEvent e) {
                if(e.widget == identifiedUserStudyId){
                    anon = true;
                    named = false;
                    boolean edit = !anon;
                    firstName.setEditable(edit);
                    lastName.setEditable(edit);
                    emailAddress.setEditable(edit);
                    SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            	// don't care about default selection
            }
        });
        
        Group idUserCont = new Group(container, SWT.SHADOW_ETCHED_IN);
        layout = new GridLayout(2, true);
        layout.verticalSpacing = 9;
        layout.horizontalSpacing = 4;
        idUserCont.setLayout(layout);
        idUserCont.setText("Identified By User Study Id");
        
        Label label = new Label(idUserCont, SWT.NULL);
        label.setText("User Id:");       
        
        uid= new Text(idUserCont, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 1;
        uid.setLayoutData(gd);
        uid.setEditable(false);
        id = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarPlugin.USER_ID);
        if(id == 0)
            id = -1;
        uid.setText(id+"");
        
        identifiedNameEmail = new Button(container, SWT.RADIO);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        identifiedNameEmail.setLayoutData(gd);
        identifiedNameEmail.setSelection(false);
        identifiedNameEmail.addSelectionListener(new SelectionListener(){
            public void widgetSelected(SelectionEvent e) {
                if(e.widget == identifiedNameEmail){
                    anon = false;
                    named = true;
                    boolean edit = !anon;
                    firstName.setEditable(edit);
                    lastName.setEditable(edit);
                    emailAddress.setEditable(edit);
                    SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            	// don't care about default selection
            }
        });

        Group idCont = new Group(container, SWT.SHADOW_ETCHED_IN);
        layout = new GridLayout(1, true);
        layout.verticalSpacing = 9;
        layout.horizontalSpacing = 4;
        idCont.setLayout(layout);
        idCont.setText("Identifed By Name and Email");
        
        
        Composite names = new Composite(idCont, SWT.NULL);
        layout = new GridLayout(6, true);
        layout.verticalSpacing = 9;
        layout.horizontalSpacing = 4;
        names.setLayout(layout);
        
        label = new Label(names, SWT.NULL);
        label.setText("First Name:");       
        
        firstName= new Text(names, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        firstName.setLayoutData(gd);
        firstName.setEditable(true);
        firstName.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                first = firstName.getText();
                SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
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
                SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
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
                boolean valid = vaidateEmail(email);
                if(!valid)
                    SubmitFeedbackPage.this.setErrorMessage("Must be a valid e-mail address");
                else
                    SubmitFeedbackPage.this.setErrorMessage(null);
                SubmitFeedbackPage.this.setPageComplete(SubmitFeedbackPage.this.isPageComplete());
            }
        });

        if(id == -1){
            identifiedUserStudyId.setEnabled(false);
            identifiedNameEmail.setSelection(true);
        } else{
            identifiedUserStudyId.setSelection(true);
            firstName.setEditable(false);
            lastName.setEditable(false);
            emailAddress.setEditable(false);
        }
        return container;
    }
    
    private boolean vaidateEmail(String emailToVerify){
        if(emailToVerify.contains("@")){
            emailToVerify = emailToVerify.substring(emailToVerify.indexOf("@"));
            return emailToVerify.contains(".");
        }
        return false;
    }
    
    public boolean hasAllFields(){
        if(uploadWizard != null)
            return (((!firstName.getText().equals("") && !lastName.getText().equals("") && !emailAddress.getText().equals("")) || anon) && !feedback.getText().equals(""));
        else
            return !feedback.getText().equals("");
    }
    
    @Override
    public boolean isPageComplete(){
        if(hasAllFields() && this.getErrorMessage() == null)
            return true;
        else return false;
    }
    
    @Override
    public IWizardPage getNextPage(){
        return super.getNextPage();
        
    }

    /** ONLY VALID IF WIZARD NOT STARTED FROM UPLOAD WIZARD */
    public boolean isAnon(){
        return anon;
    }
    

    public int getUid(){
    	if(anon || named)
    		return -1;
        return id;
    }
    
    public String getStringUid(){
    	if(anon){
    		return "anon";
    	}else if(named){
    		return "named";
    	}else{
    		return "" + getUid();
    	}
    }
    
    /** ONLY VALID IF WIZARD NOT STARTED FROM UPLOAD WIZARD */
    public String getEmailAddress() {
    	if(anon)
    		return "null";
        return email;
    }

    /** ONLY VALID IF WIZARD NOT STARTED FROM UPLOAD WIZARD */
    public String getFirstName() {
    	if(anon)
    		return "null";
        return first;
    }

    /** ONLY VALID IF WIZARD NOT STARTED FROM UPLOAD WIZARD */
    public String getLastName() {
    	if(anon)
    		return "null";
        return last;
    }
    
    /** ONLY VALID IF WIZARD NOT STARTED FROM UPLOAD WIZARD */
    public String getFeedback() {
        return feed;
    }
    
    public File createFeedbackFile() {
		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String path = rootPath.toString() + File.separator
				+ "feedback.txt";
		File feedbackFile = new File(path);
		
		if (feedbackFile.exists()) {
			feedbackFile.delete();
		}
		
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(feedbackFile);

			String buffer = "First Name: " + getFirstName() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "Last Name: " + getLastName() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "E-mail Address: " + getEmailAddress() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "UID: " + getUid() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "Feedback: " + getFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			outputStream.close();
			return feedbackFile;
		} catch (FileNotFoundException e) {
			MylarPlugin.log(e, "failed to submit");
		} catch (IOException e) {
			MylarPlugin.log(e, "failed to submit");
		}
		return null;
	}
    
}
