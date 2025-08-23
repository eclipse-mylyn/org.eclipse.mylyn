/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
*******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util.junit4;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;

public class ManagedSuite extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface TestConfigurationProperty {

		boolean localOnly() default false;

		boolean defaultOnly() default false;

		boolean headless() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SuiteClassProvider {

	}

	public static TestConfiguration testConfiguration;

	public static TestConfiguration getTestConfiguration() {
		return testConfiguration;
	}

	public static TestConfiguration getTestConfigurationOrCreateDefault() {
		if (testConfiguration == null) {
			testConfiguration = TestConfiguration.getDefault();
		}
		return testConfiguration;
	}

	public static void setTestConfiguration(TestConfiguration testConfiguration) {
		ManagedSuite.testConfiguration = testConfiguration;
	}

	public ManagedSuite(Class<?> klass, RunnerBuilder builder) throws Throwable {
		super(klass, builder.runners(klass, getSuiteClasses(klass, klass.getClasses())));
	}

	private static Class<?>[] getSuiteClasses(Class<?> klass, Class<?>[] classes) throws SecurityException {
		final List<Class<?>> suiteClassList = new ArrayList<>();
		for (Annotation annotation : klass.getAnnotations()) {
			if (annotation.annotationType() == TestConfigurationProperty.class) {
				if (getTestConfiguration() == null) {
					TestConfigurationProperty configurationProperty = (TestConfigurationProperty) annotation;
					TestConfiguration testConfiguration = new TestConfiguration();
					testConfiguration.setLocalOnly(configurationProperty.localOnly());
					testConfiguration.setDefaultOnly(configurationProperty.defaultOnly());
					testConfiguration.setHeadless(configurationProperty.headless());
					setTestConfiguration(testConfiguration);
				}
			} else if (annotation.annotationType() == SuiteClasses.class) {
				SuiteClasses suiteClasses = (SuiteClasses) annotation;
				Collections.addAll(suiteClassList, suiteClasses.value());
			}
		}
		if (getTestConfiguration() == null) {
			setTestConfiguration(TestConfiguration.getDefault());
		}
		for (Method method : klass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(SuiteClassProvider.class)) {
				try {
					TestConfiguration conf = getTestConfiguration();
					Parameter[] parameters = method.getParameters();
					if (parameters.length == 2) {
						if (parameters[0].getType() == List.class
								&& parameters[1].getType() == TestConfiguration.class) {
							if (Modifier.isStatic(method.getModifiers())) {
								if (!Modifier.isPublic(method.getModifiers())) {
									method.setAccessible(true);
								}
								method.invoke(null, suiteClassList, conf);
							}
						}
					}
				} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
					// Ignore
				}
			}
		}
		return suiteClassList.toArray(new Class<?>[suiteClassList.size()]);
	}

	@Override
	public void run(RunNotifier notifier) {
		JUnitExecutionListener listener = JUnitExecutionListener.getDefault();
		if (listener == null) {
			notifier.addListener(JUnitExecutionListener.createDefault());
		}
		super.run(notifier);
	}

}
