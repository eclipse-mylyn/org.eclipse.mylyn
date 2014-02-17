/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements some utility for the Gerrit servers.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the server selection
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.internal.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.mylyn.gerrit.dashboard.GerritPlugin;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;


/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 *
 */
@SuppressWarnings("restriction")
public class GerritServerUtility {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field GERRIT_PORT. (value is "":29418"")
	 */
	private static final String GERRIT_PORT = ":29418"; 

	/**
	 * Field AT. (value is ""@"")
	 */
	private static final String AT = "@"; 

	/**
	 * Field AT. (value is ""https://"")
	 */
	private static final String HTTPS = "https://"; 
	
	/**
	 * Field LAST_GERRIT_FILE. (value is ""lastGerrit.txt"")
	 */
	private static final String LAST_GERRIT_FILE = "lastGerrit.txt";

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private static GerritServerUtility instance = null;

	private static Map<TaskRepository, String> fResultTask = new HashMap<TaskRepository,String>();
	
	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	public GerritServerUtility() {
		instance = this;
		mapConfiguredGerritServer();
		
		//LATER: Map the workspace gerrit to the menu option
		//addWorkspaceGerritRepo();
		
		//Begin Test
		//testTaskRepo(); not needed anymore
		//End Test
	}

	// ------------------------------------------------------------------------
	// Methods Private
	// ------------------------------------------------------------------------
	
	/**
	 * Map the configured Gerrit server found in the TaskList
	 * @return Map<TaskRepository, String>
	 */
	private Map<TaskRepository, String> mapConfiguredGerritServer () {
		//Reset the list of Gerrit server
		fResultTask.clear();
		
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		
		//Only get the TaskRepository related to Gerrit review connnector
		GerritPlugin.Ftracer.traceInfo("--------Review repo ---------------");
		Set<TaskRepository> reviewRepo = repositoryManager.getRepositories(GerritConnector.CONNECTOR_KIND);
		for (TaskRepository taskRepo: reviewRepo) {
		    GerritPlugin.Ftracer.traceInfo("Add Gerrit Review repo: " + taskRepo.getRepositoryLabel() + "\t url: " + taskRepo.getRepositoryUrl());
			fResultTask.put(taskRepo, taskRepo.getRepositoryUrl());
			if (null != taskRepo.getRepositoryUrl()  ) {
				adjustTemplatemanager(taskRepo);			
			}
		}
		//Print a the end the info for all Gerrit 
		printRepositoryTemplate();
		return fResultTask;
	}
	
	/**
	 * Build a list of Gerrit server to display in the combo box in the dialogue window
	 * @param aTaskRepo
	 */
	private void adjustTemplatemanager (TaskRepository aTaskRepo) {
		RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		//Verify to only add once in the repository template
		Boolean found = false;
	//	printTaskRepository(aTaskRepo);
		for (RepositoryTemplate template : templateManager.getTemplates(GerritConnector.CONNECTOR_KIND)) {
			String convertedRemoteURL = aTaskRepo.getRepositoryUrl() ;
			GerritPlugin.Ftracer.traceInfo("\t template.label: " + template.label
					+ "\t repo label: " + aTaskRepo.getRepositoryLabel() +" repo getname: " + convertedRemoteURL );
			//Test the name and the remoteURL to reduce duplications
			if (template.label.equals(aTaskRepo.getRepositoryLabel()) ||
			    template.repositoryUrl.equals(convertedRemoteURL) ) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			//Set each parameter of the Gerrit server
			String userName = aTaskRepo.getUserName();
			Boolean anonymous = (userName != null &&  !userName.isEmpty()) ? false: true;
			
			//Create a repository template
			RepositoryTemplate templateTest = new RepositoryTemplate(aTaskRepo.getRepositoryLabel(), 
					aTaskRepo.getRepositoryUrl(),
					aTaskRepo.getCharacterEncoding(),
					aTaskRepo.getVersion(),
					"", "", "", 
					aTaskRepo.getUserName(), anonymous, true);
			
			//Set the attributes 
			Map<String, String> attributes = aTaskRepo.getProperties();
			
			Set<Entry<String, String>> value = attributes.entrySet();
			for ( Map.Entry <String, String> entry: value){
				templateTest.addAttribute(entry.getKey(), entry.getValue());
			}
			templateManager.addTemplate(GerritConnector.CONNECTOR_KIND, templateTest);
			
		}
	}
	
	private void printRepositoryTemplate() {
		RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		for (RepositoryTemplate template : templateManager.getTemplates(GerritConnector.CONNECTOR_KIND)) {
		    GerritPlugin.Ftracer.traceInfo("------------======================------------------");
			 Set<Entry<String, String>> value = template.getAttributes().entrySet();
			for (Map.Entry <String, String> entry: value) {
			    GerritPlugin.Ftracer.traceInfo("key: " + entry.getKey() + "\tvalue: " +
						entry.getValue());
			}
		}
	}

