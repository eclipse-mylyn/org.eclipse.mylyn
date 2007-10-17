/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * Initialized with default values.
 * 
 * @author Mik Kersten
 */
public class InteractionContextScaling {

	private static final float DEFAULT_INTERESTING = 0f;

	private static final float DEFAULT_EVENT = 1f;	
	
	private static final float DEFAULT_EVENT_DECAY = .017f;

	private static final float DEFAULT_EVENT_EDIT = .7f;

	private static final float DEFAULT_LANDMARK = 30f;
	
	private static final float DEFAULT_FORCED_LANDMARK = 7 * DEFAULT_LANDMARK;
	
	private static final InteractionEventScalingFactor DEFAULT_SCALING_FACTOR = new InteractionEventScalingFactor("<default>", DEFAULT_EVENT);
	
	private Map<InteractionEvent.Kind, InteractionEventScalingFactor> interactionScalingFactors = new HashMap<InteractionEvent.Kind, InteractionEventScalingFactor>();
	
	private InteractionEventScalingFactor defaultDecay = new InteractionEventScalingFactor("decay", DEFAULT_EVENT_DECAY);

	private InteractionEventScalingFactor defaultEdit = new InteractionEventScalingFactor("edit", DEFAULT_EVENT_EDIT);
	
	private float interesting = DEFAULT_INTERESTING;
	
	private float landmark = DEFAULT_LANDMARK;

	private float forcedLandmark = DEFAULT_FORCED_LANDMARK;

	@Deprecated
	private float errorInterest = .3f;

	@Deprecated
	private int maxNumInterestingErrors = 20;

	public InteractionContextScaling() {
		interactionScalingFactors.put(InteractionEvent.Kind.EDIT, defaultEdit);		
	}

	public InteractionEventScalingFactor get(InteractionEvent.Kind kind) {
		InteractionEventScalingFactor factor = interactionScalingFactors.get(kind);
		if (factor != null) {
			return factor;
		} else {
			return DEFAULT_SCALING_FACTOR;
		}
	}

	public InteractionEventScalingFactor getDecay() {
		return defaultDecay;
	}

	public void setDecay(InteractionEventScalingFactor decay) {
		this.defaultDecay = decay;
	}

	public float getInteresting() {
		return interesting;
	}

	public void setInteresting(float interesting) {
		this.interesting = interesting;
	}

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

	public float getForcedLandmark() {
		return forcedLandmark;
	}

	public void setForcedLandmark(float userLandmark) {
		this.forcedLandmark = userLandmark;
	}
}
