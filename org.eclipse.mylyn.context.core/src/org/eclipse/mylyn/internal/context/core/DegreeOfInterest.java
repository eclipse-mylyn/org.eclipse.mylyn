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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 * 
 * TODO: make package-visible
 */
public class DegreeOfInterest implements IDegreeOfInterest {

	private List<InteractionEvent> events = new ArrayList<InteractionEvent>();

	private Map<InteractionEvent.Kind, InteractionEvent> collapsedEvents = new HashMap<InteractionEvent.Kind, InteractionEvent>();

	protected ScalingFactors scaling;

	private float edits = 0;

	private float selections = 0;

	private float commands = 0;

	private float predictedBias = 0;

	private float propagatedBias = 0;

	private float manipulationBias = 0;

	private InteractionContext context;

	private int eventCountOnCreation;

	public DegreeOfInterest(InteractionContext context, ScalingFactors scaling) {
		this.context = context;
		this.eventCountOnCreation = context.getUserEventCount();
		this.scaling = scaling;
	}

	/**
	 * TODO: make package-visible
	 */
	public void addEvent(InteractionEvent event) {
		events.add(0, event);
		InteractionEvent last = collapsedEvents.get(event.getKind());
		if (last != null) {
			InteractionEvent aggregateEvent = new InteractionEvent(event.getKind(), event.getStructureKind(),
					event.getStructureHandle(), event.getOriginId(), event.getNavigation(), event.getDelta(),
					last.getInterestContribution() + event.getInterestContribution(), last.getDate(),
					event.getEndDate());
			collapsedEvents.put(event.getKind(), aggregateEvent);
		} else {
			collapsedEvents.put(event.getKind(), event);
		}
		updateEventState(event);
	}

	private void updateEventState(InteractionEvent event) {
		switch (event.getKind()) {
		case EDIT:
			edits += event.getInterestContribution();
			break;
		case SELECTION:
			selections += event.getInterestContribution();
			break;
		case COMMAND:
			commands += event.getInterestContribution();
			break;
		case PREDICTION:
			predictedBias += event.getInterestContribution();
			break;
		case PROPAGATION:
			propagatedBias += event.getInterestContribution();
			break;
		case MANIPULATION:
			manipulationBias += event.getInterestContribution();
			break;
		}
	}

	public float getValue() {
		float value = getEncodedValue();
		value += predictedBias;
		value += propagatedBias;
		return value;
	}

	public float getEncodedValue() {
		float value = 0;
		value += selections * scaling.get(InteractionEvent.Kind.SELECTION).getValue();
		value += edits * scaling.get(InteractionEvent.Kind.EDIT).getValue();
		value += commands * scaling.get(InteractionEvent.Kind.COMMAND).getValue();
		value += manipulationBias;
		value -= getDecayValue();
		return value;
	}

	/**
	 * @return a scaled decay count based on the number of events since the creation of this interest object
	 */
	public float getDecayValue() {
		if (context != null) {
			return (context.getUserEventCount() - eventCountOnCreation) * scaling.getDecay().getValue();
		} else {
			return 0;
		}
	}

	/**
	 * Sums predicted and propagated values
	 */
	public boolean isPropagated() {
		float value = selections * scaling.get(InteractionEvent.Kind.SELECTION).getValue() + edits
				* scaling.get(InteractionEvent.Kind.EDIT).getValue();
		return value <= 0 && propagatedBias > 0;
	}

	public boolean isPredicted() {
		return getEncodedValue() <= 0 && predictedBias > 0;
	}

	public boolean isLandmark() {
		return getValue() >= scaling.getLandmark();
	}

	public boolean isInteresting() {
		return getValue() > scaling.getInteresting();
	}

	@Override
	public String toString() {
		return "(" + "selections: " + selections + ", edits: " + edits + ", commands: " + commands + ", predicted: "
				+ predictedBias + ", propagated: " + propagatedBias + ", manipulation: " + manipulationBias + ")";
	}

	/**
	 * TODO: make unmodifiable? Clients should not muck with this list.
	 */
	public List<InteractionEvent> getEvents() {
		return events;
	}

	public List<InteractionEvent> getCollapsedEvents() {
		List<InteractionEvent> allCollapsed = new ArrayList<InteractionEvent>();
		allCollapsed.addAll(collapsedEvents.values());
		if (!allCollapsed.isEmpty()) {
			allCollapsed.add(0, new InteractionEvent(InteractionEvent.Kind.MANIPULATION, allCollapsed.get(0)
					.getStructureKind(), allCollapsed.get(0).getStructureHandle(),
					InteractionContextManager.SOURCE_ID_DECAY, -getDecayValue()));
		}
		return allCollapsed;
	}

	// private void writeObject(ObjectOutputStream stream) throws IOException {
	// stream.defaultWriteObject();
	// stream.writeObject(events);
	// }
	//    
	// @SuppressWarnings(value="unchecked")
	// private void readObject(ObjectInputStream stream) throws IOException,
	// ClassNotFoundException {
	// stream.defaultReadObject();
	// events = (List<InteractionEvent>)stream.readObject();
	// init();
	// for (InteractionEvent event : events) {
	// updateEventState(event);
	// }
	// }
}