	private void printTaskRepository(TaskRepository aTask) {
		Set<Entry<String, String>> value = aTask.getProperties().entrySet();
		for (Map.Entry<String, String> entry : value) {
		    GerritPlugin.Ftracer.traceInfo("TaskRepo key: " + entry.getKey()
					+ "\tvalue: " + entry.getValue());
		}
		GerritPlugin.Ftracer.traceInfo(" UserName: " + aTask.getUserName());
		GerritPlugin.Ftracer
				.traceInfo("===================================");
	}

	/**
	 * This method use the Gerrit from the git server in the workspace
	 */
    private void addWorkspaceGerritRepo () {
		RepositoryUtil repoUtil = org.eclipse.egit.core.Activator.getDefault().getRepositoryUtil();
		List<String> repoPaths = repoUtil.getConfiguredRepositories();
		RepositoryCache repositoryCache = org.eclipse.egit.core.Activator.getDefault().getRepositoryCache();
        Repository repo = null;
		
		for (String repoPath : repoPaths) {
		    GerritPlugin.Ftracer.traceInfo("List Gerrit repository: " + repoPath );
			File gitDir = new File(repoPath);
			if (!gitDir.exists()) {
			    GerritPlugin.Ftracer.traceInfo("Gerrit repository do not exist: " + gitDir.getPath());
				continue;		
			}
			try {
				repo = repositoryCache.lookupRepository(gitDir);
				GerritPlugin.Ftracer.traceInfo("\trepository config after lookup: " +
						repo.getConfig());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (repo != null) {
				Config config  = new Config(repo.getConfig());
				//Look to get the remotes URL
				Set<String> remotes = config.getSubsections(ConfigConstants.CONFIG_REMOTE_SECTION);
				for (String remote: remotes) {
					String remoteURL = config.getString(ConfigConstants.CONFIG_REMOTE_SECTION,
							remote,
							ConfigConstants.CONFIG_KEY_URL);
					GerritPlugin.Ftracer.traceInfo("\t\t " + remote +" -> remoteURL: " + remoteURL );
					
					//Test if this is a Gerrit server and add it to the Dialogue combo
					String convertedRemoteURL = getReformatGerritServer(remoteURL) ;
					if (null != convertedRemoteURL  ) {
						TaskRepository taskRepo =  new TaskRepository(GerritConnector.CONNECTOR_KIND, convertedRemoteURL);
						taskRepo.setRepositoryLabel(convertedRemoteURL);
						fResultTask.put(taskRepo, taskRepo.getRepositoryUrl());
						adjustTemplatemanager(taskRepo);
						
					}
				}			
			}
		}
	}
	

	//Note the Gerrit server for "git.eclipse.org" in config is 
	//      not the same as in the task Repository: "git.eclipse.org/r"
	/**
	 * Verify if the gerrit remote URL has the gerrit port (29418 )
	 * @param aRemoteURL
	 * @return String remote converted URL
	 */
	private String getReformatGerritServer(String aRemoteURL) {
		//Test if this is a Gerrit server or not
		String[] strParsePort = aRemoteURL.split(GERRIT_PORT);
		if (strParsePort.length == 2) {
			//Do not convert it for now
			return aRemoteURL;
//			//We found a Gerrit server, lets build the URL
//			//String[] strParseServer = strParsePort[0].split(AT);
//			int index = strParsePort[0].indexOf(AT);
//			String server = strParsePort[0].substring(++index);
//			StringBuilder sb = new StringBuilder();
//			sb.append(HTTPS);
//			sb.append(server);
//			return sb.toString();
		}
		return null;
	}

	private File getLastGerritFile () {
		IPath ipath = GerritUi.getDefault().getStateLocation();
		String fileName = ipath.append(LAST_GERRIT_FILE).toPortableString();
		File file = new File (fileName);
		return file;
	}
	
	// ------------------------------------------------------------------------
	// Methods Public
	// ------------------------------------------------------------------------
	public static GerritServerUtility getDefault () {
		if (instance == null) {
			new GerritServerUtility();
		}
		return instance;
	}
	
	/**
	 * Return the mapping of the available Gerrit server used in the user workspace
	 * @return Map<Repository, String>
	 */
	public static Map<TaskRepository, String> getGerritMapping () {
		return fResultTask;
	}
	
	/**
	 * Save the selected Gerrit server URL
	 * @param aURL
	 * @return Boolean
	 */
	public Boolean saveLastGerritServer (String aURL) {
		Boolean ok = true;
		File file = getLastGerritFile();
		try {
			FileWriter fw= new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(aURL);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			ok = false;
		}
		
		return ok;
	}
	
	/**
	 * Return the last selected Gerrit server used
	 * @return String
	 */
	public String getLastSavedGerritServer () {
		String lastGerritURL = null;
		File file = getLastGerritFile();
		if (file != null) {
			try {
				FileReader fr= new FileReader(file);
				BufferedReader in = new BufferedReader(fr);
				lastGerritURL = in.readLine();
				in.close();
			} catch (IOException e1) {
				//When there is no file, 
				//e1.printStackTrace();
			}			
		}
		return lastGerritURL;
	}
	
	/**
	 * Get the Gerrit URL based on the provided string
	 * 
	 * @param  Menu string aSt
	 * @return URL as a string
	 * 
	 */
	public String getMenuSelectionURL (String aSt) {
		String urlStr = null;
		if (!fResultTask.isEmpty()) {
			Set<TaskRepository> mapSet = fResultTask.keySet();
			GerritPlugin.Ftracer.traceInfo("-------------------");
			for (TaskRepository key: mapSet) {
				if (key.getRepositoryLabel().equals(aSt)) {
					urlStr = fResultTask.get(key);
					
					GerritPlugin.Ftracer.traceInfo("Map Key: " + key.getRepositoryLabel() + "\t URL: " + fResultTask.get(key));
					return urlStr;
				}
			}
		}
		
		return urlStr;
	}

	/**
	 * Get the Gerrit task Repository
	 * 
	 * @param  string aSt
	 * @return TaskRepository
	 * 
	 */
	public TaskRepository getTaskRepo (String aStURL) {
		
		if (aStURL != null && !fResultTask.isEmpty()) {
			Set<TaskRepository> mapSet = fResultTask.keySet();
			GerritPlugin.Ftracer.traceInfo("-------------------");
			for (TaskRepository key: mapSet) {
				if (key.getRepositoryUrl().equals(aStURL)) {
					
				    GerritPlugin.Ftracer.traceInfo("Key label : " + key.getRepositoryLabel() + "\t URL: " + fResultTask.get(key));
					return key;
				}
			}
		}
		
		return null;
	}

	/**
	 * Read the Gerrit server to populate the list of reviews
	 */
	public void getReviewListFromServer () {
		//Get the Gerrit URL to query
		String urlToUsed = getLastSavedGerritServer ();
		
		if (urlToUsed != null) {
			//Initiate the request to populate the list of Reviews
		    GerritPlugin.Ftracer.traceInfo("use the following Gerrit URL to populate the list of reviews: " +  urlToUsed);
			
			// TODO: Make it pick the right repository
			Set<TaskRepository> gerritRepositories = fResultTask.keySet();
			Iterator<TaskRepository> it = gerritRepositories.iterator();
			if (it.hasNext()) {
	            TaskRepository repository = it.next();
	            // TODO: populate the Gerrit Dashboard with 'reviews'
			}
		} else {
			//Open the dialogue to populate a Gerrit server, Should not happen here
		    GerritPlugin.Ftracer.traceInfo("Need to open the dialogue to populate a gerrit server" ); 			
		}
	}

	/******************************************************************/
	/******************************************************************/
	/******************************************************************/
	/******************************************************************/
	/********  TEST   *************************************************/
	/******************************************************************/
	/******************************************************************/
	/******************************************************************/
	/******************************************************************/
	
	private void testTaskRepo () {
	//	TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
//		final TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "https://"); //$NON-NLS-1$
		
		final TaskRepository repository = getTaskRepository(); //$NON-NLS-1$
		GerritPlugin.Ftracer.traceInfo("repository:   " + repository.getUrl()); //$NON-NLS-1$
//		int ret = TasksUiUtil.openEditRepositoryWizard(repository); //Generate a null pointer for the workbench window
		
		
		GerritPlugin.Ftracer.traceInfo("Before: repository url:   " + repository.getUrl() ); //$NON-NLS-1$

	}
	
	/**
	 * Look at the current Gerrit repository and return a default value 
	 * i.e the first Gerrit if found ???
	 * @return TaskRepository
	 */
	private TaskRepository getTaskRepository () {
		TaskRepository taskRepo = null;
		/**
		 * Field DEFAULT_REPOSITORY. (value is ""https://repository"")
		 */
		String DEFAULT_REPOSITORY = "https://";
		//Reset the list of Gerrit server
		fResultTask.clear();


		//Test to read the TaskRepositories
		
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		
		//List all repositories in the the TaskRepositories view
		List <TaskRepository>listallRepo = repositoryManager.getAllRepositories();
		for (int i = 0;i < listallRepo.size(); i++) {
		    GerritPlugin.Ftracer.traceInfo("TaskRepositoryManager repository: [ " + i + " ] : " + listallRepo.get(i).getRepositoryLabel() );
		}
		
		//Only get the TaskRepository related to Gerrit review connnector
		GerritPlugin.Ftracer.traceInfo("--------Review repo ---------------");
		Set<TaskRepository> reviewRepo = repositoryManager.getRepositories(GerritConnector.CONNECTOR_KIND);
		for (TaskRepository tp: reviewRepo) {
		    GerritPlugin.Ftracer.traceInfo("Only Gerrit Review repo: " + tp.getRepositoryLabel() + "\t url: " + tp.getRepositoryUrl());
		}

		//Testing bugzilla but need to add the mylyn bugzilla in plugin dependencies
//		for (RepositoryTemplate template : templateManager.getTemplates(BugzillaCorePlugin.CONNECTOR_KIND)) {
//			GerritPlugin.Ftracer.traceInfo("Gerrit Bugzilla repository: " + template.label + "\t URL: " + template.repositoryUrl);
//		}
		
		return taskRepo;
	}

}
