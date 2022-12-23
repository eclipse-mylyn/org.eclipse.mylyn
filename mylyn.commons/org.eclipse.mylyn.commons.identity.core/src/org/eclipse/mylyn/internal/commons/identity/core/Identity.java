/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.identity.core.Account;
import org.eclipse.mylyn.commons.identity.core.IIdentity;
import org.eclipse.mylyn.commons.identity.core.IProfile;
import org.eclipse.mylyn.commons.identity.core.IProfileImage;
import org.eclipse.mylyn.commons.identity.core.spi.Profile;
import org.eclipse.mylyn.commons.identity.core.spi.ProfileImage;

/**
 * @author Steffen Pingel
 */
public class Identity implements IIdentity {

	private static abstract class FutureJob<T> extends Job implements Future<T> {

		private boolean cancelled;

		private final AtomicReference<Throwable> futureException = new AtomicReference<Throwable>();

		private final AtomicReference<T> futureResult = new AtomicReference<T>();

		private final CountDownLatch resultLatch = new CountDownLatch(1);

		public FutureJob(String name) {
			super(name);
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			this.cancelled = true;
			return this.cancel();
		}

		public T get() throws InterruptedException, ExecutionException {
			resultLatch.await();
			return getFutureResult();
		}

		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			if (!resultLatch.await(timeout, unit)) {
				throw new TimeoutException();
			}
			return getFutureResult();
		}

		public boolean isCancelled() {
			return this.cancelled;
		}

		public boolean isDone() {
			return getResult() != null;
		}

		private T getFutureResult() throws ExecutionException {
			Throwable t = futureException.get();
			if (t != null) {
				throw new ExecutionException(t);
			}
			return futureResult.get();
		}

		protected void done() {
			if (resultLatch.getCount() > 0) {
				error(new RuntimeException());
			}
			resultLatch.countDown();
		}

		protected IStatus error(Throwable t) {
			futureException.set(t);
			resultLatch.countDown();
			if (t instanceof OperationCanceledException) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}

		protected IStatus success(T result) {
			futureResult.set(result);
			resultLatch.countDown();
			return Status.OK_STATUS;
		}

	}

	private static final class FutureResult<T> implements Future<T> {

		private final T result;

		private FutureResult(T result) {
			this.result = result;
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			return true;
		}

		public T get() throws InterruptedException, ExecutionException {
			return result;
		}

		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return result;
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return true;
		}
	}

	private final Set<Account> accounts;

	private final UUID id;

	private List<ProfileImage> images;

	private final List<PropertyChangeListener> listeners;

	private final IdentityModel model;

	private Profile profile;

	private boolean refreshProfile;

	public Identity(IdentityModel model) {
		this.model = model;
		this.id = UUID.randomUUID();
		this.accounts = new CopyOnWriteArraySet<Account>();
		this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
	}

	public void addAccount(Account account) {
		accounts.add(account);
		refreshProfile = true;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	public Account[] getAccounts() {
		return accounts.toArray(new Account[accounts.size()]);
	}

	public Account getAccountById(String id) {
		if (id == null) {
			return null;
		}
		for (Account account : accounts) {
			if (id.equals(account.getId())) {
				return account;
			}
		}
		return null;
	}

	public Account getAccountByKind(String kind) {
		if (kind == null) {
			return null;
		}
		for (Account account : accounts) {
			if (kind.equals(account.getKind())) {
				return account;
			}
		}
		return null;
	}

	public String[] getAliases() {
		Set<String> aliases = new HashSet<String>(accounts.size());
		for (Account account : accounts) {
			aliases.add(account.getId());
		}
		return aliases.toArray(new String[aliases.size()]);
	}

	public UUID getId() {
		return id;
	}

	public boolean is(Account account) {
		return accounts.contains(account);
	}

	public boolean is(String id) {
		return getAccountById(id) != null;
	}

	public void removeAccount(Account account) {
		accounts.remove(account);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	public synchronized Future<IProfileImage> requestImage(final int preferredWidth, final int preferredHeight) {
		if (images != null) {
			for (final ProfileImage image : images) {
				if (image.getWidth() == preferredWidth && image.getHeight() == preferredHeight) {
					return new FutureResult<IProfileImage>(image);
				}
			}
		}
		FutureJob<IProfileImage> job = new FutureJob<IProfileImage>(Messages.Identity_Retrieving_Image) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ProfileImage image = model.getImage(Identity.this, preferredWidth, preferredHeight, monitor);
					if (image != null) {
						addImage(image);
					}
					return success(image);
				} catch (Throwable t) {
					return error(t);
				} finally {
					done();
				}
			}
		};
		job.schedule();
		return job;
	}

	public Future<IProfile> requestProfile() {
		if (profile != null && !refreshProfile) {
			return new FutureResult<IProfile>(profile);
		}

		refreshProfile = false;
		FutureJob<IProfile> job = new FutureJob<IProfile>(Messages.Identity_Retrieving_Profile) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					Profile profile = new Profile(Identity.this);
					model.updateProfile(profile, monitor);
					setProfile(profile);
					return success(profile);
				} catch (Throwable t) {
					return error(t);
				} finally {
					done();
				}
			}
		};
		job.schedule();
		return job;
	}

	private void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}

	protected synchronized void addImage(ProfileImage image) {
		if (images == null) {
			images = new ArrayList<ProfileImage>();
		}
		images.add(image);
		firePropertyChangeEvent("image", null, image); //$NON-NLS-1$
	}

	protected synchronized void removeImage(ProfileImage image) {
		if (images != null) {
			images.remove(image);
		}
	}

	protected void setProfile(Profile profile) {
		this.profile = profile;
	}

}
