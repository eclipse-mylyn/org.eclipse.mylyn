/*******************************************************************************
 * Copyright (c) 2014, 2015 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 ******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#git-personal-info" >GitPersonalInfo</a>.
 */
public class GitPersonalInfo {

	private String name;

	private String email;

	private int tz;

	private String date;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public int getTimeZoneOffset() {
		return tz;
	}

	public String getDate() {
		return date;
	}

}
