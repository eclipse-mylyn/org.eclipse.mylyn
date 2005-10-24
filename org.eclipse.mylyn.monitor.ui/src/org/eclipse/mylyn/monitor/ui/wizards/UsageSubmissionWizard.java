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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.monitor.IQuestionnairePage;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;


/**
 * A wizard for uploading the Mylar statistics to a website
 * @author Shawn Minto
 */
public class UsageSubmissionWizard extends Wizard implements INewWizard {
   
    public static final String LOG = "log";
    public static final String STATS = "usage";
    public static final String QUESTIONAIRE = "questionaire";
    
    private boolean failed = false;
    
    /** The id of the user */
    private int uid;

    private final File monitorFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
//    private final File logFile = MylarMonitorPlugin.getDefault().getLogFile();
	
    private UsageUploadWizardPage uploadPage;
    private GetNewUserIdPage getUidPage;
    private IQuestionnairePage questionnairePage;
    private boolean performUpload = true;
    
	public UsageSubmissionWizard() {
		super();
		setTitles();
		init(true);
	}

	public UsageSubmissionWizard(boolean performUpload) {
		super();
		setTitles();
		init(performUpload);
	}

	private void setTitles() {
		super.setDefaultPageImageDescriptor(MylarMonitorPlugin.imageDescriptorFromPlugin(MylarMonitorPlugin.PLUGIN_ID, "icons/wizban/banner-user.gif"));
		super.setWindowTitle("Mylar Feedback");
	}
	
	private void init(boolean performUpload) {
		this.performUpload = performUpload;
		setNeedsProgressMonitor(true);
        uid = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarPlugin.USER_ID);
        if(uid == 0) {
            uid = -1;
        }
        uploadPage = new UsageUploadWizardPage(this);
        getUidPage = new GetNewUserIdPage(this, performUpload);
        if (MylarMonitorPlugin.getDefault().isQuestionnaireEnabled() && performUpload) {
	        IQuestionnairePage page = MylarMonitorPlugin.getDefault().getStudyParameters().getQuestionnairePage();
        	questionnairePage = page;
        }
        super.setForcePreviousAndNextButtons(true);
    }

	private File questionnaireFile = null;
	
    @Override
	public boolean performFinish() {
    	
//    	int numEvents = MylarMonitorPlugin.getPrefs().getInt(MylarMonitorPlugin.PREF_NUM_USER_EVENTS);
//    	int numSinceLastPhase = 
//    		MylarMonitorPlugin.NEXT_PHASE_EVENT_THRESHOLD 
//    		- MylarMonitorPlugin.getPrefs().getInt(MylarMonitorPlugin.PREF_NUM_USER_EVENTS) 
//    		- MylarMonitorPlugin.getPrefs().getInt(MylarMonitorPlugin.PREF_NUM_USER_EVENTS_LAST_PHASE);
//    	MylarPlugin.log("Number user events: " + numEvents, this);
//    	MylarPlugin.log("Number events needed: " + numSinceLastPhase, this);
//    	MylarPlugin.log("Date next release: " + DateUtil.getFormattedDateTime(MylarMonitorPlugin.NEXT_RELEASE_AVAILABLE.getTimeInMillis()), this);
    	if (!performUpload) return true;
    	if (MylarMonitorPlugin.getDefault().isQuestionnaireEnabled() 
    		&& performUpload
    		&& questionnairePage != null) {
        	questionnaireFile = questionnairePage.createFeedbackFile();
    	}
    	
    	final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(final IProgressMonitor monitor) throws CoreException {
					monitor.beginTask("Uploading user statistics", 3);
					performUpload(monitor);
					monitor.done();
			}
    	};
    	
    	Job j = new Job("Upload User Statistics"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					op.run(monitor);
					return Status.OK_STATUS;
				} catch (Exception e){
					MylarPlugin.log(e, "Error uploading statistics");
					return new Status(Status.ERROR, MylarMonitorPlugin.PLUGIN_ID, Status.ERROR, "Error uploading statistics", e);
				}
			}
    	};
