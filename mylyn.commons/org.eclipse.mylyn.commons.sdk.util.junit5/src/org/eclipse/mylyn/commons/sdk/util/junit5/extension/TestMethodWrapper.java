/*******************************************************************************
 * Copyright (c) 2025 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util.junit5.extension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import org.eclipse.mylyn.commons.sdk.util.junit5.MylynTestSetup;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * print name of each test method as it starts, and <br/>
 * do a thread dump if a test times out.
 * <p/>
 * To use, annotate test classes with @ExtendWith(TestMethodWrapper.class) or create a custom annotation that includes it, e.g.
 * {@link MylynTestSetup}.
 */
@SuppressWarnings("nls")
public class TestMethodWrapper implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		Object testInstance = context.getRequiredTestInstance();
		// Try to get 'fixture' field if present, searching superclasses
		String fixtureInfo = "";
		try {
			Class<?> clazz = testInstance.getClass();
			Field fixtureField = null;
			while (clazz != null) {
				try {
					fixtureField = clazz.getDeclaredField("fixture");
					break;
				} catch (NoSuchFieldException e) {
					clazz = clazz.getSuperclass();
				}
			}
			if (fixtureField != null) {
				fixtureField.setAccessible(true);
				Object fixture = fixtureField.get(testInstance);
				if (fixture != null) {
					try {
						Method getInfo = fixture.getClass().getMethod("getInfo");
						Object info = getInfo.invoke(fixture);
						fixtureInfo = String.valueOf(info);
					} catch (Exception ignored) {
					}
				}
			}
		} catch (Exception ignored) {
			// ignore
		}

		System.err.println("Running " + qualifiedTestName(context) + (fixtureInfo.isEmpty() ? "" : "@" + fixtureInfo));
	}

	private static String qualifiedTestName(ExtensionContext context) {
		return context.getTestClass().get().getSimpleName() + "." + context.getTestMethod().get().getName();
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		context.getExecutionException().ifPresent(ex -> {
			if (ex instanceof TimeoutException) {
				System.err.println("\n=== THREAD DUMP ===");
				for (Thread t : Thread.getAllStackTraces().keySet()) {
					System.err.println("Thread: " + t.getName());
					for (StackTraceElement ste : t.getStackTrace()) {
						System.err.println("\tat " + ste);
					}
				}
				System.err.println("=== END THREAD DUMP ===\n");
			}
		});
	}
}