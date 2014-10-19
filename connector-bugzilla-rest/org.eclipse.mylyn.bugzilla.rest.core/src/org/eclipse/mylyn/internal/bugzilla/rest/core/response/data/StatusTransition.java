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