/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Tasktop EULA
 * which accompanies this distribution, and is available at
 * http://tasktop.com/legal
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.feed;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.Assert;
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
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedReader;
import org.eclipse.mylyn.internal.commons.notifications.feed.INotificationsFeed;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class ServiceMessageManager {

	private static final String ID_EVENT_SERVICE_MESSAGE = "org.eclipse.mylyn.notifications.feed.events.ServiceMessage"; //$NON-NLS-1$

	private static final long RECHECK_DELAY = 14 * 24 * 60 * 60 * 1000;

	private static final long START_DELAY = 30 * 1000;

	private final long checktime;

	private final NotificationEnvironment environment;

	private String eTag;

	private String eventId;

	private String lastModified;

	private final List<IServiceMessageListener> listeners = new CopyOnWriteArrayList<IServiceMessageListener>();

	private Job messageCheckJob;

	private volatile List<? extends ServiceMessage> messages = Collections.emptyList();

	private boolean statusLogged;

	private String url;

	public ServiceMessageManager(String serviceMessageUrl, String lastModified, String eTag, long checktime) {
		this(serviceMessageUrl, lastModified, eTag, checktime, new NotificationEnvironment());
	}

	public ServiceMessageManager(String serviceMessageUrl, String lastModified, String eTag, long checktime,
			NotificationEnvironment environment) {
		this.url = serviceMessageUrl;
		this.lastModified = lastModified;
		this.checktime = checktime;
		this.eTag = eTag;
		this.environment = environment;
		this.eventId = ID_EVENT_SERVICE_MESSAGE;
	}

	public void addServiceMessageListener(IServiceMessageListener listener) {
		listeners.add(listener);
	}

	public final String getEventId() {
		return eventId;
	}

	public List<ServiceMessage> getServiceMessages() {
		return new ArrayList<ServiceMessage>(this.messages);
	}

	public String getUrl() {
		return url;
	}

	public void removeServiceMessageListener(IServiceMessageListener listener) {
		listeners.remove(listener);
	}

	public final void setEventId(String eventId) {
		Assert.isNotNull(eventId);
		this.eventId = eventId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void start() {
		if (messageCheckJob == null) {
			messageCheckJob = new Job("Checking for new service message") { //$NON-NLS-1$
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						refresh(monitor);
						return Status.OK_STATUS;
					} catch (Throwable t) {
						// fail silently
						return Status.CANCEL_STATUS;
					}
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
			long now = System.currentTimeMillis();
			if (nextCheckTime < now) {
				messageCheckJob.schedule(START_DELAY);
			} else if (nextCheckTime > now) {
				if (nextCheckTime - now < START_DELAY) {
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

		final ServiceMessageEvent event = new ServiceMessageEvent(this, ServiceMessageEvent.Kind.STOP);

		for (final IServiceMessageListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					listener.handleEvent(event);
				}
			});
		}
	}

	public int refresh(IProgressMonitor monitor) {
		int status = -1;
		List<? extends ServiceMessage> messages = null;
		try {
			HttpClient httpClient = new HttpClient(WebUtil.getConnectionManager());
			WebUtil.configureHttpClient(httpClient, null);

			WebLocation location = new WebLocation(url);
			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location,
					new SubProgressMonitor(monitor, 1));

			GetMethod method = new GetMethod(url);
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
						messages = readMessages(in, monitor);
					} finally {
						in.close();
					}
				} else if (status == HttpStatus.SC_NOT_FOUND) {
					// no messages
				} else if (status == HttpStatus.SC_NOT_MODIFIED) {
					// no new messages
				} else {
					logStatus(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN,
							"Http error retrieving service message: " + HttpStatus.getStatusText(status))); //$NON-NLS-1$
				}
			} finally {
				WebUtil.releaseConnection(method, monitor);
			}
		} catch (Exception e) {
			logStatus(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN,
					"Http error retrieving service message.", e)); //$NON-NLS-1$
		}

		if (messages != null && messages.size() > 0) {
			notifyListeners(messages);
		}
		return status;
	}

	private void logStatus(IStatus status) {
		if (!statusLogged) {
			statusLogged = true;
			//StatusHandler.log(status);
		}
	}

	private void notifyListeners(List<? extends ServiceMessage> messages) {
		this.messages = messages;
		for (final ServiceMessage message : messages) {
			message.setETag(eTag);
			message.setLastModified(lastModified);
		}

		ArrayList<ServiceMessage> sortedMessages = new ArrayList<ServiceMessage>(messages);
		Collections.sort(messages);
		final ServiceMessageEvent event = new ServiceMessageEvent(this, ServiceMessageEvent.Kind.MESSAGE_UPDATE,
				sortedMessages);
		for (final IServiceMessageListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				public void run() throws Exception {
					listener.handleEvent(event);
				}
			});
		}
	}

	private List<? extends ServiceMessage> readMessages(InputStream in, IProgressMonitor monitor) throws IOException {
		FeedReader reader = new FeedReader(eventId, environment);
		reader.parse(in, monitor);
		return reader.getEntries();
	}

}
