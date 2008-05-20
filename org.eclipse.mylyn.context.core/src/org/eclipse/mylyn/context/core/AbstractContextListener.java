/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.List;

/**
 * Notified of changes to the context model activity.
 * 
 * API-3.0: rename to InteractionContextListener, consider breaking out interest listener, consider removing relations
 * stuff
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @since 3.0
 */
public abstract class AbstractContextListener {

	/**
	 * @param context
	 *            can be null
	 * @since 3.0
	 */
	public void contextPreActivated(IInteractionContext context) {
	}

	/**
	 * The context is now active, e.g. as a result of a task activation.
	 */
	public void contextActivated(IInteractionContext context) {
	}

	/**
	 * The context has been deactivated, e.g. as a result of a task deactivation.
	 */
	public void contextDeactivated(IInteractionContext context) {
	}

	/**
	 * The context has been cleared, typically done by the user.
	 */
	public void contextCleared(IInteractionContext context) {
	}

	/**
	 * Called when the interest level for multiple elements changes, sorted according to the containment hierarchy. The
	 * last element is the element invoking the change.
	 */
	public void interestChanged(List<IInteractionElement> elements) {
	}

	/**
	 * @param newLandmarks
	 *            list of IJavaElement(s)
	 */
	public void landmarkAdded(IInteractionElement element) {
	}

	/**
	 * @param newLandmarks
	 *            list of IJavaElement(s)
	 */
	public void landmarkRemoved(IInteractionElement element) {
	}

	/**
	 * @since 3.0
	 */
	public void elementsDeleted(List<IInteractionElement> elements) {
	}

}
