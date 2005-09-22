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
 * Created on Jul 20, 2004
  */
package org.eclipse.mylar.monitor.internal;




/**
 * @author Mik Kersten
 */
public class CardinalStatistic extends UsageStatistic {
    
    private long count = 0;
    
    public CardinalStatistic(String handle) {
        super(handle);
    }
    
    public void increment() {
        count++;
    }
    
    public void increment(long increment) {
        count += increment;
    }
    
    public long getCount() {
        return count;
    }
    
    public void setCount(long count) {
        this.count = count;
    }
    
    @Override
    public String toFormattedString() {
        return toValueString();
    }    
 
    @Override
    public String toValueString() {
        return "" + count;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof CardinalStatistic) {
            CardinalStatistic cardinalStatistic = (CardinalStatistic)object;
            return cardinalStatistic.getCount() == count
            	&& super.equals(object);
        } else {
            return false;
        }
    }
}
