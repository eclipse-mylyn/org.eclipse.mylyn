/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.internal.ide.ui.MarkerViewerInterestSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 */
public class ProblemsListTest extends AbstractJavaContextTest {

	boolean done = false;

	public void testInterestSorting() throws CoreException, InvocationTargetException, InterruptedException {
		IViewPart problemsPart = JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");
		assertNotNull(problemsPart);

		IMethod m1 = type1.createMethod("void m1() { int a; }\n", null, true, null);
		IMethod m2 = type1.createMethod("void m2() { int b; }\n", null, true, null);
		type1.createMethod("void m3() { c; }", null, true, null);
		project.build();

		manager.processInteractionEvent(mockInterestContribution(m1.getHandleIdentifier(), 3f));
		manager.processInteractionEvent(mockInterestContribution(m2.getHandleIdentifier(), 2f));

		TableViewer viewer = new TableViewer(new Table(problemsPart.getViewSite().getShell(), SWT.NULL));
		viewer.setSorter(new MarkerViewerInterestSorter());

		IMarker[] markers = type1.getResource().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
				IResource.DEPTH_INFINITE);
		List<ProblemMarker> problemMarkers = new ArrayList<ProblemMarker>();
		for (IMarker marker2 : markers) {
			ProblemMarker marker = new ProblemMarker(marker2);
			problemMarkers.add(marker);
			viewer.add(marker);
		}

		// TODO: re-enable
		// // item 0 should be error
		// assertEquals(problemMarkers.get(0),
		// viewer.getTable().getItem(1).getData());
		// viewer.refresh();
		// manager.handleInteractionEvent(mockInterestContribution(m2.getHandleIdentifier(),
		// 4f));
		// for (int i = 0; i < markers.length; i++) viewer.add(new
		// ProblemMarker(markers[i]));
		// assertEquals(problemMarkers.get(1),
		// viewer.getTable().getItem(1).getData());
	}
}
