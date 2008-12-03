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

package org.eclipse.mylyn.internal.provisional.commons.ui;

import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Mik Kersten
 */
public class SubstringPatternFilter extends PatternFilter {

	@Override
	public void setPattern(String patternString) {
		if (patternString == null || patternString.startsWith("*")) { //$NON-NLS-1$
			super.setPattern(patternString);
		} else {
			super.setPattern("*" + patternString); //$NON-NLS-1$
		}
	}
}
