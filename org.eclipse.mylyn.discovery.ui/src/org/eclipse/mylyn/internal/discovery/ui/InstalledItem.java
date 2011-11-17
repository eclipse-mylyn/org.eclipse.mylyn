/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
