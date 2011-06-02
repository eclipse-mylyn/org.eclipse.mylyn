/*******************************************************************************
 * Copyright (c) 2011 Ericsson Research Canada and others.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ericsson - Initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.git.ui.connector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.egit.core.GitProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.git.ui.GetChangeSetDialog;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.ui.spi.ScmConnectorUi;

/**
 * Entry class to resolve the generic versions components from a User interface.
 * 
 * @author Alvaro Sanchez-Leon
 */
public class GitConnectorUi extends ScmConnectorUi {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.git.ui"; //$NON-NLS-1$

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IResource resource) {
		Assert.isNotNull(resource);

		final IProject project = resource.getProject();
		Assert.isNotNull(project);

		// Resolve Git Scm connector
		final ScmConnector scmConnector = ScmCore.getConnector(resource);
		Assert.isNotNull(scmConnector);

		// Check if the provider is for Git
		if (!GitProvider.class.getName().equals(scmConnector.getProviderId())) {
			throw new RuntimeException("No Git connector: " + scmConnector.getProviderId());
		}

		final GetChangeSetDialog dialog = new GetChangeSetDialog(null, project);
		final int result = dialog.open();
		if (result == Window.OK) {
			return dialog.getChangeSet();
		} // else Window.CANCEL
		return null;
	}

}
