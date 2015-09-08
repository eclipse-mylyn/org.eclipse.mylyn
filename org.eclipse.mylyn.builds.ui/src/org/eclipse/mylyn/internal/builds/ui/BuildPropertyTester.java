/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
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
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * @author Steffen Pingel
 */
public class BuildPropertyTester extends PropertyTester {

	public BuildPropertyTester() {
		// ignore
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IBuild) {
			IBuild build = (IBuild) receiver;
			if ("hasConsole".equals(property)) {
				return CoreUtil.propertyEquals(Platform.getBundle("org.eclipse.ui.console") != null, expectedValue);
			}
			if ("hasTests".equals(property)) {
				return CoreUtil.propertyEquals(build.getTestResult() != null, expectedValue);
			}
		}
		return false;
	}

}
