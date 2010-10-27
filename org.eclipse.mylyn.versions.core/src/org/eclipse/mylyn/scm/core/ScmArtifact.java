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

package org.eclipse.mylyn.scm.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public abstract class ScmArtifact {

	ChangeType changeType;

	String path;

	public ChangeType getChangeType() {
		return changeType;
	}

	public abstract IFileRevision getFileRevision(String id, IProgressMonitor monitor);

	public String getPath() {
		return path;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
