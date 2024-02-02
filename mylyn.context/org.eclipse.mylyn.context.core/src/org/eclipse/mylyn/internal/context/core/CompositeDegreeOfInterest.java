/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.context.core.IDegreeOfInterest;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class CompositeDegreeOfInterest implements IDegreeOfInterest {

	private final Set<IDegreeOfInterest> composed = new HashSet<>();

	protected IInteractionContextScaling contextScaling;

	public CompositeDegreeOfInterest(IInteractionContextScaling contextScaling) {
		this.contextScaling = contextScaling;
	}

	public void addEvent(InteractionEvent event) {
		for (IDegreeOfInterest info : composed) {
			((DegreeOfInterest) info).addEvent(event);
		}
	}

	@Override
	public List<InteractionEvent> getEvents() {
		Set<InteractionEvent> events = new HashSet<>();
		for (IDegreeOfInterest info : composed) {
			events.addAll(info.getEvents());
		}
		return new ArrayList<>(events);
	}

	@Override
	public float getValue() {
		float value = 0;
		for (IDegreeOfInterest info : composed) {
			value += info.getValue();
		}
		return value;
	}

	@Override
	public float getDecayValue() {
		float value = 0;
		for (IDegreeOfInterest info : composed) {
			value += info.getDecayValue();
		}
		return value;
	}

	@Override
	public float getEncodedValue() {
		float value = 0;
		for (IDegreeOfInterest info : composed) {
			value += info.getEncodedValue();
		}
		return value;
	}

	/**
	 * @return true if one is interesting
	 */
	@Override
	public boolean isInteresting() {
		boolean isInteresting = false;
		for (IDegreeOfInterest info : composed) {
			if (info.isInteresting()) {
				isInteresting = true;
			}
		}
		return isInteresting;
	}

	/**
	 * @return true if all are predicted
	 */
	@Override
	public boolean isPropagated() {
		if (composed.isEmpty()) {
			return false;
		}
		boolean allPropagated = true;
		for (IDegreeOfInterest info : composed) {
			if (!info.isPropagated()) {
				allPropagated = false;
			}
		}
		return allPropagated;
	}

	@Override
	public boolean isPredicted() {
		if (composed.isEmpty()) {
			return false;
		}
		boolean allPredicted = true;
		for (IDegreeOfInterest info : composed) {
			if (!info.isPredicted()) {
				allPredicted = false;
			}
		}
		return allPredicted;
	}

	@Override
	public boolean isLandmark() {
		return getValue() >= contextScaling.getLandmark();
	}

	public Set<IDegreeOfInterest> getComposedDegreesOfInterest() {
		return composed;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("composite("); //$NON-NLS-1$
		for (IDegreeOfInterest info : composed) {
			result.append(info.toString());
		}
		result.append(")"); //$NON-NLS-1$
		return result.toString();
	}
}
