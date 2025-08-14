/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.state;

/**
 * @author Steffen Pingel
 */
public abstract class ContextStateParticipant {

	public abstract void clearState(String contextHandle, boolean isActiveContext);

	public abstract boolean isEnabled();

	public abstract void restoreDefaultState(ContextState memento);

	public abstract void restoreState(ContextState memento);

	public abstract void saveDefaultState(ContextState memento);

	public abstract void saveState(ContextState memento, boolean allowModifications);

}
