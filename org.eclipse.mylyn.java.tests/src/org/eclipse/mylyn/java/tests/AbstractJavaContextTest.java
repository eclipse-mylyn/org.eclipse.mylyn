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

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.internal.ScalingFactors;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.core.tests.support.TestProject;
import org.eclipse.mylar.java.JavaEditingMonitor;
import org.eclipse.mylar.java.JavaStructureBridge;
import org.eclipse.mylar.java.MylarJavaPlugin;

/**
 * @author Mik Kersten
 */
public class AbstractJavaContextTest extends AbstractContextTest {

	protected MylarContextManager manager = MylarPlugin.getContextManager();
    protected JavaEditingMonitor monitor = new JavaEditingMonitor();
    	
    protected TestProject project1;
    protected IPackageFragment p1;
    protected IType type1;
    protected String taskId = this.getClass().getCanonicalName();
    protected MylarContext context;
    protected ScalingFactors scaling = new ScalingFactors();
     
    @Override
    protected void setUp() throws Exception {
    	assertNotNull(MylarJavaPlugin.getDefault());
    	project1 = new TestProject(this.getClass().getSimpleName());
        p1 = project1.createPackage("p1");
        type1 = project1.createType(p1, "Type1.java", "public class Type1 { }" );
        context = new MylarContext(taskId, scaling);
        manager.contextActivated(context);
        assertNotNull(MylarJavaPlugin.getDefault());

        assertTrue(MylarPlugin.getDefault().getStructureBridges().toString().indexOf(
    		JavaStructureBridge.class.getCanonicalName()) != -1);
    }
    
    @Override
    protected void tearDown() throws Exception {
    	project1.dispose();
        context.reset(); 
        manager.getFileForContext(taskId).delete();
        manager.contextDeactivated(taskId, taskId);
        manager.contextDeleted(taskId, taskId);
    }
	
}
