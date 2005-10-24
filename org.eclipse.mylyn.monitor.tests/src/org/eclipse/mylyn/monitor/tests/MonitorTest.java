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
 * Created on Jun 9, 2005
  */
package org.eclipse.mylar.monitor.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.monitor.InteractionEventLogger;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.monitor.monitors.KeybindingCommandMonitor;
import org.eclipse.mylar.monitor.monitors.SelectionMonitor;
import org.eclipse.ui.internal.Workbench;


public class MonitorTest extends TestCase {

    private InteractionEventLogger logger = MylarMonitorPlugin.getDefault().getInteractionLogger();
    private SelectionMonitor selectionMonitor = new SelectionMonitor();
    private KeybindingCommandMonitor commandMonitor = new KeybindingCommandMonitor();
        
    @Override
	protected void setUp() throws Exception {
		super.setUp();
//		MylarMonitorPlugin.getDefault().startMonitoring();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
//		MylarMonitorPlugin.getDefault().stopMonitoring();
	}
	
	public void testEnablement() throws IOException {
    	File monitorFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
    	assertTrue(monitorFile.exists());
    	MylarMonitorPlugin.getDefault().stopMonitoring();
        logger.clearInteractionHistory();
        assertEquals(0, logger.getHistoryFromFile(monitorFile).size());
    	generateSelection();
        assertEquals(0, logger.getHistoryFromFile(monitorFile).size());
        
    	MylarMonitorPlugin.getDefault().startMonitoring();
        generateSelection();
        assertEquals(1, logger.getHistoryFromFile(monitorFile).size());
        
        MylarMonitorPlugin.getDefault().stopMonitoring();
        generateSelection();
        assertEquals(1, logger.getHistoryFromFile(monitorFile).size());
        
        MylarMonitorPlugin.getDefault().startMonitoring();
        generateSelection();
        assertEquals(2, logger.getHistoryFromFile(monitorFile).size());
    }
    
//    public void testLogFileMove() throws IOException {
//    	File defaultFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
//    	MylarMonitorPlugin.getDefault().stopMonitoring();
//    	assertTrue(logger.clearInteractionHistory());
//    	
//    	MylarMonitorPlugin.getDefault().startMonitoring();
//        generateSelection();
//        generateSelection();
//        assertEquals(2, logger.getHistoryFromFile(defaultFile).size());
//        
//        File newFile = MylarMonitorPlugin.getDefault().moveMonitorLogFile(MylarPlugin.getDefault().getMylarDataDirectory() + "/monitor-test-new.xml");
//        assertNotNull(newFile);
//        File movedFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
//    	assertTrue(!newFile.equals(defaultFile));
//        assertEquals(newFile, movedFile);
//    	assertEquals(newFile, logger.getOutputFile());
//    	assertEquals(2, logger.getHistoryFromFile(newFile).size());
//    	assertEquals(0, logger.getHistoryFromFile(defaultFile).size());
//    	
//        generateSelection();
//        assertEquals(3, logger.getHistoryFromFile(newFile).size());
//        File restoredFile = MylarMonitorPlugin.getDefault().moveMonitorLogFile(defaultFile.getAbsolutePath());        
//        assertNotNull(restoredFile);
//    }
    
    public void testLogging() {
    	MylarMonitorPlugin.getDefault().startMonitoring();
        logger.stop();
        MylarMonitorPlugin.getDefault().getMonitorLogFile().delete();
        logger.start();
         
        generateSelection();        
        commandMonitor.preExecute("foo.command", new ExecutionEvent(new HashMap(), "trigger", "context"));
        File monitorFile = MylarMonitorPlugin.getDefault().getMonitorLogFile();
        assertTrue(monitorFile.exists());
        logger.stop();
        List<InteractionEvent> events = logger.getHistoryFromFile(monitorFile);
        assertTrue(events.size() >= 2); 
    }    

	private void generateSelection() {
		selectionMonitor.selectionChanged(
                Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart(),
                new StructuredSelection("yo"));
	}
}
