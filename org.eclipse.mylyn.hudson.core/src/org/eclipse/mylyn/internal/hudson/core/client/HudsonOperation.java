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

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.eclipse.mylyn.commons.http.CommonHttpClient;
import org.eclipse.mylyn.commons.http.HttpOperation;

/**
 * @author Steffen Pingel
 */
public abstract class HudsonOperation<T> extends HttpOperation<T> {

	public HudsonOperation(CommonHttpClient client) {
		super(client);
	}

	public T run() throws HudsonException {
		try {
			return execute();
		} catch (IOException e) {
			throw new HudsonException(e);
		} catch (JAXBException e) {
			throw new HudsonException(e);
		}
	}

	protected abstract T execute() throws IOException, HudsonException, JAXBException;

}