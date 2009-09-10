/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.context.core;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * @author Shawn Minto
 * @since 3.2
 */
public class ContextChangeEvent {

	/**
	 * @author Shawn Minto
	 * @since 3.2
	 */
	public enum ContextChangeKind {
		PRE_ACTIVATED, ACTIVATED, DEACTIVATED, CLEARED, INTEREST_CHANGED, LANDMARKS_ADDED, LANDMARKS_REMOVED, ELEMENTS_DELETED;
	}

	private final String contextHandle;

	private final IInteractionContext context;

	private final ContextChangeKind eventKind;

	private final List<IInteractionElement> elements;

	private final boolean isExplicitManipulation;

	public ContextChangeEvent(ContextChangeKind eventKind, String contextHandle, IInteractionContext context,
			List<IInteractionElement> elements) {
		this(eventKind, contextHandle, context, elements, false);
	}

	/**
	 * @since 3.3
	 */
	public ContextChangeEvent(ContextChangeKind eventKind, String contextHandle, IInteractionContext context,
			List<IInteractionElement> elements, boolean isExplicitManipulation) {
		Assert.isNotNull(eventKind);
		this.contextHandle = contextHandle;
		this.context = context;
		this.eventKind = eventKind;
		if (elements == null) {
			this.elements = Collections.emptyList();
		} else {
			this.elements = elements;
		}
		this.isExplicitManipulation = isExplicitManipulation;
	}

	/**
	 * The Type of context event that occurred
	 * 
	 * @since 3.2
	 */
	public ContextChangeKind getEventKind() {
		return eventKind;
	}

	/**
	 * The elements that were manipulated for the event (may be empty)
	 * 
	 * @since 3.2
	 */
	public List<IInteractionElement> getElements() {
		return elements;
	}

	/**
	 * The handle of the context that was changed (Can be null if a composite context with multiple
	 * IInteractionContext's is changed)
	 * 
	 * @since 3.2
	 */
	public String getContextHandle() {
		return contextHandle;
	}

	/**
	 * The context that was changed (Can be null e.g. context deleted)
	 * 
	 * @since 3.2
	 */
	public IInteractionContext getContext() {
		return context;
	}

	/**
	 * Utility for whether the manipulated context is the active one
	 * 
	 * @since 3.2
	 */
	public boolean isActiveContext() {
		IInteractionContext activeContext = ContextCore.getContextManager().getActiveContext();
		return activeContext != null && activeContext.getHandleIdentifier() != null
				&& activeContext.getHandleIdentifier().equals(contextHandle);
	}

	/**
	 * Whether the event was a result of a users explicit manipulation of the context (i.e. mark as landmark) This can
	 * only be true for {@link ContextChangeKind#LANDMARKS_ADDED},{@link ContextChangeKind#LANDMARKS_REMOVED} and
	 * {@link ContextChangeKind#ELEMENTS_DELETED}
	 * 
	 * @since 3.3
	 */
	public boolean isExplicitManipulation() {
		return isExplicitManipulation;
	}
}