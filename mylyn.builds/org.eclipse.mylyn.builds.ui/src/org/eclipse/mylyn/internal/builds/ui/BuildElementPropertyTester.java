/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * @author Steffen Pingel
 */
public class BuildElementPropertyTester extends PropertyTester {

	public BuildElementPropertyTester() {
		// ignore
	}

	/**
	 *
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("available".equals(property)) { //$NON-NLS-1$
			if (args.length > 0) {
				String kind = (String) args[0];
				if ("console".equals(kind)) { //$NON-NLS-1$
					return CoreUtil.propertyEquals(Platform.getBundle("org.eclipse.ui.console") != null, expectedValue); //$NON-NLS-1$
				} else if ("junit".equals(kind)) { //$NON-NLS-1$
					return CoreUtil.propertyEquals(Platform.getBundle("org.eclipse.jdt.junit") != null, expectedValue); //$NON-NLS-1$
				}
			}
		}
		return false;
	}

}
