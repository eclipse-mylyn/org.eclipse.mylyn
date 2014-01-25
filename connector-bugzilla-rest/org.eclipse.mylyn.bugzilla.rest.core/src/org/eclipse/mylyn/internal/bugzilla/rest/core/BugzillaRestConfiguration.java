/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.Serializable;

public class BugzillaRestConfiguration implements Serializable {

	private static final long serialVersionUID = 3433667217913466746L;

	private final String url;

	public BugzillaRestConfiguration(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}
