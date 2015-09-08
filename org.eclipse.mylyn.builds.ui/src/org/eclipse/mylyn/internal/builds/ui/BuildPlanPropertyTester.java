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
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * @author Steffen Pingel
 */
public class BuildPlanPropertyTester extends PropertyTester {

	public BuildPlanPropertyTester() {
		// ignore
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IBuildPlan) {
			IBuildPlan plan = (IBuildPlan) receiver;
			if ("hasBuild".equals(property)) {
				return CoreUtil.propertyEquals(plan.getLastBuild() != null, expectedValue);
			}
		}
		return false;
	}

}
