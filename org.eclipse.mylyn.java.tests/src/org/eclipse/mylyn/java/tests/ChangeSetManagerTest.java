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

package org.eclipse.mylar.java.tests;

import org.eclipse.mylar.java.MylarChangeSetManager;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;



/**
 * @author Mik Kersten
 */
public class ChangeSetManagerTest extends AbstractJavaContextTest {

	private MylarChangeSetManager changeSetManager = MylarJavaPlugin.getDefault().getChangeSetManager();
		
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    }
    
    public void testSingleContextActivation() {
    	manager.contextDeactivated(taskId, taskId);
    	assertEquals(0, changeSetManager.getChangeSets().size());
    	Task task1 = new Task("task1", "label", true);
    	MylarTasklistPlugin.getTaskListManager().activateTask(task1);
    	assertEquals(1, changeSetManager.getChangeSets().size());
    	MylarTasklistPlugin.getTaskListManager().deactivateTask(task1);
    	assertEquals(0, changeSetManager.getChangeSets().size());
    }
}
