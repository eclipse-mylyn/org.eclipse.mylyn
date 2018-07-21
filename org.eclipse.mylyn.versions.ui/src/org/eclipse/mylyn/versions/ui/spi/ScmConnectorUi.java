/*******************************************************************************
 * Copyright (c) 2011, 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Ericsson - Initial API and Implementation
 *   Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.versions.ui.spi;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;

/**
 * Provides an interface intended to be associated with a UI, so the user can select available version control artifacts
 * 
 * @author Alvaro Sanchez-Leon
 * @author Kilian Matt
 */
public abstract class ScmConnectorUi {

	/**
	 * Resolves change sets for a given repository and narrow down the selection possibilities to the ones related to
	 * the given resource provided. This method is suitable to open a UI Wizard, the selection is expected to be driven
	 * by the user.
	 * 
	 * @param repository
	 *            associated repository
	 * @param resource
	 *            work space resource e.g. project used to narrow down the change set options presented to the user
	 * @return the selected change set
	 */
	public abstract ChangeSet getChangeSet(ScmRepository repository, IResource resource);

	/**
	 * Display a changesets in a connector specific view.
	 * <p>
	 * Throws {@link UnsupportedOperationException} by default.
	 * 
	 * @param changeSet
	 *            the changeset to display
	 */
	public void showChangeSetInView(ChangeSet changeSet) {
		throw new UnsupportedOperationException();
	}

}
