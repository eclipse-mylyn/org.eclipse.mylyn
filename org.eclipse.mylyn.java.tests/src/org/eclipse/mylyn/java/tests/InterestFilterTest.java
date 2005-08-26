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

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.internal.ScalingFactors;
import org.eclipse.mylar.core.tests.support.TestProject;
import org.eclipse.mylar.java.JavaEditingMonitor;
import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.ui.InterestFilter;

/**
 * @author Mik Kersten
 */
public class InterestFilterTest extends TestCase {

	private InterestFilter filter;
	private MylarContextManager manager = MylarPlugin.getContextManager();
	private JavaEditingMonitor monitor = new JavaEditingMonitor();	
	private PackageExplorerPart explorer;

	private TestProject project1;
	private IPackageFragment p1;
	private IType type1;
	private String taskId = "123";
	private MylarContext taskscape;
	private ScalingFactors scaling = new ScalingFactors();
    
    @Override
    protected void setUp() throws Exception {
    	assertNotNull(MylarJavaPlugin.getDefault());
    	project1 = new TestProject("project-filter");
        p1 = project1.createPackage("p1");
        type1 = project1.createType(p1, "Type1.java", "public class Type1 { }" );
        taskscape = new MylarContext("1", scaling);
        

		explorer = PackageExplorerPart.openInActivePerspective();
    	assertNotNull(explorer);
    	
		ApplyMylarToPackageExplorerAction.getDefault().update(true);
        filter = ApplyMylarToPackageExplorerAction.getDefault().getInterestFilter();
        assertNotNull(filter);

		project1.build();
    }
	
    @Override
    protected void tearDown() throws Exception {
        project1.dispose();
        manager.contextDeleted(taskId, taskId);
    }
    
	public void testPatternMatch() {
//		String exclusion = 
//		filter.setExcludedMatches()
	}
    
	public void testSelections() throws CoreException, InvocationTargetException, InterruptedException {
		assertFalse(filter.select(explorer.getTreeViewer(), null, type1));
		monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
        manager.contextActivated(taskscape);

        monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
        assertTrue(filter.select(explorer.getTreeViewer(), null, type1));
        
        filter.setExcludedMatches("*.java");
        assertFalse(filter.select(explorer.getTreeViewer(), null, type1));
		        
//        monitor.selectionChanged(PackageExplorerPart.getFromActivePerspective(), new StructuredSelection(type1));
//        assertTrue(filter.select(explorer.getTreeViewer(), null, type1));
	}
}
