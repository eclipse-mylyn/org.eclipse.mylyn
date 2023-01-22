/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryAttribute implements Serializable {

	private static final long serialVersionUID = -4535033208999685315L;

	private String name;

	public TracRepositoryAttribute(String name) {
		Assert.isNotNull(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		// FIXME serialization can restore null values here
		return (name != null) ? name : ""; //$NON-NLS-1$
	}

}
