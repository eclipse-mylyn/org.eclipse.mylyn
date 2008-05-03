/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Calendar;

/**
 * @author Rob Elves
 */
public class DateRange {

	public Calendar startDate;

	public Calendar endDate;

	public boolean isFloating = false;

	public DateRange(Calendar startDate, Calendar endDate) {
		this(startDate, endDate, false);
	}

	public DateRange(Calendar startDate, Calendar endDate, boolean isFloating) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.isFloating = isFloating;
	}
}
