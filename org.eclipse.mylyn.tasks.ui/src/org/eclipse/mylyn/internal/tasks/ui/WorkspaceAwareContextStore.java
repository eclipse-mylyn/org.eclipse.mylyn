/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;

import org.eclipse.mylyn.context.core.AbstractContextStore;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class WorkspaceAwareContextStore extends AbstractContextStore {

	public static final String CONTEXTS_DIRECTORY = "contexts";

	private File rootDirectory;

	private File contextDirectory;

	@Override
	public synchronized void init() {
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
