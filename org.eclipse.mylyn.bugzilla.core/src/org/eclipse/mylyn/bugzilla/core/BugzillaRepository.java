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
package org.eclipse.mylar.bugzilla.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.bugzilla.core.internal.BugParser;
import org.eclipse.mylar.bugzilla.core.internal.NewBugParser;
import org.eclipse.mylar.bugzilla.core.internal.ProductParser;
import org.eclipse.mylar.bugzilla.core.offline.OfflineReportsFile;


/**
 * Singleton class that creates <code>BugReport</code> objects by fetching
 * bug's state and contents from the Bugzilla server.
 */
public class BugzillaRepository 
{
	
	/**
	 * Test method.
	 */
	public static void main(String[] args) throws Exception {
		instance =
			new BugzillaRepository(BugzillaPlugin.getDefault().getServerName() + "/long_list.cgi?buglist=");
		BugReport bug = instance.getBug(16161);
		System.out.println("Bug " + bug.getId() + ": " + bug.getSummary());
		for (Iterator<Attribute> it = bug.getAttributes().iterator(); it.hasNext();) {
			Attribute attribute = it.next();
			System.out.println(attribute.getName() + ": " + attribute.getValue());
		}
		System.out.println(bug.getDescription());
		for (Iterator<Comment> it = bug.getComments().iterator(); it.hasNext();) {
			Comment comment = it.next();
			System.out.println(comment.getAuthorName() + "<" + comment.getAuthor() + "> (" + comment.getCreated() + ")");
			System.out.print(comment.getText());
			System.out.println();
		}
	}

	/** URL of the Bugzilla server */
	private static String bugzillaUrl;

	/** singleton instance */
	private static BugzillaRepository instance;
		
	/**
	 * Constructor
	 * @param bugzillaUrl - the url of the bugzilla repository
	 */
	private BugzillaRepository(String bugzillaUrl) 
	{
		BugzillaRepository.bugzillaUrl = bugzillaUrl;
	}

	/**
	 * Get the singleton instance of the <code>BugzillaRepository</code>
	 * @return The instance of the repository
	 */
	public synchronized static BugzillaRepository getInstance() 
	{
		if (instance == null) 
		{
			// if the instance hasn't been created yet, create one
			instance = new BugzillaRepository(
							BugzillaPlugin.getDefault().getServerName());
		}
		
		// fix bug 58 by updating url if it changes
		if(! BugzillaRepository.bugzillaUrl.equals(BugzillaPlugin.getDefault().getServerName()))
		{
			BugzillaRepository.bugzillaUrl = BugzillaPlugin.getDefault().getServerName();
		}
		
		return instance;
	}
	
