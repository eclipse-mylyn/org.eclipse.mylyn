/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
