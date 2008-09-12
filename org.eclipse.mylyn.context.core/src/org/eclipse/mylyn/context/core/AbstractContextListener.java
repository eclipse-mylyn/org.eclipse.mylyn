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

import java.util.List;

/**
 * Override methods in this class in order to be notified of the corresponding events.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 * @since 3.0
 */
public abstract class AbstractContextListener {

	/**
	 * Invoked before the context is activated.
	 * 
	 * @since 3.0
	 */
	public void contextPreActivated(IInteractionContext context) {
	}

	/**
	 * Invoked after the context is activated.
	 * 
	 * @since 3.0
	 */
	public void contextActivated(IInteractionContext context) {
	}

	/**
	 * Invoked after the context is deactivated.
	 * 
	 * @since 3.0
	 */
	public void contextDeactivated(IInteractionContext context) {
	}

	/**
	 * The context has been cleared, typically done by the user.
	 * 
	 * @since 3.0
	 */
	public void contextCleared(IInteractionContext context) {
	}

	/**
	 * The interest level of one or more elements has changed. The last element in the list is the element invoking the
	 * change.
	 * 
	 * @since 3.0
	 */
	public void interestChanged(List<IInteractionElement> elements) {
	}

	/**
	 * An element with landmark interest has been added to the context.
	 * 
	 * @since 3.0
	 */
	public void landmarkAdded(IInteractionElement element) {
	}

	/**
	 * An element with landmark interest has been removed from the task context.
	 * 
	 * @since 3.0
	 */
	public void landmarkRemoved(IInteractionElement element) {
	}

	/**
	 * One or more elements have been deleted from the task context.
	 * 
	 * @since 3.0
	 */
	public void elementsDeleted(List<IInteractionElement> elements) {
	}

}
