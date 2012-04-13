/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui.team;

import org.eclipse.osgi.util.NLS;

/**
 * @deprecated use classes in the <code>org.eclipse.mylyn.commons.repositories.ui</code> bundle instead
 */
@Deprecated
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.commons.ui.team.messages"; //$NON-NLS-1$

	public static String NewRepositoryHandler_New_Repository;

	public static String RepositoriesView_Root;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
