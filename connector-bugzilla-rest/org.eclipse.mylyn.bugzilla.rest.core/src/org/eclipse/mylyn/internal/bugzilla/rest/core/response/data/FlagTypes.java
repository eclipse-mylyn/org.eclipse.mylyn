/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
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
