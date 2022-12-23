/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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

public class FlagTypes implements Serializable {
	private static final long serialVersionUID = 2770084982332029237L;

	private FlagType[] bug;

	private FlagType[] attachment;

	public FlagType[] getBug() {
		return bug;
	}

	public FlagType[] getAttachment() {
		return attachment;
	}
}
