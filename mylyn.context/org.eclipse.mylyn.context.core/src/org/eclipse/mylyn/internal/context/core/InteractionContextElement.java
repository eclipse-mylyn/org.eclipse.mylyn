/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;

/**
 * Can only have one edge from a node to a particular target.
 * 
 * @author Mik Kersten
 */
public class InteractionContextElement implements IInteractionElement {

	private String handle;

	private String kind;

	private final DegreeOfInterest interest;

	private final InteractionContext context;

	private final Map<String/* target handle */, InteractionContextRelation> edges = new HashMap<>();

	public InteractionContextElement(String kind, String elementHandle, InteractionContext context) {
		this(kind, elementHandle, context, -1);
	}

	public InteractionContextElement(String kind, String elementHandle, InteractionContext context,
			int eventCountOnCreation) {
		if (elementHandle == null) {
			throw new RuntimeException("malformed context: null handle"); //$NON-NLS-1$
		}
		interest = new DegreeOfInterest(context, context.getScaling(), eventCountOnCreation);
		handle = elementHandle.intern();
		this.kind = kind != null ? kind.intern() : null;
		this.context = context;
	}

	@Override
	public String getHandleIdentifier() {
		return handle;
	}

	@Override
	public void setHandleIdentifier(String handle) {
		this.handle = handle;
	}

	@Override
	public String getContentType() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	@Override
	public Collection<InteractionContextRelation> getRelations() {
		return edges.values();
	}

	@Override
	public InteractionContextRelation getRelation(String targetHandle) {
		return edges.get(targetHandle);
	}

	/**
	 * TODO: reduce visibility
	 */
	public void addEdge(InteractionContextRelation edge) {
		edges.put(edge.getTarget().getHandleIdentifier(), edge);
	}

	@Override
	public void clearRelations() {
		edges.clear();
	}

	void removeEdge(IInteractionRelation edge) {
		edges.remove(edge.getTarget().getHandleIdentifier());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getHandleIdentifier() == null) {
			return false;
		}
		if (obj instanceof InteractionContextElement node) {
			return getHandleIdentifier().equals(node.getHandleIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (handle != null) {
			return handle.hashCode();
		} else {
			return super.hashCode();
		}
	}

	@Override
	public IDegreeOfInterest getInterest() {
		return interest;
	}

	@Override
	public InteractionContext getContext() {
		return context;
	}

	@Override
	public String toString() {
		return handle;
	}

}
