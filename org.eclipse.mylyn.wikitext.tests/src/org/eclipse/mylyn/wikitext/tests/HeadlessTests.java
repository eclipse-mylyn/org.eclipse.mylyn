/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.tests.ClassTraversal.Visitor;

/**
 * A test case that runs all headless tests that are available on the classpath
 * 
 * @author dgreen
 */
public class HeadlessTests extends TestCase {

	public static Test suite() {
		final TestSuite testSuite = new TestSuite();
		testSuite.setName("All Headless Tests");

		// find all tests that meet the right criteria for running in a headless environment
		new ClassTraversal().visitClasses(new Visitor() {
			public void visit(Class<?> clazz) {
				if (isQualifyingTestClass(clazz)) {
					testSuite.addTest(new TestSuite(clazz));
				}
			}
		});

		return testSuite;
	}

	protected static boolean isQualifyingTestClass(Class<?> clazz) {
		if (HeadlessTests.class == clazz) {
			return false;
		}
		if (!TestCase.class.isAssignableFrom(clazz)) {
			return false;
		}
		int modifiers = clazz.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			return false;
		}
		if (!Modifier.isPublic(modifiers)) {
			return false;
		}
		if (clazz.getAnnotation(HeadRequired.class) != null) {
			return false;
		}

		final boolean inEclipse = WikiTextPlugin.getDefault() != null;

		if (!inEclipse && clazz.getAnnotation(EclipseRuntimeRequired.class) != null) {
			return false;
		}

		return true;
	}

}
