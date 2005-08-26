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
 * Created on Jul 15, 2004
 */
package org.eclipse.mylar.core.util;

/**
 * Check against the system clock--doesn't need to run as thread.
 * 
 * @author Mik Kersten
 */
public class PassiveTimer {
    
    private long elapsed = 0;
    private long lastStartTime = System.currentTimeMillis();

    public void restart() {
        lastStartTime = System.currentTimeMillis(); 
        elapsed = 0;
    } 

    public long getElapsedInSeconds() {
        elapsed = System.currentTimeMillis() - lastStartTime;
        return elapsed/1000;
    }
}