/*******************************************************************************
 * Copyright (c) 2011 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ericsson AB - Initial API and Implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.subclipse.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Alvaro Sanchez-Leon
 */
public class SubclipseCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.subclipse.core"; //$NON-NLS-1$

	private static final String FTMP_FOLDER_NAME = "subversionTmp"; //$NON-NLS-1$

	static private SubclipseCorePlugin plugin = null;

	public static File tmpDir = null;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		deleteTmpFolderTask();
	}

	/**
	 * Gets the plug-in
	 * 
	 * @return the shared instance
	 */
	public static SubclipseCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Get the associated temporary directory
	 * 
	 * @return
	 * @throws IOException
	 */
	public File getTmpDir() throws IOException {
		if (tmpDir == null || (!tmpDir.exists())) {
			tmpDir = plugin.getStateLocation().addTrailingSeparator().append(FTMP_FOLDER_NAME).toFile();
			tmpDir.mkdir();
		}
		return tmpDir;
	}

	/**
	 * Delete plug-in temporary files
	 */
	private void deleteTmpFolderTask() {
		if (tmpDir == null || (!tmpDir.exists())) {
			return;
		}

		// Delete files in a separate thread
		Runnable runnable = new Runnable() {
			public void run() {
				deleteTmpFolder(tmpDir);
			}
		};

		// Start
		Thread thread = new Thread(runnable);
		thread.start();
	}

	/**
	 * Delete folder contents and given folder last
	 * 
	 * @param tmpFolder
	 */
	private void deleteTmpFolder(final File tmpFolder) {
		// Need to delete each file before the directory
		if (tmpFolder.exists()) {
			for (File f : tmpFolder.listFiles()) {
				if (f.isDirectory()) {
					deleteTmpFolder(f);
				} else {
					f.delete();
				}
			}

			// finally, Delete the directory
			tmpFolder.delete();
		}
	}
}
