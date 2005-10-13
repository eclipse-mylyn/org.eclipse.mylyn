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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class AbstractJavaContextTest extends AbstractContextTest {

	protected MylarContextManager manager = MylarPlugin.getContextManager();
    protected JavaEditingMonitor monitor = new JavaEditingMonitor();
    	
    protected TestProject project;
    protected IPackageFragment p1;
    protected IType type1;
    protected String taskId = this.getClass().getCanonicalName();
    protected MylarContext context;
    protected ScalingFactors scaling = new ScalingFactors();
     
    @Override
    protected void setUp() throws Exception {
    	assertNotNull(MylarJavaPlugin.getDefault());
    	project = new TestProject(this.getClass().getSimpleName());
        p1 = project.createPackage("p1");
        type1 = project.createType(p1, "Type1.java", "public class Type1 { }" );
        context = new MylarContext(taskId, scaling);
        manager.contextActivated(context);
        assertNotNull(MylarJavaPlugin.getDefault());

        assertTrue(MylarPlugin.getDefault().getStructureBridges().toString().indexOf(
    		JavaStructureBridge.class.getCanonicalName()) != -1);
    }
    
    @Override
    protected void tearDown() throws Exception {
    	project.dispose();
        context.reset(); 
        manager.contextDeactivated(taskId, taskId);
        manager.contextDeleted(taskId, taskId);
        manager.getFileForContext(taskId).delete(); 
    }
	
    protected int countItemsInTree(Tree tree) {
    	List<TreeItem> collectedItems = new ArrayList<TreeItem>();
    	collectTreeItemsInView(tree.getItems(), collectedItems);
    	return collectedItems.size();
    }
    
	protected void collectTreeItemsInView(TreeItem[] items, List<TreeItem> collectedItems) {
		if (items.length > 0) {
			for (TreeItem childItem : Arrays.asList(items)) {
				collectedItems.add(childItem);
				collectTreeItemsInView(childItem.getItems(), collectedItems);
			}
		}
	}

	protected IViewPart openView(String id) {
    	if (Workbench.getInstance() == null) return null;
    	IWorkbenchPage activePage= Workbench.getInstance().getActiveWorkbenchWindow().getActivePage();
        if (activePage == null) return null;
        IViewPart view = activePage.findView(id);
        return view;
	}
}
