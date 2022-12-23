/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;

/**
 * 
 * @author Kilian Matt
 */
public final class ChangeSetProvider{
	private ScmRepository repo;

	public ChangeSetProvider(ScmRepository repo) {
		this.repo=repo;
	}
	
	public ChangeSet getChangeset(String revision, 
			IProgressMonitor monitor) throws CoreException {
		return repo.getConnector().getChangeSet(repo,
				new FileRevision(revision), monitor);
	}
}