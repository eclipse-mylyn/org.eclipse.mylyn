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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;

/**
 * Task used with generic web-based repositories
 * 
 * @author Eugene Kuleshov
 */
public class WebTask extends AbstractRepositoryTask {

	// TODO: move
	public static final String REPOSITORY_TYPE = "web";

	private static final String UNKNOWN_OWNER = "<unknown>";
	
	private final String taskPrefix;

	private final String repsitoryType;

	public WebTask(String id, String label, String taskPrefix, String repositoryUrl, String repsitoryType) {
		super(repositoryUrl, id, label);
		this.taskPrefix = taskPrefix;
		this.repositoryUrl = repositoryUrl;
		this.repsitoryType = repsitoryType;
		setTaskUrl(taskPrefix + id);
	}
	
	public String getTaskPrefix() {
		return this.taskPrefix;
	}

	@Override
	public String getRepositoryKind() {
		return repsitoryType;
	}
	
	@Override
	public String getTaskKey() {
		return null;
	}

	public String getOwner() {
		return UNKNOWN_OWNER;
	}

	@Override
	public boolean isLocal() {
		// ignore
		return false;
	}
	
}
