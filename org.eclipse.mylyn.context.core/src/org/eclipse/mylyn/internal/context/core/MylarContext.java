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
 * Created on Jul 22, 2004
 */
package org.eclipse.mylar.internal.context.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class MylarContext implements IMylarContext {

	private String handleIdentifier;

	private List<InteractionEvent> interactionHistory = new ArrayList<InteractionEvent>();

	protected ConcurrentHashMap<String, MylarContextElement> elementMap;

	protected Map<String, IMylarElement> landmarkMap;
	
	protected MylarContextElement activeNode = null;

	private InteractionEvent lastEdgeEvent = null;

	private MylarContextElement lastEdgeNode = null;

	private int numUserEvents = 0;

	protected ScalingFactors scalingFactors;

	void parseInteractionHistory() {
		elementMap = new ConcurrentHashMap<String, MylarContextElement>();
		landmarkMap = new HashMap<String, IMylarElement>();
		for (InteractionEvent event : interactionHistory)
			parseInteractionEvent(event);
		updateLandmarks();
		activeNode = lastEdgeNode;
	}

	public MylarContext(String id, ScalingFactors scaling) {
		this.handleIdentifier = id;
		this.scalingFactors = scaling;
		parseInteractionHistory();
	}

	public IMylarElement parseEvent(InteractionEvent event) {
		interactionHistory.add(event);
		return parseInteractionEvent(event);
	}

	/**
	 * Propagations and predictions are not added as edges
	 */
	private IMylarElement parseInteractionEvent(InteractionEvent event) {
		if (event.getStructureHandle() == null) {
			return null;
		}
		
		if (event.getKind().isUserEvent()) {
			numUserEvents++;
		}
		MylarContextElement node = elementMap.get(event.getStructureHandle());
		if (node == null) {
			node = new MylarContextElement(event.getStructureKind(), event.getStructureHandle(), this);
			elementMap.put(event.getStructureHandle(), node);
		}

		if (event.getNavigation() != null && !event.getNavigation().equals("null") && lastEdgeEvent != null
				&& lastEdgeNode != null && event.getKind() != InteractionEvent.Kind.PROPAGATION
				&& event.getKind() != InteractionEvent.Kind.PREDICTION) {
			IMylarElement navigationSource = elementMap.get(lastEdgeEvent.getStructureHandle());
			if (navigationSource != null) {
				MylarContextRelation edge = lastEdgeNode.getRelation(event.getStructureHandle());
				if (edge == null) {
					edge = new MylarContextRelation(event.getStructureKind(), event.getNavigation(), lastEdgeNode, node,
							this);
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
		for (MylarContextElement node : elementMap.values()) {
			if (node.getInterest().isLandmark())
				landmarkMap.put(node.getHandleIdentifier(), node);
		}
	}

	public IMylarElement get(String elementHandle) {
		if (elementHandle == null) {
			return null;
		} else {
			return elementMap.get(elementHandle);
		}
	}

	public List<IMylarElement> getInteresting() {
		List<IMylarElement> elements = new ArrayList<IMylarElement>();
		synchronized (elementMap) {
//			Set<String> keys = Collections.synchronizedSet(elementMap.keySet());
			for (String key : elementMap.keySet()) {
				MylarContextElement info = elementMap.get(key);
				if (info != null && info.getInterest().isInteresting()) {
					elements.add(info);
				}
			}
		}
		return elements;
	}

	public List<IMylarElement> getLandmarkMap() {
		return Collections.unmodifiableList(new ArrayList<IMylarElement>(landmarkMap.values()));
	}

	public void updateElementHandle(IMylarElement element, String newHandle) {
		MylarContextElement currElement = elementMap.remove(element.getHandleIdentifier());
		if (currElement != null) {
			currElement.setHandleIdentifier(newHandle);
			elementMap.put(newHandle, currElement);
		}
	}

	public IMylarElement getActiveNode() {
		return activeNode;
	}

	public void delete(IMylarElement node) {
		landmarkMap.remove(node.getHandleIdentifier());
		elementMap.remove(node.getHandleIdentifier());
	}

	public synchronized List<IMylarElement> getAllElements() {
		return new ArrayList<IMylarElement>(elementMap.values());
	}

	public String getHandleIdentifier() {
		return handleIdentifier;
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
		for (MylarContextElement node : elementMap.values()) {
			if (!node.equals(activeNode)) {
				collapseNode(collapsedHistory, node);
			}
		}
		collapseNode(collapsedHistory, activeNode);
		interactionHistory.clear();
		interactionHistory.addAll(collapsedHistory);
	}

	private void collapseNode(List<InteractionEvent> collapsedHistory, MylarContextElement node) {
		if (node != null) {
			collapsedHistory.addAll(((DegreeOfInterest) node.getInterest()).getCollapsedEvents());
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof MylarContext))
			return false;
		MylarContext context = (MylarContext) object;

		return (handleIdentifier == null ? context.handleIdentifier == null : handleIdentifier
				.equals(context.handleIdentifier))
				&& (interactionHistory == null ? context.interactionHistory == null : interactionHistory
						.equals(context.interactionHistory))
				&& (elementMap == null ? context.elementMap == null : elementMap.equals(context.elementMap))
				&& (activeNode == null ? context.activeNode == null : activeNode.equals(context.activeNode))
				&& (landmarkMap == null ? context.landmarkMap == null : landmarkMap.equals(context.landmarkMap))
				&& (scalingFactors == null ? context.scalingFactors == null : scalingFactors.equals(context.scalingFactors))
				&& (numUserEvents == context.numUserEvents);
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
		if (scalingFactors != null)
			hashCode += scalingFactors.hashCode();
		hashCode += 37 * numUserEvents;
		return hashCode;
	}

	public ScalingFactors getScalingFactors() {
		return scalingFactors;
	}
}
