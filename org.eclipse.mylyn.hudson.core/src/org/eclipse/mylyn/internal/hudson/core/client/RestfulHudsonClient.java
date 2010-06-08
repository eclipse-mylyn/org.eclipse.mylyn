/*******************************************************************************
 * Copyright (c) 2010 Markus Knittig and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Knittig - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;

/**
 * Represents the Hudson repository that is accessed through REST.
 * 
 * @author Markus Knittig
 */
public class RestfulHudsonClient {

	private final AbstractWebLocation location;

	private final HttpClient httpClient;

	public RestfulHudsonClient(AbstractWebLocation location) {
		this.location = location;

		httpClient = new HttpClient();
		WebUtil.configureHttpClient(httpClient, "Mylyn"); //$NON-NLS-1$
		WebUtil.createHostConfiguration(httpClient, location, new NullProgressMonitor());
	}

	public IStatus validate(IOperationMonitor monitor) {
		GetMethod get = new GetMethod(location.getUrl() + "/api/xml"); //$NON-NLS-1$
		try {
			if (httpClient.executeMethod(get) >= HttpStatus.SC_BAD_REQUEST) {
				return Status.CANCEL_STATUS;
			}
		} catch (IOException e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

}
