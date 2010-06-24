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

/**
 * @author Steffen Pingel
 */
public class BuildsUiConstants {

	/**
	 * Refresh every 15 minutes by default.
	 */
	public static final int DEFAULT_REFRESH_INTERVAL = 15 * 60 * 1000;

	public static final String ID_PREFERENCE_PAGE_BUILDS = "org.eclipse.mylyn.builds.preferences.BuildsPage"; //$NON-NLS-1$

	public static final int MIN_REFRESH_INTERVAL = 1 * 60 * 1000;

	public static final String PREF_AUTO_REFRESH_ENABLED = "refresh.enabled"; //$NON-NLS-1$

	public static final String PREF_AUTO_REFRESH_INTERVAL = "refresh.interval"; //$NON-NLS-1$

}
