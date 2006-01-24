/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist.ui;

import org.eclipse.ui.internal.dialogs.PatternFilter;

public class TaskListPatternFilter extends PatternFilter {

	@Override
	public void setPattern(String patternString) {
		if (patternString == null || patternString.startsWith("*")) {
			super.setPattern(patternString);
		} else {
			super.setPattern("*" + patternString);
		}
	}
}
