/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.Set;

import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public final class ContextCore {

	public static final String CONTENT_TYPE_RESOURCE = "resource"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static IInteractionContextManager getContextManager() {
		return ContextCorePlugin.getContextManager();
	}

	public static IInteractionContextScaling getCommonContextScaling() {
		return ContextCorePlugin.getDefault().getCommonContextScaling();
	}

	/**
	 * @return null if there are no bridges loaded, null bridge otherwise
	 */
	public static AbstractContextStructureBridge getStructureBridge(Object object) {
		return ContextCorePlugin.getDefault().getStructureBridge(object);
	}

	public static AbstractContextStructureBridge getStructureBridge(String contentType) {
		return ContextCorePlugin.getDefault().getStructureBridge(contentType);
	}

	public static Set<String> getContentTypes() {
		return ContextCorePlugin.getDefault().getContentTypes();
	}

	public static Set<String> getChildContentTypes(String contentType) {
		return ContextCorePlugin.getDefault().getChildContentTypes(contentType);
	}

	public static IContextStore getContextStore() {
		return ContextCorePlugin.getContextStore();
	}

}
