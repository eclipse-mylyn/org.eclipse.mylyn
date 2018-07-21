/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core;

import java.util.EventObject;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class RepositoryLocationChangeEvent extends EventObject {

	public enum Type {
		ALL, CREDENTIALS, PROYX
	};

	private static final long serialVersionUID = -8177578930986469693L;

	private final Type type;

	public RepositoryLocationChangeEvent(RepositoryLocation source, Type type) {
		super(source);
		this.type = type;
		Assert.isNotNull(source);
	}

	@Override
	public RepositoryLocation getSource() {
		return (RepositoryLocation) super.getSource();
	}

	public Type getType() {
		return type;
	}

}
