/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.service;

import org.apache.commons.httpclient.HttpMethod;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public interface BaseHttpMethodInterceptor {

	public abstract void processRequest(HttpMethod method);

	public abstract void processResponse(HttpMethod method);

}
