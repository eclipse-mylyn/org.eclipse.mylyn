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

package org.eclipse.mylar.internal.context.core;

import java.util.*;

import org.eclipse.mylar.context.core.IDegreeOfInterest;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class CompositeDegreeOfInterest implements IDegreeOfInterest {

	private Set<IDegreeOfInterest> infos = new HashSet<IDegreeOfInterest>();

	public void addEvent(InteractionEvent event) {
		for (IDegreeOfInterest info : infos)
			((DegreeOfInterest) info).addEvent(event);
	}

	public List<InteractionEvent> getEvents() {
		Set<InteractionEvent> events = new HashSet<InteractionEvent>();
		for (IDegreeOfInterest info : infos)
			events.addAll(info.getEvents());
		return new ArrayList<InteractionEvent>(events);
	}

	public float getValue() {
		float value = 0;
		for (IDegreeOfInterest info : infos)
			value += info.getValue();
		return value;
	}

	public float getDecayValue() {
		float value = 0;
		for (IDegreeOfInterest info : infos)
			value += info.getDecayValue();
		return value;
	}

	public float getEncodedValue() {
		float value = 0;
		for (IDegreeOfInterest info : infos)
			value += info.getEncodedValue();
		return value;
	}

	/**
	 * @return true if one is interesting
	 */
	public boolean isInteresting() {
		boolean isInteresting = false;
		for (IDegreeOfInterest info : infos)
			if (info.isInteresting())
				isInteresting = true;
		return isInteresting;
	}

	/**
	 * @return true if all are predicted
	 */
	public boolean isPropagated() {
		if (infos.isEmpty())
			return false;
		boolean allPropagated = true;
		for (IDegreeOfInterest info : infos)
			if (!info.isPropagated())
				allPropagated = false;
		return allPropagated;
	}

	public boolean isPredicted() {
		if (infos.isEmpty())
			return false;
		boolean allPredicted = true;
		for (IDegreeOfInterest info : infos)
			if (!info.isPredicted())
				allPredicted = false;
		return allPredicted;
	}

	public boolean isLandmark() {
		return getValue() >= MylarContextManager.getScalingFactors().getLandmark();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("composite(");
		for (IDegreeOfInterest info : infos)
			result.append(info.toString());
		result.append(")");
		return result.toString();
	}

	public Set<IDegreeOfInterest> getInfos() {
		return infos;
	}

	public MylarContext getCorrespondingContext() {
		return null;
	}

	// public boolean hasChildWithEncodedInterest() {
	// boolean has = true;
	// for (IDegreeOfInterest info : infos) {
	// if (!info.hasChildWithEncodedInterest()) has = false;
	// }
	// return has;
	// }
	//
	// public void setHasChildWithEncodedInterest(boolean value) {
	// for (IDegreeOfInterest info : infos) {
	// info.setHasChildWithEncodedInterest(value);
	// }
	// }
}
