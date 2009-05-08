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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author David Green bug 257977 isInteresting
 */
public class InteractionContext implements IInteractionContext {

	private String handleIdentifier;

	private final List<InteractionEvent> interactionHistory;

	private final Map<String, InteractionContextElement> elementMap;

	private final Map<String, IInteractionElement> landmarkMap;

	/**
	 * The last element that was added to this context.
	 */
	private InteractionContextElement activeNode;

	private InteractionEvent lastEdgeEvent;

	private InteractionContextElement lastEdgeNode;

	private String contentLimitedTo;

	private int numUserEvents;

	private final IInteractionContextScaling contextScaling;

	public InteractionContext(String id, IInteractionContextScaling scaling) {
		this.handleIdentifier = id;
		this.contextScaling = scaling;
		this.interactionHistory = new ArrayList<InteractionEvent>();
		this.elementMap = new HashMap<String, InteractionContextElement>();
		this.landmarkMap = new HashMap<String, IInteractionElement>();

		for (InteractionEvent event : interactionHistory) {
			parseInteractionEvent(event);
		}

		for (InteractionContextElement node : elementMap.values()) {
			if (node.getInterest().isLandmark()) {
				landmarkMap.put(node.getHandleIdentifier(), node);
			}
		}

		activeNode = lastEdgeNode;
	}

	public synchronized IInteractionElement parseEvent(InteractionEvent event) {
		interactionHistory.add(event);
		return parseInteractionEvent(event);
	}

	/**
	 * Propagations and predictions are not added as edges
	 */
	private IInteractionElement parseInteractionEvent(InteractionEvent event) {
		if (event.getStructureHandle() == null || event.getKind() == null) {
			return null;
		}

		if (event.getKind().isUserEvent()) {
			numUserEvents++;
		}

		InteractionContextElement node = elementMap.get(event.getStructureHandle());
		if (node == null) {
			if (event instanceof AggregateInteractionEvent) {
				node = new InteractionContextElement(event.getStructureKind(), event.getStructureHandle(), this,
						((AggregateInteractionEvent) event).getEventCountOnCreation());
			} else {
				node = new InteractionContextElement(event.getStructureKind(), event.getStructureHandle(), this);
			}
			elementMap.put(event.getStructureHandle(), node);
		}

		if (event.getKind().isUserEvent() && event instanceof AggregateInteractionEvent) {
			// add the rest of the events that this event represented
			numUserEvents += ((AggregateInteractionEvent) event).getNumCollapsedEvents() - 1;
		}

		if (event.getNavigation() != null
				&& !event.getNavigation().equals("null") && lastEdgeEvent != null //$NON-NLS-1$
				&& lastEdgeNode != null && lastEdgeEvent.getStructureHandle() != null
				&& event.getKind() != InteractionEvent.Kind.PROPAGATION
				&& event.getKind() != InteractionEvent.Kind.PREDICTION) {
			IInteractionElement navigationSource = elementMap.get(lastEdgeEvent.getStructureHandle());
			if (navigationSource != null) {
				InteractionContextRelation edge = lastEdgeNode.getRelation(event.getStructureHandle());
				if (edge == null) {
					edge = new InteractionContextRelation(event.getStructureKind(), event.getNavigation(),
							lastEdgeNode, node, this);
					lastEdgeNode.addEdge(edge);
				}
				DegreeOfInterest doi = (DegreeOfInterest) edge.getInterest();
				doi.addEvent(event);
			}
		}
		DegreeOfInterest doi = (DegreeOfInterest) node.getInterest();

		doi.addEvent(event);
		if (doi.isLandmark()) {
			landmarkMap.put(node.getHandleIdentifier(), node);
		} else {
			landmarkMap.remove(node.getHandleIdentifier()); // TODO: redundant
		}
		if (event.getKind().isUserEvent()) {
			lastEdgeEvent = event;
			lastEdgeNode = node;
			activeNode = node;
		}
		return node;
	}

	public synchronized IInteractionElement get(String elementHandle) {
		if (elementHandle == null) {
			return null;
		} else {
			return elementMap.get(elementHandle);
		}
	}

	public synchronized boolean isInteresting(String elementHandle) {
		InteractionContextElement element = elementMap.get(elementHandle);
		if (element != null) {
			return element.getInterest().isInteresting();
		}
		return false;
	}

