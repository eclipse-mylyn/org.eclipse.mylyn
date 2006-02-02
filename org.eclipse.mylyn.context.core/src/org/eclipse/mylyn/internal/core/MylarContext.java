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
package org.eclipse.mylar.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.internal.core.dt.MylarInterest;

/**
 * @author Mik Kersten
 */
public class MylarContext implements IMylarContext {

	private String handleIdentifier;

	private List<InteractionEvent> interactionHistory = new ArrayList<InteractionEvent>();

	protected Map<String, MylarContextElement> nodes = new HashMap<String, MylarContextElement>();

	protected MylarContextElement activeNode = null;

	protected List tempRaised = new ArrayList();

	protected Map<String, IMylarElement> landmarks;

	private InteractionEvent lastEdgeEvent = null;

	private MylarContextElement lastEdgeNode = null;

	private int numUserEvents = 0;

	protected ScalingFactors scalingFactors;

	void parseInteractionHistory() {
		nodes = new HashMap<String, MylarContextElement>();
		landmarks = new HashMap<String, IMylarElement>();
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
	 * Propagations and predictions are not addes as edges
	 */
	@MylarInterest(level = MylarInterest.Level.LANDMARK)
	private IMylarElement parseInteractionEvent(InteractionEvent event) {
		if (event.getKind().isUserEvent())
			numUserEvents++;
		MylarContextElement node = nodes.get(event.getStructureHandle());
		if (node == null) {
			node = new MylarContextElement(event.getContentType(), event.getStructureHandle(), this);
			nodes.put(event.getStructureHandle(), node);
		}

		if (event.getNavigation() != null && !event.getNavigation().equals("null") && lastEdgeEvent != null
				&& lastEdgeNode != null && event.getKind() != InteractionEvent.Kind.PROPAGATION
				&& event.getKind() != InteractionEvent.Kind.PREDICTION) {
			IMylarElement navigationSource = nodes.get(lastEdgeEvent.getStructureHandle());
			if (navigationSource != null) {
				MylarContextRelation edge = lastEdgeNode.getRelation(event.getStructureHandle());
				if (edge == null) {
					edge = new MylarContextRelation(event.getContentType(), event.getNavigation(), lastEdgeNode, node,
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
			landmarks.put(node.getHandleIdentifier(), node);
		} else {
			landmarks.remove(node.getHandleIdentifier()); // TODO: redundant
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
		for (MylarContextElement node : nodes.values()) {
			if (node.getInterest().isLandmark())
				landmarks.put(node.getHandleIdentifier(), node);
		}
	}

	public IMylarElement get(String elementHandle) {
		return nodes.get(elementHandle);
	}

	public List<IMylarElement> getInteresting() {
		List<IMylarElement> elements = new ArrayList<IMylarElement>();

		for (String key : new ArrayList<String>(nodes.keySet())) {
			MylarContextElement info = nodes.get(key);
			if (info.getInterest().isInteresting()) {
				elements.add(info);
			}
		}
		return elements;
	}

	public List<IMylarElement> getLandmarks() {
		return Collections.unmodifiableList(new ArrayList<IMylarElement>(landmarks.values()));
	}

	public void updateElementHandle(IMylarElement element, String newHandle) {
		MylarContextElement currElement = nodes.remove(element.getHandleIdentifier());
		if (currElement != null) {
			currElement.setHandleIdentifier(newHandle);
			nodes.put(newHandle, currElement);
		}
	}

	public IMylarElement getActiveNode() {
		return activeNode;
	}

	public void delete(IMylarElement node) {
		landmarks.remove(node.getHandleIdentifier());
		nodes.remove(node.getHandleIdentifier());
	}

	public synchronized List<IMylarElement> getAllElements() {
		return new ArrayList<IMylarElement>(nodes.values());
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
		nodes.clear();
		interactionHistory.clear();
		landmarks.clear();
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
		for (MylarContextElement node : nodes.values()) {
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
				&& (nodes == null ? context.nodes == null : nodes.equals(context.nodes))
				&& (activeNode == null ? context.activeNode == null : activeNode.equals(context.activeNode))
				&& (tempRaised == null ? context.tempRaised == null : tempRaised.equals(context.tempRaised))
				&& (landmarks == null ? context.landmarks == null : landmarks.equals(context.landmarks))
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
		if (nodes != null)
			hashCode += nodes.hashCode();
		if (activeNode != null)
			hashCode += activeNode.hashCode();
		if (tempRaised != null)
			hashCode += tempRaised.hashCode();
		if (landmarks != null)
			hashCode += landmarks.hashCode();
		if (scalingFactors != null)
			hashCode += scalingFactors.hashCode();
		hashCode += 37 * numUserEvents;
		return hashCode;
	}

	public ScalingFactors getScalingFactors() {
		return scalingFactors;
	}
}