	/**
	 * Get a bug from the server
	 * @param id - the id of the bug to get
	 * @return - a <code>BugReport</code> for the selected bug or null if it doesn't exist
	 * @throws IOException
	 */
	public BugReport getBug(int id) throws IOException, MalformedURLException, LoginException
	{

		BufferedReader in = null;
		try {
			
			// create a new input stream for getting the bug
			
			String url = bugzillaUrl + "/show_bug.cgi?id=" + id;
	
			// allow the use to only see the operations that they can do to a bug if they have
			// their user name and password in the preferences
			if(BugzillaPreferences.getUserName() != null && !BugzillaPreferences.getUserName().equals("") && BugzillaPreferences.getPassword() != null && !BugzillaPreferences.getPassword().equals(""))
			{
				/*
				 * The UnsupportedEncodingException exception for
				 * URLEncoder.encode() should not be thrown, since every
				 * implementation of the Java platform is required to support
				 * the standard charset "UTF-8"
				 */
				url += "&GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(BugzillaPreferences.getUserName(), "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(BugzillaPreferences.getPassword(), "UTF-8");
			}
			
			URL bugUrl = new URL(url);
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(bugUrl);
			if(cntx != null){
				InputStream input = cntx.getInputStream();
				if(input != null) {
					in = new BufferedReader(new InputStreamReader(input));
		
					// get the actual bug fron the server and return it
					BugReport bug = BugParser.parseBug(in, id, BugzillaPlugin.getDefault().getServerName(), BugzillaPreferences.is218(), BugzillaPreferences.getUserName(), BugzillaPreferences.getPassword());
					return bug; 
				}
			}
			// TODO handle the error
			return null;
		} 
		catch (MalformedURLException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		catch(LoginException e)
		{
			throw e;
		}
		catch(Exception e) {
			// throw an exception if there is a problem reading the bug from the server
			throw new IOException(e.getMessage());
		}
		finally
		{
			try{
				if(in != null)
					in.close();
			}catch(IOException e)
			{
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID,IStatus.ERROR,"Problem closing the stream", e));
			}
		}
	}
	
	/**
	 * Get a bug from the server. 
	 * If a bug with the given id is saved offline, the offline version is returned instead.
	 * @param id - the id of the bug to get
	 * @return - a <code>BugReport</code> for the selected bug or null if it doesn't exist
	 * @throws IOException, MalformedURLException, LoginException
	 */
	public BugReport getCurrentBug(int id) throws MalformedURLException, LoginException, IOException {
		// Look among the offline reports for a bug with the given id.
		OfflineReportsFile reportsFile = BugzillaPlugin.getDefault().getOfflineReports();
		int offlineId = reportsFile.find(id);
		
		// If an offline bug was found, return it if possible.
		if (offlineId != -1) {
			IBugzillaBug bug = reportsFile.elements().get(offlineId);
			if (bug instanceof BugReport) {
				return (BugReport)bug;
			}
		}
		
		// If a suitable offline report was not found, try to get one from the server.
		return getBug(id);
	}
	
	/**
	 * Get the list of products when creating a new bug
	 * @return The list of valid products a bug can be logged against
	 * @throws IOException
	 */
	public List<String> getProductList() throws IOException, LoginException, Exception
	{
		BufferedReader in = null;
		try 
		{
			// connect to the bugzilla server
			String urlText = "";

			// use the usename and password to get into bugzilla if we have it
			if(BugzillaPreferences.getUserName() != null && !BugzillaPreferences.getUserName().equals("") && BugzillaPreferences.getPassword() != null && !BugzillaPreferences.getPassword().equals(""))
			{
				/*
				 * The UnsupportedEncodingException exception for
				 * URLEncoder.encode() should not be thrown, since every
				 * implementation of the Java platform is required to support
				 * the standard charset "UTF-8"
				 */
				urlText += "?GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(BugzillaPreferences.getUserName(), "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(BugzillaPreferences.getPassword(), "UTF-8");
			}

			URL url = new URL(bugzillaUrl + "/enter_bug.cgi"+urlText);

			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(url);
			if(cntx != null){
				InputStream input = cntx.getInputStream();
				if(input != null) {
			
					// create a new input stream for getting the bug
					in = new BufferedReader(new InputStreamReader(input));
				
					return new ProductParser(in).getProducts();
				}
			}
			return null;
		}
		finally
		{
			try{
				if(in != null)
					in.close();
			}catch(IOException e)
			{
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID,IStatus.ERROR,"Problem closing the stream", e));
			}
		}
	}
	
	/**
	 * Get the attribute values for a new bug
	 * @param nbm A reference to a NewBugModel to store all of the data
	 * @throws Exception
	 */
	public void getnewBugAttributes(NewBugModel nbm, boolean getProd) throws Exception
	{
		BufferedReader in = null;
		try 
		{
			// create a new input stream for getting the bug
			String prodname = URLEncoder.encode(nbm.getProduct(), "UTF-8");
			
			String url = bugzillaUrl + "/enter_bug.cgi";
			
			// use the proper url if we dont know the product yet
			if(!getProd)
				url += "?product=" + prodname + "&";
			else
				url += "?"; 
				
			// add the password and username to the url so that bugzilla logs us in
			/*
			 * The UnsupportedEncodingException exception for
			 * URLEncoder.encode() should not be thrown, since every
			 * implementation of the Java platform is required to support
			 * the standard charset "UTF-8"
			 */
			url += "&GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(BugzillaPreferences.getUserName(), "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(BugzillaPreferences.getPassword(), "UTF-8");
			
			URL bugUrl = new URL(url);
			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(bugUrl);
			if(cntx != null){
				InputStream input = cntx.getInputStream();
				if(input != null) {	
					in = new BufferedReader(new InputStreamReader(input));

					new NewBugParser(in).parseBugAttributes(nbm, getProd);
				}
			}
			
		} catch(Exception e) {
			
			if ( e instanceof KeyManagementException || e instanceof NoSuchAlgorithmException || e instanceof IOException ){
			 	if(MessageDialog.openQuestion(null, "Bugzilla Connect Error", "Unable to connect to Bugzilla server.\n" +
				   "Bug report will be created offline and saved for submission later.")){
			 		nbm.setConnected(false);
			 		getProdConfigAttributes(nbm);
			 	}
			 	else
			 		throw new Exception("Bug report will not be created.");
			 }
			 else
			 	throw e;
		}
		finally
		{
			try{
				if(in != null)
					in.close();
			}catch(IOException e)
			{
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID,IStatus.ERROR,"Problem closing the stream", e));
			}
		}
	}
	
	/**
	 * Get the bugzilla url that the repository is using
	 * @return A <code>String</code> containing the url of the bugzilla server
	 */
	public static String getURL()
	{
		return bugzillaUrl;
	}
	
	
	/** Method to get attributes from ProductConfiguration if unable to connect
	 *  to Bugzilla server
	 * @param model - the NewBugModel to store the attributes
	 */
	public void getProdConfigAttributes(NewBugModel model){
		
		HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();
		
		// ATTRIBUTE: Severity
		Attribute a = new Attribute("Severity");
		a.setParameterName("bug_severity");
		// get optionValues from ProductConfiguration
		String[] optionValues = BugzillaPlugin.getDefault().getProductConfiguration().getSeverities();			
		// add option values from ProductConfiguration to Attribute optionValues
		for( int i=0; i<optionValues.length; i++ ){
			a.addOptionValue(optionValues[i], optionValues[i]);
		}								
		// add Attribute to model
		attributes.put("severites", a);
		
		// ATTRIBUTE: OS
		a = new Attribute("OS");
		a.setParameterName("op_sys");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration().getOSs();
		for( int i=0; i<optionValues.length; i++ ){
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("OSs", a);
	
		// ATTRIBUTE: Platform
		a = new Attribute("Platform");
		a.setParameterName("rep_platform");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration().getPlatforms();
		for( int i=0; i<optionValues.length; i++ ){
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("platforms",a);
		
		// ATTRIBUTE: Version
		a = new Attribute("Version");
		a.setParameterName("version");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration().getVersions(model.getProduct());
		for( int i=0; i<optionValues.length; i++ ){
			a.addOptionValue(optionValues[i], optionValues[i]);						
		}
		attributes.put("versions", a);

		// ATTRIBUTE: Component
		a = new Attribute("Component");
		a.setParameterName("component");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration().getComponents(model.getProduct());			
		for( int i=0; i<optionValues.length; i++ ){
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
		attributes.put("components", a);

		// ATTRIBUTE: Priority
		a = new Attribute("Priority");
		a.setParameterName("bug_severity");
		optionValues = BugzillaPlugin.getDefault().getProductConfiguration().getPriorities();
		for( int i=0; i<optionValues.length; i++ ){
			a.addOptionValue(optionValues[i], optionValues[i]);
		}
							
		// set NBM Attributes (after all Attributes have been created, and added to attributes map)
		model.attributes = attributes;
	}
	
	public static String getBugUrl(int id) {		
		String url = BugzillaPlugin.getDefault().getServerName() + "/show_bug.cgi?id=" + id;
		try {
			if (BugzillaPreferences.getUserName() != null
					&& !BugzillaPreferences.getUserName().equals("")
					&& BugzillaPreferences.getPassword() != null
					&& !BugzillaPreferences.getPassword().equals("")) {

				url += "&GoAheadAndLogIn=1&Bugzilla_login="
						+ URLEncoder.encode(BugzillaPreferences.getUserName(), "UTF-8")
						+ "&Bugzilla_password="
						+ URLEncoder.encode(BugzillaPreferences.getPassword(),"UTF-8");
			}
		} catch (UnsupportedEncodingException e) {		
			return "";
		}
		return url;
	}
	
	public static String getBugUrlWithoutLogin(int id) {		
		String url = BugzillaPlugin.getDefault().getServerName() + "/show_bug.cgi?id=" + id;
		return url;
	}
}
