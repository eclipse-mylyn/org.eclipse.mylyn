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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UsernamePasswordCredentials;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpOperation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public abstract class HudsonOperation<T> extends CommonHttpOperation<T> {

	public HudsonOperation(CommonHttpClient client) {
		super(client);
	}

	@Override
	protected void authenticate(IOperationMonitor monitor) throws IOException {
		UsernamePasswordCredentials credentials = getClient().getLocation().getCredentials(
				AuthenticationType.REPOSITORY, UsernamePasswordCredentials.class);

		HttpPost request = createPostRequest(getClient().getLocation().getUrl() + "/j_acegi_security_check"); //$NON-NLS-1$
		HudsonLoginForm form = new HudsonLoginForm();
		form.j_username = credentials.getUserName();
		form.j_password = credentials.getPassword();
		form.from = ""; //$NON-NLS-1$
		request.setEntity(form.createEntity());
		HttpResponse response = getClient().execute(request, monitor);
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
				getClient().setAuthenticated(false);
				throw new IOException(NLS.bind("Unexpected response from Hudson server while logging in: {0}",
						HttpUtil.getStatusText(statusCode)));
			}
			Header header = response.getFirstHeader("Location");
			if (header != null && header.getValue().endsWith("/loginError")) {
				getClient().setAuthenticated(false);
				throw new IOException("Login failed");
			}
			getClient().setAuthenticated(true);
		} finally {
			HttpUtil.release(request, response, monitor);
		}
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

	protected T doProcess(CommonHttpResponse response, IOperationMonitor monitor) throws IOException, HudsonException,
			JAXBException {
		return null;
	}

	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor) throws IOException,
			HudsonException {
		validate(response, HttpStatus.SC_OK, monitor);
	}

	protected abstract T execute() throws IOException, HudsonException, JAXBException;

	protected T process(CommonHttpResponse response, IOperationMonitor monitor) throws IOException, HudsonException,
			JAXBException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} catch (IOException e) {
			response.release(monitor);
			throw e;
		} catch (HudsonException e) {
			response.release(monitor);
			throw e;
		} catch (JAXBException e) {
			response.release(monitor);
			throw e;
		} catch (RuntimeException e) {
			response.release(monitor);
			throw e;
		}
	}

	protected T processAndRelease(CommonHttpResponse response, IOperationMonitor monitor) throws IOException,
			HudsonException, JAXBException {
		try {
			doValidate(response, monitor);
			return doProcess(response, monitor);
		} finally {
			response.release(monitor);
		}
	}

	protected void validate(CommonHttpResponse response, int expected, IOperationMonitor monitor)
			throws HudsonException {
		int statusCode = response.getStatusCode();
		if (statusCode != expected) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new HudsonResourceNotFoundException(NLS.bind("Requested resource ''{0}'' does not exist",
						response.getRequestPath()));
			}
			throw new HudsonException(NLS.bind("Unexpected response from Hudson server for ''{0}'': {1}",
					response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
		}
	}

	@Override
	protected boolean needsAuthentication() {
		return !getClient().isAuthenticated()
				&& getClient().getLocation().getCredentials(AuthenticationType.REPOSITORY,
						UsernamePasswordCredentials.class) != null;
	}

}