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
 * JUnit 5 annotation that enables a test if it needs a CI server to complete.
 * <p/>
 * Usage: @EnabledIfCI
 */

package org.eclipse.mylyn.commons.sdk.util.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.mylyn.commons.sdk.util.junit5.condition.EnabledIfCICondition;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(EnabledIfCICondition.class)
@Inherited
public @interface EnabledIfCI {

}