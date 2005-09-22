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
import java.lang.reflect.InvocationTargetException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * A wizard for uploading the Mylar statistics to a website
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class FeedbackWizard extends Wizard implements INewWizard {
       
    private SubmitFeedbackPage feedbackPage;
    
	/**
	 * Constructor for SampleNewWizard.
	 */
	public FeedbackWizard(UserStudySubmissionWizard wizard) {
		super();
		setNeedsProgressMonitor(true);

        feedbackPage = new SubmitFeedbackPage(wizard);
	}

    public FeedbackWizard() {
        super();
        setNeedsProgressMonitor(true);
        super.setDefaultPageImageDescriptor(MylarMonitorPlugin.imageDescriptorFromPlugin(MylarMonitorPlugin.PLUGIN_ID, "icons/wizban/banner-user.gif"));
		super.setWindowTitle("Mylar Feedback");
        feedbackPage = new SubmitFeedbackPage(null);
    }
    
    @Override
	public boolean performFinish() {
    	File f = feedbackPage.createFeedbackFile();
    	if(f == null)
    		return true;
    	upload(f, feedbackPage.getStringUid());
    	if(f.exists()){
    		f.delete();
    	}
		return true;
	}
    
    private int status;
    
    /**
     * Method to upload a file to a cgi script
     * @param f The file to upload
     */
    private void upload(File f, String uid) {
    	String uploadScript;
    	uploadScript = MylarMonitorPlugin.UPLOAD_SERVER + MylarMonitorPlugin.UPLAOD_SCRIPT_QUESTIONNAIRE;
    	
    	if(f.length() == 0)
    		return;
    	
        try {
            final PostMethod filePost = new PostMethod(uploadScript);
            
            long time = new Date().getTime();
            
            Part[] parts;            
         	Part[] p = { new FilePart("MYLAR" + uid, MylarMonitorPlugin.UPLOAD_FILE_LABEL + "-" + MylarMonitorPlugin.VERSION + "-"+ "feedback" + "-" + uid + "-" + DateUtil.getFormattedDateTime(time) + ".txt", f) };
        	parts =  p;
  
        
            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost
                .getParams()));          

            final HttpClient client = new HttpClient();
            MylarMonitorPlugin.getDefault().configureProxy(client);
            
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            pmd.run(false, false, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try{
						status = client.executeMethod(filePost);
			            filePost.releaseConnection();      
			            
					}catch (Exception e){
			            // there was a problem with the file upload so throw up an error
			            // dialog to inform the user and log the exception
			        	if(e instanceof NoRouteToHostException || e instanceof UnknownHostException){
				            MessageDialog.openError(null, "Error Uploading", 
				            		"There was an error uploading the feedback" + ": \n" +
				            		"No network connection.  Please try again later");
			        	}else{
			        		MessageDialog.openError(null, "Error Uploading", 
			        				"There was an error uploading the feedback" + ": \n" +
				            		e.getClass().getCanonicalName());
			            	MylarPlugin.log(e, "could not uplaod feedback");
			        	}
			        }
					monitor.worked(1);
					monitor.done();
				}
            });
                                       
            
            if (status == 401) {
                // The uid was incorrect so inform the user 
                MessageDialog.openError(null, "Error Uploading", 
                        "There was an error uploading the feedback: \n" +
                        "Your uid was incorrect: " + uid + "\n");
            } else if (status == 407) {
                MessageDialog.openError(null, "Error Uploading", 
                		"Could not upload because proxy server authentication failed.  Please check your proxy server settings.");
            } else if (status != 200){
                // there was a problem with the file upload so throw up an error
                // dialog to inform the user
                MessageDialog.openError(null, "Error Uploading", 
                		"There was an error uploading the feedback: \n" +
                        "HTTP Response Code " + status + "\n" + 
                        "Please try again later");
            } else{
                // the file was uploaded successfully
            }
            
        } catch (Exception e){
            // there was a problem with the file upload so throw up an error
            // dialog to inform the user and log the exception
        	if(e instanceof NoRouteToHostException){
	            MessageDialog.openError(null, "Error Uploading", 
	            		"There was an error uploading the feedback: \n" +
	            		"No network connection.  Please try again later");
        	}else{
        		MessageDialog.openError(null, "Error Uploading", 
	            		"There was an error uploading the feedback: \n" +
	            		e.getClass().getCanonicalName());
            	MylarPlugin.log(e, "error uploading");
        	}
        }finally{
        	f.delete();
        }
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// don't need to do any initialization
	}

    @Override
	public void addPages() {
        addPage(feedbackPage);
    }
}
