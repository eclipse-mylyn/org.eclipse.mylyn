/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.versions.core.spi.ScmInfoAttributes;
import org.eclipse.team.core.history.IFileRevision;

/**
 * @author Steffen Pingel
 */
public abstract class ScmArtifact implements ScmInfoAttributes {

	private final String id;

	private final String path;

	private final Map<String, String> fAtrributes = new HashMap<>();

	private String fProjName = null;

	/**
	 * Relative path to the associated project
	 */
	private String fProjectRelativePath = null;

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

	@Override
	public Map<String, String> getInfoAtrributes() {
		return fAtrributes;
	}

	/**
	 * Set the associated Project name
	 * 
	 * @param projName
	 */
	public void setProjectName(String projName) {
		fProjName = projName;
	}

	/**
	 * Set the relative path from the associated project
	 * 
	 * @param projRelPath
	 */
	public void setProjectRelativePath(String projRelPath) {
		fProjectRelativePath = projRelPath;
	}

	/**
	 * Get the associated Project name
	 * 
	 * @return
	 */
	public String getProjectName() {
		return fProjName;
	}

	/**
	 * Get the relative path from the associated project
	 * 
	 * @return
	 */
	public String getProjectRelativePath() {
		return fProjectRelativePath;
	}

}
