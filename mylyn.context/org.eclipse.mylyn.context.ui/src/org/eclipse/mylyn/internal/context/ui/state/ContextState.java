/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.state;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * Stores workspace specific settings for a context.
 *
 * @author Steffen Pingel
 */
public class ContextState {

	private boolean dirty;

	private final IInteractionContext context;

	private String contextHandle;

	private final XMLMemento memento;

	public ContextState(IInteractionContext context, String contextHandle, XMLMemento memento) {
		Assert.isNotNull(memento);
		Assert.isNotNull(contextHandle);
		this.context = context;
		this.contextHandle = contextHandle;
		this.memento = memento;
	}

	void setContextHandle(String contextHandle) {
		this.contextHandle = contextHandle;
	}

	// XXX equals and hashcode should probably use the handle to determine equality
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContextState)) {
			return false;
		}
		return context.equals(((ContextState) obj).context);
	}

	@Override
	public int hashCode() {
		return context.hashCode();
	}

	public String getContextHandle() {
		return contextHandle;
	}

	public IInteractionContext getContext() {
		return context;
	}

	XMLMemento getMemento() {
		return memento;
	}

	public IMemento getMemento(String type) {
		return memento.getChild(type);
	}

	public IMemento createMemento(String type) {
		dirty = true;
		return memento.createChild(type);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void removeMemento(String type) {
		memento.createChild(type);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
