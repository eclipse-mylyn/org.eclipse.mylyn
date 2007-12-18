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

	private static final float DEFAULT_EVENT_EDIT = .7f;
	
	private static final float DEFAULT_DECAY = .017f;

	private static final float DEFAULT_LANDMARK = 30f;
	
	private static final float DEFAULT_FORCED_LANDMARK = 7 * DEFAULT_LANDMARK;
	
	private Map<InteractionEvent.Kind, Float> interactionScalingFactors = new HashMap<InteractionEvent.Kind, Float>();
	
	private float interesting = DEFAULT_INTERESTING;
	
	private float landmark = DEFAULT_LANDMARK;

	private float forcedLandmark = DEFAULT_FORCED_LANDMARK;
	
	private float decay = DEFAULT_DECAY;

	@Deprecated
	private float errorInterest = .3f;

	@Deprecated
	private int maxNumInterestingErrors = 20;

	public InteractionContextScaling() {
		interactionScalingFactors.put(InteractionEvent.Kind.EDIT, DEFAULT_EVENT_EDIT);		
	}

	public float get(InteractionEvent.Kind kind) {
		if (interactionScalingFactors.containsKey(kind)) {
			return  interactionScalingFactors.get(kind);
		} else {
			return DEFAULT_EVENT;
		}
	}
	
	public void set(InteractionEvent.Kind kind, float value) {
		interactionScalingFactors.put(kind, value);
	}

	public float getDecay() {
		return decay;
	}

	public void setDecay(float decay) {
		this.decay = decay;
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
