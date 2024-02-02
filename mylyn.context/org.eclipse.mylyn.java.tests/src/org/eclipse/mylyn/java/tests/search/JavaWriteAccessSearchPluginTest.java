/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.java.tests.search;

import java.util.List;

import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.util.search.ISearchPluginTest;
import org.eclipse.mylyn.context.sdk.util.search.TestActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.java.ui.search.JavaWriteAccessProvider;

import junit.framework.TestCase;

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

	@Override
	public List<?> search(int dos, IInteractionElement node) {

		if (node == null) {
			return null;
		}

		// test with each of the sepatations
		JavaWriteAccessProvider prov = new JavaWriteAccessProvider();

		TestActiveSearchListener l = new TestActiveSearchListener(prov);
		IActiveSearchOperation o = prov.getSearchOperation(node, IJavaSearchConstants.WRITE_ACCESSES, dos);
		SearchPluginTestHelper.search(o, l);
		return l.getResults();
	}
}
