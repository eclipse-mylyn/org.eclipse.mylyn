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
 * Created on Jul 16, 2004
  */
package org.eclipse.mylar.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.IActiveTimerListener;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class ActivityTimerThread extends Thread implements Runnable {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private int sleepInterval = 5 * SECOND;
    private int timeout = 0;
    private int elapsed = 0;
    private List<IActiveTimerListener> listeners = new ArrayList<IActiveTimerListener>();
    boolean killed = false;
    
    public ActivityTimerThread(int timeoutInMillis, int sleepInterval) {
        this.timeout = timeoutInMillis;
        this.sleepInterval = sleepInterval;
    }
    
    public ActivityTimerThread(int timeoutInMinutes) {
        this.timeout = timeoutInMinutes * MINUTE;  
    }

    public boolean addListener(IActiveTimerListener listener) {
    	return listeners.add(listener);
    }
    
    public boolean removeListener(IActiveTimerListener listener) {
    	return listeners.remove(listener);
    }
    
    public void run() {
    	try {
	    	while(!killed) {
		    	while(elapsed < timeout && /*!stopped && */ !killed){
			        elapsed += sleepInterval;
			        sleep(sleepInterval);
		    	}
		        if (elapsed >= timeout && /*!stopped && */  !killed) {
		            for (IActiveTimerListener listener : listeners) {
		            	listener.fireTimedOut();
					}
		        }
		        sleep(sleepInterval);
	    	}
        } catch (InterruptedException e) {
        	MylarPlugin.log(e, "timer interrupted");
        }
    }
        
    public void killThread(){
    	killed = true;
    }
        
    public void resetTimer(){
    	elapsed = 0;
    }
}
