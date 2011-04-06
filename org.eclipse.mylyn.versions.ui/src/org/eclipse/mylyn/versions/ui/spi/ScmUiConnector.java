/*******************************************************************************
 * Copyright (c) 2011 Ericsson and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ericsson - Initial API and Implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.ui.spi;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;

/**
 * @author Alvaro Sanchez-Leon
 */
public interface ScmUiConnector {
	/**
	 * Derive changes for a given repository and narrow down the selection to the ones related to the option resource
	 * provided. This method is suitable to open a UI Wizard to reduce the selection focus, driven by the user.
	 * 
	 * @param repo
	 * @param resource
	 * @param monitor
	 * @return ChnageSet
	 * @throws CoreException
	 */
	public ChangeSet getChangeSet(ScmRepository repo, IResource resource, IProgressMonitor monitor)
			throws CoreException;

}
