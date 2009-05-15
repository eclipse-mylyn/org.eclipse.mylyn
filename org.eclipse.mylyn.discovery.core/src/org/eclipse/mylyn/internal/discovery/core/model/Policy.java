/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.model;

/**
 * a policy defines what is permitted.
 * 
 * @author David Green
 */
public class Policy {
	private static final Policy DEFAULT = new Policy(false);

	private final boolean permitCategories;

	public Policy(boolean permitCategories) {
		this.permitCategories = permitCategories;
	}

	public boolean isPermitCategories() {
		return permitCategories;
	}

	public static Policy defaultPolicy() {
		return DEFAULT;
	}
}
