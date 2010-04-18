/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.NTLMScheme;
import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;

/**
 * @author Steffen Pingel
 */
public abstract class XmlRpcOperation<T> {

	private static final Pattern RPC_METHOD_NOT_FOUND_PATTERN = Pattern.compile("No such handler: "); //$NON-NLS-1$

	protected static final int XML_FAULT_GENERAL_ERROR = 1;

	protected static final int XML_FAULT_PERMISSION_DENIED = 403;

	private final CommonXmlRpcClient client;

	public XmlRpcOperation(CommonXmlRpcClient client) {
		this.client = client;
	}

	protected Object call(IProgressMonitor monitor, String method, Object... parameters) throws XmlRpcException {
		monitor = Policy.monitorFor(monitor);
		XmlRpcException lastException = null;
		for (int attempt = 0; attempt < 3; attempt++) {
//			if (!client.isProbed()) {
//				try {
//					probeAuthenticationScheme(monitor);
//				} finally {
//					client.setProbed(true);
//				}
//			}

			try {
				return executeCall(monitor, method, parameters);
			} catch (XmlRpcLoginException e) {
				try {
					client.getLocation().requestCredentials(AuthenticationType.REPOSITORY, null, monitor);
				} catch (UnsupportedRequestException ignored) {
					throw e;
				}
				lastException = e;
			} catch (XmlRpcPermissionDeniedException e) {
				try {
					client.getLocation().requestCredentials(AuthenticationType.REPOSITORY, null, monitor);
				} catch (UnsupportedRequestException ignored) {
					throw e;
				}
				lastException = e;
			} catch (XmlRpcProxyAuthenticationException e) {
				try {
					client.getLocation().requestCredentials(AuthenticationType.PROXY, null, monitor);
				} catch (UnsupportedRequestException ignored) {
					throw e;
				}
				lastException = e;
			}
		}
		if (lastException != null) {
			throw lastException;
		} else {
			// this path should never be reached
			throw new IllegalStateException();
		}
	}

	private void checkForException(Object result) throws NumberFormatException, XmlRpcException {
		if (result instanceof Map<?, ?>) {
			Map<?, ?> exceptionData = (Map<?, ?>) result;
			if (exceptionData.containsKey("faultCode") && exceptionData.containsKey("faultString")) { //$NON-NLS-1$ //$NON-NLS-2$ 
				throw new XmlRpcException(Integer.parseInt(exceptionData.get("faultCode").toString()), //$NON-NLS-1$
						(String) exceptionData.get("faultString")); //$NON-NLS-1$
			} else if (exceptionData.containsKey("title")) { //$NON-NLS-1$
				String message = (String) exceptionData.get("title"); //$NON-NLS-1$
				String detail = (String) exceptionData.get("_message"); //$NON-NLS-1$
				if (detail != null) {
					message += ": " + detail; //$NON-NLS-1$
				}
				throw new XmlRpcException(XML_FAULT_GENERAL_ERROR, message);
			}
		}
	}

	protected boolean credentialsValid(AuthenticationCredentials credentials) {
		return credentials != null;
	}

	public abstract T execute() throws XmlRpcException;

	protected Object executeCall(IProgressMonitor monitor, String method, Object... parameters) throws XmlRpcException {
		try {
			if (CommonXmlRpcClient.DEBUG_XMLRPC) {
				System.err.println("Calling " + client.getLocation().getUrl() + ": " + method + " " + CoreUtil.toString(parameters)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			AuthenticationCredentials credentials = client.updateCredentials();
			XmlRpcClientRequest request = new XmlRpcClientRequest(client.getClient().getClientConfig(),
					getXmlRpcUrl(credentials), method, parameters, monitor);
			return client.getClient().execute(request);
		} catch (XmlRpcHttpException e) {
			handleAuthenticationException(e.code, e.getAuthScheme());
			// if not handled, re-throw exception
			throw e;
		} catch (XmlRpcException e) {
			// XXX work-around for http://trac-hacks.org/ticket/5848 
			if ("XML_RPC privileges are required to perform this operation".equals(e.getMessage()) //$NON-NLS-1$
					|| e.code == XML_FAULT_PERMISSION_DENIED) {
				handleAuthenticationException(HttpStatus.SC_FORBIDDEN, null);
				// should never happen as call above should always throw an exception
				throw new XmlRpcRemoteException(e);
			} else if (isNoSuchMethodException(e)) {
				throw new XmlRpcNoSuchMethodException(e);
			} else {
				throw new XmlRpcRemoteException(e);
			}
		} catch (OperationCanceledException e) {
			throw e;
		} catch (Exception e) {
			throw new XmlRpcException("Unexpected exception", e); //$NON-NLS-1$
		}
	}

	protected final CommonXmlRpcClient getClient() {
		return client;
	}

	protected Object getMultiCallResult(Object item) {
		return ((Object[]) item)[0];
	}

	protected URL getXmlRpcUrl(AuthenticationCredentials credentials) {
		try {
			return new URL(client.getLocation().getUrl());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Encoding of URL failed", e); //$NON-NLS-1$
		}
	}

	protected boolean handleAuthenticationException(int code, AuthScheme authScheme) throws XmlRpcException {
		if (code == HttpStatus.SC_UNAUTHORIZED) {
			if (CommonXmlRpcClient.DEBUG_AUTH) {
				System.err.println(client.getLocation().getUrl() + ": Unauthorized (" + code + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			client.digestScheme = null;
			XmlRpcLoginException exception = new XmlRpcLoginException();
			exception.setNtlmAuthRequested(authScheme instanceof NTLMScheme);
			throw exception;
		} else if (code == HttpStatus.SC_FORBIDDEN) {
			if (CommonXmlRpcClient.DEBUG_AUTH) {
				System.err.println(client.getLocation().getUrl() + ": Forbidden (" + code + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			client.digestScheme = null;
			throw new XmlRpcPermissionDeniedException();
		} else if (code == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
			if (CommonXmlRpcClient.DEBUG_AUTH) {
				System.err.println(client.getLocation().getUrl() + ": Proxy authentication required (" + code + ")"); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			throw new XmlRpcProxyAuthenticationException();
		}
		return false;
	}

	protected boolean isNoSuchMethodException(XmlRpcException e) {
		if (RPC_METHOD_NOT_FOUND_PATTERN.matcher(e.getMessage()).find()) {
			return true;
		}
		return false;
	}

//	protected Object[] multicall(IProgressMonitor monitor, Map<String, Object>... calls) throws XmlRpcException {
//		Object[] result = (Object[]) call(monitor, "system.multicall", new Object[] { calls }); //$NON-NLS-1$
//		for (Object item : result) {
//			checkForException(item);
//		}
//		return result;
//	}

	protected MulticallResult call(IProgressMonitor monitor, Multicall call) throws XmlRpcException {
		Object[] response = (Object[]) call(monitor, "system.multicall", new Object[] { call.getCalls() }); //$NON-NLS-1$
		for (Object item : response) {
			checkForException(item);
		}
		return new MulticallResult(response);
	}

}