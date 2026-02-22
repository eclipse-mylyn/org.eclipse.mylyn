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

import java.util.List;

/**
 * Provides additional fields used by Gerrit 2.2.
 *
 * @author Steffen Pingel
 */
public class PatchSetPublishDetailX extends com.google.gerrit.common.data.PatchSetPublishDetail {

	protected boolean canSubmit;

	protected List<PermissionLabel> labels;

	public boolean canSubmit() {
		return canSubmit;
	}

	public List<PermissionLabel> getLabels() {
		return labels;
	}

	public void setLabels(List<PermissionLabel> labels) {
		this.labels = labels;
	}
}
