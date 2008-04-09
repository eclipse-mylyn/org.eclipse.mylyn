/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public final class ContextCore {

	private static InteractionContextScaling commonContextScaling = new InteractionContextScaling();

	/**
	 * @since 3.0
	 */
	public static IInteractionContextManager getContextManager() {
		return ContextCorePlugin.getContextManager();
	}

	public static IInteractionContextScaling getCommonContextScaling() {
		return commonContextScaling;
	}

}
