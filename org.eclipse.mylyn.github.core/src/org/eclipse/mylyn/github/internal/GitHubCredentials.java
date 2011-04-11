/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class GitHubCredentials {
	private final String username;
	private final String password;


	public GitHubCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public GitHubCredentials(AuthenticationCredentials credentials) {
		this(credentials.getUserName(),credentials.getPassword());
	}

	public static GitHubCredentials create(TaskRepository repository) {
		return new GitHubCredentials(repository.getCredentials(AuthenticationType.REPOSITORY));
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	
}
