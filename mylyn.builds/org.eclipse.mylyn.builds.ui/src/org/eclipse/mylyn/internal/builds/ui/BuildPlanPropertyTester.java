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
import org.eclipse.mylyn.builds.core.BuildState;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.commons.core.CoreUtil;

/**
 * @author Steffen Pingel
 */
public class BuildPlanPropertyTester extends PropertyTester {

	public BuildPlanPropertyTester() {
		// ignore
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IBuildPlan plan) {
			if ("hasBuild".equals(property)) { //$NON-NLS-1$
				return CoreUtil.propertyEquals(plan.getLastBuild() != null, expectedValue);
			}
			if ("isLastBuildRunning".equals(property)) { //$NON-NLS-1$
				return CoreUtil.propertyEquals(plan.getLastBuild().getState() == BuildState.RUNNING, expectedValue);
			}
		}
		return false;
	}

}
