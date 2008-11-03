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

package org.eclipse.mylyn.internal.context.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class CompositeContextElement implements IInteractionElement {

	private final List<InteractionContextElement> nodes;

	private String handle;

	private final IInteractionContextScaling contextScaling;

	public CompositeContextElement(String handle, List<InteractionContextElement> nodes,
			IInteractionContextScaling contextScaling) {
		Assert.isNotNull(handle);
		this.handle = handle;
		this.nodes = nodes;
		this.contextScaling = contextScaling;
	}

	/**
	 * @return the context with the highest value TODO: is this always best?
	 */
	public IInteractionContext getContext() {
		IInteractionElement highestValueNode = null;
		for (IInteractionElement node : nodes) {
			if (highestValueNode == null || node.getInterest().getValue() < highestValueNode.getInterest().getValue()) {
				highestValueNode = node;
			}
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
		Assert.isNotNull(handle);
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
	public IInteractionRelation getRelation(String targetHandle) {
		Set<IInteractionRelation> edges = new HashSet<IInteractionRelation>();
		for (IInteractionElement node : nodes) {
			edges.add(node.getRelation(targetHandle));
		}
		if (edges.size() == 0) {
			return null;
		} else if (edges.size() > 1) {
			StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
					"Multiple edges found in composite, not supported")); //$NON-NLS-1$
		}
		return edges.iterator().next();
	}

	public Collection<InteractionContextRelation> getRelations() {
		Set<InteractionContextRelation> edges = new HashSet<InteractionContextRelation>();

		for (InteractionContextElement node : nodes) {
			edges.addAll(node.getRelations());
		}
		return edges;
	}

	public void clearRelations() {
		for (InteractionContextElement node : nodes) {
			node.clearRelations();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof CompositeContextElement) {
			CompositeContextElement element = (CompositeContextElement) object;
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
		return "composite" + nodes; //$NON-NLS-1$
	}
}
