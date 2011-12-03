/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("available".equals(property)) {
			if (args.length > 0) {
				String kind = (String) args[0];
				if ("console".equals(kind)) {
					return CoreUtil.propertyEquals(Platform.getBundle("org.eclipse.ui.console") != null, expectedValue);
				} else if ("junit".equals(kind)) {
					return CoreUtil.propertyEquals(Platform.getBundle("org.eclipse.jdt.junit") != null, expectedValue);
				}
			}
		}
		return false;
	}

}
