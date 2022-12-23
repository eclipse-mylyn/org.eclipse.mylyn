/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.ErrorHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GerritRestClient {

	static abstract class Operation<T> implements AsyncCallback<T> {

		private Throwable exception;

		private T result;

		private final GerritHttpClient client;

		public abstract void execute(IProgressMonitor monitor) throws GerritException;

		public Operation(GerritHttpClient client) {
			this.client = client;
		}

		public Throwable getException() {
			return exception;
		}

		public T getResult() {
			return result;
		}

		public void onFailure(Throwable exception) {
			if (isAuthenticationException(exception)) {
				// invalidate login cookie to force re-authentication
				client.setXsrfCookie(null);
			}
			this.exception = exception;
		}

		public void onSuccess(T result) {
			setResult(result);
		}

		protected void setResult(T result) {
			this.result = result;
		}

		public void reset() {
			this.result = null;
			this.exception = null;
		}

	}

	protected static final String GET_LABELS_OPTION = "LABELS"; //$NON-NLS-1$

	protected static final String GET_DETAILED_ACCOUNTS_OPTION = "DETAILED_ACCOUNTS"; //$NON-NLS-1$

	private final GerritHttpClient client;

	public GerritRestClient(GerritHttpClient client) {
		this.client = client;
	}

	private static boolean isAuthenticationException(Throwable exception) {
		if (exception instanceof GerritException) {
			return ((GerritException) exception).getCode() == -32603
					&& "Invalid xsrfKey in request".equals(((GerritException) exception).getMessage()); //$NON-NLS-1$
		}
		return false;
	}

	protected <T> T execute(IProgressMonitor monitor, Operation<T> operation) throws GerritException {
		try {
			GerritRequest.setCurrentRequest(new GerritRequest(monitor));
			try {
				return executeOnce(monitor, operation);
			} catch (GerritException e) {
				if (isAuthenticationException(e)) {
					operation.reset();
					return executeOnce(monitor, operation);
				}
				throw e;
			}
		} finally {
			GerritRequest.setCurrentRequest(null);
		}
	}

	protected <T> T executePostRestRequest(final String url, final Object input, final Type resultType,
			final ErrorHandler handler, IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<T>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> postRestRequest(url, input, resultType, handler, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	protected <T> T executeGetRestRequest(final String url, final Type resultType, IProgressMonitor monitor)
			throws GerritException {
		return execute(monitor, new Operation<T>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> getRestRequest(url, resultType, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	protected <T> T executePutRestRequest(final String url, final Object input, final Type resultType,
			final ErrorHandler handler, IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<T>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> putRestRequest(url, input, resultType, handler, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	public <T> T executeDeleteRestRequest(final String url, final Object input, final Type resultType,
			final ErrorHandler handler, IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<T>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> deleteRestRequest(url, input, resultType, handler, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	private <T> T executeOnce(IProgressMonitor monitor, Operation<T> operation) throws GerritException {
		operation.execute(monitor);
		if (operation.getException() instanceof GerritException) {
			throw (GerritException) operation.getException();
		} else if (operation.getException() instanceof OperationCanceledException) {
			throw (OperationCanceledException) operation.getException();
		} else if (operation.getException() instanceof RuntimeException) {
			throw (RuntimeException) operation.getException();
		} else if (operation.getException() != null) {
			GerritException e = new GerritException();
			e.initCause(operation.getException());
			throw e;
		}
		return operation.getResult();
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server.
	 * 
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	public List<GerritQueryResult> executeQuery(IProgressMonitor monitor, final String queryString)
			throws GerritException {
		return executeQuery(monitor, queryString, ImmutableList.of(GET_LABELS_OPTION, GET_DETAILED_ACCOUNTS_OPTION));
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server with the possibility of adding options
	 * to the query. Uses the gerrit REST API.
	 * 
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @param optionsList
	 *            List of query options ("&o=" parameter). Only applicable for the REST API, ignored otherwise. May be
	 *            null or empty.
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	public List<GerritQueryResult> executeQuery(IProgressMonitor monitor, final String queryString,
			List<String> optionsList) throws GerritException {
		String uri = "/changes/?q=" + GerritClient.encode(queryString); //$NON-NLS-1$
		if (optionsList != null && !optionsList.isEmpty()) {
			for (String option : optionsList) {
				if (StringUtils.isNotBlank(option)) {
					uri += "&o=" + GerritClient.encode(option); //$NON-NLS-1$
				}
			}
		}
		TypeToken<List<GerritQueryResult>> queryResultListType = new TypeToken<List<GerritQueryResult>>() {
		};
		return executeGetRestRequest(uri, queryResultListType.getType(), monitor);
	}

	/**
	 * Returns the latest 25 reviews.
	 */
	public List<GerritQueryResult> queryAllReviews(IProgressMonitor monitor) throws GerritException {
		return executeQuery(monitor, "status:open"); //$NON-NLS-1$
	}

	/**
	 * Returns the latest 25 reviews for the given project.
	 */
	public List<GerritQueryResult> queryByProject(IProgressMonitor monitor, final String project)
			throws GerritException {
		return executeQuery(monitor, "status:open project:" + project); //$NON-NLS-1$
	}

	/**
	 * Returns changes associated with the logged in user. This includes all open, closed and review requests for the
	 * user. On Gerrit 2.4 and earlier closed reviews are not included.
	 */
	public List<GerritQueryResult> queryMyReviews(IProgressMonitor monitor) throws GerritException {
		return executeQuery(monitor, "owner:self OR reviewer:self"); //$NON-NLS-1$
	}

	/**
	 * Returns watched changes of the currently logged in user
	 */
	public List<GerritQueryResult> queryWatchedReviews(IProgressMonitor monitor) throws GerritException {
		return executeQuery(monitor, "is:watched status:open"); //$NON-NLS-1$
	}
}
