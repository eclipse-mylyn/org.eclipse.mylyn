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

/**
 * @author Steffen Pingel
 */
public class ScmArtifactInfo {

	private String path;

	private ScmRepository repository;

	private String revision;

	public String getPath() {
		return path;
	}

	public ScmRepository getRepository() {
		return repository;
	}

	public String getRevision() {
		return revision;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setRepository(ScmRepository repository) {
		this.repository = repository;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

}
