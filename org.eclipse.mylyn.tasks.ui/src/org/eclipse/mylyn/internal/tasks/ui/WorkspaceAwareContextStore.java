/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.context.core.AbstractContextStore;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class WorkspaceAwareContextStore extends AbstractContextStore {

	private static final String DIRECTORY_METADATA = ".metadata";

	private static final String OLD_DATA_DIR = ".mylar";

	public static final String CONTEXTS_DIRECTORY = "contexts";

	private File rootDirectory;

	private File contextDirectory;

	@Override
	public void init() {
		// Migrate .mylar data folder to .metadata/.mylyn
		String oldDefaultDataPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
				+ OLD_DATA_DIR;
		File newDefaultDataDir = new File(TasksUiPlugin.getDefault().getDefaultDataDirectory());
		File oldDefaultDataDir = new File(oldDefaultDataPath);
//		if (newDefaultDataDir.exists() && oldDefaultDataDir.exists()) {
//			StatusHandler.log("Legacy data folder detected: " + oldDefaultDataDir.getAbsolutePath(), this);
//		} else 
		if (oldDefaultDataDir.exists() && !newDefaultDataDir.exists()) {
			File metadata = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + '/'
					+ DIRECTORY_METADATA);
			if (!metadata.exists()) {
				if (!metadata.mkdirs()) {
					StatusHandler.log("Unable to create metadata folder: " + metadata.getAbsolutePath(), this);
				}
			}

			if (metadata.exists()) {
				if (!oldDefaultDataDir.renameTo(new File(TasksUiPlugin.getDefault().getDefaultDataDirectory()))) {
					StatusHandler.log("Failed to migrate legacy data from " + oldDefaultDataDir.getAbsolutePath()
							+ " to " + TasksUiPlugin.getDefault().getDefaultDataDirectory(), this);
				}
			}
		}

		rootDirectory = new File(TasksUiPlugin.getDefault().getDataDirectory());
		if (!rootDirectory.exists()) {
			rootDirectory.mkdir();
		}

		contextDirectory = new File(rootDirectory, CONTEXTS_DIRECTORY);
		if (!contextDirectory.exists()) {
			contextDirectory.mkdir();
		}
	}

	@Override
	public File getRootDirectory() {
		return rootDirectory;
	}

	@Override
	public File getContextDirectory() {
		return contextDirectory;
	}
}
