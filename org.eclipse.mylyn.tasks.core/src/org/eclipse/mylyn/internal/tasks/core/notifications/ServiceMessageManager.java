/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Tasktop EULA
 * which accompanies this distribution, and is available at
 * http://tasktop.com/legal
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.notifications;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class ServiceMessageManager {

	protected static final long START_DELAY = 30 * 1000;

	protected static final long RECHECK_DELAY = 24 * 60 * 60 * 1000;

	private String serviceMessageUrl;

	private volatile List<ServiceMessage> messages = Collections.emptyList();

	private Job messageCheckJob;

	private final List<IServiceMessageListener> listeners = new CopyOnWriteArrayList<IServiceMessageListener>();

	private String lastModified;

	private String eTag;

	private boolean statusLogged;

	private final long checktime;

	public ServiceMessageManager(String serviceMessageUrl, String lastModified, String eTag, long checktime) {
		this.serviceMessageUrl = serviceMessageUrl;
		this.lastModified = lastModified;
		this.checktime = checktime;
		this.eTag = eTag;
	}

	public void start() {
		if (messageCheckJob == null) {
			messageCheckJob = new Job("Checking for new service message") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					updateServiceMessage(monitor);
					return Status.OK_STATUS;
				}

			};
			messageCheckJob.setSystem(true);
			messageCheckJob.setPriority(Job.DECORATE);
			messageCheckJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (messageCheckJob != null) {
						messageCheckJob.schedule(RECHECK_DELAY);
					}
				}
			});
		}
		if (checktime == 0) {
			messageCheckJob.schedule(START_DELAY);
		} else {
			long nextCheckTime = checktime + RECHECK_DELAY;
			long now = new Date().getTime();
			if (nextCheckTime < now) {
				messageCheckJob.schedule(START_DELAY);
			} else if (nextCheckTime > now) {
				if ((nextCheckTime - now) < START_DELAY) {
					messageCheckJob.schedule(START_DELAY);
				} else {
					messageCheckJob.schedule(nextCheckTime - now);
				}
			}
		}

	}

	public void stop() {
		if (messageCheckJob != null) {
			messageCheckJob.cancel();
			messageCheckJob = null;
		}

		final ServiceMessageEvent event = new ServiceMessageEvent(this, ServiceMessageEvent.EVENT_KIND.STOP);

		for (final IServiceMessageListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					listener.handleEvent(event);
				}

				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}
			});
		}
	}

	public void setServiceMessageUrl(String url) {
		this.serviceMessageUrl = url;
	}

	public void addServiceMessageListener(IServiceMessageListener listener) {
		listeners.add(listener);
	}

	public void removeServiceMessageListener(IServiceMessageListener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners(List<ServiceMessage> messages) {
		this.messages = messages;
		for (final ServiceMessage message : messages) {
			message.setETag(eTag);
			message.setLastModified(lastModified);
		}

		final ServiceMessageEvent event = new ServiceMessageEvent(this, ServiceMessageEvent.EVENT_KIND.MESSAGE_UPDATE,
				messages);

		for (final IServiceMessageListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					listener.handleEvent(event);
				}

				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}
			});
		}
	}

	public List<ServiceMessage> getServiceMessages() {
		return new ArrayList<ServiceMessage>(this.messages);
	}

	/**
	 * Public for testing
	 */
	public int updateServiceMessage(IProgressMonitor monitor) {
		int status = -1;
		List<ServiceMessage> messages = null;
		try {
			HttpClient httpClient = new HttpClient(WebUtil.getConnectionManager());
			WebUtil.configureHttpClient(httpClient, null);

			WebLocation location = new WebLocation(serviceMessageUrl);
			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location,
					new SubProgressMonitor(monitor, 1));

			GetMethod method = new GetMethod(serviceMessageUrl);
			method.setRequestHeader("If-Modified-Since", lastModified); //$NON-NLS-1$
			method.setRequestHeader("If-None-Match", eTag); //$NON-NLS-1$

			try {
				status = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
				if (status == HttpStatus.SC_OK && !monitor.isCanceled()) {
					Header lastModifiedHeader = method.getResponseHeader("Last-Modified"); //$NON-NLS-1$
					if (lastModifiedHeader != null) {
						lastModified = lastModifiedHeader.getValue();
					}
					Header eTagHeader = method.getResponseHeader("ETag"); //$NON-NLS-1$
					if (eTagHeader != null) {
						eTag = eTagHeader.getValue();
					}

					InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
					try {
						SAXParserFactory factory = SAXParserFactory.newInstance();
						factory.setValidating(false);
						SAXParser parser = factory.newSAXParser();

						ServiceMessageXmlHandler handler = new ServiceMessageXmlHandler();
						parser.parse(in, handler);
						messages = handler.getMessages();
					} finally {
						in.close();
					}
				} else if (status == HttpStatus.SC_NOT_FOUND) {
					// no messages
				} else if (status == HttpStatus.SC_NOT_MODIFIED) {
					// no new messages
				} else {
					if (!statusLogged) {
						statusLogged = true;
						StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
								"Http error retrieving service message: " + HttpStatus.getStatusText(status))); //$NON-NLS-1$
					}
				}
			} finally {
				WebUtil.releaseConnection(method, monitor);
			}
		} catch (Exception e) {
			if (!statusLogged) {
				statusLogged = true;
				StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						"Http error retrieving service message.", e)); //$NON-NLS-1$
			}
		}

		if (messages != null && messages.size() > 0) {
			notifyListeners(messages);
		}
		return status;
	}

}
