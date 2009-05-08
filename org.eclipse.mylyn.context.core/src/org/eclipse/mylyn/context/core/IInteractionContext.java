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

import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * A model of task context weighted by interaction based on frequency and recency of access, as determined by the
 * degree-of-interest weighting mechanism.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @author David Green bug 257977 isInteresting
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface IInteractionContext {

	/**
	 * Returns the unique handle identifier of the context.
	 * 
	 * @return null if no unique handle, e.g. if a composite context
	 * @since 2.0
	 */
	public abstract String getHandleIdentifier();

	/**
	 * Returns a list of all interesting interaction elements. Modifying the list will not affect the context.
	 * 
	 * @since 2.0
	 */
	public abstract List<InteractionEvent> getInteractionHistory();

	/**
	 * indicate if the interaction element identified by the given handle is interesting
	 * 
	 * @param elementHandle
	 *            the {@link IInteractionElement#getHandleIdentifier() handle identifier}
	 * 
	 * @return true if an {@link #getInteresting() interesting} interaction element exists with the given handle
	 * @since 3.2
	 */
	public boolean isInteresting(String elementHandle);

	/**
	 * Returns a list of all interesting interaction elements. Modifying the list will not affect the context.
	 * 
	 * @since 2.0
	 */
	public List<IInteractionElement> getInteresting();

	/**
	 * Returns a list of all interaction elements that are landmarks. Modifying the list will not affect the context.
	 * 
	 * @since 2.2
	 */
	public List<IInteractionElement> getLandmarks();

	/**
	 * 
	 * @since 2.0
	 */
	public abstract IInteractionElement get(String element);

	/**
	 * 
	 * @since 2.0
	 */
	public abstract IInteractionElement getActiveNode();

	/**
	 * 
	 * @since 2.0
	 */
	public abstract void delete(IInteractionElement element);

	/**
	 * @since 3.2
	 */
	public abstract void delete(Collection<IInteractionElement> elements);

	/**
	 * @since 2.0
	 */
	public abstract void updateElementHandle(IInteractionElement element, String newHandle);

	/**
	 * Returns a list of all interaction elements. Modifying the list will not affect the context.
	 * 
	 * @since 2.0
	 */
	public abstract List<IInteractionElement> getAllElements();

	/**
	 * @since 3.0
	 */
	public IInteractionContextScaling getScaling();

	/**
	 * @since 2.2
	 */
	public String getContentLimitedTo();

	/**
	 * @since 2.2
	 */
	public void setContentLimitedTo(String contentLimitedTo);

}
