/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
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

package org.eclipse.mylyn.internal.trac.core.client;

import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.commons.xmlrpc.CommonXmlRpcClient;
import org.eclipse.mylyn.internal.commons.xmlrpc.XmlRpcOperation;
import org.eclipse.mylyn.internal.commons.xmlrpc.XmlRpcPermissionDeniedException;

/**
 * @author Steffen Pingel
 */
abstract class TracXmlRpcOperation<T> extends XmlRpcOperation<T> {

	private static final Pattern RPC_METHOD_NOT_FOUND_PATTERN = Pattern.compile("RPC method \".*\" not found"); //$NON-NLS-1$

	private boolean accountMangerAuthenticationFailed;

	public TracXmlRpcOperation(CommonXmlRpcClient client) {
		super(client);
	}

	@Override
	protected Object executeCall(IProgressMonitor monitor, String method, Object... parameters) throws XmlRpcException {
		try {
			// first attempt
			preCall();
			return super.executeCall(monitor, method, parameters);
		} catch (XmlRpcPermissionDeniedException e) {
			if (accountMangerAuthenticationFailed) {
				// do not try again if this has failed in the past since it
				// is more likely that XML_RPC permissions have not been set
				throw e;
			}

			AuthenticationCredentials credentials = getClient().getLocation().getCredentials(
					AuthenticationType.REPOSITORY);
			if (!credentialsValid(credentials)) {
				throw e;
			}

			// try form-based authentication via AccountManagerPlugin as a
			// fall-back
//			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(getClient().getHttpClient(),
//					getClient().getLocation(), monitor);
//			try {
//				authenticateAccountManager(getClient().httpClient, hostConfiguration, credentials, monitor);
//			} catch (XmlRpcLoginException loginException) {
//				// caused by wrong user name or password
//				throw loginException;
//			} catch (IOException ignore) {
//				accountMangerAuthenticationFailed = true;
//				throw e;
//			}
//
//			try {
//				validateAuthenticationState(getClient().httpClient);
//			} catch (XmlRpcLoginException ignore) {
//				// most likely form based authentication is not supported by
//				// repository
//				accountMangerAuthenticationFailed = true;
//				throw e;
//			}

			// the authentication information is available through the shared state in httpClient
		}

		// second attempt
		preCall();
		return super.executeCall(monitor, method, parameters);
	}

	private void preCall() {
//		if (isTracd && digestScheme != null) {
//		probeAuthenticationScheme(monitor);
//	}
	}

//	void probeAuthenticationScheme(IProgressMonitor monitor) throws XmlRpcException {
//	AuthenticationCredentials credentials = client.getLocation().getCredentials(AuthenticationType.REPOSITORY);
//	if (!credentialsValid(credentials)) {
//		return;
//	}
//
//	if (CommonXmlRpcClient.DEBUG_AUTH) {
//		System.err.println(client.getLocation().getUrl() + ": Probing authentication"); //$NON-NLS-1$ 
//	}
//	HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
//	HeadMethod method = new HeadMethod(getXmlRpcUrl(credentials).toString());
//	try {
//		// execute without any credentials set
//		int result = WebUtil.execute(httpClient, hostConfiguration, method, new HttpState(), monitor);
//		if (CommonXmlRpcClient.DEBUG_AUTH) {
//			System.err.println(client.getLocation().getUrl()
//					+ ": Received authentication response (" + result + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
//		}
//		if (result == HttpStatus.SC_UNAUTHORIZED || result == HttpStatus.SC_FORBIDDEN) {
//			AuthScheme authScheme = method.getHostAuthState().getAuthScheme();
//			if (authScheme instanceof DigestScheme) {
//				client.digestScheme = (DigestScheme) authScheme;
//				if (CommonXmlRpcClient.DEBUG_AUTH) {
//					System.err.println(client.getLocation().getUrl() + ": Received digest scheme"); //$NON-NLS-1$ 
//				}
//			} else if (authScheme instanceof BasicScheme) {
//				httpClient.getParams().setAuthenticationPreemptive(true);
//				if (CommonXmlRpcClient.DEBUG_AUTH) {
//					System.err.println(client.getLocation().getUrl() + ": Received basic scheme"); //$NON-NLS-1$ 
//				}
//			} else if (authScheme != null) {
//				if (CommonXmlRpcClient.DEBUG_AUTH) {
//					System.err.println(client.getLocation().getUrl()
//							+ ": Received scheme (" + authScheme.getClass() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
//				}
//			} else {
//				if (CommonXmlRpcClient.DEBUG_AUTH) {
//					System.err.println(client.getLocation().getUrl() + ": No authentication scheme received"); //$NON-NLS-1$ 
//				}
//			}
//
//			Header header = method.getResponseHeader("Server"); //$NON-NLS-1$
//			isTracd = (header != null && header.getValue().startsWith("tracd")); //$NON-NLS-1$
//			if (CommonXmlRpcClient.DEBUG_AUTH && isTracd) {
//				System.err.println(client.getLocation().getUrl() + ": Tracd detected"); //$NON-NLS-1$ 
//			}
//		}
//	} catch (IOException e) {
//		// ignore
//	} finally {
//		method.releaseConnection();
//	}
//}

	@Override
	protected boolean isNoSuchMethodException(XmlRpcException e) {
		// the fault code is used for various errors, therefore detection is based on the message
		// message format by XML-RPC Plugin version:
		//  1.0.1: XML-RPC method "ticket.ge1t" not found
		//  1.0.6: RPC method "ticket.ge1t" not found
		//  1.10:  RPC method "ticket.ge1t" not found' while executing 'ticket.ge1t()
		if (e.code == XML_FAULT_GENERAL_ERROR && e.getMessage() != null
				&& RPC_METHOD_NOT_FOUND_PATTERN.matcher(e.getMessage()).find()) {
			return true;
		}
		return false;
	}

}
