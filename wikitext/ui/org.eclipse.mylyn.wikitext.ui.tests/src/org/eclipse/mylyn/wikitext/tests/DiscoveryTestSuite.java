/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.core.runtime.Platform;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

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
		new ClassTraversal().visitClasses(clazz -> {
			if (!filter(clazz) && !filter.filter(clazz)) {
				if (TestSuite.class.isAssignableFrom(clazz)) {
					try {
						Method suiteMethod = clazz.getMethod("suite");
						if (Modifier.isStatic(suiteMethod.getModifiers())
								&& Modifier.isPublic(suiteMethod.getModifiers())) {
							addTest((Test) suiteMethod.invoke(null));
						} else {
							throw new IllegalStateException(clazz.getName() + "#" + suiteMethod.getName());
						}
					} catch (NoSuchMethodException e2) {
						try {
							addTest((Test) clazz.getConstructor().newInstance());
						} catch (InstantiationException e11) {
							throw new IllegalStateException(clazz.getName(), e11);
						} catch (IllegalAccessException e12) {
							throw new IllegalStateException(clazz.getName(), e12);
						} catch (IllegalArgumentException e13) {
							throw new IllegalStateException(clazz.getName(), e13);
						} catch (InvocationTargetException e14) {
							throw new IllegalStateException(clazz.getName(), e14);
						} catch (NoSuchMethodException e15) {
							throw new IllegalStateException(clazz.getName(), e15);
						} catch (SecurityException e16) {
							throw new IllegalStateException(clazz.getName(), e16);
						}
					} catch (Throwable e3) {
						throw new IllegalStateException(clazz.getName(), e3);
					}
				} else {
					addTest(new JUnit4TestAdapter(clazz));
				}
			}
		});
	}

	private boolean hasTestMethods(Class<?> clazz) {
		for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
			Method[] methods = c.getDeclaredMethods();
			if (methods != null) {
				for (Method m : methods) {
					if (m.getAnnotation(org.junit.Test.class) != null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * indicate if the eclipse runtime is active
	 */
	public static boolean inEclipseRuntime() {
		return Platform.isRunning();
	}

	public boolean filter(Class<?> clazz) {
		if (!Test.class.isAssignableFrom(clazz)) {
			if (!hasTestMethods(clazz)) {
				return true;
			}
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
