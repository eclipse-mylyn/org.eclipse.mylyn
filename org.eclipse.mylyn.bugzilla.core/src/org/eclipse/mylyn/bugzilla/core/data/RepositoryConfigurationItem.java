/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.core.data;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;

public class RepositoryConfigurationItem implements Serializable {

	private static final long serialVersionUID = -2915368043846801298L;

	private final String name;

	public RepositoryConfigurationItem(String name) {
		Assert.isNotNull(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
