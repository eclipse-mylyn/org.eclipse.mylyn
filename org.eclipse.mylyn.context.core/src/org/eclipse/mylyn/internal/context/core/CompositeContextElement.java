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
/*
 * Created on Dec 29, 2004
 */
package org.eclipse.mylyn.internal.context.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.core.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class CompositeContextElement implements IInteractionElement {

	private Set<InteractionContextElement> nodes = null;

	private String handle = "<no handle>";

	public CompositeContextElement(String handle, Set<InteractionContextElement> nodes) {
		this.nodes = nodes;
		this.handle = handle;
	}

	/**
	 * @return the taskscape with the hightest value TODO: is this always best?
	 */
	public IInteractionContext getContext() {
		IInteractionElement highestValueNode = null;
		for (IInteractionElement node : nodes) {
			if (highestValueNode == null || node.getInterest().getValue() < highestValueNode.getInterest().getValue())
				highestValueNode = node;
		}
		if (highestValueNode != null) {
			return highestValueNode.getContext();
		} else {
			return null;
		}
	}

	public IDegreeOfInterest getInterest() {
		if (nodes.size() == 1) {
			return nodes.iterator().next().getInterest();
		} else {
			CompositeDegreeOfInterest degreeOfInterest = new CompositeDegreeOfInterest();
			for (IInteractionElement node : nodes) {
				degreeOfInterest.getComposedDegreesOfInterest().add(node.getInterest());
			}
			return degreeOfInterest;
		}
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setHandleIdentifier(String handle) {
		this.handle = handle;
		for (IInteractionElement node : nodes) {
			node.setHandleIdentifier(handle);
		}
	}

	public Set<InteractionContextElement> getNodes() {
		return nodes;
	}

	/**
	 * @return empty string if all kinds aren't equal
	 */
	public String getContentType() {
		Set<String> kinds = new HashSet<String>();
		String lastKind = null;
		for (IInteractionElement node : nodes) {
			lastKind = node.getContentType();
			kinds.add(lastKind);
		}
		if (kinds.size() == 1) {
			return lastKind;
		} else {
			return null;
		}
	}

	/**
	 * TODO: need composite edges here
	 */
	public InteractionContextRelation getRelation(String targetHandle) {
		Set<InteractionContextRelation> edges = new HashSet<InteractionContextRelation>();
		for (IInteractionElement node : nodes)
			edges.add(node.getRelation(targetHandle));
		if (edges.size() == 0) {
			return null;
		} else if (edges.size() > 1) {
			MylarStatusHandler.log("Multiple edges found in composite, not supported", this);
		}
		return edges.iterator().next();
	}

	public Collection<InteractionContextRelation> getRelations() {
		Set<InteractionContextRelation> edges = new HashSet<InteractionContextRelation>();

		for (InteractionContextElement node : nodes)
			edges.addAll(node.getRelations());
		return edges;
	}

	public void clearRelations() {
		for (InteractionContextElement node : nodes)
			node.clearRelations();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof CompositeContextElement) {
			CompositeContextElement node = (CompositeContextElement) obj;
			return this.getHandleIdentifier().equals(node.getHandleIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return handle.hashCode();
	}

	@Override
	public String toString() {
		return "composite" + nodes;
	}
}
