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
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetCollector;

/**
 * Implementation of an {@link IChangeSetCollector}, which maps the found matches to changesets
 * @author Kilian Matt
 */
public class MappingChangeSetCollector implements IChangeSetCollector {
	private final IProgressMonitor monitor;
	private final IChangeSetMapping mapping;
	private final ChangeSetProvider changeSetProvider;

	public MappingChangeSetCollector(IProgressMonitor monitor,
			IChangeSetMapping mapping, ChangeSetProvider changeSetProvider) {
		this.monitor = monitor;
		this.mapping = mapping;
		this.changeSetProvider=changeSetProvider;
	}

	public void collect(String revision, String repositoryUrl) throws CoreException {
		mapping.addChangeSet(changeSetProvider.getChangeset(revision, monitor));
	}

}
