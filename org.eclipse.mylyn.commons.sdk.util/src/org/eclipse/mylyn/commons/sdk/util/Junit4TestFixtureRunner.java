/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class Junit4TestFixtureRunner extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface RunOnlyWhenProperty {
		String property() default "";

		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface FixtureDefinition {
		Class<?> fixtureClass();

		String fixtureType();
	}

	@SuppressWarnings("restriction")
	private class TestClassRunnerForFixture extends BlockJUnit4ClassRunner {
		private final int fFixtureSetNumber;

		private final List<AbstractTestFixture> fFixtureList;

		TestClassRunnerForFixture(Class<?> type, List<AbstractTestFixture> fixtureList, int i)
				throws InitializationError {
			super(type);
			fFixtureList = fixtureList;
			fFixtureSetNumber = i;
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(fFixtureList.get(fFixtureSetNumber));
		}

		@Override
		protected String getName() {
			return String.format("[%s %s]", fFixtureSetNumber, fFixtureList.get(fFixtureSetNumber).getDescription());
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			if (Boolean.parseBoolean(System.getProperty("org.eclipse.mylyn.tests.all"))) {
				return String.format("%s[%s %s]", method.getName(), fFixtureSetNumber, fFixtureList.get(0)
						.getDescription());
			} else {
				return super.testName(method);
			}
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
	}

	private final ArrayList<Runner> runners = new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public Junit4TestFixtureRunner(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner> emptyList());

		String restrictProperty = null;
		String restrictValue = null;
		Class<?> fixtureClass = null;

		String fixtureType = null;
		for (Annotation annotation : getTestClass().getAnnotations()) {
			if ("org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.OnlyRunWithProperty".equals(annotation.annotationType()
					.getCanonicalName())) {
				RunOnlyWhenProperty onlyWhenProperty = (RunOnlyWhenProperty) annotation;
				restrictProperty = onlyWhenProperty.property();
				restrictValue = onlyWhenProperty.value();
			}
			if ("org.eclipse.mylyn.commons.sdk.util.Junit4TestFixtureRunner.FixtureDefinition".equals(annotation.annotationType()
					.getCanonicalName())) {
				FixtureDefinition fixtueDef = (FixtureDefinition) annotation;
				fixtureClass = fixtueDef.fixtureClass();
				fixtureType = fixtueDef.fixtureType();
			}
		}
		if (fixtureType != null) {
			List<AbstractTestFixture> parametersList = (List<AbstractTestFixture>) TestConfiguration.getDefault()
					.discover(fixtureClass, fixtureType);
			List<AbstractTestFixture> fixturesToExecute = new ArrayList<AbstractTestFixture>();
			if (restrictProperty != null) {
				for (AbstractTestFixture abstractFixture : parametersList) {
					String tempProperty = abstractFixture.getProperty(restrictProperty);
					if (tempProperty != null && tempProperty.equals(restrictValue)) {
						fixturesToExecute.add(abstractFixture);
					}
				}
				if (fixturesToExecute.size() > 0) {
					for (int i = 0; i < fixturesToExecute.size(); i++) {
						runners.add(new TestClassRunnerForFixture(getTestClass().getJavaClass(), fixturesToExecute, i));
					}
				}
			} else if (parametersList.size() > 0) {
				for (int i = 0; i < parametersList.size(); i++) {
					runners.add(new TestClassRunnerForFixture(getTestClass().getJavaClass(), parametersList, i));
				}
			}
		} else {
			throw new InitializationError("Missing Annotation FixtureDefinition for Junit4TestFixtureRunner");
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

}
