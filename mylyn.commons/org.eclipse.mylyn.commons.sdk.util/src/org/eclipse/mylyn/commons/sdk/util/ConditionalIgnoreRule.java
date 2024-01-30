/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker and others - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.commons.sdk.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Cobbled together from: https://www.codeaffine.com/2013/11/18/a-junit-rule-to-conditionally-ignore-tests/
 * https://gist.github.com/yinzara/9980184 https://cwd.dhemery.com/2010/12/junit-rules/
 * (https://stackoverflow.com/questions/28145735/androidjunit4-class-org-junit-assume-assumetrue-assumptionviolatedexception/
 */

public class ConditionalIgnoreRule implements TestRule {

	private final IFixtureJUnitClass fixtureJUnitClass;

	public ConditionalIgnoreRule(IFixtureJUnitClass fixtureJUnitClass) {
		this.fixtureJUnitClass = fixtureJUnitClass;
	}

	public interface IgnoreCondition {
		boolean isSatisfied(AbstractTestFixture fixture);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface ConditionalIgnore {
		Class<? extends IgnoreCondition> condition();
	}

	@Override
	public Statement apply(Statement aStatement, Description aDescription) {
		Statement result = aStatement;
		if (hasConditionalIgnoreAnnotation(aDescription)) {
			IgnoreCondition condition = getIgnoreCondition(aDescription);
			if (condition.isSatisfied(fixtureJUnitClass.getActualFixture())) {
				result = new IgnoreStatement();
			}
		}

		return result;
	}

	private static boolean hasConditionalIgnoreAnnotation(Description aDescription) {
		return aDescription.getAnnotation(ConditionalIgnore.class) != null;
	}

	private static IgnoreCondition getIgnoreCondition(Description aDescription) {
		ConditionalIgnore annotation = aDescription.getAnnotation(ConditionalIgnore.class);
		return new IgnoreConditionCreator(aDescription.getTestClass(), annotation).create();
	}

	private static class IgnoreConditionCreator {
		private final Class<?> testClass;

		private final Class<? extends IgnoreCondition> conditionType;

		IgnoreConditionCreator(Class<?> testClass, ConditionalIgnore annotation) {
			this.testClass = testClass;
			conditionType = annotation.condition();
		}

		IgnoreCondition create() {
			checkConditionType();
			try {
				return createCondition();
			} catch (Exception re) {
				throw new RuntimeException(re);
			}
		}

		private IgnoreCondition createCondition() throws Exception {
			IgnoreCondition result;
			if (isConditionTypeStandalone()) {
				result = conditionType.newInstance();
			} else {
				result = conditionType.getDeclaredConstructor(testClass).newInstance(testClass);
			}
			return result;
		}

		private void checkConditionType() {
			if (!isConditionTypeStandalone() && !isConditionTypeDeclaredInTarget()) {
				String msg = """
						Conditional class '%s' is a member class\s\
						but was not declared inside the test case using it.
						Either make this class a static class,\s\
						standalone class (by declaring it in it's own file)\s\
						or move it inside the test case using it""";
				throw new IllegalArgumentException(String.format(msg, conditionType.getName()));
			}
		}

		private boolean isConditionTypeStandalone() {
			return !conditionType.isMemberClass() || Modifier.isStatic(conditionType.getModifiers());
		}

		private boolean isConditionTypeDeclaredInTarget() {
			return testClass.getClass().isAssignableFrom(conditionType.getDeclaringClass());
		}
	}

}