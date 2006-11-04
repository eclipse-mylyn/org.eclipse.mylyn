/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.core;

import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;

/**
 * Task used with generic web-based repositories
 * 
 * @author Eugene Kuleshov
 */
public class WebTask extends AbstractRepositoryTask {

	private final String id;

	private final String taskPrefix;

	private final String repositoryUrl;
	
	private final String repsitoryType;

	public WebTask(String id, String label, String taskPrefix, String repositoryUrl, String repsitoryType) {
		super(taskPrefix + id, label, false);
		this.id = id;
		this.taskPrefix = taskPrefix;
		this.repositoryUrl = repositoryUrl;
		this.repsitoryType = repsitoryType;
		setUrl(taskPrefix + id);
	}

	public String getId() {
		return this.id;
	}
	
	public String getTaskPrefix() {
		return this.taskPrefix;
	}

	public String getRepositoryKind() {
		return repsitoryType;
	}
	
	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

}
