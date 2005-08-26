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
package org.eclipse.mylar.core.util;

import org.eclipse.mylar.core.MylarPlugin;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class ActiveTimerThread extends Thread implements Runnable {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private int sleepInterval = SECOND;
    private int timeout = 0;
    private int elapsed = 0;
    private IActiveTimerListener listener;
    boolean killed = false;
    
    public ActiveTimerThread(int timeoutInMillis, int sleepInterval, IActiveTimerListener listener) {
        this.timeout = timeoutInMillis;
        this.sleepInterval = sleepInterval;
        this.listener = listener;
    }
    
    public ActiveTimerThread(int timeoutInMinutes, IActiveTimerListener listener) {
        this.timeout = timeoutInMinutes * MINUTE;  
        this.listener = listener;
    }

    public void run() {
    	while(!killed){
//    		try {
//	            sleep(SECOND);
//	        } catch (InterruptedException e) {
//	        	MylarPlugin.log(e, "");
//	        }
//    		if(stopped)
//    			continue;
    		
	    	while(elapsed < timeout && /*!stopped && */ !killed){
		        try {
		            sleep(sleepInterval);
		        } catch (InterruptedException e) {
		        	MylarPlugin.log(e, "");
		        }
		        elapsed += sleepInterval;
	    	}
	        if (elapsed >= timeout && /*!stopped && */  !killed) {
	            listener.fireTimedOut();
	        }
    	}
    }
        
    public void killThread(){
    	killed = true;
    }
        
    public void resetTimer(){
    	elapsed = 0;
    }
}
