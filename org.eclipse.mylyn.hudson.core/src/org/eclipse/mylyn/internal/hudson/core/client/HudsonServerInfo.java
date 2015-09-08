/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

/**
 * @author Steffen Pingel
 */
public class HudsonServerInfo {

	public enum Type {
		HUDSON, JENKINS
	};

	private final String version;

	private final Type type;

	public HudsonServerInfo(Type type, String version) {
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
