/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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

/**
 * This class implements some utility for the Gerrit servers.
 *
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

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private static GerritServerUtility instance = null;

	private static Map<TaskRepository, String> fResultTask = new HashMap<TaskRepository, String>();

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	public GerritServerUtility() {
		instance = this;
		mapConfiguredGerritServer();
	}

	// ------------------------------------------------------------------------
	// Methods Private
	// ------------------------------------------------------------------------

	/**
	 * Map the configured Gerrit server found in the TaskList
	 *
	 * @return Map<TaskRepository, String>
	 */
	private Map<TaskRepository, String> mapConfiguredGerritServer() {
		//Reset the list of Gerrit server
		fResultTask.clear();

		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();

		//Only get the TaskRepository related to Gerrit review connnector
		GerritPlugin.Ftracer.traceInfo("--------Review repo ---------------"); //$NON-NLS-1$
		Set<TaskRepository> reviewRepo = repositoryManager.getRepositories(GerritConnector.CONNECTOR_KIND);
		for (TaskRepository taskRepo : reviewRepo) {
			GerritPlugin.Ftracer.traceInfo("Add Gerrit Review repo: " + taskRepo.getRepositoryLabel() + "\t url: " //$NON-NLS-1$ //$NON-NLS-2$
					+ taskRepo.getRepositoryUrl());
			fResultTask.put(taskRepo, taskRepo.getRepositoryUrl());
			if (null != taskRepo.getRepositoryUrl()) {
				adjustTemplatemanager(taskRepo);
			}
		}
		//Print a the end the info for all Gerrit
		printRepositoryTemplate();
		return fResultTask;
	}

	/**
	 * Build a list of Gerrit server to display in the combo box in the dialogue window
	 *
	 * @param aTaskRepo
	 */
	private void adjustTemplatemanager(TaskRepository aTaskRepo) {
		RepositoryTemplateManager templateManager = TasksUiPlugin.getRepositoryTemplateManager();
		//Verify to only add once in the repository template
		Boolean found = false;
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
			Boolean anonymous = (userName != null && !userName.isEmpty()) ? false : true;

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
		for (RepositoryTemplate template : templateManager.getTemplates(GerritConnector.CONNECTOR_KIND)) {
			GerritPlugin.Ftracer.traceInfo("------------======================------------------"); //$NON-NLS-1$
			Set<Entry<String, String>> value = template.getAttributes().entrySet();
			for (Map.Entry<String, String> entry : value) {
				GerritPlugin.Ftracer.traceInfo("key: " + entry.getKey() + "\tvalue: " + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private File getLastGerritFile() {
		IPath ipath = GerritUi.getDefault().getStateLocation();
		String fileName = ipath.append(LAST_GERRIT_FILE).toPortableString();
		File file = new File(fileName);
		return file;
	}

	// ------------------------------------------------------------------------
	// Methods Public
	// ------------------------------------------------------------------------
	public static GerritServerUtility getDefault() {
		if (instance == null) {
			new GerritServerUtility();
		}
		return instance;
	}

	/**
	 * Return the mapping of the available Gerrit server used in the user workspace
	 *
	 * @return Map<Repository, String>
	 */
	public static Map<TaskRepository, String> getGerritMapping() {
		return fResultTask;
	}

	/**
	 * Save the selected Gerrit server URL
	 *
	 * @param aURL
	 * @return Boolean
	 */
	public Boolean saveLastGerritServer(String aURL) {
		Boolean ok = true;
		File file = getLastGerritFile();
		try {
			FileWriter fw = new FileWriter(file);
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
	 *
	 * @return String
	 */
	public String getLastSavedGerritServer() {
		String lastGerritURL = null;
		File file = getLastGerritFile();
		if (file != null) {
			try {
				FileReader fr = new FileReader(file);
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
	 * @param Menu
	 *            string aSt
	 * @return URL as a string
	 */
	public String getMenuSelectionURL(String aSt) {
		String urlStr = null;
		if (!fResultTask.isEmpty()) {
			Set<TaskRepository> mapSet = fResultTask.keySet();
			GerritPlugin.Ftracer.traceInfo("-------------------"); //$NON-NLS-1$
			for (TaskRepository key : mapSet) {
				if (key.getRepositoryLabel().equals(aSt)) {
					urlStr = fResultTask.get(key);

					GerritPlugin.Ftracer.traceInfo("Map Key: " + key.getRepositoryLabel() + "\t URL: " //$NON-NLS-1$ //$NON-NLS-2$
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

}
