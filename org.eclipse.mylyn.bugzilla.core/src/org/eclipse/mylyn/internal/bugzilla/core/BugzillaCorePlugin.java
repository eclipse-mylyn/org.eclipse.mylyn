/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaCorePlugin extends Plugin {

	private static final String ERROR_DELETING_CONFIGURATION = "Error removing corrupt repository configuration file.";

	private static final String ERROR_INCOMPATIBLE_CONFIGURATION = "Reset Bugzilla repository configuration cache due to format change";

	public static final String CONNECTOR_KIND = "bugzilla";

	public static final String ID_PLUGIN = "org.eclipse.mylyn.bugzilla";

	private static BugzillaCorePlugin INSTANCE;

	private static boolean cacheFileRead = false;

	private static File repositoryConfigurationFile = null;

	private static BugzillaRepositoryConnector connector;

	private static final String OPTION_ALL = "All";

	// A Map from Java's  Platform to Buzilla's
	private final Map<String, String> java2buzillaPlatformMap = new HashMap<String, String>();

	/** Product configuration for the current server */
	private static Map<String, RepositoryConfiguration> repositoryConfigurations = new HashMap<String, RepositoryConfiguration>();

	public BugzillaCorePlugin() {
		super();
		java2buzillaPlatformMap.put("x86", "PC"); // can be PC or Macintosh!
		java2buzillaPlatformMap.put("x86_64", "PC");
		java2buzillaPlatformMap.put("ia64", "PC");
		java2buzillaPlatformMap.put("ia64_32", "PC");
		java2buzillaPlatformMap.put("sparc", "Sun");
		java2buzillaPlatformMap.put("ppc", "Power PC"); // not Power!

	}

	public static BugzillaCorePlugin getDefault() {
		return INSTANCE;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (!repositoryConfigurations.isEmpty()) {
			writeRepositoryConfigFile();
		}

		INSTANCE = null;
		super.stop(context);
	}

	static void setConnector(BugzillaRepositoryConnector theConnector) {
		connector = theConnector;
	}

	public static Map<String, RepositoryConfiguration> getConfigurations() {
		if (!cacheFileRead) {
			readRepositoryConfigurationFile();
			cacheFileRead = true;
		}
		return repositoryConfigurations;
	}

	public static void setConfigurationCacheFile(File file) {
		repositoryConfigurationFile = file;
	}

	/**
	 * @since 2.1
	 * @return cached repository configuration. If not already cached, null is returned.
	 */
	public static RepositoryConfiguration getRepositoryConfiguration(String repositoryUrl) {
		if (!cacheFileRead) {
			readRepositoryConfigurationFile();
			cacheFileRead = true;
		}
		return repositoryConfigurations.get(repositoryUrl);
	}

	/**
	 * Retrieves the latest repository configuration from the server
	 */
	public static RepositoryConfiguration getRepositoryConfiguration(TaskRepository repository, boolean forceRefresh,
			IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			if (!cacheFileRead) {
				readRepositoryConfigurationFile();
				cacheFileRead = true;
			}
			if (repositoryConfigurations.get(repository.getRepositoryUrl()) == null || forceRefresh) {
				BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
				RepositoryConfiguration config = client.getRepositoryConfiguration(monitor);
				if (config != null) {
					addRepositoryConfiguration(config);
				}

			}
			return repositoryConfigurations.get(repository.getRepositoryUrl());
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, 1,
					"Error updating attributes.\n\n" + e.getMessage(), e));
		}
	}

	/** public for testing */
	public static void addRepositoryConfiguration(RepositoryConfiguration config) {
		repositoryConfigurations.remove(config.getRepositoryUrl());
		repositoryConfigurations.put(config.getRepositoryUrl(), config);
	}

	// /**
	// * Returns the path to the file cacheing the product configuration.
	// */
	// private static IPath getProductConfigurationCachePath() {
	// IPath stateLocation =
	// Platform.getStateLocation(BugzillaPlugin.getDefault().getBundle());
	// IPath configFile = stateLocation.append("repositoryConfigurations");
	// return configFile;
	// }

	/** public for testing */
	public static void removeConfiguration(RepositoryConfiguration config) {
		repositoryConfigurations.remove(config.getRepositoryUrl());
	}

	/** public for testing */
	public static void readRepositoryConfigurationFile() {
		// IPath configFile = getProductConfigurationCachePath();
		if (repositoryConfigurationFile == null || !repositoryConfigurationFile.exists()) {
			return;
		}
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(repositoryConfigurationFile));
			int size = in.readInt();
			for (int nX = 0; nX < size; nX++) {
				RepositoryConfiguration item = (RepositoryConfiguration) in.readObject();
				if (item != null) {
					repositoryConfigurations.put(item.getRepositoryUrl(), item);
				}
			}
		} catch (Exception e) {
			log(new Status(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN, ERROR_INCOMPATIBLE_CONFIGURATION));
			try {
				if (in != null) {
					in.close();
				}
				if (repositoryConfigurationFile != null && repositoryConfigurationFile.exists()) {
					if (repositoryConfigurationFile.delete()) {
						// successfully deleted
					} else {
						log(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, 0, ERROR_DELETING_CONFIGURATION, e));
					}
				}

			} catch (Exception ex) {
				log(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, 0, ERROR_DELETING_CONFIGURATION, e));
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/** public for testing */
	public static void writeRepositoryConfigFile() {
		// IPath configFile = getProductConfigurationCachePath();
		if (repositoryConfigurationFile != null) {
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(new FileOutputStream(repositoryConfigurationFile));
				out.writeInt(repositoryConfigurations.size());
				for (String key : repositoryConfigurations.keySet()) {
					RepositoryConfiguration item = repositoryConfigurations.get(key);
					if (item != null) {
						out.writeObject(item);
					}
				}
			} catch (IOException e) {
				log(e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	/**
	 * Convenience method for logging statuses to the plugin log
	 * 
	 * @param status
	 *            the status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Convenience method for logging exceptions to the plugin log
	 * 
	 * @param e
	 *            the exception to log
	 */
	public static void log(Exception e) {
		String message = e.getMessage();
		if (e.getMessage() == null) {
			message = e.getClass().toString();
		}
		log(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, 0, message, e));
	}

	/**
	 * Returns the path to the file caching bug reports created while offline.
	 */
	protected IPath getCachedBugReportPath() {
		IPath stateLocation = Platform.getStateLocation(BugzillaCorePlugin.getDefault().getBundle());
		IPath bugFile = stateLocation.append("bugReports");
		return bugFile;
	}

	public void setPlatformDefaultsOrGuess(TaskRepository repository, RepositoryTaskData newBugModel) {

		String platform = repository.getProperty(IBugzillaConstants.BUGZILLA_DEF_PLATFORM);
		String os = repository.getProperty(IBugzillaConstants.BUGZILLA_DEF_OS);

		// set both or none
		if (null != os && null != platform) {
			RepositoryTaskAttribute opSysAttribute = newBugModel.getAttribute(BugzillaAttribute.OP_SYS.getKey());
			RepositoryTaskAttribute platformAttribute = newBugModel.getAttribute(BugzillaAttribute.REP_PLATFORM.getKey());

			// TODO something can still go wrong when the allowed values on the repository change...
			opSysAttribute.setValue(os);
			platformAttribute.setValue(platform);
			return;
		}
		// fall through to old code
		setPlatformOptions(newBugModel);
	}

	public void setPlatformOptions(RepositoryTaskData newBugModel) {
		try {

			// Get OS Lookup Map
			// Check that the result is in Values, if it is not, set it to other
			// Defaults to the first of each (sorted) list All, All
			RepositoryTaskAttribute opSysAttribute = newBugModel.getAttribute(BugzillaAttribute.OP_SYS.getKey());
			RepositoryTaskAttribute platformAttribute = newBugModel.getAttribute(BugzillaAttribute.REP_PLATFORM.getKey());

			String OS = Platform.getOS();
			String platform = Platform.getOSArch();

			String bugzillaOS = null; // Bugzilla String for OS
			String bugzillaPlatform = null; // Bugzilla String for Platform
/*
			AIX -> AIX
			Linux -> Linux
			HP-UX -> HP-UX
			Solaris -> Solaris
			MacOS X -> Mac OS X
 */

			bugzillaOS = System.getProperty("os.name") + " " + System.getProperty("os.version");
			// We start with the most specific Value as the Search String.
			// If we didn't find it we remove the last part of the version String or the OS Name from
			// the Search String and continue with the test until we found it or the Search String is empty.
			//
			// The search in casesensitive.
			if (opSysAttribute != null) {
				while (bugzillaOS != null && opSysAttribute.getOptionParameter(bugzillaOS) == null) {
					int dotindex = bugzillaOS.lastIndexOf('.');
					if (dotindex > 0) {
						bugzillaOS = bugzillaOS.substring(0, dotindex);
					} else {
						int spaceindex = bugzillaOS.lastIndexOf(' ');
						if (spaceindex > 0) {
							bugzillaOS = bugzillaOS.substring(0, spaceindex);
						} else {
							bugzillaOS = null;
						}
					}
				}
			} else {
				bugzillaOS = null;
			}

			if (platform != null && java2buzillaPlatformMap.containsKey(platform)) {
				bugzillaPlatform = java2buzillaPlatformMap.get(platform);
				// Bugzilla knows the following Platforms [All, Macintosh, Other, PC, Power PC, Sun]
				// Platform.getOSArch() returns "x86" on Intel Mac's and "ppc" on Power Mac's
				// so bugzillaPlatform is "Power" or "PC".
				//
				// If the OS is "macosx" we change the Platform to "Macintosh"
				//
				if (bugzillaPlatform != null
						&& (bugzillaPlatform.compareTo("Power") == 0 || bugzillaPlatform.compareTo("PC") == 0)
						&& OS != null && OS.compareTo("macosx") == 0) {
					// TODO: this may not even be a legal value in another repository!
					bugzillaPlatform = "Macintosh";
				} else if (platformAttribute != null && platformAttribute.getOptionParameter(bugzillaPlatform) == null) {
					// If the platform we found is not int the list of available
					// optinos, set the
					// Bugzilla Platform to null, and juse use "other"
					bugzillaPlatform = null;
				}
			}
			// Set the OS and the Platform in the taskData
			if (bugzillaOS != null && opSysAttribute != null) {
				opSysAttribute.setValue(bugzillaOS);
			} else if (opSysAttribute != null && opSysAttribute.getOptionParameter(OPTION_ALL) != null) {
				opSysAttribute.setValue(OPTION_ALL);
			}

			if (bugzillaPlatform != null && platformAttribute != null) {
				platformAttribute.setValue(bugzillaPlatform);
			} else if (opSysAttribute != null && platformAttribute != null
					&& platformAttribute.getOptionParameter(OPTION_ALL) != null) {
				opSysAttribute.setValue(OPTION_ALL);
			}

		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, "could not set platform options",
					e));
		}
	}

	public Set<BugzillaLanguageSettings> getLanguageSettings() {
		return connector.getLanguageSettings();
	}

	public BugzillaLanguageSettings getLanguageSetting(String language) {
		return connector.getLanguageSetting(language);
	}
}
