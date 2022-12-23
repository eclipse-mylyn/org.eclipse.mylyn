/*******************************************************************************
 * Copyright (c) 2004, 2012, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastian Schmidt - bug 155333
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

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

	/**
	 * @since 3.9
	 */
	public abstract void addListener(IContextListener listener);

	/**
	 * @since 3.9
	 */
	public abstract void removeListener(IContextListener listener);

	public abstract void addListener(AbstractContextListener listener);

	public abstract void removeListener(AbstractContextListener listener);

	public abstract void activateContext(String handleIdentifier);

	public abstract void deactivateContext(String handleIdentifier);

	public abstract void deleteContext(String handleIdentifier);

	public abstract IInteractionContext getActiveContext();

	public abstract Set<IInteractionElement> getActiveLandmarks();

	public abstract Set<IInteractionElement> getActiveDocuments(IInteractionContext context);

	public abstract void updateHandle(IInteractionElement element, String newHandle);

	/**
	 * @deprecated use {@link #deleteElements(Collection)}
	 */
	@Deprecated
	public abstract void deleteElement(IInteractionElement element);

	/**
	 * @since 3.2
	 */
	public abstract void deleteElements(Collection<IInteractionElement> elements);

	/**
	 * @since 3.9
	 */
	public abstract void deleteElements(Collection<IInteractionElement> elements, IInteractionContext context);

	public IInteractionElement getActiveElement();

	public IInteractionElement processInteractionEvent(InteractionEvent event);

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

	/**
	 * Returns additional context data stored among the given context using the given identifier. Use
	 * {@link IContextContributor} to store additional data.
	 * 
	 * @param context
	 *            Context to query for additional data
	 * @param identifier
	 *            Identifier used to store the requested data
	 * @return InputStream content of the requested file or null if not existent
	 * @since 3.9
	 */
	public InputStream getAdditionalContextData(IInteractionContext context, String identifier);
}