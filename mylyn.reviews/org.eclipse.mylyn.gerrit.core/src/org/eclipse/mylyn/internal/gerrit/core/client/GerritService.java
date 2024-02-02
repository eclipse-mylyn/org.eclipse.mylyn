/*********************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Tasktop Technologies - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.JsonEntity;
import org.osgi.framework.Version;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.RemoteJsonService;

/**
 * @author Steffen Pingel
 */
public class GerritService implements InvocationHandler {

	public static class GerritRequest {

		private static ThreadLocal<GerritRequest> currentRequest = new ThreadLocal<>();

		public static GerritRequest getCurrentRequest() {
			return currentRequest.get();
		}

		public static void setCurrentRequest(GerritRequest request) {
			currentRequest.set(request);
		}

		private final IProgressMonitor monitor;

		public GerritRequest(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public IProgressMonitor getMonitor() {
			return monitor;
		}

	}

	public static <T extends RemoteJsonService> T create(Class<T> serviceClass, GerritHttpClient gerritHttpClient,
			Version version) {
		InvocationHandler handler = new GerritService(gerritHttpClient,
				GerritConnector.GERRIT_260_RPC_URI + serviceClass.getSimpleName());
		return serviceClass.cast(
				Proxy.newProxyInstance(GerritService.class.getClassLoader(), new Class<?>[] { serviceClass }, handler));
	}

	private final String uri;

	protected GerritHttpClient client;

	public GerritService(GerritHttpClient client, String uri) {
		this.client = client;
		this.uri = uri;
	}

	public String getServiceUri() {
		return uri;
	}

	@Override
	public Object invoke(Object proxy, final Method method, Object[] args) {
		final JSonSupport json = new JSonSupport();

		// construct request
		final List<Object> parameters = new ArrayList<>(args.length - 1);
		for (int i = 0; i < args.length - 1; i++) {
			parameters.add(args[i]);
		}
		@SuppressWarnings("unchecked")
		AsyncCallback<Object> callback = (AsyncCallback<Object>) args[args.length - 1];

		try {
			GerritRequest request = GerritRequest.getCurrentRequest();
			IProgressMonitor monitor = request != null ? request.getMonitor() : null;

			// execute request
			String responseMessage = client.postJsonRequest(getServiceUri(), new JsonEntity() {
				@Override
				public String getContent() {
					String methodName = method.getName();
					if (methodName.endsWith("X")) { //$NON-NLS-1$
						methodName = methodName.substring(0, methodName.length() - 1);
					}
					return json.createRequest(client.getId(), client.getXsrfKey(), methodName, parameters);
				}
			}, monitor);

			// the last parameter is a parameterized callback that defines the return type
			Type[] types = method.getGenericParameterTypes();
			final Type resultType = ((ParameterizedType) types[types.length - 1]).getActualTypeArguments()[0];

			Object result = json.parseJsonResponse(responseMessage, resultType);
			callback.onSuccess(result);
		} catch (Throwable e) {
			callback.onFailure(e);
		}
		// all methods are designed to be asynchronous and expected to return void
		return null;
	}

}
