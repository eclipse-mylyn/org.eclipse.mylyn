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

package org.eclipse.mylyn.htmltext.model;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public enum TriState {

	ON("1.0"), OFF("2.0"), DISABLED("3.0");

	private final String representation;

	TriState(String representation) {
		this.representation = representation;
	}

	public static TriState fromString(String str) {
		for (TriState state : TriState.values()) {
			if (state.representation.equals(str)) {
				return state;
			}
		}
		return DISABLED;
	}

	public String getRepresentation() {
		return representation;
	}

}
