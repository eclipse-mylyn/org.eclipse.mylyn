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
 * Created on Oct 22, 2004
  */
package org.eclipse.mylar.core;

import java.util.Comparator;

/**
 * @author Mik Kersten
 */
public class InterestComparator<T> implements Comparator<T> {

    public int compare(T e1, T e2) {
        if (e1 instanceof IMylarContextNode && e2 instanceof IMylarContextNode) {
            IMylarContextNode info1 = (IMylarContextNode)e1;
            IMylarContextNode info2 = (IMylarContextNode)e2;
            if (info1 != null && info2 != null) {
                float v1 = info1.getDegreeOfInterest().getValue();
                float v2 = info2.getDegreeOfInterest().getValue();
                if (v1 >= v2) return -1;
                if (v1 < v2) return 1;
            }
        } 
        return 0;
    }
    
}
