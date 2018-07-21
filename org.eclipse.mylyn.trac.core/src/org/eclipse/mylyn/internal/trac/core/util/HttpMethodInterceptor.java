/*******************************************************************************
 * Copyright (c) 2006 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.util;

import org.apache.commons.httpclient.HttpMethod;

/**
 * @author Steffen Pingel
 */
public interface HttpMethodInterceptor {

	public abstract void processRequest(HttpMethod method);

	public abstract void processResponse(HttpMethod method);

}
