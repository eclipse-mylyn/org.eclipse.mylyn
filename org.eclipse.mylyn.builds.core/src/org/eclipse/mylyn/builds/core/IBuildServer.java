/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public interface IBuildServer extends IBuildElement {

	public TaskRepository getRepository();

	public List<IBuildPlan> getPlans();

	public List<IBuildPlan> refreshPlans(IOperationMonitor monitor) throws CoreException;

	public IStatus validate(IOperationMonitor monitor) throws CoreException;

	public String getRepositoryUrl();

	public RepositoryLocation getLocation();

}
