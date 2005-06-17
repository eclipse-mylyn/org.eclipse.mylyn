/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.bugzilla.core.internal.ProductConfiguration;
import org.eclipse.mylar.bugzilla.core.internal.ProductConfigurationFactory;
import org.eclipse.mylar.bugzilla.favorites.FavoritesFile;
import org.eclipse.mylar.bugzilla.offlineReports.OfflineReportsFile;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * @author kvesik
 *
 * Created on Mar 26, 2003
 */

/**
 * The main plugin class to be used in the desktop.
 */
public class BugzillaPlugin extends AbstractUIPlugin {
	
	/** Singleton instance of the plug-in */
	private static BugzillaPlugin plugin;
		
	/** The file that contains all of the bugzilla favorites */
	private FavoritesFile favoritesFile;
	
	/** The file that contains all of the offline bug reports */
	private OfflineReportsFile offlineReportsFile;
	
	/** Product configuration for the current server */
	private ProductConfiguration productConfiguration;
	
	/**
	 * Constructor
	 * @param descriptor passed in when the plugin is loaded
	 */
	public BugzillaPlugin() 
	{
		super();
	}
	
	/**
	 * Get the singleton instance for the plugin
	 * 
	 * @return The instance of the plugin
	 */
	public static BugzillaPlugin getDefault() 
	{
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		plugin = this;
		readFavoritesFile();
		readOfflineReportsFile();
		readCachedProductConfiguration();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}


	/**
	 * Get the favorites file contatining the favorites
	 * 
	 * @return The FavoritesFile
	 */
	public FavoritesFile getFavorites()
	{
		return favoritesFile;
	}

	/**
	 * Get the OfflineReports file contatining the offline bug reports
	 * 
	 * @return The OfflineReportsFile
	 */
	public OfflineReportsFile getOfflineReports()
	{
		return offlineReportsFile;
	}

	/**
	 * Get the name of the bugzilla server
	 * 
	 * @return A string containing the prefered name of the bugzilla server
	 */
	public String getServerName() 
	{
		return plugin.getPreferenceStore().getString(IBugzillaConstants.BUGZILLA_SERVER);		
	}
	
	/**
	 * Get the most recent query key for the preferences
	 * 
	 * @return A string containing the most recent query key
	 */
	public static String getMostRecentQuery() 
	{
		return plugin.getPreferenceStore().getString(IBugzillaConstants.MOST_RECENT_QUERY);
	}

	/**
	 * Returns the current product configuration or <code>null</code> if it is unknown.
	 */
	public ProductConfiguration getProductConfiguration() {
		return productConfiguration;
	}

	/**
	 * Sets the current product configuration.
	 */
	protected void setProductConfiguration(ProductConfiguration productConfiguration) {
		this.productConfiguration = productConfiguration;
	}

	/**
	 * Reads cached product configuration and stores it in the <code>productConfiguration</code> field.
	 */
	private void readFavoritesFile() {
		IPath favoritesPath = getFavoritesFile();

		try {
			favoritesFile = new FavoritesFile(favoritesPath.toFile());
		} catch (Exception e) {
		    logAndShowExceptionDetailsDialog(e, "occurred while restoring saved Bugzilla favorites.", "Bugzilla Favorites Error");
		}
	}

	/**
	 * Reads cached product configuration and stores it in the <code>productConfiguration</code> field.
	 */
	private void readOfflineReportsFile() {
		IPath offlineReportsPath = getOfflineReportsFile();

		try {
			offlineReportsFile = new OfflineReportsFile(offlineReportsPath.toFile());
		} catch (Exception e) {
		    logAndShowExceptionDetailsDialog(e, "occurred while restoring saved offline Bugzilla reports.", "Bugzilla Offline Reports Error");
		}
	}

	/**
	 * Returns the path to the file cacheing the query favorites.
	 */
	private IPath getFavoritesFile() {
		IPath stateLocation = Platform.getPluginStateLocation(BugzillaPlugin.getDefault());
		IPath configFile = stateLocation.append("favorites");
		return configFile;
	}
	
	/**
	 * Returns the path to the file cacheing the offline bug reports.
	 */
	private IPath getOfflineReportsFile() {
		IPath stateLocation = Platform.getPluginStateLocation(BugzillaPlugin.getDefault());
		IPath configFile = stateLocation.append("offlineReports");
		return configFile;
	}
	
	/**
	 * Reads cached product configuration and stores it in the <code>productConfiguration</code> field.
	 */
	private void readCachedProductConfiguration() {
		IPath configFile = getProductConfigurationCachePath();

		try {
			productConfiguration = ProductConfigurationFactory.getInstance().readConfiguration(configFile.toFile());
		} catch (IOException ex) {
			try {
				log(ex);
				productConfiguration = ProductConfigurationFactory.getInstance().getConfiguration(getServerName());
			} catch (IOException e) {
				log(e);
				MessageDialog.openInformation(null, "Bugzilla product attributes check", 
						"An error occurred while restoring saved Bugzilla product attributes: \n\n" +
						ex.getMessage() + 
						"\n\nUpdating them from the server also caused an error:\n\n" +
						e.getMessage() + 
						"\n\nCheck the server URL in Bugzila preferences.\n" +
						"Offline submission of new bugs will be disabled until valid product attributes have been loaded.");
			}
		}
	}

	/**
	 * Returns the path to the file cacheing the product configuration.
	 */
	protected IPath getProductConfigurationCachePath() {
		IPath stateLocation = Platform.getPluginStateLocation(BugzillaPlugin.getDefault());
		IPath configFile = stateLocation.append("productConfig");
		return configFile;
	}

	@Override
	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
		BugzillaPreferences.initDefaults(store);
	}
	
	/**
	 * Convenience method for logging statuses to the plugin log
	 * 
	 * @param status the status to log
	 */
	public static void log(IStatus status) 
	{
		getDefault().getLog().log(status);
	}
	
	/**
	 * Convenience method for logging exceptions to the plugin log
	 * @param e the exception to log
	 */
	public static void log(Exception e) {
		log(new Status(Status.ERROR, IBugzillaConstants.PLUGIN_ID, 0, e.getMessage(), e));
	}
	
	
	/**
	 * Returns the path to the file caching bug reports created while offline.
	 */
	protected IPath getCachedBugReportPath(){
		IPath stateLocation = Platform.getPluginStateLocation(BugzillaPlugin.getDefault());
		IPath bugFile = stateLocation.append("bugReports");
		return bugFile;
	}
	
	/**
	 * Logs the exception and shows an error dialog with exception details shown in a "Details" pane.
     * @param e
     * 		exception to be shown in the details pane
     * @param message
     * 		message to be used in the dialog
     * @param title
     * 		error dialog's title
     */
    public IStatus logAndShowExceptionDetailsDialog(Exception e, String message, String title) {
        MultiStatus status = new MultiStatus( IBugzillaConstants.PLUGIN_ID, 
                IStatus.ERROR, e.getClass().toString() 
                + " " + message + "\n\n" 
                + "Click Details or see log for more information.", e);
        Status s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + ":  ", e);
        status.add(s);
        String error = (e.getMessage() == null)?e.getClass().toString():e.getMessage();
        s = new Status (IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, error, e);
        status.add(s);
        log(status);
        ErrorDialog.openError(null, title, null, status);
        return status;
    }

	/**
	 * @return a list of the BugReports saved offline.
	 */
	public List<IBugzillaBug> getSavedBugReports() {
		return offlineReportsFile.elements();
	}
}
