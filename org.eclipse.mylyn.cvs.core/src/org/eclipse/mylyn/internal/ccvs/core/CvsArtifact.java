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

package org.eclipse.mylyn.internal.cvs.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ScmArtifact;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public class CvsArtifact extends ScmArtifact {

	private final IResource resource;

	private final CvsConnector connector;

	public CvsArtifact(CvsConnector connector, IResource resource) {
		this.connector = connector;
		this.resource = resource;
	}

	@Override
	public IFileRevision getFileRevision(String id, IProgressMonitor monitor) {
		RepositoryProvider provider = RepositoryProvider.getProvider(resource.getProject(), connector.getProviderId());
		IFileHistory history = provider.getFileHistoryProvider().getFileHistoryFor(resource, IFileHistoryProvider.NONE,
				monitor);
		return history.getFileRevision(id);
	}

}
