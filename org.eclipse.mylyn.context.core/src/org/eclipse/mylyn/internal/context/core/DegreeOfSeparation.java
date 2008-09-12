/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public String getLabel() {
		return label;
	}

	public int getDegree() {
		return degree;
	}

}
