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
public abstract class UsageStatistic {
    
    private String handle;
    
    public abstract String toFormattedString(); 
    
    /**
     * String value of this statistic, used for serialization.
     */
    public abstract String toValueString();
    
    public UsageStatistic(String handle) {
        this.handle = handle;
    }
    
    public String getHandle() {
        return handle;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof UsageStatistic) {
            UsageStatistic statistic = (UsageStatistic)object;
            return statistic.handle.equals(handle);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return handle + ": " + toValueString();
    }
}
