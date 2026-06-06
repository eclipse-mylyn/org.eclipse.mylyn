/*******************************************************************************
 * Copyright (c) 2019, 2026 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

public class InheritedBooleanInfo {

	private boolean value;

	//FIXME: AF: was unused private, most probably should be removed
	boolean configured_value;

	//FIXME: AF: was unused private, most probably should be removed
	boolean inherited_value;

	/**
	 * @return the value
	 */
	public boolean isValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(boolean value) {
		this.value = value;
	}

}
