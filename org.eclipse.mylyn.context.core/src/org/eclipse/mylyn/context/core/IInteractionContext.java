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
 * @noimplement
 * @since 2.0
 */
public interface IInteractionContext {

	/**
	 * @return null if no unique handle, e.g. if a composite context
	 */
	public abstract String getHandleIdentifier();

	public abstract List<InteractionEvent> getInteractionHistory();

	public List<IInteractionElement> getInteresting();

	/**
	 * @since 2.2
	 */
	public List<IInteractionElement> getLandmarks();

	public abstract IInteractionElement get(String element);

	public abstract IInteractionElement getActiveNode();

	public abstract void delete(IInteractionElement element);

	/**
	 * @since 3.2
	 */
	public abstract void delete(Collection<IInteractionElement> elements);

	public abstract void updateElementHandle(IInteractionElement element, String newHandle);

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
