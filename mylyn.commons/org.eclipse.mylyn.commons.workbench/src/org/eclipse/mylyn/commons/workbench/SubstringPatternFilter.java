/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Mik Kersten
 * @since 3.7
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
