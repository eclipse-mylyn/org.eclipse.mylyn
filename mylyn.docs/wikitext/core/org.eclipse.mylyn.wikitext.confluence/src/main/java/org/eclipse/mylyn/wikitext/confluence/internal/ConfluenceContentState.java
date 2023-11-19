/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal;

import org.eclipse.mylyn.wikitext.parser.markup.ContentState;

public class ConfluenceContentState extends ContentState {
	private boolean withinLink = false;

	public boolean isWithinLink() {
		return withinLink;
	}

	public void setWithinLink(boolean withinLink) {
		this.withinLink = withinLink;
	}
}
