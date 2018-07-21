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

package org.eclipse.mylyn.internal.bugzilla.rest.core.response.data;

import java.io.Serializable;

public class StatusTransition implements Serializable {

	private static final long serialVersionUID = 791312498240951654L;

	public String name;

	public boolean comment_required;

	public String getName() {
		return name;
	}

	public boolean isCommentRequired() {
		return comment_required;
	}
}