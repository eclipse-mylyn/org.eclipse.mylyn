/*******************************************************************************
 * Copyright (c) 2011, 2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.docs.epub.ui.commands;

import org.eclipse.osgi.util.NLS;

/**
 * @author Torkild U. Resheim
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.docs.epub.ui.commands.messages"; //$NON-NLS-1$

	public static String ConvertMarkupToEPUB_fileExistsOverwrite;

	public static String ConvertMarkupToEPUB_overwrite;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
