/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

/**
 * @author Shawn Minto
 */
public class DegreeOfSeparation implements IDegreeOfSeparation {

	private final String label;

	private final int degree;

	public DegreeOfSeparation(String label, int degree) {
		this.label = label;
		this.degree = degree;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public int getDegree() {
		return degree;
	}

}
