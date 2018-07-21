/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;

import com.google.gson.reflect.TypeToken;

public class BugzillaRestUnauthenticatedGetRequest<T> extends BugzillaRestGetRequest<T> {

	public BugzillaRestUnauthenticatedGetRequest(CommonHttpClient client, String urlSuffix, TypeToken<?> responseType) {
		super(client, urlSuffix, responseType, false);
	}

}