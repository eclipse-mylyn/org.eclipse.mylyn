/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;

public class ManagedSuite extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface TestConfigurationProperty {
		TestConfiguration.TestKind kind() default TestConfiguration.TestKind.UNIT;

		boolean localOnly() default false;

		boolean defaultOnly() default false;

		boolean headless() default false;
	}

	public static TestConfiguration testConfiguration;

	public static TestConfiguration getTestConfiguration() {
		return testConfiguration;
	}

	public static void setTestConfiguration(TestConfiguration testConfiguration) {
		ManagedSuite.testConfiguration = testConfiguration;
	}

	public ManagedSuite(Class<?> klass, RunnerBuilder builder) throws Throwable {
		super(klass, builder.runners(klass, getSuiteClasses(klass, klass.getClasses())));
	}

	private static Class<?>[] getSuiteClasses(Class<?> klass, Class<?>[] classes) throws SecurityException {
		final List<Class<?>> suiteClassList = new ArrayList<Class<?>>();
		for (Annotation annotation : klass.getAnnotations()) {
			if (annotation.annotationType() == TestConfigurationProperty.class) {
				if (getTestConfiguration() == null) {
					TestConfigurationProperty configurationProperty = (TestConfigurationProperty) annotation;
					TestConfiguration testConfiguration = new TestConfiguration(configurationProperty.kind());
					testConfiguration.setLocalOnly(configurationProperty.localOnly());
					testConfiguration.setDefaultOnly(configurationProperty.defaultOnly());
					testConfiguration.setHeadless(configurationProperty.headless());
					setTestConfiguration(testConfiguration);
				}
			} else if (annotation.annotationType() == SuiteClasses.class) {
				SuiteClasses suiteClasses = (SuiteClasses) annotation;
				for (Class<?> suiteClass : suiteClasses.value()) {
					suiteClassList.add(suiteClass);
				}
			}
		}
		if (getTestConfiguration() == null) {
			setTestConfiguration(TestConfiguration.getDefault());
		}
		try {
			Method method = klass.getMethod("add2SuiteClasses", java.util.List.class,
					org.eclipse.mylyn.commons.sdk.util.TestConfiguration.class);
			if (method != null) {
				TestConfiguration conf = getTestConfiguration();
				method.invoke(null, suiteClassList, conf);
			}
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException
				| IllegalArgumentException e) {
			// Ignore
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
