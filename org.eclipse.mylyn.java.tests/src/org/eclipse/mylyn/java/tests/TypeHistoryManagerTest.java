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

package org.eclipse.mylyn.java.tests;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.OpenTypeHistory;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.java.TypeHistoryManager;

/**
 * @author Mik Kersten
 */
public class TypeHistoryManagerTest extends AbstractJavaContextTest {

	private TypeHistoryManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = new TypeHistoryManager();
//		ContextCorePlugin.getContextManager().addListener(manager);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
//		ContextCorePlugin.getContextManager().removeListener(manager);
	}

	public void testPredictedElementPopulationOfTypeHistory() throws JavaModelException {
		manager.clearTypeHistory();
		assertEquals(0, OpenTypeHistory.getInstance().getTypeInfos().length);
 
		StructuredSelection sm1 = new StructuredSelection(type1);
		monitor.selectionChanged(PackageExplorerPart.openInActivePerspective(), sm1);
		assertEquals(1, OpenTypeHistory.getInstance().getTypeInfos().length);

		IType type2 = project.createType(p1, "Type2.java", "public class Type2 { }");
		IMethod m1 = type2.createMethod("void m1() { }", null, true, null);
		StructuredSelection sm2 = new StructuredSelection(m1);
		monitor.selectionChanged(PackageExplorerPart.openInActivePerspective(), sm2);
		assertEquals(2, OpenTypeHistory.getInstance().getTypeInfos().length);
	}
}
