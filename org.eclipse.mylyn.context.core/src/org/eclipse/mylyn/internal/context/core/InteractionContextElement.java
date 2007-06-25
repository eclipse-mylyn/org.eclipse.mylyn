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

	private DegreeOfInterest interest;

	private InteractionContext context;

	private Map<String/* target handle */, InteractionContextRelation> edges = new HashMap<String, InteractionContextRelation>();

	public InteractionContextElement(String kind, String elementHandle, InteractionContext context) {
		if (elementHandle == null)
			throw new RuntimeException("malformed context: null handle");
		interest = new DegreeOfInterest(context, context.getScalingFactors());
		this.handle = elementHandle;
		this.kind = kind;
		this.context = context;
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setHandleIdentifier(String handle) {
		this.handle = handle;
	}

	public String getContentType() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Collection<InteractionContextRelation> getRelations() {
		return edges.values();
	}

	public InteractionContextRelation getRelation(String targetHandle) {
		return edges.get(targetHandle);
	}

	/**
	 * TODO: reduce visibility
	 */
	public void addEdge(InteractionContextRelation edge) {
		edges.put(edge.getTarget().getHandleIdentifier(), edge);
	}

	public void clearRelations() {
		edges.clear();
	}

	void removeEdge(IInteractionRelation edge) {
		edges.remove(edge.getTarget().getHandleIdentifier());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getHandleIdentifier() == null)
			return false;
		if (obj instanceof InteractionContextElement) {
			InteractionContextElement node = (InteractionContextElement) obj;
			return this.getHandleIdentifier().equals(node.getHandleIdentifier());
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

	public IDegreeOfInterest getInterest() {
		return interest;
	}

	public InteractionContext getContext() {
		return context;
	}

	@Override
	public String toString() {
		return handle;
	}

}