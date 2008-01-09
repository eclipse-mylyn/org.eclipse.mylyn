/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Dec 29, 2004
 */
package org.eclipse.mylyn.internal.context.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class CompositeContextElement implements IInteractionElement {

	private List<InteractionContextElement> nodes = null;

	private String handle = "<no handle>";

	private InteractionContextScaling contextScaling;
	
	public CompositeContextElement(String handle, List<InteractionContextElement> nodes, InteractionContextScaling contextScaling) {
		this.nodes = nodes;
		this.handle = handle;
		this.contextScaling = contextScaling;
	}

	/**
	 * @return the context with the highest value 
	 * TODO: is this always best?
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
			// TODO: fix this inefficiency, but not currently used by existing code
			CompositeDegreeOfInterest degreeOfInterest = new CompositeDegreeOfInterest(contextScaling);
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

	public List<InteractionContextElement> getNodes() {
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
			StatusHandler.log("Multiple edges found in composite, not supported", this);
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
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object instanceof CompositeContextElement) {
			CompositeContextElement element = (CompositeContextElement)object;
			return this.getHandleIdentifier().equals(element.getHandleIdentifier());
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
