/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
// TODO: make package-visible
public class DegreeOfInterest implements IDegreeOfInterest {

	private final List<InteractionEvent> events = new ArrayList<>();

	private final Map<InteractionEvent.Kind, InteractionEvent> collapsedEvents = new HashMap<>();

	protected IInteractionContextScaling contextScaling;

	private float edits = 0;

	private float selections = 0;

	private float commands = 0;

	private float predictedBias = 0;

	private float propagatedBias = 0;

	private float manipulationBias = 0;

	private final InteractionContext context;

	private final int eventCountOnCreation;

	public DegreeOfInterest(InteractionContext context, IInteractionContextScaling scaling) {
		this(context, scaling, context.getUserEventCount());
	}

	public DegreeOfInterest(InteractionContext context, IInteractionContextScaling scaling, int eventCountOnCreation) {
		this.context = context;
		if (eventCountOnCreation <= 0) {
			this.eventCountOnCreation = context.getUserEventCount();
		} else {
			this.eventCountOnCreation = eventCountOnCreation;
		}
		contextScaling = scaling;
	}

	/**
	 * TODO: make package-visible
	 */
	public void addEvent(InteractionEvent event) {
		events.add(event); // NOTE: was events.add(0, event);
		InteractionEvent last = collapsedEvents.get(event.getKind());
		if (last != null) {

			int numCollapsedEvents = 1;
			if (last instanceof AggregateInteractionEvent) {
				numCollapsedEvents = ((AggregateInteractionEvent) last).getNumCollapsedEvents();
			}

			AggregateInteractionEvent aggregateEvent = new AggregateInteractionEvent(event.getKind(),
					event.getStructureKind(), event.getStructureHandle(), event.getOriginId(), event.getNavigation(),
					event.getDelta(), last.getInterestContribution() + event.getInterestContribution(), last.getDate(),
					event.getEndDate(), numCollapsedEvents + 1, eventCountOnCreation);
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

	@Override
	public float getValue() {
		float value = getEncodedValue();
		value += predictedBias;
		value += propagatedBias;
		return value;
	}

	@Override
	public float getEncodedValue() {
		float value = 0;
		value += selections * contextScaling.get(InteractionEvent.Kind.SELECTION);
		value += edits * contextScaling.get(InteractionEvent.Kind.EDIT);
		value += commands * contextScaling.get(InteractionEvent.Kind.COMMAND);
		value += manipulationBias;
		value -= getDecayValue();
		return value;
	}

	/**
	 * @return a scaled decay count based on the number of events since the creation of this interest object
	 */
	@Override
	public float getDecayValue() {
		if (context != null) {
			return (context.getUserEventCount() - eventCountOnCreation) * contextScaling.getDecay();
		} else {
			return 0;
		}
	}

	/**
	 * Sums predicted and propagated values
	 */
	@Override
	public boolean isPropagated() {
		float value = selections * contextScaling.get(InteractionEvent.Kind.SELECTION)
				+ edits * contextScaling.get(InteractionEvent.Kind.EDIT);
		return value <= 0 && propagatedBias > 0;
	}

	@Override
	public boolean isPredicted() {
		return getValue() - predictedBias <= 0 && predictedBias > 0;
	}

	@Override
	public boolean isLandmark() {
		return getValue() >= contextScaling.getLandmark();
	}

	@Override
	public boolean isInteresting() {
		return getValue() > contextScaling.getInteresting();
	}

	@Override
	public String toString() {
		return "(" + "selections: " + selections + ", edits: " + edits + ", commands: " + commands + ", predicted: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				+ predictedBias + ", propagated: " + propagatedBias + ", manipulation: " + manipulationBias + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * TODO: make unmodifiable? Clients should not muck with this list.
	 */
	@Override
	public List<InteractionEvent> getEvents() {
		return events;
	}

	public List<InteractionEvent> getCollapsedEvents() {
		return new ArrayList<>(collapsedEvents.values());
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
