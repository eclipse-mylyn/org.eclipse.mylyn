/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Specifies attributes for a task repository.
 * 
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 * @since 2.0
 */
public final class RepositoryTemplate {

	public final Map<String, String> genericAttributes = new LinkedHashMap<String, String>();
	
	public final String label;

	public final String repositoryUrl;

	public final String newTaskUrl;

	public final String taskPrefixUrl;

	public final String taskQueryUrl;

	public final String newAccountUrl;

	public final boolean anonymous;

	public final String version;

	public final boolean addAutomatically;
	
	public final String characterEncoding;

	public RepositoryTemplate(String label, String repositoryUrl, String characterEncoding, String version, String newTaskUrl, String taskPrefix,
			String taskQuery, String newAccountUrl, boolean anonymous, boolean addAutomatically) {
		this.label = label;
		this.repositoryUrl = repositoryUrl;
		this.newTaskUrl = newTaskUrl;
		this.taskPrefixUrl = taskPrefix;
		this.taskQueryUrl = taskQuery;
		this.newAccountUrl = newAccountUrl;
		this.version = version;
		this.anonymous = anonymous;
		this.characterEncoding = characterEncoding;
		this.addAutomatically = addAutomatically;
	}
	
	public void addAttribute(String name, String value) {
		genericAttributes.put(name, value);
	}
	
	public String getAttribute(String name) {
		return genericAttributes.get(name);
	}
	
	public Map<String, String> getAttributes() {
		return this.genericAttributes;
	}
}

