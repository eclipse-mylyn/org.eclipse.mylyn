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

package org.eclipse.mylyn.versions.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public abstract class ScmArtifact {

	private final String id;

	private final String path;

	protected ScmArtifact(String id, String path) {
		this.id = id;
		this.path = path;
	}

	public abstract IFileRevision[] getContributors(IProgressMonitor monitor);

	public abstract IFileRevision getFileRevision(IProgressMonitor monitor);

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public abstract IFileRevision[] getTargets(IProgressMonitor monitor);

}
