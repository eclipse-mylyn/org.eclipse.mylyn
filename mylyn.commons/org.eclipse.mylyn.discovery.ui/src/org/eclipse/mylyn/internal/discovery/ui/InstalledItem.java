/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.ui;

import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 */
public class InstalledItem<T> {

	private final String id;

	private final Version version;

	private final T data;

	public InstalledItem(T data, String id, Version version) {
		this.data = data;
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public Version getVersion() {
		return version;
	}

	public T getData() {
		return data;
	}

}
