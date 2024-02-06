/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Description:
 * 	This class implements some utility for the Gerrit servers.
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the server selection
 *   See git history
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.mylyn.gerrit.dashboard.GerritPlugin;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("restriction")
public class GerritServerUtility {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field LAST_GERRIT_FILE. (value is ""lastGerrit.txt"")
	 */
	private static final String LAST_GERRIT_FILE = "lastGerrit.txt"; //$NON-NLS-1$

	/**
	 * Field LAST_COMMANDS_FILE. (value is ""lastCommands.txt"")
	 */
	private static final String LAST_COMMANDS_FILE = "lastCommands.txt"; //$NON-NLS-1$

	/**
	 * Field ECLIPSE_GERRIT_DEFAULT. (value is ""https://git.eclipse.org/r/"")
	 */
	private final String ECLIPSE_GERRIT_DEFAULT = "https://git.eclipse.org/r/"; //$NON-NLS-1$

	/**
	 * Field SLASH. (value is ""/"")
	 */
	private final String SLASH = "/"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private static GerritServerUtility fInstance = null;

	private Map<TaskRepository, String> fResultTask = new HashMap<>();

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	protected GerritServerUtility() {
		fInstance = this;

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
	 * Build a list of Gerrit server to display in the combo box in the dialogue window
	 *
	 * @param aTaskRepo
	 */
	private void adjustTemplatemanager(TaskRepository aTaskRepo) {
		RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		//Verify to only add once in the repository template
		boolean found = false;
		//	printTaskRepository(aTaskRepo);
		for (RepositoryTemplate template : templateManager.getTemplates(GerritConnector.CONNECTOR_KIND)) {
			String convertedRemoteURL = aTaskRepo.getRepositoryUrl();
			GerritPlugin.Ftracer.traceInfo("\t template.label: " + template.label + "\t repo label: " //$NON-NLS-1$ //$NON-NLS-2$
					+ aTaskRepo.getRepositoryLabel() + " repo getname: " + convertedRemoteURL); //$NON-NLS-1$
			//Test the name and the remoteURL to reduce duplications
			if (template.label.equals(aTaskRepo.getRepositoryLabel())
					|| template.repositoryUrl.equals(convertedRemoteURL)) {
				found = true;
				break;
			}
		}

		if (!found) {
			//Set each parameter of the Gerrit server
			String userName = aTaskRepo.getUserName();
			Boolean anonymous = userName != null && !userName.isEmpty() ? false : true;

			//Create a repository template
			RepositoryTemplate templateTest = new RepositoryTemplate(aTaskRepo.getRepositoryLabel(),
					aTaskRepo.getRepositoryUrl(), aTaskRepo.getCharacterEncoding(), aTaskRepo.getVersion(), "", "", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					aTaskRepo.getUserName(), anonymous, true);

			//Set the attributes
			Map<String, String> attributes = aTaskRepo.getProperties();

			Set<Entry<String, String>> value = attributes.entrySet();
			for (Map.Entry<String, String> entry : value) {
				templateTest.addAttribute(entry.getKey(), entry.getValue());
			}
			templateManager.addTemplate(GerritConnector.CONNECTOR_KIND, templateTest);

		}
	}

	private void printRepositoryTemplate() {
		RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		if (templateManager != null) {
			for (RepositoryTemplate template : templateManager.getTemplates(GerritConnector.CONNECTOR_KIND)) {
				GerritPlugin.Ftracer.traceInfo("------------======================------------------"); //$NON-NLS-1$
				Set<Entry<String, String>> value = template.getAttributes().entrySet();
				if (value != null) {
					for (Map.Entry<String, String> entry : value) {
						GerritPlugin.Ftracer.traceInfo("key: " + entry.getKey() + "\tvalue: " + entry.getValue()); //$NON-NLS-1$//$NON-NLS-2$
					}
				}
			}
		}
	}

	/**
	 * Build and return the File storing the persistent data
	 *
	 * @param String
	 *            aFile
	 * @return File
	 */
	private File getLastGerritFile(String aFile) {
		IPath ipath = GerritPlugin.getDefault().getStateLocation();
		String fileName = ipath.append(aFile).toPortableString();
		File file = new File(fileName);
		return file;
	}

	/**
	 * Build a URL for Gerrit documentation
	 *
	 * @param aRequest
	 *            specific documentation
	 * @return URL complete URL fo the selected site based on the Gerrit server and version
	 * @throws MalformedURLException
	 */
	private URL buildDocumentationURL(String aRequest) throws MalformedURLException {
		StringBuilder sb = new StringBuilder();

		String lastSaved = getInstance().getLastSavedGerritServer();
		if (lastSaved == null) {
			//Use Default, so ECLIPSE_GERRIT_DEFAULT
			lastSaved = ECLIPSE_GERRIT_DEFAULT;
		}
		if (!lastSaved.endsWith(SLASH)) {
			lastSaved = lastSaved.concat(SLASH);
		}
		sb.append(lastSaved);
		sb.append(aRequest);
		return new URL(sb.toString());
	}

	/**
	 * Search for a similar page in the eclipse editor
	 *
	 * @param aUrl
	 * @return String
	 */
	private String getEditorId(URL aUrl) {
		//Try to get the editor id
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(aUrl.getFile());
		String id = null;
		if (desc != null) {
			id = desc.getId();
		}

		return id;
	}

	// ------------------------------------------------------------------------
	// Methods Public
	// ------------------------------------------------------------------------
	public static GerritServerUtility getInstance() {
		if (fInstance == null) {
			new GerritServerUtility();
		}
		return fInstance;
	}

	/**
	 * Return the mapping of the available Gerrit server used in the user workspace
	 *
	 * @return Map<Repository, String>
	 */
	public Map<TaskRepository, String> getGerritMapping() {
		if (fResultTask == null) {
			fResultTask = new HashMap<>();
		}

		//Reset the list of Gerrit server
		fResultTask.clear();

		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		if (repositoryManager != null) {
			//Only get the TaskRepository related to Gerrit review connnector
			GerritPlugin.Ftracer.traceInfo("--------Review repo ---------------"); //$NON-NLS-1$
			Set<TaskRepository> reviewRepo = repositoryManager.getRepositories(GerritConnector.CONNECTOR_KIND);
			if (reviewRepo != null) {
				for (TaskRepository taskRepo : reviewRepo) {
					GerritPlugin.Ftracer.traceInfo("Add Gerrit Review repo: " + taskRepo.getRepositoryLabel() //$NON-NLS-1$
					+ "\t url: " + taskRepo.getRepositoryUrl()); //$NON-NLS-1$
					fResultTask.put(taskRepo, taskRepo.getRepositoryUrl());
					if (null != taskRepo.getRepositoryUrl()) {
						adjustTemplatemanager(taskRepo);
					}
				}
			}
			//Print a the end the info for all Gerrit
			printRepositoryTemplate();

		}
		return fResultTask;
	}

	/**
	 * Save the selected Gerrit server URL
	 *
	 * @param aURL
	 * @return Boolean
	 */
	public Boolean saveLastGerritServer(String aURL) {
		boolean ok = true;
		File file = getLastGerritFile(LAST_GERRIT_FILE);
		try (FileWriter fw = new FileWriter(file); BufferedWriter out = new BufferedWriter(fw)) {

			out.write(aURL);
		} catch (IOException e1) {
			e1.printStackTrace();
			ok = false;
		}

		return ok;
	}

	/**
	 * Return the last selected Gerrit server used
	 *
	 * @return String
	 */
	public String getLastSavedGerritServer() {
		String lastGerritURL = null;
		File file = getLastGerritFile(LAST_GERRIT_FILE);
		if (file != null) {
			try (FileReader fr = new FileReader(file); BufferedReader in = new BufferedReader(fr)) {

				lastGerritURL = in.readLine();
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
	 * @param Menu
	 *            string aSt
	 * @return URL as a string
	 */
	public String getMenuSelectionURL(String aSt) {
		String urlStr = null;
		fResultTask = getGerritMapping();
		if (!fResultTask.isEmpty()) {
			Set<TaskRepository> mapSet = fResultTask.keySet();
			GerritPlugin.Ftracer.traceInfo("-------------------"); //$NON-NLS-1$
			for (TaskRepository key : mapSet) {
				if (key.getRepositoryLabel().equals(aSt)) {
					urlStr = fResultTask.get(key);

					GerritPlugin.Ftracer.traceInfo("Map Key: " + key.getRepositoryLabel() + "\t URL: " //$NON-NLS-1$//$NON-NLS-2$
							+ fResultTask.get(key));
					return urlStr;
				}
			}
		}

		return urlStr;
	}

	/**
	 * Get the Gerrit task Repository
	 *
	 * @param string
	 *            aSt
	 * @return TaskRepository
	 */
	public TaskRepository getTaskRepo(String aStURL) {
		fResultTask = getGerritMapping();
		if (aStURL != null && !fResultTask.isEmpty()) {
			Set<TaskRepository> mapSet = fResultTask.keySet();
			GerritPlugin.Ftracer.traceInfo("-------------------"); //$NON-NLS-1$
			for (TaskRepository key : mapSet) {
				if (key.getRepositoryUrl().equals(aStURL)) {

					GerritPlugin.Ftracer.traceInfo("Key label : " + key.getRepositoryLabel() + "\t URL: " //$NON-NLS-1$ //$NON-NLS-2$
							+ fResultTask.get(key));
					return key;
				}
			}
		}

		return null;
	}

	/**
	 * Open the web browser for the specific documentation
	 *
	 * @param String
	 *            aDocumentation requested documentation
	 */
	public void openWebBrowser(String aDocumentation) {
		if (fInstance == null) {
			fInstance = getInstance();
		}

		IWorkbenchBrowserSupport workBenchSupport = PlatformUI.getWorkbench().getBrowserSupport();
		URL url = null;
		try {
			url = buildDocumentationURL(aDocumentation);
			try {

				//Using NULL as a browser id will create a new editor each time,
				//so we need to see if there is already an editor for this help
				String id = getEditorId(url);
				workBenchSupport.createBrowser(id).openURL(url);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		GerritUi.Ftracer.traceInfo("openWebBrowser for " + url); //$NON-NLS-1$
	}

	/**
	 * Save the list of the last 5 commands
	 *
	 * @param LinkedHashSet
	 *            <String>
	 * @return Boolean
	 */
	public Boolean saveLastCommandList(Set<String> aCommands) {
		boolean ok = true;
		File file = getLastGerritFile(LAST_COMMANDS_FILE);
		try (FileWriter fw = new FileWriter(file); BufferedWriter out = new BufferedWriter(fw)) {

			for (String s : aCommands) {
				out.write(s);
				out.newLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			ok = false;
		}

		return ok;
	}

	/**
	 * Return the list of the last commands saved
	 *
	 * @return Set
	 */
	public Set<String> getListLastCommands() {
		LinkedHashSet<String> lastCommands = new LinkedHashSet<>();
		File file = getLastGerritFile(LAST_COMMANDS_FILE);
		if (file != null) {
			try (FileReader fr = new FileReader(file);
					BufferedReader in = new BufferedReader(fr)) {
				while (in.ready()) {
					String line = in.readLine();
					lastCommands.add(line);
				}
			} catch (IOException e1) {
				//When there is no file,
				//e1.printStackTrace();
			}
		}
		return lastCommands;
	}

}
