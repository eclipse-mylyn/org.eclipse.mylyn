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

package org.eclipse.mylar.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.util.IInteractionEventListener;
import org.eclipse.mylar.monitor.ui.wizards.UserStudySubmissionWizard;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.internal.core.UpdateCore;
import org.eclipse.update.internal.ui.security.Authentication;
import org.eclipse.update.internal.ui.security.UserValidationDialog;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarMonitorPlugin extends AbstractUIPlugin implements IStartup {

	public static String VERSION = "0.3.8";
	public static String UPLOAD_FILE_LABEL = "USAGE";

	public static final String ETHICS_FORM = "doc/study-ethics.html";
	public static final String UPLOAD_SERVER = "http://ws.cs.ubc.ca/~mylar/userStudy/";
	public static final String UPLOAD_SCRIPT_ID = "getUID.cgi";
	public static final String UPLOAD_SCRIPT = "upload.cgi";
	public static final String UPLAOD_SCRIPT_QUESTIONNAIRE = "questionnaire.cgi";
	
    private static final long HOUR = 3600*1000; 
	private static final long DELAY_BETWEEN_TRANSMITS = 6 * 24 * HOUR; 
	private static final long DELAY_ON_USER_REQUEST = 3 * HOUR;
	private static final long DELAY_ON_FAILURE = 5 * HOUR; 
	
	public static final String UI_PLUGIN_ID = "org.eclipse.mylar.ui";
    public static final String MONITOR_FILE_NAME = "workspace";

	public static final String PREF_NUM_USER_EVENTS = "org.eclipse.mylar.monitor.events.observed";
	public static final String PREF_PREVIOUS_TRANSMIT_DATE = "org.eclipse.mylar.monitor.upload.previousTransmit";
		
	public static final String PLUGIN_ID = "org.eclipse.mylar.monitor";
	public static final String OBFUSCATED_LABEL = "[obfuscated]";

    private InteractionEventLogger interactionLogger;
    private File logFile;	
    private PreferenceChangeMonitor preferenceMonitor;    
    private PerspectiveChangeMonitor perspectiveMonitor;
    private ActivityChangeMonitor activityMonitor;
    private MenuCommandMonitor menuMonitor;
    private WindowChangeMonitor windowMonitor;
    private static MylarMonitorPlugin plugin;
	private ResourceBundle resourceBundle;
	private static Date lastTransmit = null;
	private boolean notifiedOfUserIdSubmission = false;
    private Authentication uploadAuthentication = null;
    private static boolean performingUpload = false;
	private boolean questionnaireEnabled = true;
	private SelectionMonitor selectionMonitor;
    
	private ShellListener SHELL_LISTENER = new ShellListener() {		
		
        public void shellDeactivated(ShellEvent arg0) {
        	if(!isPerformingUpload() && MylarPlugin.getDefault() != null) {
        		for (IInteractionEventListener listener : MylarPlugin.getDefault().getInteractionListeners()) listener.stop();
        	}
        }
        
        public void shellActivated(ShellEvent arg0) { 
        	if (!MylarPlugin.getDefault().suppressWizardsOnStartup() && MylarPlugin.getDefault() != null) {
//        		checkForStudyPhasePromotion();
        		checkForStatisticsUpload(); 
        	} 
        	if(!isPerformingUpload() && MylarPlugin.getDefault() != null) {
        		for (IInteractionEventListener listener : MylarPlugin.getDefault().getInteractionListeners()) listener.start();
        	}
        }

		public void shellDeiconified(ShellEvent arg0) { }
		
        public void shellIconified(ShellEvent arg0) { }
        
        public void shellClosed(ShellEvent arg0) { }  
    };
 
    private IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty().equals(MylarPlugin.MYLAR_DIR)) {                
                if (event.getOldValue() instanceof String) {
                    stopLog();
                    File f = getLogFileLocation();
                    logFile.renameTo(f);
                    logFile = f;
                    startLog();

                    if(!isPerformingUpload()) {
                    	for (IInteractionEventListener listener : MylarPlugin.getDefault().getInteractionListeners()) listener.stop();
                    	interactionLogger.setOutputFile(getMonitorFile());
		                for (IInteractionEventListener listener : MylarPlugin.getDefault().getInteractionListeners()) listener.start();
                    }
                }
            } else {
            }
        }
    };
    
	public MylarMonitorPlugin() {
		plugin = this;		
	}
 
    /**
     * Used to start plugin on startup -> entry in plugin.xml to invoke this
     */
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {				
                interactionLogger = new InteractionEventLogger(getMonitorFile());
                interactionLogger.start();
                MylarPlugin.getDefault().addInteractionListener(interactionLogger);
                
                preferenceMonitor = new PreferenceChangeMonitor();
                perspectiveMonitor = new PerspectiveChangeMonitor();
                activityMonitor = new ActivityChangeMonitor();
                windowMonitor = new WindowChangeMonitor();
                
                MylarPlugin.getDefault().getCommandMonitors().add(new KeybindingCommandMonitor());
                selectionMonitor = new SelectionMonitor();
                MylarPlugin.getDefault().getSelectionMonitors().add(selectionMonitor);

                MylarPlugin.getContextManager().getActionExecutionListeners().add(new ActionExecutionMonitor());
                
				if (plugin.getPreferenceStore().contains(PREF_PREVIOUS_TRANSMIT_DATE)) {
					lastTransmit = new Date(plugin.getPreferenceStore().getLong(PREF_PREVIOUS_TRANSMIT_DATE));
				} else {
					lastTransmit = new Date();
					plugin.getPreferenceStore().setValue(PREF_PREVIOUS_TRANSMIT_DATE, lastTransmit.getTime());
				}
                workbench.getActiveWorkbenchWindow().getShell().addShellListener(SHELL_LISTENER);
                MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(PREFERENCE_LISTENER);

                MylarPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(preferenceMonitor);
                JavaPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(preferenceMonitor);
                WorkbenchPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(preferenceMonitor);
                EditorsPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(preferenceMonitor);
                PDEPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(preferenceMonitor);
                
                
                // To listen to perspective changes
                workbench.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveMonitor);
                
                // To listen to activity/capability changes
                workbench.getActivitySupport().getActivityManager().addActivityManagerListener(activityMonitor);
                
                // To listen to menu and toolbar selections
                menuMonitor = new MenuCommandMonitor();
                workbench.getDisplay().addFilter(SWT.Selection, menuMonitor);
                
                // To listen to windows opening and closing
                workbench.addWindowListener(windowMonitor);               
            	
                if (!MylarPlugin.getDefault().suppressWizardsOnStartup()) {
                	checkForFirstMonitorUse();
                }
            }
        });
    }

    @Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
        logFile = getLogFileLocation();
        startLog();
	} 
    
    private File getLogFileLocation() {
        return new File(MylarPlugin.getDefault().getMylarDataDirectory() + File.separator + MylarPlugin.LOG_FILE_NAME);
    }
    
    public void startLog() {
        try {
            MylarPlugin.getDefault().setLogStream(new PrintStream(new FileOutputStream(logFile, true)));
        } catch (FileNotFoundException e) {
            MylarPlugin.log("could not start log file", this);
        }
    }
    
    public void stopLog() {
    	MylarPlugin.getDefault().getLogStream().close();
    	MylarPlugin.getDefault().setLogStream(null);
    }
    
    public File getMonitorFile() {
        File file = new File(
                MylarPlugin.getDefault().getMylarDataDirectory()
                + File.separator
                + MONITOR_FILE_NAME
                + MylarContextManager.FILE_EXTENSION);
        
        if (!file.exists() || !file.canWrite())
			try {
				file.createNewFile();
			} catch (IOException e) {
				MylarPlugin.log(e, "could not create monitor file");
			}
        return file;
    }
    
    private long getUserTransimitDelay() {
        return DELAY_ON_USER_REQUEST / HOUR;
    }
    
    public void userCancelSubmitFeedback(Date currentTime, boolean wait3Hours){ 
    	if (wait3Hours) {
    		lastTransmit.setTime(currentTime.getTime() + DELAY_ON_USER_REQUEST - DELAY_BETWEEN_TRANSMITS);
    		plugin.getPreferenceStore().setValue(PREF_PREVIOUS_TRANSMIT_DATE, lastTransmit.getTime());
    	} else {
    		long day = HOUR * 24;
    		lastTransmit.setTime(currentTime.getTime() + day - DELAY_BETWEEN_TRANSMITS);
    		plugin.getPreferenceStore().setValue(PREF_PREVIOUS_TRANSMIT_DATE, lastTransmit.getTime());
    	}    	
    }
        
	/**
	 * This method is called when the plug-in is stopped
	 */
    @Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
		MylarPlugin.getDefault().getSelectionMonitors().remove(selectionMonitor);
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarMonitorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MylarMonitorPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylar.monitor.MonitorPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	public File getLogFile() {
		return logFile;
	}
	
	private void checkForFirstMonitorUse() {
		if (!notifiedOfUserIdSubmission && !MylarPlugin.getDefault().getPreferenceStore().contains(MylarPlugin.USER_ID)) {
			notifiedOfUserIdSubmission = true;
			UserStudySubmissionWizard wizard = new UserStudySubmissionWizard(false);
			wizard.init(PlatformUI.getWorkbench(), null);
			WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
			dialog.create();
			dialog.open();
		}
	}    
	
	private void checkForStatisticsUpload() {
		if (plugin == null || plugin.getPreferenceStore() == null) return;
		if (plugin.getPreferenceStore().contains(PREF_PREVIOUS_TRANSMIT_DATE)) {
			lastTransmit = new Date(plugin.getPreferenceStore().getLong(PREF_PREVIOUS_TRANSMIT_DATE));
		} else { 
			lastTransmit = new Date();
			plugin.getPreferenceStore().setValue(PREF_PREVIOUS_TRANSMIT_DATE, lastTransmit.getTime());
		}
		Date currentTime = new Date();        	
    	if (currentTime.getTime() > lastTransmit.getTime() + DELAY_BETWEEN_TRANSMITS) {        		
    		String ending = getUserTransimitDelay() == 1 ? "" : "s";        		
    		MessageDialog message = new MessageDialog(Display.getDefault().getActiveShell(),
    				"Send Usage Feedback",
    				null,
    				"Send Mylar Usage Statistics feedback now?",
    				MessageDialog.QUESTION,
    				new String[] { IDialogConstants.YES_LABEL,
    				"Remind me in " + getUserTransimitDelay() + " hour" + ending,
    				"Remind me tomorrow"},
                	0);
    		int result = 0;
    		if ((result = message.open()) == 0) {
    			// time must be stored right away into preferences, to prevent other threads
    			lastTransmit.setTime(new Date().getTime());
    			plugin.getPreferenceStore().setValue(PREF_PREVIOUS_TRANSMIT_DATE, currentTime.getTime());
				
				UserStudySubmissionWizard wizard = new UserStudySubmissionWizard();
				wizard.init(PlatformUI.getWorkbench(), null);
				// Instantiates the wizard container with the wizard and
				// opens it
				WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
				dialog.create();
				dialog.open();
				if (wizard.failed()) {
					lastTransmit.setTime(currentTime.getTime() + DELAY_ON_FAILURE - DELAY_BETWEEN_TRANSMITS);
					plugin.getPreferenceStore().setValue(PREF_PREVIOUS_TRANSMIT_DATE, currentTime.getTime());
				}								
    		} else {    
    			if (result == 1) {
    				userCancelSubmitFeedback(currentTime, true);
    			} else {
    				userCancelSubmitFeedback(currentTime, false);
    			}
    			
    			message.close();
    		}
    	}
	}
    
	public void incrementObservedEvents(int increment) {
		int numEvents = getPreferenceStore().getInt(PREF_NUM_USER_EVENTS);
		numEvents += increment;
		getPreferenceStore().setValue(PREF_NUM_USER_EVENTS, numEvents);
	}
    
    public void configureProxy(HttpClient httpClient) {
    	if (UpdateCore.getPlugin().getPluginPreferences().getBoolean(UpdateCore.HTTP_PROXY_ENABLE)) {
    		String proxyHost = UpdateCore.getPlugin().getPluginPreferences().getString(UpdateCore.HTTP_PROXY_HOST);
    		int proxyPort = UpdateCore.getPlugin().getPluginPreferences().getInt(UpdateCore.HTTP_PROXY_PORT);
    		httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);

    		if (uploadAuthentication == null) uploadAuthentication = UserValidationDialog.getAuthentication(
    				proxyHost, 
    				"(Leave fields blank if authentication is not required)");
    		if (uploadAuthentication != null) {
        		httpClient.getState().setProxyCredentials(
        			  new AuthScope(proxyHost, proxyPort),
      				  new UsernamePasswordCredentials(uploadAuthentication.getUser(), uploadAuthentication.getPassword()));
    		}
    	}
	}
    public static IPreferenceStore getPrefs() {
		return MylarMonitorPlugin.getDefault().getPreferenceStore();
	}

	public static boolean isPerformingUpload() {
		return performingUpload;
	}

	public static void setPerformingUpload(boolean performingUpload) {
		MylarMonitorPlugin.performingUpload = performingUpload;
	}

	public InteractionEventLogger getInteractionLogger() {
		return interactionLogger;
	}

	public boolean isQuestionnaireEnabled() {
		return questionnaireEnabled;
	}

	public void setQuestionnaireEnabled(boolean questionnaireEnabled) {
		this.questionnaireEnabled = questionnaireEnabled;
	}

	public String getUPLOAD_SERVER() {
		return UPLOAD_SERVER;
	}
}
