/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 29, 2004
  */
package org.eclipse.mylar.core.model.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.core.model.InteractionEvent;


/**
 * @author Mik Kersten
 */
public class ScalingFactors implements Serializable {

    private static final long serialVersionUID = 1L;

    // weird
    private ScalingFactor decay = new ScalingFactor("decay", .05f);
    
    // thesholds, not factors
    private float landmark = 40f;
    private float interesting = 0f;
    private float delete = -2f;  
 
    // search
    private int degreeOfSeparation = 2;
    private int degreeOfSeparationScale = 3;

    // misc
//    XXX never used
//    private int maxParentPropagationdDistance = 7;
    private float errorInterest = 1.234f;
    private int maxNumInterestingErrors = 20;
    
    private Map<InteractionEvent.Kind, ScalingFactor> factors = new HashMap<InteractionEvent.Kind, ScalingFactor>();
    
    private static final ScalingFactor DEFAULT_SCALING_FACTOR = new ScalingFactor("<default", 1);
    
    public ScalingFactor get(InteractionEvent.Kind kind) {
        ScalingFactor factor = factors.get(kind);
        if (factor != null) {
            return factor;
        } else {
            return DEFAULT_SCALING_FACTOR;
        }
    }

    public ScalingFactor getDecay() {
        return decay;
    }
    public void setDecay(ScalingFactor decay) {
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
    public int getDegreeOfSeparation() {
        return degreeOfSeparation;
    }
    public void setDegreeOfSeparation(int degreeOfSeparation) {
        this.degreeOfSeparation = degreeOfSeparation;
    }
    public int getDegreeOfSeparationScale() {
        return degreeOfSeparationScale;
    }
    public void setDegreeOfSeparationScale(int degreeOfSeparationScale) {
        this.degreeOfSeparationScale = degreeOfSeparationScale;
    }
    public float getDelete() {
        return delete;
    }
    
    public void setDelete(float delete) {
        this.delete = delete;
    }
    public float getParentPropagationIncrement(int level) {
//        int d = ;
        return 1f / (level*level);
//        return 1f - (float)(1f/Math.sqrt(level+1));
//                
//                Math.abs(PARENT_PROPAGATION_STRENGTH
//                
//                (level/Math.sqrt(level));//   (level*level);
    }
    public float getErrorInterest() {
        return errorInterest; 
    }
    
    public int getMaxNumInterestingErrors() {
        return maxNumInterestingErrors;
    }
    
}
