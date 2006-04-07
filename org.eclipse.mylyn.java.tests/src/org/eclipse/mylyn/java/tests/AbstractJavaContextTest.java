/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.ide.tests.ResourceTestUtil;
import org.eclipse.mylar.internal.core.MylarContext;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.ScalingFactors;
import org.eclipse.mylar.internal.ide.MylarIdePlugin;
import org.eclipse.mylar.internal.java.JavaEditingMonitor;
import org.eclipse.mylar.internal.java.JavaStructureBridge;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Mik Kersten
 */
public abstract class AbstractJavaContextTest extends AbstractContextTest {

	protected MylarContextManager manager = MylarPlugin.getContextManager();

	protected JavaEditingMonitor monitor = new JavaEditingMonitor();

	protected TestJavaProject project;

	protected IPackageFragment p1;

	protected IType type1;

	protected String contextId = this.getClass().getSimpleName();

	protected MylarContext context;

	protected ScalingFactors scaling = new ScalingFactors();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(JavaPlugin.getDefault());
		assertNotNull(MylarJavaPlugin.getDefault());
		project = new TestJavaProject(this.getClass().getSimpleName());// + "-"
																		// +
																		// projectCounter++);
		p1 = project.createPackage("p1");
		type1 = project.createType(p1, "Type1.java", "public class Type1 { }");
		context = new MylarContext(contextId, scaling);
		context.reset();
		// assertTrue(manager.getActiveContext().getInteresting().toString(),
		// manager.getActiveContext().getInteresting().isEmpty());
		manager.activateContext(context);
		assertNotNull(MylarJavaPlugin.getDefault());
		assertTrue(MylarPlugin.getDefault().getStructureBridges().toString().indexOf(
				JavaStructureBridge.class.getCanonicalName()) != -1);

		MylarUiPlugin.getDefault().getViewerManager().setSyncRefreshMode(true);
		MylarIdePlugin.getDefault().setResourceMonitoringEnabled(false);
	}

	@Override
	protected void tearDown() throws Exception {
		context.reset();
		assertTrue(context.getInteresting().isEmpty());
		manager.deactivateContext(contextId);
		manager.deleteContext(contextId);
		manager.getFileForContext(contextId).delete();

		ResourceTestUtil.deleteProject(project.getProject());

		for (MylarContext context : manager.getActiveContexts()) {
			manager.deactivateContext(context.getHandleIdentifier());
		}
		if (manager.isContextActive())
			System.err.println("> still active: " + manager.getActiveContext().getInteresting());
		assertFalse(manager.isContextActive());
		waitForAutoBuild();
		super.tearDown();
	}

	public static void waitForAutoBuild() {
		boolean wasInterrupted = false;
		do {
			try {
				Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				wasInterrupted = false;
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				wasInterrupted = true;
			}
		} while (wasInterrupted);
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

	class TestProgressMonitor implements IProgressMonitor {

		boolean done = false;

		public void beginTask(String name, int totalWork) {
			// TODO Auto-generated method stub

		}

		public void done() {
			done = true;
		}

		public void internalWorked(double work) {
			// TODO Auto-generated method stub

		}

		public boolean isCanceled() {
			// TODO Auto-generated method stub
			return false;
		}

		public void setCanceled(boolean value) {
			// TODO Auto-generated method stub

		}

		public void setTaskName(String name) {
			// TODO Auto-generated method stub

		}

		public void subTask(String name) {
			// TODO Auto-generated method stub

		}

		public void worked(int work) {
			// TODO Auto-generated method stub

		}

		public boolean isDone() {
			return done;
		}
	}
}
