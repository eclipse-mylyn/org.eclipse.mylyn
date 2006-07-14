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

package org.eclipse.mylar.tasks.core;

/**
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class RepositoryTemplate {

	public final String label;

	public final String repositoryUrl;

	public final String newTaskUrl;

	public final String taskPrefix;

	public final boolean anonymous;

	public final String version;

	public RepositoryTemplate(String label, String repositoryUrl, String version, String newTaskUrl, String taskPrefix,
			boolean anonymous) {
		this.label = label;
		this.repositoryUrl = repositoryUrl;
		this.newTaskUrl = newTaskUrl;
		this.taskPrefix = taskPrefix;
		this.version = version;
		this.anonymous = anonymous;
	}
}