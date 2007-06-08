/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.io.Serializable;

/**
 * @author Steffen Pingel
 */
public class TracTicketAttribute implements Comparable<TracTicketAttribute>, Serializable {

	private static final long serialVersionUID = -8611030780681519787L;

	private String name;

	private int value;

	public TracTicketAttribute(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public int compareTo(TracTicketAttribute o) {
		return value - o.value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name;
	}

}
