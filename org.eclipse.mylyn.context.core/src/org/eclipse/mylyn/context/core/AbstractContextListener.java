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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;

/**
 * Override {@link #contextChanged(ContextChangeEvent)} to be notified of context change events.
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
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void contextPreActivated(IInteractionContext context) {
	}

	/**
	 * Invoked after the context is activated.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void contextActivated(IInteractionContext context) {
	}

	/**
	 * Invoked after the context is deactivated.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void contextDeactivated(IInteractionContext context) {
	}

	/**
	 * The context has been cleared, typically done by the user.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void contextCleared(IInteractionContext context) {
	}

	/**
	 * The interest level of one or more elements has changed. The last element in the list is the element invoking the
	 * change.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void interestChanged(List<IInteractionElement> elements) {
	}

	/**
	 * An element with landmark interest has been added to the context.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void landmarkAdded(IInteractionElement element) {
	}

	/**
	 * An element with landmark interest has been removed from the task context.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void landmarkRemoved(IInteractionElement element) {
	}

	/**
	 * One or more elements have been deleted from the task context.
	 * 
	 * @since 3.0
	 * @deprecated use {@link #contextChanged(ContextChangeEvent)} instead
	 */
	@Deprecated
	public void elementsDeleted(List<IInteractionElement> elements) {
	}

	/**
	 * @since 3.2
	 */
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case PRE_ACTIVATED:
			contextPreActivated(event.getContext());
			break;
		case ACTIVATED:
			contextActivated(event.getContext());
			break;
		case DEACTIVATED:
			contextDeactivated(event.getContext());
			break;
		case CLEARED:
			contextCleared(event.getContext());
			break;
		case INTEREST_CHANGED:
			interestChanged(event.getElements());
			break;
		case LANDMARKS_ADDED:
			for (IInteractionElement element : event.getElements()) {
				landmarkAdded(element);
			}
			break;
		case LANDMARKS_REMOVED:
			for (IInteractionElement element : event.getElements()) {
				landmarkRemoved(element);
			}
			break;
		case ELEMENTS_DELETED:
			elementsDeleted(event.getElements());
			break;
		default:
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN,
					"Unknown context changed event type")); //$NON-NLS-1$
		}

	}
}
