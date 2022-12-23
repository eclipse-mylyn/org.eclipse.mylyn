/*******************************************************************************
 * Copyright (c) 2011, 2013 GitHub Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     GitHub Inc. - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.JobRemoteService;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class RemoteServiceTest {

	class Consumer extends AbstractRemoteConsumer {

		boolean retrieve;

		boolean apply;

		boolean notify;

		boolean async = true;

		IStatus status;

		@Override
		public void pull(boolean force, IProgressMonitor monitor) throws CoreException {
			retrieve = true;
		}

		@Override
		public void applyModel(boolean force) {
			apply = true;
		}

		@Override
		public void notifyDone(IStatus status) {
			this.status = status;
		}

		@Override
		public boolean isAsynchronous() {
			return async;
		}

		@Override
		public String getDescription() {
			return "";
		}

		protected void waitForDone() {
			long delay;
			delay = 0;
			while (delay < 100) {
				if (status == null) {
					try {
						Thread.sleep(10);
						delay += 10;
					} catch (InterruptedException e) {
					}
				} else {
					break;
				}
			}
			assertThat(status, notNullValue());
		}

		@Override
		public boolean isUserJob() {
			// ignore
			return false;
		}

		@Override
		public boolean isSystemJob() {
			// ignore
			return false;
		}
	}

	@Test
	public void testExecute() throws CoreException {
		JobRemoteService remoteService = new JobRemoteService();
		Consumer consumer = new Consumer();
		remoteService.retrieve(consumer, false);
		consumer.waitForDone();
		assertThat(consumer.status.getSeverity(), is(IStatus.OK));
		assertThat(consumer.retrieve, is(true));
		assertThat(consumer.apply, is(true));
	}

	@Test
	public void testExecuteSync() throws CoreException {
		JobRemoteService remoteService = new JobRemoteService();
		Consumer consumer = new Consumer();
		consumer.async = false;
		remoteService.retrieve(consumer, false);
		consumer.waitForDone();
		assertThat(consumer.status.getSeverity(), is(IStatus.OK));
		assertThat(consumer.retrieve, is(true));
		assertThat(consumer.apply, is(true));
	}

	class BrokenConsumer extends Consumer {
		@Override
		public void pull(boolean force, IProgressMonitor monitor) throws CoreException {
			throw new CoreException(new Status(IStatus.ERROR, "blah", "Whoops!"));
		}
	}

	@Test
	public void testExecuteCoreException() throws CoreException {
		JobRemoteService remoteService = new JobRemoteService();
		Consumer consumer = new BrokenConsumer();
		remoteService.retrieve(consumer, false);
		consumer.waitForDone();
		assertThat(consumer.status.getSeverity(), is(IStatus.WARNING));
		assertThat(consumer.retrieve, is(false));
		assertThat(consumer.apply, is(false));
	}

	@Test
	public void testExecuteCoreExceptionSync() throws CoreException {
		JobRemoteService remoteService = new JobRemoteService();
		Consumer consumer = new BrokenConsumer();
		consumer.async = false;
		remoteService.retrieve(consumer, false);
		consumer.waitForDone();
		assertThat(consumer.status.getSeverity(), is(IStatus.ERROR));
		assertThat(consumer.retrieve, is(false));
		assertThat(consumer.apply, is(false));
	}

	Thread testThread;

	class ThreadedService extends JobRemoteService {

		@Override
		public void modelExec(Runnable runnable, boolean block) {
			testThread = new Thread(runnable, "Test Thread");
			testThread.start();
		}
	}

	class ModelThreadConsumer extends Consumer {

		Thread modelThread;

		Thread retrieveThread;

		@Override
		public void pull(boolean force, IProgressMonitor monitor) throws CoreException {
			retrieveThread = Thread.currentThread();
			super.pull(force, monitor);
		}

		@Override
		public void applyModel(boolean force) {
			modelThread = Thread.currentThread();
			super.applyModel(force);
		}
	}

	@Test
	public void testExecuteModelThread() throws CoreException {
		JobRemoteService remoteService = new ThreadedService();
		ModelThreadConsumer consumer = new ModelThreadConsumer();
		remoteService.retrieve(consumer, false);
		consumer.waitForDone();
		assertThat(consumer.modelThread.getName(), is("Test Thread"));
		assertThat(consumer.retrieveThread.getName(), not("Test Thread"));
		assertThat(consumer.status.getSeverity(), is(IStatus.OK));
		assertThat(consumer.retrieve, is(true));
		assertThat(consumer.apply, is(true));

		assertThat(consumer.retrieveThread, not(Thread.currentThread()));
	}

	@Test
	public void testExecuteModelThreadSync() throws CoreException {
		JobRemoteService remoteService = new ThreadedService();
		ModelThreadConsumer consumer = new ModelThreadConsumer();
		consumer.async = false;
		remoteService.retrieve(consumer, false);
		consumer.waitForDone();
		assertThat(consumer.modelThread.getName(), is("Test Thread"));
		assertThat(consumer.retrieveThread.getName(), not("Test Thread"));
		assertThat(consumer.status.getSeverity(), is(IStatus.OK));
		assertThat(consumer.retrieve, is(true));
		assertThat(consumer.apply, is(true));

		assertThat(consumer.retrieveThread, is(Thread.currentThread()));
	}
}
