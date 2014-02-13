/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import java.util.Collection;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * TODO: to be removed once https://git.eclipse.org/r/#/c/14691/ is merged
 */
public class IsEmpty<T> extends BaseMatcher<T> {

	public boolean matches(Object o) {
		if (o instanceof Collection) {
			Collection<?> collection = (Collection<?>) o;
			return collection.isEmpty();
		}
		if (o instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) o;
			return map.isEmpty();
		}
		return false;
	}

	public void describeTo(Description buffer) {
		buffer.appendText("is empty");
	}

	@Factory
	public static <T> Matcher<T> isEmpty() {
		return new IsEmpty<T>();
	}

	@Factory
	public static <T> Matcher<T> empty() {
		return isEmpty();
	}
}
