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
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.io.File;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * Eclipse-based implementation of {@link IIndexLocationProvider}, which uses a user-defineable Location stored in the preferences.
 * 
 * @author Kilian Matt
 */
public class EclipseIndexLocationProvider implements IIndexLocationProvider{

	public File getIndexLocation(){
		return new File(TasksUiPlugin.getDefault().getDataDirectory(),".changeSetIndex");
	}
	
}
