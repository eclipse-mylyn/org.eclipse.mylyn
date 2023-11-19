/*******************************************************************************
 * Copyright (c) 2010, 2011 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.htmltext.events;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class NodeSelectionEvent {

	private final String selectedHtml;

	public NodeSelectionEvent(String selectedHtml) {
		this.selectedHtml = selectedHtml;
	}

	public String getSelectedHtml() {
		return selectedHtml;
	}

}