//    	j.setUser(true);
    	j.setPriority(Job.DECORATE);
    	j.schedule();
    	return true;
	}
    
    public void performUpload(IProgressMonitor monitor){
    	
    	if (MylarMonitorPlugin.getDefault().isQuestionnaireEnabled() && performUpload && questionnaireFile != null) {
        	upload(questionnaireFile, QUESTIONAIRE, monitor);
        
	        if(failed){
	        	failed = false;
	        }
	        
	        if (questionnaireFile.exists()) {
				questionnaireFile.delete();
			}
    	}
        File f = zipFilesForUpload();
        if(f == null)
        	return;
                
//        upload(logFile, LOG, monitor);
//        if(!failed){
//        	logFile.delete();
//        }else{
//        	failed = false;
//        }
        
        upload(f, STATS, monitor);
        if(f.exists()){
        	f.delete();
        }
                
        if(!failed){
        	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
				public void run() {
		        	// popup a dialog telling the user that the upload was good
		        	MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
		        			"Successful Upload", "Your usage statistics have been successfully uploaded.\n Thank you for participating in the study.");
				}
        	});
        	
            // clear the files
            if(!monitorFile.delete()){
            	MylarPlugin.log("Unable to delete the monitor file", this);
            }
        }
        
//    	MylarMonitorPlugin.getDefault().startLog();
        MylarMonitorPlugin.getDefault().getInteractionLogger().start();
        MylarMonitorPlugin.setPerformingUpload(false);
		return;
    }
    
    public boolean performCancel(){
    	MylarMonitorPlugin.getDefault().userCancelSubmitFeedback(new Date(), true);
    	return true;
    }
    
    @Override
    public boolean canFinish(){
        if (!performUpload) {
        	return getUidPage.isPageComplete();
        } else {
        	return this.getContainer().getCurrentPage() == uploadPage || !performUpload;
        }
    }
    
    public UsageUploadWizardPage getUploadPage() {
        return uploadPage;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

    @Override
	public void addPages() {
    	if (MylarMonitorPlugin.getDefault().isQuestionnaireEnabled() 
    		&& performUpload
    		&& questionnairePage != null) {
    		addPage(questionnairePage);
    	}
		if(uid == -1){
            addPage(getUidPage);
        } else if (performUpload){
            addPage(uploadPage);
        }
	}
    
    /**
     * Method to upload a file to a cgi script
     * @param f The file to upload
     */
    private void upload(File f, String type, IProgressMonitor monitor) {
    	if(failed) return;
    	String uploadFile;
    	String uploadScript;
    	if (type.equals(STATS) || type.equals(LOG)) {        	
        	uploadFile = "usage statistics file";
        	uploadScript = MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl() + MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUpload();
        } else {
        	uploadFile = "questionnaire";
        	uploadScript = MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl() + MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsQuestionnaire();
        }
    	
    	if(f.length() == 0)
    		return;
    	
        try {
            final PostMethod filePost = new PostMethod(uploadScript);
            
            long time = new Date().getTime();
            
            Part[] parts;            
            if (type.equals(STATS)) {
            	Part[] p = { new FilePart("MYLAR" + uid, MylarMonitorPlugin.UPLOAD_FILE_LABEL + "-"+ MylarMonitorPlugin.VERSION + "-"+ STATS + "-" + uid + "-" + DateUtil.getFormattedDateTime(time) + ".zip", f) };
            	parts =  p;
            	uploadFile = "usage statistics file";
            }else if (type.equals(LOG)) {
            	Part[] p = { new FilePart("MYLAR" + uid, MylarMonitorPlugin.UPLOAD_FILE_LABEL + "-"+ MylarMonitorPlugin.VERSION + "-"+ LOG + "-" + uid + "-" + DateUtil.getFormattedDateTime(time) + ".txt", f) };
            	parts =  p;
            	uploadFile = "mylar log file";
            }
            else if(type.equals(QUESTIONAIRE)){
            	Part[] p ={ new FilePart("MYLAR" + uid, MylarMonitorPlugin.UPLOAD_FILE_LABEL + "-"+ MylarMonitorPlugin.VERSION + "-"+ QUESTIONAIRE + "-" + uid + "-" + DateUtil.getFormattedDateTime(time) + ".txt", f) };
            	parts = p;
            	uploadFile = "questionnaire";
            }
            else{
            	failed = true;
            	return;
            }
        
            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost
                .getParams()));          

            final HttpClient client = new HttpClient();
            MylarMonitorPlugin.getDefault().configureProxy(client);

            try{
				status = client.executeMethod(filePost);
	            filePost.releaseConnection();      
	            
			}catch (final Exception e){
	            // there was a problem with the file upload so throw up an error
	            // dialog to inform the user and log the exception
	        	failed = true;
	        	if(e instanceof NoRouteToHostException || e instanceof UnknownHostException){
	        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
						public void run() {
				            MessageDialog.openError(null, "Error Uploading", 
				            		"There was an error uploading the file" + ": \n" +
				            		"No network connection.  Please try again later");
						}
	        		});
	        	}else{
	        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
						public void run() {
			        		MessageDialog.openError(null, "Error Uploading", 
			        				"There was an error uploading the file" + ": \n" +
				            		e.getClass().getCanonicalName());
						}
	        		});
	            	MylarPlugin.log(e, "failed to upload");
	        	}
	        }
			monitor.worked(1);
            
			final String filedesc = uploadFile;
			
            if(status == 401){
                // The uid was incorrect so inform the user
            	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		                MessageDialog.openError(null, "Error Uploading", 
		                        "There was an error uploading the " + filedesc + ": \n" +
		                        "Your uid was incorrect: " + uid + "\n");
					}
            	});
            } else if (status == 407) {
            	failed = true;
            	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		            	MessageDialog.openError(null, "Error Uploading", 
		            		"Could not upload because proxy server authentication failed.  Please check your proxy server settings.");
					}
            	});
            } else if(status != 200){
	            failed = true;
                // there was a problem with the file upload so throw up an error
                // dialog to inform the user
	            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		                MessageDialog.openError(null, "Error Uploading", 
		                		"There was an error uploading the " + filedesc + ": \n" +
		                        "HTTP Response Code " + status + "\n" + 
		                        "Please try again later");
					}
	            });
            }
            else{
                // the file was uploaded successfully
            }
            
        } catch (final Exception e){
            // there was a problem with the file upload so throw up an error
            // dialog to inform the user and log the exception
        	failed = true;
        	final String filedesc = uploadFile;
        	if(e instanceof NoRouteToHostException){
        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
			            MessageDialog.openError(null, "Error Uploading", 
			            		"There was an error uploading the " + filedesc + ": \n" +
			            		"No network connection.  Please try again later");
					}
        		});
        	}else{
        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		        		MessageDialog.openError(null, "Error Uploading", 
			            		"There was an error uploading the " + filedesc + ": \n" +
			            		e.getClass().getCanonicalName());
					}
        		});
            	MylarPlugin.log(e, "error uploading");
        	}
        }
    }

	public String getMonitorFileName(){
    	return monitorFile.getAbsolutePath();
    }
    