	public synchronized List<IInteractionElement> getInteresting() {
		List<IInteractionElement> elements = new ArrayList<IInteractionElement>();
		for (String key : elementMap.keySet()) {
			InteractionContextElement info = elementMap.get(key);
			if (info != null && info.getInterest().isInteresting()) {
				elements.add(info);
			}
		}
		return elements;
	}

	public synchronized List<IInteractionElement> getLandmarks() {
		return new ArrayList<IInteractionElement>(landmarkMap.values());
	}

	public synchronized void updateElementHandle(IInteractionElement element, String newHandle) {
		InteractionContextElement currElement = elementMap.remove(element.getHandleIdentifier());
		if (currElement != null) {
			currElement.setHandleIdentifier(newHandle);
			elementMap.put(newHandle, currElement);
		}
	}

	public synchronized IInteractionElement getActiveNode() {
		return activeNode;
	}

	public synchronized void delete(Collection<IInteractionElement> nodes) {
		// remove elements
		Set<String> handlesToRemove = new HashSet<String>();
		for (IInteractionElement node : nodes) {
			handlesToRemove.add(node.getHandleIdentifier());
			landmarkMap.remove(node.getHandleIdentifier());
			elementMap.remove(node.getHandleIdentifier());

			if (activeNode != null && node.getHandleIdentifier().equals(activeNode.getHandleIdentifier())) {
				activeNode = null;
			}
		}

		// remove events
		List<InteractionEvent> eventsToRemove = new ArrayList<InteractionEvent>();
		for (InteractionEvent event : interactionHistory) {
			if (handlesToRemove.contains(event.getStructureHandle())) {
				eventsToRemove.add(event);
			}
		}
		interactionHistory.removeAll(eventsToRemove);
	}

	public synchronized void delete(IInteractionElement node) {
		delete(Collections.singleton(node));
	}

	public synchronized List<IInteractionElement> getAllElements() {
		return new ArrayList<IInteractionElement>(elementMap.values());
	}

	public String getHandleIdentifier() {
		return handleIdentifier;
	}

	/**
	 * @since 2.1
	 */
	public void setHandleIdentifier(String handle) {
		this.handleIdentifier = handle;
	}

	@Override
	public String toString() {
		return handleIdentifier;
	}

	public synchronized void reset() {
		elementMap.clear();
		interactionHistory.clear();
		landmarkMap.clear();
		activeNode = null;
		numUserEvents = 0;
		lastEdgeEvent = null;
		lastEdgeNode = null;
	}

	public synchronized int getUserEventCount() {
		return numUserEvents;
	}

	public synchronized List<InteractionEvent> getInteractionHistory() {
		return new ArrayList<InteractionEvent>(interactionHistory);
	}

	public synchronized void collapse() {
		List<InteractionEvent> collapsedHistory = new ArrayList<InteractionEvent>();
		for (InteractionContextElement node : elementMap.values()) {
			if (!node.equals(activeNode)) {
				collapseNode(collapsedHistory, node);
			}
		}
		if (activeNode != null) {
			collapseNode(collapsedHistory, activeNode);
		}

		interactionHistory.clear();
		interactionHistory.addAll(collapsedHistory);
	}

	private void collapseNode(List<InteractionEvent> collapsedHistory, InteractionContextElement node) {
		collapsedHistory.addAll(((DegreeOfInterest) node.getInterest()).getCollapsedEvents());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InteractionContext other = (InteractionContext) obj;
		if (contentLimitedTo == null) {
			if (other.contentLimitedTo != null) {
				return false;
			}
		} else if (!contentLimitedTo.equals(other.contentLimitedTo)) {
			return false;
		}
		if (handleIdentifier == null) {
			if (other.handleIdentifier != null) {
				return false;
			}
		} else if (!handleIdentifier.equals(other.handleIdentifier)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentLimitedTo == null) ? 0 : contentLimitedTo.hashCode());
		result = prime * result + ((handleIdentifier == null) ? 0 : handleIdentifier.hashCode());
		return result;
	}

	public IInteractionContextScaling getScaling() {
		return contextScaling;
	}

	public String getContentLimitedTo() {
		return contentLimitedTo;
	}

	public void setContentLimitedTo(String contentLimitedTo) {
		this.contentLimitedTo = contentLimitedTo;
	}
}
