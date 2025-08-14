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
	IInteractionElement getElement(String elementHandle);

	/**
	 * @since 3.9
	 */
	void addListener(IContextListener listener);

	/**
	 * @since 3.9
	 */
	void removeListener(IContextListener listener);

	void addListener(AbstractContextListener listener);

	void removeListener(AbstractContextListener listener);

	void activateContext(String handleIdentifier);

	void deactivateContext(String handleIdentifier);

	void deleteContext(String handleIdentifier);

	IInteractionContext getActiveContext();

	Set<IInteractionElement> getActiveLandmarks();

	Set<IInteractionElement> getActiveDocuments(IInteractionContext context);

	void updateHandle(IInteractionElement element, String newHandle);

	/**
	 * @deprecated use {@link #deleteElements(Collection)}
	 */
	@Deprecated
	void deleteElement(IInteractionElement element);

	/**
	 * @since 3.2
	 */
	void deleteElements(Collection<IInteractionElement> elements);

	/**
	 * @since 3.9
	 */
	void deleteElements(Collection<IInteractionElement> elements, IInteractionContext context);

	IInteractionElement getActiveElement();

	IInteractionElement processInteractionEvent(InteractionEvent event);

	boolean isContextActive();

	/**
	 * Lazily loads set of handles with corresponding contexts.
	 */
	boolean hasContext(String handleIdentifier);

	boolean isContextActivePropertySet();

	boolean isContextCapturePaused();

	/**
	 * NOTE: If pausing ensure to restore to original state.
	 */
	void setContextCapturePaused(boolean paused);

	/**
	 * Returns additional context data stored among the given context using the given identifier. Use {@link IContextContributor} to store
	 * additional data.
	 *
	 * @param context
	 *            Context to query for additional data
	 * @param identifier
	 *            Identifier used to store the requested data
	 * @return InputStream content of the requested file or null if not existent
	 * @since 3.9
	 */
	InputStream getAdditionalContextData(IInteractionContext context, String identifier);
}