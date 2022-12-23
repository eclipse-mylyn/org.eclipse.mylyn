/*******************************************************************************
 * Copyright (c) 2011 Ericsson Research Canada and others.
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Ericsson - Initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.subclipse.ui.connector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.subclipse.ui.GetChangeSetDialog;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.ui.spi.ScmConnectorUi;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;

/**
 * Entry class to resolve the generic versions components from a User interface.
 * 
 * @author Alvaro Sanchez-Leon
 */
public class SVNConnectorUi extends ScmConnectorUi {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.subclipse.ui"; //$NON-NLS-1$

	@Override
	public ChangeSet getChangeSet(ScmRepository repository, IResource resource) {
		Assert.isNotNull(resource);

		final IProject project = resource.getProject();
		Assert.isNotNull(project);

		// Resolve Scm connector
		final ScmConnector scmConnector = ScmCore.getConnector(resource);
		Assert.isNotNull(scmConnector);

		// Check if the provider is for Subclipse
		if (!SVNProviderPlugin.getTypeId().equals(scmConnector.getProviderId())) {
			throw new RuntimeException("No Subclipse connector: " + scmConnector.getProviderId());
		}

		final IProgressMonitor monitor = new NullProgressMonitor();
		final GetChangeSetDialog dialog = new GetChangeSetDialog(null, project, monitor);
		final int result = dialog.open();

		//Cancel any ongoing tasks operations
		monitor.setCanceled(true);

		if (result == Window.OK) {
			return dialog.getChangeSet();
		} // else Window.CANCEL
		return null;
	}

}
