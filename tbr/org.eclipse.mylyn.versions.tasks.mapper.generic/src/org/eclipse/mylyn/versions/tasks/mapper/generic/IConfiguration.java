/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.tasks.mapper.generic;

import java.util.List;

import org.eclipse.core.resources.IProject;

/**
 * 
 * @author Kilian Matt
 *
 */
public interface IConfiguration {

	List<IProject> getProjectsForTaskRepository(String connectorKind,
			String repositoryUrl);

}
