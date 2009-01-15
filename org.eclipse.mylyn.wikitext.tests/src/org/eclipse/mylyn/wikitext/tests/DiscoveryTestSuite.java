/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.mylyn.internal.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.tests.ClassTraversal.Visitor;

/**
 * a test suite that discovers tests by traversing the classpath looking for test classes that match specific criteria.
 * 
 * @author David Green
 */
@NoDiscovery
public class DiscoveryTestSuite extends TestSuite implements ClassFilter {

	public DiscoveryTestSuite(ClassFilter filter) {
		discoverTests(filter);
	}

	protected void discoverTests(final ClassFilter filter) {
		// find all tests that meet the right criteria for running in a headless environment
		new ClassTraversal().visitClasses(new Visitor() {
			public void visit(Class<?> clazz) {
				if (!filter(clazz) && !filter.filter(clazz)) {
					addTest(new TestSuite(clazz));
				}
			}
		});
	}

	/**
	 * indicate if the eclipse runtime is active
	 */
	public static boolean inEclipseRuntime() {
		return WikiTextPlugin.getDefault() != null;
	}

	public boolean filter(Class<?> clazz) {
		if (!TestCase.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (DiscoveryTestSuite.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (clazz.getAnnotation(NoDiscovery.class) != null) {
			return true;
		}
		int modifiers = clazz.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			return true;
		}
		if (!Modifier.isPublic(modifiers)) {
			return true;
		}
		return false;
	}
}
