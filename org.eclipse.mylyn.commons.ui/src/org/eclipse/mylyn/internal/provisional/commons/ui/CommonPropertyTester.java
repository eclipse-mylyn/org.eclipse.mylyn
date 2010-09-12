/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import org.eclipse.core.expressions.PropertyTester;

/**
 * @author Steffen Pingel
 */
public abstract class CommonPropertyTester extends PropertyTester {

	protected boolean equals(boolean value, Object expectedValue) {
		return (expectedValue == null) ? value == true : new Boolean(value).equals(expectedValue);
	}

}
