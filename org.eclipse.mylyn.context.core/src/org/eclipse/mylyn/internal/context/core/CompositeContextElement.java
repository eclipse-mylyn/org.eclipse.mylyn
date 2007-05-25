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
package org.eclipse.mylar.internal.context.core;

import java.util.*;

import org.eclipse.mylar.context.core.IDegreeOfInterest;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.core.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class CompositeContextElement implements IMylarElement {

	private Set<MylarContextElement> nodes = null;

	private String handle = "<no handle>";

	public CompositeContextElement(String handle, Set<MylarContextElement> nodes) {
		this.nodes = nodes;
		this.handle = handle;
	}

	/**
	 * @return the taskscape with the hightest value TODO: is this always best?
	 */
	public IMylarContext getContext() {
		IMylarElement highestValueNode = null;
		for (IMylarElement node : nodes) {
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
			for (IMylarElement node : nodes) {
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
		for (IMylarElement node : nodes) {
			node.setHandleIdentifier(handle);
		}
	}

	public Set<MylarContextElement> getNodes() {
		return nodes;
	}

	/**
	 * @return empty string if all kinds aren't equal
	 */
	public String getContentType() {
		Set<String> kinds = new HashSet<String>();
		String lastKind = null;
		for (IMylarElement node : nodes) {
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
	public MylarContextRelation getRelation(String targetHandle) {
		Set<MylarContextRelation> edges = new HashSet<MylarContextRelation>();
		for (IMylarElement node : nodes)
			edges.add(node.getRelation(targetHandle));
		if (edges.size() == 0) {
			return null;
		} else if (edges.size() > 1) {
			MylarStatusHandler.log("Multiple edges found in composite, not supported", this);
		}
		return edges.iterator().next();
	}

	public Collection<MylarContextRelation> getRelations() {
		Set<MylarContextRelation> edges = new HashSet<MylarContextRelation>();

		for (MylarContextElement node : nodes)
			edges.addAll(node.getRelations());
		return edges;
	}

	public void clearRelations() {
		for (MylarContextElement node : nodes)
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
