/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import org.eclipse.mylyn.internal.gerrit.core.client.rest.ApprovalUtil;

import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;

/**
 * Manages permissions for Gerrit 2.2 and later.
 * 
 * @author Steffen Pingel
 */
public class PermissionLabel {

	protected int max;

	protected int min;

	protected String name;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String toLabelName(String identifier) {
		return "label-" + ApprovalUtil.toNameWithDash(identifier); //$NON-NLS-1$
	}

	public boolean matches(ApprovalCategoryValue value) {
		return value.getValue() >= min && value.getValue() <= max;
	}

	public boolean matches(ApprovalCategory approvalCategory) {
		return toLabelName(approvalCategory.getName()).equals(getName());
	}

}