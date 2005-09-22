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
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.monitor.KeybindingCommandMonitor;
import org.eclipse.mylar.monitor.SelectionMonitor;
import org.eclipse.mylar.monitor.InteractionEventLogger;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.ui.internal.Workbench;


public class MonitorTest extends TestCase {

    private InteractionEventLogger logger = MylarMonitorPlugin.getDefault().getInteractionLogger();
    private SelectionMonitor selectionMonitor = new SelectionMonitor();
    private KeybindingCommandMonitor commandMonitor = new KeybindingCommandMonitor();
    
    public void testLogging() {
        logger.stop();
        MylarMonitorPlugin.getDefault().getMonitorFile().delete();
        logger.start();
         
        selectionMonitor.selectionChanged(
                Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart(),
                new StructuredSelection("yo"));
                
        commandMonitor.preExecute("foo.command", new ExecutionEvent(new HashMap(), "trigger", "context"));
        File monitorFile = MylarMonitorPlugin.getDefault().getMonitorFile();
        assertTrue(monitorFile.exists());
        logger.stop();
        List<InteractionEvent> events = logger.getHistoryFromFile(monitorFile);
        assertTrue(events.size() >= 2);
//        assertEquals(2, events.size()); 
    }

}
