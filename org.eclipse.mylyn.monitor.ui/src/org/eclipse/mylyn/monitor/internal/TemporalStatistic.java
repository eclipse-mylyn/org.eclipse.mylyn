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

import org.eclipse.jface.resource.ImageDescriptor;



/**
 * @author Mik Kersten
 */
public class TemporalStatistic extends UsageStatistic {

    private String time;
    
    public TemporalStatistic(String handle, String time, ImageDescriptor icon) {
        super(handle);
        this.time = time;
    }

    @Override
    public String toFormattedString() {
        return toValueString();
    }
    
    @Override
    public String toValueString() {
        return time;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof TemporalStatistic) {
            TemporalStatistic statistic = (TemporalStatistic)object;
            return statistic.getTime().equals(time)
            	&& super.equals(object);
        } else {
            return false;
        }
    }
} 

