/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

/**
 * Data model object for <a
 * href="https://gerrit-review.googlesource.com/Documentation/rest-api-accounts.html#account-info">AccountInfo</a>.
 */
public class AccountInfo {
	private int _account_id = -1;

	private String name;

	private String email;

	private String username;

	public int getId() {
		return _account_id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public void setId(int id) {
		_account_id = id;
	}
}
