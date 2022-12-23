/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class TaskRepositoryDelta {

	public enum Type {
		ALL, CREDENTIALS, PROPERTY, PROYX, OFFLINE
	};

	private final Type type;

	private final Object key;

	public TaskRepositoryDelta(Type type, Object key) {
		Assert.isNotNull(type);
		this.type = type;
		this.key = key;
	}

	public TaskRepositoryDelta(Type type) {
		this(type, null);
	}

	public Type getType() {
		return type;
	}

	public Object getKey() {
		return key;
	}

}
