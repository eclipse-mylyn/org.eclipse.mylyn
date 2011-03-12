/*******************************************************************************
 * Copyright (c) 2011 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * Contributors:
 *   Alvaro Sanchez-Leon - Initial API and Implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.ui.spi;

import javax.swing.ProgressMonitor;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;

/**
 * @author alvsan09
 */
public interface ScmUiConnector {
	/**
	 * Derive changes for a given repository and narrow down the selection to the ones related to the option resource provided.
	 * This method is suitable to open a UI Wizard to reduce the focus even more, driven by user selection.
	 * @param repo
	 * @param resource
	 * @param monitor
	 * @return
	 */
	public ChangeSet getChangeSet(ScmRepository repo, IResource resource, ProgressMonitor monitor);
}
