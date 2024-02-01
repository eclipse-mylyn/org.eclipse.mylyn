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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Initialized with default values.
 * 
 * @author Mik Kersten
 */
public class InteractionContextScaling implements IInteractionContextScaling {

	private static final float DEFAULT_INTERESTING = 0f;

	private static final float DEFAULT_EVENT = 1f;

	private static final float DEFAULT_EVENT_EDIT = .7f;

	private static final float DEFAULT_DECAY = .017f;

	private static final float DEFAULT_LANDMARK = 30f;

	private static final float DEFAULT_FORCED_LANDMARK = 7 * DEFAULT_LANDMARK;

	private final Map<InteractionEvent.Kind, Float> interactionScalingFactors = new HashMap<>();

	private float interesting = DEFAULT_INTERESTING;

	private float landmark = DEFAULT_LANDMARK;

	private float forcedLandmark = DEFAULT_FORCED_LANDMARK;

	private float decay = DEFAULT_DECAY;

	@Deprecated
	private final float errorInterest = .3f;

	@Deprecated
	private final int maxNumInterestingErrors = 20;

	public InteractionContextScaling() {
		interactionScalingFactors.put(InteractionEvent.Kind.EDIT, DEFAULT_EVENT_EDIT);
	}

	@Override
	public float get(InteractionEvent.Kind kind) {
		if (interactionScalingFactors.containsKey(kind)) {
			return interactionScalingFactors.get(kind);
		} else {
			return DEFAULT_EVENT;
		}
	}

	public void set(InteractionEvent.Kind kind, float value) {
		interactionScalingFactors.put(kind, value);
	}

	@Override
	public float getDecay() {
		return decay;
	}

	public void setDecay(float decay) {
		this.decay = decay;
	}

	@Override
	public float getInteresting() {
		return interesting;
	}

	public void setInteresting(float interesting) {
		this.interesting = interesting;
	}

	@Override
	public float getLandmark() {
		return landmark;
	}

	public void setLandmark(float landmark) {
		this.landmark = landmark;
	}

	@Deprecated
	public float getErrorInterest() {
		return errorInterest;
	}

	@Deprecated
	public int getMaxNumInterestingErrors() {
		return maxNumInterestingErrors;
	}

	@Override
	public float getForcedLandmark() {
		return forcedLandmark;
	}

	public void setForcedLandmark(float userLandmark) {
		forcedLandmark = userLandmark;
	}
}
