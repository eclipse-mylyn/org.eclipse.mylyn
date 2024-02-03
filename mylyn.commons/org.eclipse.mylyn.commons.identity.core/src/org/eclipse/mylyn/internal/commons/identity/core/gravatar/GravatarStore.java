/*******************************************************************************
 *  Copyright (c) 2011, 2024 GitHub Inc and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Class that loads and stores gravatars.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GravatarStore implements Serializable, ISchedulingRule {

	/**
	 * TIMEOUT
	 */
	public static final int TIMEOUT = 30 * 1000;

	/**
	 * BUFFER_SIZE
	 */
	public static final int BUFFER_SIZE = 8192;

	public enum Rating {
		G, PG, R, X
	}

	private static final long serialVersionUID = 6084425297832914970L;

	private long lastRefresh = 0L;

	private final String url;

	private Map<String, Gravatar> avatars;

	/**
	 * Create gravatar store
	 */
	public GravatarStore() {
		this(IGravatarConstants.URL);
	}

	/**
	 * Create gravatar store
	 * 
	 * @param url
	 */
	public GravatarStore(String url) {
		Assert.isNotNull(url, "Url cannot be null"); //$NON-NLS-1$
		// Ensure trailing slash
		if (!url.endsWith("/")) { //$NON-NLS-1$
			url += "/"; //$NON-NLS-1$
		}
		this.url = url;
	}

	public boolean isCacheEnabled() {
		return avatars != null;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		if (cacheEnabled && avatars == null) {
			avatars = Collections.synchronizedMap(new HashMap<String, Gravatar>());
		} else if (!cacheEnabled && avatars != null) {
			avatars = null;
		}
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#getRefreshTime()
	 */
	public long getRefreshTime() {
		return lastRefresh;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#containsGravatar(java.lang.String)
	 */
	public boolean containsGravatar(String hash) {
		return hash != null && avatars != null ? avatars.containsKey(hash) : false;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#scheduleRefresh()
	 */
	public GravatarStore scheduleRefresh() {
		Job refresh = new Job(Messages.GravatarStore_RefreshJobName) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				refresh(monitor);
				return Status.OK_STATUS;
			}
		};
		refresh.setRule(this);
		refresh.schedule();
		return this;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#refresh(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public GravatarStore refresh(IProgressMonitor monitor) {
		if (avatars == null) {
			return this;
		}
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		String[] entries = null;
		synchronized (avatars) {
			entries = new String[avatars.size()];
			entries = avatars.keySet().toArray(entries);
		}
		monitor.beginTask("", entries.length); //$NON-NLS-1$
		for (String entry : entries) {
			if (monitor.isCanceled()) {
				break;
			}
			monitor.setTaskName(MessageFormat.format(Messages.GravatarStore_LoadingAvatar, entry));
			try {
				loadGravatarByHash(entry);
			} catch (IOException ignore) {
			}
			monitor.worked(1);
		}
		monitor.done();
		lastRefresh = System.currentTimeMillis();
		return this;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#loadGravatarByHash(java.lang.String,
	 *      org.eclipse.mylyn.internal.commons.identity.core.gravatar.IGravatarCallback)
	 */
	public GravatarStore loadGravatarByHash(final String hash, final IGravatarCallback callback) {
		String title = MessageFormat.format(Messages.GravatarStore_LoadingAvatar, hash);
		Job job = new Job(title) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Gravatar avatar = loadGravatarByHash(hash);
					if (avatar != null && callback != null) {
						callback.loaded(avatar);
					}
				} catch (IOException e) {
					if (callback != null) {
						callback.error(e);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(this);
		job.schedule();
		return this;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#loadGravatarByEmail(java.lang.String,
	 *      org.eclipse.mylyn.internal.commons.identity.core.gravatar.IGravatarCallback)
	 */
	public GravatarStore loadGravatarByEmail(String email, IGravatarCallback callback) {
		loadGravatarByHash(GravatarUtils.getHash(email), callback);
		return this;
	}

	public Gravatar loadGravatarByHash(String hash) throws IOException {
		return loadGravatarByHash(hash, -1, null);
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#loadGravatarByHash(java.lang.String)
	 */
	public Gravatar loadGravatarByHash(String hash, int size, Rating rating) throws IOException {
		Assert.isLegal(size == -1 || size >= 1 && size <= 512, "size must have a value of -1 or between 1 and 512"); //$NON-NLS-1$
		if (!GravatarUtils.isValidHash(hash)) {
			return null;
		}

		Gravatar avatar = null;
		String location = url + hash + "?d=404"; //$NON-NLS-1$
		if (size != -1) {
			location += "&s=" + size; //$NON-NLS-1$
		}
		if (rating != null) {
			location += "&r=" + rating.name().toLowerCase(); //$NON-NLS-1$
		}
		HttpURLConnection connection = (HttpURLConnection) new URL(location).openConnection();
		connection.setConnectTimeout(TIMEOUT);
		connection.setUseCaches(false);
		connection.connect();

		if (connection.getResponseCode() != 200) {
			return null;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (InputStream input = connection.getInputStream()) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = -1;
			while ((read = input.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
		}
		avatar = new Gravatar(hash, System.currentTimeMillis(), output.toByteArray());
		if (avatars != null) {
			avatars.put(hash, avatar);
		}
		return avatar;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#loadGravatarByEmail(java.lang.String)
	 */
	public Gravatar loadGravatarByEmail(String email) throws IOException {
		return loadGravatarByHash(GravatarUtils.getHash(email));
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#getGravatarByHash(java.lang.String)
	 */
	public Gravatar getGravatarByHash(String hash) {
		return hash != null && avatars != null ? avatars.get(hash) : null;
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarStore#getGravatarByEmail(java.lang.String)
	 */
	public Gravatar getGravatarByEmail(String email) {
		return getGravatarByHash(GravatarUtils.getHash(email));
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	@Override
	public boolean contains(ISchedulingRule rule) {
		return this == rule;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		return this == rule;
	}
}
