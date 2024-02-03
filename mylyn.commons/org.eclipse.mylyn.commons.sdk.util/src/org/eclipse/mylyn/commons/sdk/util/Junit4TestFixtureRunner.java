/*******************************************************************************
 * Copyright (c) 2013, 2024 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.osgi.util.NLS;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

@SuppressWarnings({ "nls", "restriction" })
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
			return String.format("[%s %s]", fFixtureSetNumber, fFixtureList.get(fFixtureSetNumber).getSimpleInfo());
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			if (Boolean.getBoolean("org.eclipse.mylyn.tests.all")) {
				return String.format("%s[%s %s]", method.getName(), fFixtureSetNumber,
						fFixtureList.get(fFixtureSetNumber).getSimpleInfo());
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

		@Override
		protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
			Description description = describeChild(method);
			if (isIgnored(method)) {
				notifier.fireTestIgnored(description);
			} else {
				Object test = null;
				try {
					test = new ReflectiveCallable() {
						@Override
						protected Object runReflectiveCall() throws Throwable {
							return createTest();
						}
					}.run();
				} catch (Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, "org.eclipse.mylyn.commons.sdk.util", //$NON-NLS-1$
							NLS.bind("TestClassRunnerForFixture: Testclass {0} has no public constructor", //$NON-NLS-1$
									getTestClass().getName()),
							e));
					return;
				}
				boolean skipped = false;
				if (test != null) {
					List<TestRule> testRules = getTestRules(test);
					for (TestRule testRule : testRules) {
						if (testRule instanceof ConditionalIgnoreRule) {
							Statement statement = testRule.apply(null, description);
							if (statement instanceof IgnoreStatement) {
								skipped = true;
								break;
							}
						}
					}
					if (skipped) {
						notifier.fireTestIgnored(description);
					} else {
						runTest(methodBlock(method), description, notifier);
					}
				}
			}
		}

		protected final void runTest(Statement statement, Description description, RunNotifier notifier) {
			EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
			eachNotifier.fireTestStarted();
			try {
				statement.evaluate();
			} catch (AssumptionViolatedException e) {
				eachNotifier.addFailedAssumption(e);
			} catch (IgnoreRuleRuntimeException e) {
				eachNotifier.fireTestIgnored();
			} catch (Throwable e) {
				eachNotifier.addFailure(e);
			} finally {
				eachNotifier.fireTestFinished();
			}
		}
	}

	private final ArrayList<Runner> runners = new ArrayList<>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public Junit4TestFixtureRunner(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner> emptyList());

		if (CommonTestUtil.fixProxyConfiguration()) {
			CommonTestUtil.dumpSystemInfo(System.err);
		}

		String restrictProperty = null;
		String restrictValue = null;
		Class<?> fixtureClass = null;

		String fixtureType = null;
		for (Annotation annotation : getTestClass().getAnnotations()) {
			if (annotation.annotationType() == RunOnlyWhenProperty.class) {
				RunOnlyWhenProperty onlyWhenProperty = (RunOnlyWhenProperty) annotation;
				restrictProperty = onlyWhenProperty.property();
				restrictValue = onlyWhenProperty.value();
			}
			if (annotation.annotationType() == FixtureDefinition.class) {
				FixtureDefinition fixtueDef = (FixtureDefinition) annotation;
				fixtureClass = fixtueDef.fixtureClass();
				fixtureType = fixtueDef.fixtureType();
			}
		}
		if (fixtureType != null) {
			TestConfiguration defFixture = TestConfiguration.getDefault();
			List<AbstractTestFixture> parametersList = (List<AbstractTestFixture>) defFixture.discover(fixtureClass,
					fixtureType);
			ArrayList<AbstractTestFixture> sortedParametersList = new ArrayList<>(parametersList);
			sortedParametersList.sort(Comparator.comparing(AbstractTestFixture::getInfo));
			List<AbstractTestFixture> fixturesToExecute = new ArrayList<>();
			if (restrictProperty != null) {
				for (AbstractTestFixture abstractFixture : sortedParametersList) {
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
			} else if (sortedParametersList.size() > 0) {
				for (int i = 0; i < sortedParametersList.size(); i++) {
					runners.add(new TestClassRunnerForFixture(getTestClass().getJavaClass(), sortedParametersList, i));
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
