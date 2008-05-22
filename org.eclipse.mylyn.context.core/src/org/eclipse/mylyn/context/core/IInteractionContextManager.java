/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @since 3.0
 * @noimplement
 */
public interface IInteractionContextManager {

	/**
	 * @return null if the element handle is null or if the element is not found in the active task context.
	 */
	public abstract IInteractionElement getElement(String elementHandle);

	public abstract void addListener(AbstractContextListener listener);

	public abstract void removeListener(AbstractContextListener listener);

	public abstract void activateContext(String handleIdentifier);

	public abstract void deactivateContext(String handleIdentifier);

	public abstract void deleteContext(String handleIdentifier);

	public abstract IInteractionContext getActiveContext();

	public abstract List<IInteractionElement> getActiveLandmarks();

	public abstract Collection<IInteractionElement> getActiveDocuments(IInteractionContext context);

	public abstract void updateHandle(IInteractionElement element, String newHandle);

	public abstract void deleteElement(IInteractionElement element);

	public IInteractionElement getActiveElement();

	public IInteractionElement processInteractionEvent(InteractionEvent event);

	// API-3.0: merge with above
	@Deprecated
	public void processInteractionEvents(List<InteractionEvent> events, boolean propagateToParents);

	public boolean isContextActive();

	/**
	 * Lazily loads set of handles with corresponding contexts.
	 */
	public boolean hasContext(String handleIdentifier);

	public abstract boolean isContextActivePropertySet();

	public abstract boolean isContextCapturePaused();

	/**
	 * NOTE: If pausing ensure to restore to original state.
	 */
	public void setContextCapturePaused(boolean paused);
}