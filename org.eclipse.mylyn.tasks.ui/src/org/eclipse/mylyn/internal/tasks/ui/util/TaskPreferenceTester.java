/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Steffen Pingel
 */
public class TaskPreferenceTester extends PropertyTester {

	private static final String PROPERTY_SHOW_TRIM = "showTrim"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROPERTY_SHOW_TRIM.equals(property)) {
			return CoreUtil.propertyEquals(shouldShowTrim(), expectedValue);
		}
		return false;
	}

	private boolean shouldShowTrim() {
		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		return uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.SHOW_TRIM);
	}

}
