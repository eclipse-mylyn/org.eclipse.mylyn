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
 *     See git histpry
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

/**
 * @author Steffen Pingel
 */
public class JenkinsServerInfo {

	public enum Type {
		JENKINS
	}

	private final String version;

	private final Type type;

	public JenkinsServerInfo(Type type, String version) {
		this.type = type;
		this.version = version;
	}

	public Type getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

}
