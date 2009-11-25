/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Robert Elves
 */
public interface IOslcConnector {

	public List<OslcServiceProvider> getAvailableServices(TaskRepository repository, String url,
			IProgressMonitor monitor) throws CoreException;

	public OslcServiceDescriptor getServiceDescriptor(TaskRepository repository, OslcServiceProvider selectedPovider,
			IProgressMonitor monitor) throws CoreException;

}
