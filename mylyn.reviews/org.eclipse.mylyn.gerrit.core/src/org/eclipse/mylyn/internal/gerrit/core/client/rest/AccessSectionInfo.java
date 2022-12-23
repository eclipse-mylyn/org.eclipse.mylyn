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

import java.util.Map;

public class AccessSectionInfo {

	private Map<String, PermissionInfo> permissions;

	public Map<String, PermissionInfo> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, PermissionInfo> permissions) {
		this.permissions = permissions;
	}

}
