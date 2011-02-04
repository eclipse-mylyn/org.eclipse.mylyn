/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.cdt.tests.support;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author Steffen Pingel
 */
public class CdtProject {

	private final IProject project;

	private final ICProject cProject;

	public CdtProject(String name) throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(name);
		project.create(null);
		project.open(null);

//		ICProjectDescription description = CoreModel.getDefault().createProjectDescription(project, false);
//		CoreModel.getDefault().setProjectDescription(project, description);
//		cProject = CoreModel.getDefault().create(project);
		cProject = null;
	}

	public ICProject getCProject() {
		return cProject;
	}

	public IProject getProject() {
		return project;
	}

}
