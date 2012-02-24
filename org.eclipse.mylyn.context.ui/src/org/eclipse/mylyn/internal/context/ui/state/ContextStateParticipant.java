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

	public abstract void saveState(ContextState memento);

}
