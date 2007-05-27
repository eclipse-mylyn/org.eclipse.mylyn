/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.context.core;

import java.util.List;


/**
 * @author Mik Kersten
 */
public interface IInteractionContextListener {

	/**
	 * The context is now active, e.g. as a result of a task activation.
	 */
	public void contextActivated(IInteractionContext context);

	/**
	 * The context has been deactivated, e.g. as a result of a task deactivation.
	 */
	public void contextDeactivated(IInteractionContext context);
	
	/**
	 * The context has been cleared, typically done by the user.
	 */
	public void contextCleared(IInteractionContext context);

	/**
	 * Called when the interest level for multiple elements changes, sorted
	 * according to the containment hierarchy. The last element is the element
	 * invoking the change.
	 */
	public void interestChanged(List<IInteractionElement> elements);

	/**
	 * @param newLandmarks
	 *            list of IJavaElement(s)
	 */
	public void landmarkAdded(IInteractionElement element);

	/**
	 * @param newLandmarks
	 *            list of IJavaElement(s)
	 */
	public void landmarkRemoved(IInteractionElement element);

	public void elementDeleted(IInteractionElement element);
	
	public void relationsChanged(IInteractionElement element);
}
