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
package org.eclipse.mylar.java.tests.search;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.mylar.context.core.IInteractionElement;
import org.eclipse.mylar.context.tests.support.search.ISearchPluginTest;
import org.eclipse.mylar.context.tests.support.search.TestActiveSearchListener;
import org.eclipse.mylar.internal.context.core.IMylarSearchOperation;
import org.eclipse.mylar.internal.java.search.JavaWriteAccessProvider;

public class JavaWriteAccessSearchPluginTest extends TestCase implements ISearchPluginTest {

	public void testJavaWriteAccessSearchDOS1() {
		// List<?> results = search(1);
	}

	//	
	// public void testJavaWriteAccessSearchDOS2(){
	// List<?> results = search(2);
	// }
	//	
	// public void testJavaWriteAccessSearchDOS3(){
	// List<?> results = search(3);
	// }
	//	
	// public void testJavaWriteAccessSearchDOS4(){
	// List<?> results = search(4);
	// }
	//	
	// public void testJavaWriteAccessSearchDOS5(){
	// List<?> results = search(5);
	// }

	public List<?> search(int dos, IInteractionElement node) {

		if (node == null)
			return null;

		// test with each of the sepatations
		JavaWriteAccessProvider prov = new JavaWriteAccessProvider();

		TestActiveSearchListener l = new TestActiveSearchListener(prov);
		IMylarSearchOperation o = prov.getSearchOperation(node, IJavaSearchConstants.WRITE_ACCESSES, dos);
		SearchPluginTestHelper.search(o, l);
		return l.getResults();
	}
}
