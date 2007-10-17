/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Jul 22, 2004
 */
package org.eclipse.mylyn.internal.context.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class InteractionContext implements IInteractionContext {

	private String handleIdentifier;

	private List<InteractionEvent> interactionHistory = new CopyOnWriteArrayList<InteractionEvent>();

	protected ConcurrentHashMap<String, InteractionContextElement> elementMap;

	protected Map<String, IInteractionElement> landmarkMap;

	protected InteractionContextElement activeNode = null;

	private InteractionEvent lastEdgeEvent = null;

	private InteractionContextElement lastEdgeNode = null;

	public String contentLimitedTo = null;

	private int numUserEvents = 0;

	protected InteractionContextScaling contextScaling;

	void parseInteractionHistory() {
		elementMap = new ConcurrentHashMap<String, InteractionContextElement>();
		landmarkMap = new HashMap<String, IInteractionElement>();
		for (InteractionEvent event : interactionHistory)
			parseInteractionEvent(event);
		updateLandmarks();
		activeNode = lastEdgeNode;
	}

	public InteractionContext(String id, InteractionContextScaling scaling) {
		this.handleIdentifier = id;
		this.contextScaling = scaling;
		parseInteractionHistory();
	}

	public IInteractionElement parseEvent(InteractionEvent event) {
		interactionHistory.add(event);
		return parseInteractionEvent(event);
	}

	/**
	 * Propagations and predictions are not added as edges
	 */
	private IInteractionElement parseInteractionEvent(InteractionEvent event) {
		if (event.getStructureHandle() == null) {
			return null;
		}

		if (event.getKind().isUserEvent()) {
			numUserEvents++;
		}

		InteractionContextElement node = elementMap.get(event.getStructureHandle());
		if (node == null) {
			node = new InteractionContextElement(event.getStructureKind(), event.getStructureHandle(), this);
			elementMap.put(event.getStructureHandle(), node);
		}

		if (event.getNavigation() != null && !event.getNavigation().equals("null") && lastEdgeEvent != null
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

	private void updateLandmarks() {
		// landmarks = new HashMap<String, ITaskscapeNode>();
		for (InteractionContextElement node : elementMap.values()) {
			if (node.getInterest().isLandmark())
				landmarkMap.put(node.getHandleIdentifier(), node);
		}
	}

	public IInteractionElement get(String elementHandle) {
		if (elementHandle == null) {
			return null;
		} else {
			return elementMap.get(elementHandle);
		}
	}

	public List<IInteractionElement> getInteresting() {
		List<IInteractionElement> elements = new ArrayList<IInteractionElement>();
		synchronized (elementMap) {
//			Set<String> keys = Collections.synchronizedSet(elementMap.keySet());
			for (String key : elementMap.keySet()) {
				InteractionContextElement info = elementMap.get(key);
				if (info != null && info.getInterest().isInteresting()) {
					elements.add(info);
				}
			}
		}
		return elements;
	}

	public List<IInteractionElement> getLandmarkMap() {
		return Collections.unmodifiableList(new ArrayList<IInteractionElement>(landmarkMap.values()));
	}

	public void updateElementHandle(IInteractionElement element, String newHandle) {
		InteractionContextElement currElement = elementMap.remove(element.getHandleIdentifier());
		if (currElement != null) {
			currElement.setHandleIdentifier(newHandle);
			elementMap.put(newHandle, currElement);
		}
	}

	public IInteractionElement getActiveNode() {
		return activeNode;
	}

	public void delete(IInteractionElement node) {
		landmarkMap.remove(node.getHandleIdentifier());
		elementMap.remove(node.getHandleIdentifier());
	}

	public List<IInteractionElement> getAllElements() {
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

	public void reset() {
		interactionHistory.clear();
		elementMap.clear();
		interactionHistory.clear();
		landmarkMap.clear();
		activeNode = null;
	}

	public int getUserEventCount() {
		return numUserEvents;
	}

	/**
	 * TODO: make unmodifiable?
	 */
	public List<InteractionEvent> getInteractionHistory() {
		return interactionHistory;
	}

	public void collapse() {
		List<InteractionEvent> collapsedHistory = new ArrayList<InteractionEvent>();
		for (InteractionContextElement node : elementMap.values()) {
			if (!node.equals(activeNode)) {
				collapseNode(collapsedHistory, node);
			}
		}
		collapseNode(collapsedHistory, activeNode);
		interactionHistory.clear();
		interactionHistory.addAll(collapsedHistory);
	}

	private void collapseNode(List<InteractionEvent> collapsedHistory, InteractionContextElement node) {
		if (node != null) {
			collapsedHistory.addAll(((DegreeOfInterest) node.getInterest()).getCollapsedEvents());
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof InteractionContext))
			return false;
		InteractionContext context = (InteractionContext) object;

		return (handleIdentifier == null ? context.handleIdentifier == null
				: handleIdentifier.equals(context.handleIdentifier))
				&& (interactionHistory == null ? context.interactionHistory == null
						: interactionHistory.equals(context.interactionHistory))
				&& (elementMap == null ? context.elementMap == null : elementMap.equals(context.elementMap))
				&& (activeNode == null ? context.activeNode == null : activeNode.equals(context.activeNode))
				&& (landmarkMap == null ? context.landmarkMap == null : landmarkMap.equals(context.landmarkMap))
				&& (contextScaling == null ? context.contextScaling == null
						: contextScaling.equals(context.contextScaling)) && (numUserEvents == context.numUserEvents);
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		if (handleIdentifier != null)
			hashCode += handleIdentifier.hashCode();
		if (interactionHistory != null)
			hashCode += interactionHistory.hashCode();
		if (elementMap != null)
			hashCode += elementMap.hashCode();
		if (activeNode != null)
			hashCode += activeNode.hashCode();
		if (landmarkMap != null)
			hashCode += landmarkMap.hashCode();
		if (contextScaling != null)
			hashCode += contextScaling.hashCode();
		hashCode += 37 * numUserEvents;
		return hashCode;
	}

	public InteractionContextScaling getContextScaling() {
		return contextScaling;
	}

	public String getContentLimitedTo() {
		return contentLimitedTo;
	}

	public void setContentLimitedTo(String contentLimitedTo) {
		this.contentLimitedTo = contentLimitedTo;
	}

	public void setContextScaling(InteractionContextScaling contextScaling) {
		this.contextScaling = contextScaling;
	}
}
