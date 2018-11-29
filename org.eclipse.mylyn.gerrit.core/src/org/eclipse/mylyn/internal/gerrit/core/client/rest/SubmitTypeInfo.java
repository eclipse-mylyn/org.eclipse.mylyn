/*******************************************************************************
 * Copyright (c) 2019 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import com.google.gerrit.reviewdb.Project.SubmitType;

public class SubmitTypeInfo {

	private SubmitType value;

	private SubmitType configured_value;

	private SubmitType inherited_value;

	/**
	 * @return the value
	 */
	public SubmitType getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(SubmitType value) {
		this.value = value;
	}

	/**
	 * @return the configured_value
	 */
	public SubmitType getConfigured_value() {
		return configured_value;
	}

	/**
	 * @param configured_value
	 *            the configured_value to set
	 */
	public void setConfigured_value(SubmitType configured_value) {
		this.configured_value = configured_value;
	}

	/**
	 * @return the inherited_value
	 */
	public SubmitType getInherited_value() {
		return inherited_value;
	}

	/**
	 * @param inherited_value
	 *            the inherited_value to set
	 */
	public void setInherited_value(SubmitType inherited_value) {
		this.inherited_value = inherited_value;
	}

}
