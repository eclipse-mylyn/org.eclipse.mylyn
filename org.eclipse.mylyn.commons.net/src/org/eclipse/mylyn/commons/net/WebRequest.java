/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

import java.util.concurrent.Callable;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class WebRequest<T> implements Callable<T> {

	/**
	 * @since 3.0
	 */
	public abstract void abort();

}