//    public String getLogFileName(){
//    	return logFile.getAbsolutePath();
//    }
    
    /** The status from the http request */
    private int status;
    
    /** the response for the http request */
    private String resp;
    
    public int getExistingUid(String firstName, String lastName, String emailAddress, boolean anonymous) {
    	if(failed)
    		return -1;
    	try {
            if(anonymous){
            	InputDialog d = new InputDialog(null,"Enter User Study Id", "Please enter your user study id", "", new IInputValidator(){
                        public String isValid(String newText) {
                            try{
                                int testUid = Integer.parseInt(newText);
                                if(testUid <= 0)
                                    return "User id must be a posative integer";
                                else if(testUid % 17 != 1)
                                    return "User id is invalid, please check your user id or get a new id by clicking cancel";
                            }catch (Exception e){
                                return "User id must be an integer";
                            }
                            return null;
                        }
                    });
                    int rc = d.open();
                    if(rc == InputDialog.OK){
                        uid = Integer.parseInt(d.getValue());
                        MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.USER_ID, uid);
                        return uid;
                    } else {
                    	return -1;
                }
            }
            
            // create a new post method
            final GetMethod getUidMethod = new GetMethod(MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl() + MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUserId());
            
            NameValuePair first = new NameValuePair("firstName", firstName);
            NameValuePair last = new NameValuePair("lastName", lastName);
            NameValuePair email = new NameValuePair("email", emailAddress);
            NameValuePair job = new NameValuePair("jobFunction", "");
            NameValuePair size = new NameValuePair("companySize", "");
            NameValuePair buisness = new NameValuePair("companyBuisness", "");
            NameValuePair anon = null;
            if(anonymous){                
                anon = new NameValuePair("anonymous", "true");                            
            }else{
                anon = new NameValuePair("anonymous", "false");
            }
            getUidMethod.setQueryString(new NameValuePair[]{first, last, email, job,size, buisness, anon});
            
            // create a new client and upload the file
            final HttpClient client = new HttpClient();
            MylarMonitorPlugin.getDefault().configureProxy(client);
            
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            pmd.run(false, false, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Get User Id", 1);
						            
					try{
						status = client.executeMethod(getUidMethod);
						
						resp = getData(getUidMethod.getResponseBodyAsStream());
			            
			            // release the connection to the server
			            getUidMethod.releaseConnection();
					}catch (final Exception e){
			            // there was a problem with the file upload so throw up an error
			            // dialog to inform the user and log the exception
			        	failed = true;
			        	if(e instanceof NoRouteToHostException || e instanceof UnknownHostException){
			        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
								public void run() {
						            MessageDialog.openError(null, "Error Uploading", 
						            		"There was an error getting a new user id: \n" +
						            		"No network connection.  Please try again later");
								}
			        		});
			        	}else{
			        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
								public void run() {
					        		MessageDialog.openError(null, "Error Uploading", 
						            		"There was an error getting a new user id: \n" +
						            		e.getClass().getCanonicalName() + e.getMessage());
								}
			        		});
			            	MylarPlugin.log(e, "error uploading");
			        	}
			        }
					monitor.worked(1);
					monitor.done();
				}
            });
            
            if(status != 200){
                // there was a problem with the file upload so throw up an error
                // dialog to inform the user

            	failed = true;
            	
                // there was a problem with the file upload so throw up an error
                // dialog to inform the user
            	PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		                MessageDialog.openError(null, "Error Getting User ID", 
		                		"There was an error getting a user id: \n" +
		                        "HTTP Response Code " + status + "\n" + 
		                        "Please try again later");
					}
            	});
            }
            else{
                resp = resp.substring(resp.indexOf(":")+1).trim();
                uid = Integer.parseInt(resp);
                MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.USER_ID, uid);
                return uid;
            }
            
        } catch (final Exception e){
            // there was a problem with the file upload so throw up an error
            // dialog to inform the user and log the exception
        	failed = true;
        	if(e instanceof NoRouteToHostException || e instanceof UnknownHostException){
        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		        		MessageDialog.openError(null, "Error Uploading", 
			            		"There was an error getting a new user id: \n" +
			            		"No network connection.  Please try again later");
					}
        		});
        	}else{
        		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
					public void run() {
		        		MessageDialog.openError(null, "Error Uploading", 
			            		"There was an error getting a new user id: \n" +
			            		e.getClass().getCanonicalName());
					}
        		});
            	MylarPlugin.log(e, "error uploading");
        	}
        }
        return -1;
	}
    
    public int getNewUid(String firstName, String lastName, String emailAddress, boolean anonymous, String jobFunction, String companySize, String companyFunction) {
    	if(failed)
    		return -1;
    	try {
            if(anonymous){
                boolean hasUid = MessageDialog.openQuestion(null, "Retrieve anonymous user ID", "Do you already have a user study id (e.g. have already set up Mylar in a different workspace)?");
                if(hasUid){
                    InputDialog d = new InputDialog(null, "Enter User Study Id", "Please enter your user study id", "", new IInputValidator(){

                        public String isValid(String newText) {
                            try{
                                int testUid = Integer.parseInt(newText);
                                if(testUid <= 0)
                                    return "User id must be a posative integer";
                                else if(testUid % 17 != 1)
                                    return "User id is invalid, please check your user id or get a new id by clicking cancel";
                            }catch (Exception e){
                                return "User id must be an integer";
                            }
                            return null;
                        }
                    
                    });
                    int rc = d.open();
                    if(rc == InputDialog.OK){
                        uid = Integer.parseInt(d.getValue());
                        MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.USER_ID, uid);
                        return uid;
                    }
                }
            }
            
            // create a new post method
            final GetMethod getUidMethod = new GetMethod(MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl() + MylarMonitorPlugin.getDefault().getStudyParameters().getScriptsUserId());
            
            NameValuePair first = new NameValuePair("firstName", firstName);
            NameValuePair last = new NameValuePair("lastName", lastName);
            NameValuePair email = new NameValuePair("email", emailAddress);
            NameValuePair job = new NameValuePair("jobFunction", jobFunction);
            NameValuePair size = new NameValuePair("companySize", companySize);
            NameValuePair buisness = new NameValuePair("companyBuisness", companyFunction);
            NameValuePair anon = null;
            if(anonymous){                
                anon = new NameValuePair("anonymous", "true");                            
            }else{
                anon = new NameValuePair("anonymous", "false");
            }
            getUidMethod.setQueryString(new NameValuePair[]{first, last, email, job,size, buisness, anon});
            
            // create a new client and upload the file
            final HttpClient client = new HttpClient();
            MylarMonitorPlugin.getDefault().configureProxy(client);
            
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
            pmd.run(false, false, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Get User Id", 1);
						            
					try{
						status = client.executeMethod(getUidMethod);
						
						resp = getData(getUidMethod.getResponseBodyAsStream());
			            
			            // release the connection to the server
			            getUidMethod.releaseConnection();
					}catch (Exception e){
			            // there was a problem with the file upload so throw up an error
			            // dialog to inform the user and log the exception
			        	failed = true;
			        	if(e instanceof NoRouteToHostException || e instanceof UnknownHostException){
				            MessageDialog.openError(null, "Error Communicating", 
				            		"There was an error getting a new user id. \n" +
				            		"No network connection.  Please try again later");
			        	}else{
			        		MessageDialog.openError(null, "Error Communicating", 
				            		"There was an error getting a new user id: \n" +
				            		e.getClass().getCanonicalName() + e.getMessage());
			            	MylarPlugin.log(e, "error uploading");
			        	}
			        }
					monitor.worked(1);
					monitor.done();
				}
            });
            
            if(status != 200){
                // there was a problem with the file upload so throw up an error
                // dialog to inform the user

            	failed = true;
            	
                // there was a problem with the file upload so throw up an error
                // dialog to inform the user
                MessageDialog.openError(null, "Error Getting User ID", 
                		"There was an error getting a user id: \n" +
                        "HTTP Response Code " + status + "\n" + 
                        "Please try again later");
            }
            else{
                resp = resp.substring(resp.indexOf(":")+1).trim();
                uid = Integer.parseInt(resp);
                MylarPlugin.getDefault().getPreferenceStore().setValue(MylarPlugin.USER_ID, uid);
                return uid;
            }
            
        } catch (Exception e){
            // there was a problem with the file upload so throw up an error
            // dialog to inform the user and log the exception
        	failed = true;
        	if(e instanceof NoRouteToHostException || e instanceof UnknownHostException){
	            MessageDialog.openError(null, "Error Uploading", 
	            		"There was an error getting a new user id: \n" +
	            		"No network connection.  Please try again later");
        	}else{
        		MessageDialog.openError(null, "Error Uploading", 
	            		"There was an error getting a new user id: \n" +
	            		e.getClass().getCanonicalName());
            	MylarPlugin.log(e, "error uploading");
        	}
        }
        return -1;
    }
    
    private String getData(InputStream i){
        String s = "";
        String data = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(i));
        try {
            while((s = br.readLine()) != null)
                    data += s;
        } catch (IOException e) {
        	MylarPlugin.log(e, "error uploading");
        }
        return data;
    }
    
    public int getUid(){
        return uid;
    }

	public boolean failed() {
		return failed;
	}


    private File zipFilesForUpload(){
    	MylarMonitorPlugin.setPerformingUpload(true);
   	 	MylarMonitorPlugin.getDefault().getInteractionLogger().stop();
//   	 	MylarMonitorPlugin.getDefault().stopLog();
   	 	
        List<File> files = new ArrayList<File>();
        files.add(monitorFile);
        
        File zipFile = new File(MylarPlugin.getDefault().getMylarDataDirectory() + "/mylarUpload.zip");
        
        try{
       	 createZipFile(zipFile, files);
        }catch(Exception e){
       	 MylarPlugin.log(e, "error uploading");
       	 return null;
        }

        return zipFile;
   }
	
	public static void createZipFile(File zipFile, List<File> files) throws FileNotFoundException, IOException{
        if(zipFile.exists()){
        	zipFile.delete();
        }
        
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
        
        for(File file: files){
        	try{
        		addZipEntry(zipOut, file);
        	}catch (Exception e){
        		MylarPlugin.log(e, "StatisticsUploadWizard");
        	}
        }
    
        // Complete the ZIP file
        zipOut.close();
	}
	
	private static void addZipEntry(ZipOutputStream zipOut, File file) throws FileNotFoundException, IOException {

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        
        // Compress the files
        FileInputStream in = new FileInputStream(file);

        // Add ZIP entry to output stream.
        zipOut.putNextEntry(new ZipEntry(file.getName()));

        // Transfer bytes from the file to the ZIP file
        int len;
        while ((len = in.read(buf)) > 0) {
        	zipOut.write(buf, 0, len);
        }

        // Complete the entry
        zipOut.closeEntry();
        in.close();
	}

}
