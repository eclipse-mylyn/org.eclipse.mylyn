/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 *
 * @author Kilian Matt
 *
 */
public class TaskVersionsUiPlugin extends AbstractUIPlugin {
	private static TaskVersionsUiPlugin instance;
	public static final String PLUGIN_ID = "org.eclipse.mylyn.versions.tasks.ui";

	public TaskVersionsUiPlugin() {
		instance = this;
	}

	public static TaskVersionsUiPlugin getDefault() {
		return instance;
	}

}
