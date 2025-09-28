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

/**
 * JUnit 5 annotation that sets up the JUnit 5 environment for Mylyn tests.
 * <p/>
 * Usage: @MylynTestSetup
 */
package org.eclipse.mylyn.commons.sdk.util.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.mylyn.commons.sdk.util.junit5.extension.TestMethodWrapper;
import org.eclipse.mylyn.commons.sdk.util.junit5.extension.TestResultLogger;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ TestMethodWrapper.class, TestResultLogger.class })
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited

public @interface MylynTestSetup {
}
