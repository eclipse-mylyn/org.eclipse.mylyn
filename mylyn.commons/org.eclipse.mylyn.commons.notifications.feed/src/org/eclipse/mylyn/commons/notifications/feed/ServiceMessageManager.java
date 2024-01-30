/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.feed;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.net.NetUtil;
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

	private final List<IServiceMessageListener> listeners = new CopyOnWriteArrayList<>();

	private Job messageCheckJob;

	private volatile List<? extends ServiceMessage> messages = Collections.emptyList();

	private boolean statusLogged;

	private String url;

	public ServiceMessageManager(String serviceMessageUrl, String lastModified, String eTag, long checktime) {
		this(serviceMessageUrl, lastModified, eTag, checktime, new NotificationEnvironment());
	}

	public ServiceMessageManager(String serviceMessageUrl, String lastModified, String eTag, long checktime,
			NotificationEnvironment environment) {
		url = serviceMessageUrl;
		this.lastModified = lastModified;
		this.checktime = checktime;
		this.eTag = eTag;
		this.environment = environment;
		eventId = ID_EVENT_SERVICE_MESSAGE;
	}

	public void addServiceMessageListener(IServiceMessageListener listener) {
		listeners.add(listener);
	}

	public final String getEventId() {
		return eventId;
	}

	public List<ServiceMessage> getServiceMessages() {
		return new ArrayList<>(messages);
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
				@Override
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				@Override
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
			HttpURLConnection connection = openConnection(url);
			if (lastModified != null && lastModified.length() > 0) {
				try {
					connection.setIfModifiedSince(Long.parseLong(lastModified));
				} catch (NumberFormatException e) {
					// ignore
				}
			}
			if (eTag != null && eTag.length() > 0) {
				connection.setRequestProperty("If-None-Match", eTag); //$NON-NLS-1$
			}

			try {
				connection.connect();

				status = connection.getResponseCode();
				if (status == HttpURLConnection.HTTP_OK && !monitor.isCanceled()) {
					lastModified = connection.getHeaderField("Last-Modified"); //$NON-NLS-1$
					eTag = connection.getHeaderField("ETag"); //$NON-NLS-1$

					InputStream in = new BufferedInputStream(connection.getInputStream());
					try (in) {
						messages = readMessages(in, monitor);
					}
				} else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
					// no messages
				} else if (status == HttpURLConnection.HTTP_NOT_MODIFIED) {
					// no new messages
				} else {
					logStatus(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN,
							"Http error retrieving service message: " + connection.getResponseMessage())); //$NON-NLS-1$
				}
			} finally {
				connection.disconnect();
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

	private HttpURLConnection openConnection(String url) throws IOException, MalformedURLException {
		Proxy proxy = NetUtil.getProxyForUrl(url);
		if (proxy != null) {
			return (HttpURLConnection) new URL(url).openConnection(proxy);
		}
		return (HttpURLConnection) new URL(url).openConnection();
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

		ArrayList<ServiceMessage> sortedMessages = new ArrayList<>(messages);
		Collections.sort(messages);
		final ServiceMessageEvent event = new ServiceMessageEvent(this, ServiceMessageEvent.Kind.MESSAGE_UPDATE,
				sortedMessages);
		for (final IServiceMessageListener listener : listeners) {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				@Override
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
