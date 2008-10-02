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

package org.eclipse.mylyn.internal.monitor.core;

import java.util.Date;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * 
 * @author Shawn Minto
 * @since 3.1
 */
public class AggregateInteractionEvent extends InteractionEvent {

	// these are needed for collapsed events so that we can restore the context properly
	private final int numCollapsedEvents;

	private final int eventCountOnCreation;

	/**
	 * For parameter description see this class's getters.
	 * 
	 * @since 3.1
	 */
	public AggregateInteractionEvent(Kind kind, String structureKind, String handle, String originId,
			String navigatedRelation, String delta, float interestContribution, int numCollapsedEvents,
			int eventCountOnCreation) {
		super(kind, structureKind, handle, originId, navigatedRelation, delta, interestContribution);
		this.numCollapsedEvents = numCollapsedEvents;
		this.eventCountOnCreation = eventCountOnCreation;
	}

	/**
	 * For parameter description see this class's getters.
	 */
	public AggregateInteractionEvent(Kind kind, String structureKind, String handle, String originId,
			String navigatedRelation, String delta, float interestContribution, Date startDate, Date endDate,
			int numCollapsedEvents, int eventCountOnCreation) {

		super(kind, structureKind, handle, originId, navigatedRelation, delta, interestContribution, startDate, endDate);
		this.numCollapsedEvents = numCollapsedEvents;
		this.eventCountOnCreation = eventCountOnCreation;
	}

	/**
	 * @return The number of events this event represents
	 * @since 3.1
	 */
	public int getNumCollapsedEvents() {
		return numCollapsedEvents;
	}

	/**
	 * @return the number of user events that had occurred when this was created or -1 to use the context's count
	 * @since 3.1
	 */
	public int getEventCountOnCreation() {
		return eventCountOnCreation;
	}

}
