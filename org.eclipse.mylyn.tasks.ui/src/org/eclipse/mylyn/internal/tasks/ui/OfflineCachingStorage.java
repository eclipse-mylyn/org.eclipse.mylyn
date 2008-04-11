/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.ITaskDataStorage;
import org.eclipse.mylyn.internal.tasks.core.TaskDataState;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * Least Recently Used (LRU) cache
 * 
 * TODO: Use meta context to make cache more efficient
 * 
 * API-3.0: review for race-conditions
 * 
 * @author Rob Elves
 */
public class OfflineCachingStorage implements ITaskDataStorage {

	private static final int DEFAULT_FLUSH_INTERVAL = 60 * 1000;

	private static final int MAX_READ_QUEUE_SIZE = 80;

	private final Map<String, Map<String, TaskDataState>> readCache = new ConcurrentHashMap<String, Map<String, TaskDataState>>();

	private final Map<String, Map<String, TaskDataState>> writeCache = new ConcurrentHashMap<String, Map<String, TaskDataState>>();

	private final Queue<TaskDataState> lruQueue = new ConcurrentLinkedQueue<TaskDataState>();

	private final ITaskDataStorage storage;

	private CacheFlushJob cacheFlushJob;

	private Timer cacheFlushTimer;

	public OfflineCachingStorage(ITaskDataStorage storage) {
		this.storage = storage;
	}

	/**
	 * FOR TESTING PURPOSES DESTROYS ALL DATA IN STORAGE
	 */
	public void clear() {
		if (cacheFlushJob != null) {
			cacheFlushJob.waitSaveCompleted();
		}
		readCache.clear();
		writeCache.clear();
		lruQueue.clear();
		storage.clear();
	}

	public void flush() {
		cacheFlushJob.waitSaveCompleted();
		persistToStorage();
	}

	public TaskDataState get(String repositoryUrl, String id) {
		TaskDataState result = null;
		result = retrieveFromCache(writeCache, repositoryUrl, id);
		if (result == null) {
			result = retrieveFromCache(readCache, repositoryUrl, id);
		}
		if (result == null) {
			result = retrieveFromStorage(repositoryUrl, id);
		}
		if (result != null) {
			pushRead(result);
		}
		return result;
	}

	private TaskDataState retrieveFromCache(Map<String, Map<String, TaskDataState>> cache, String repositoryUrl,
			String id) {
		Map<String, TaskDataState> idMap = cache.get(repositoryUrl);
		if (idMap != null) {
			return idMap.get(id);
		}
		return null;
	}

	private TaskDataState retrieveFromStorage(String repositoryUrl, String id) {
		TaskDataState result = null;
		synchronized (readCache) {
			Map<String, TaskDataState> idMap = readCache.get(repositoryUrl);
			if (idMap == null) {
				idMap = new ConcurrentHashMap<String, TaskDataState>();
				readCache.put(repositoryUrl, idMap);
			} else {
				result = idMap.get(id);
			}

			if (result == null) {
				result = storage.get(repositoryUrl, id);
				if (result != null) {
					idMap.put(id, result);
				}
			}
		}
		return result;
	}

	public void put(TaskDataState taskDataState) {
		putReadCache(taskDataState);
		putWriteCache(taskDataState);
		if (cacheFlushJob != null) {
			cacheFlushJob.requestSave();
		}
	}

	public void remove(String repositoryUrl, String id) {
		Map<String, TaskDataState> idMap = writeCache.get(repositoryUrl);
		if (idMap != null) {
			idMap.remove(id);
		}
		idMap = readCache.get(repositoryUrl);
		if (idMap != null) {
			idMap.remove(id);
		}

		lruQueue.remove(new TaskDataState(repositoryUrl, id));

		storage.remove(repositoryUrl, id);
	}

	public void start() throws Exception {
		storage.start();
		if (cacheFlushTimer == null && cacheFlushJob == null) {
			cacheFlushTimer = new Timer();
			cacheFlushTimer.schedule(new RequestSaveTimerTask(), DEFAULT_FLUSH_INTERVAL, DEFAULT_FLUSH_INTERVAL);
			cacheFlushJob = new CacheFlushJob();
			cacheFlushJob.schedule();
		}
	}

	public void stop() throws Exception {
		cacheFlushTimer.cancel();
		cacheFlushJob.cancel();
		this.flush();
		cacheFlushTimer = null;
		cacheFlushJob = null;
		storage.stop();
	}

	private void pushRead(TaskDataState state) {

		lruQueue.remove(state);
		lruQueue.add(state);
		if (lruQueue.size() > MAX_READ_QUEUE_SIZE) {
			flushReadCache(false);
		}
	}

	private void putReadCache(TaskDataState taskDataState) {
		if (taskDataState == null) {
			return;
		}
		synchronized (readCache) {

			Map<String, TaskDataState> idMap = readCache.get(taskDataState.getUrl());
			if (idMap == null) {
				idMap = new ConcurrentHashMap<String, TaskDataState>();
				readCache.put(taskDataState.getUrl(), idMap);
			}

			idMap.put(taskDataState.getId(), taskDataState);
		}
		pushRead(taskDataState);
	}

	private synchronized void putWriteCache(TaskDataState taskDataState) {
		if (taskDataState == null) {
			return;
		}

		Map<String, TaskDataState> idMap = writeCache.get(taskDataState.getUrl());
		if (idMap == null) {
			idMap = new ConcurrentHashMap<String, TaskDataState>();
			writeCache.put(taskDataState.getUrl(), idMap);
		}
		idMap.put(taskDataState.getId(), taskDataState);
	}

	private void persistToStorage() {
		synchronized (writeCache) {
			for (Map<String, TaskDataState> idMap : writeCache.values()) {
				for (TaskDataState state : idMap.values()) {
					storage.put(state);
				}
				idMap.clear();
			}
		}
	}

	private class CacheFlushJob extends Job {

		private volatile boolean saveRequested = false;

		private volatile boolean saveCompleted = true;

		CacheFlushJob() {
			super("Flush Cache Job");
			setPriority(Job.LONG);
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			while (true) {
				if (saveRequested) {
					saveRequested = false;
					saveCompleted = false;
					try {
						persistToStorage();
					} catch (Throwable t) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								"Error saving offline cache", t));
					}
				}

				if (!saveRequested) {
					synchronized (this) {
						saveCompleted = true;
						notifyAll();
						try {
							wait();
						} catch (InterruptedException ex) {
							// ignore
						}
					}
				}
			}
		}

		void requestSave() {
			saveRequested = true;
		}

		void runRequested() {
			synchronized (this) {
				notifyAll();
			}
		}

		void waitSaveCompleted() {
			while (!saveCompleted) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException ex) {
						// ignore
					}
				}
			}
		}
	}

	private class RequestSaveTimerTask extends TimerTask {

		@Override
		public void run() {
			if (!Platform.isRunning()) {
				return;
			} else {
				flushReadCache(false);
				cacheFlushJob.runRequested();
			}
		}
	}

	/**
	 * @param reset
	 *            if true all read cached data is dropped if false only remove until cache lower than
	 *            MAX_READ_QUEUE_SIZE
	 */
	public void flushReadCache(boolean reset) {
		if (reset) {
			lruQueue.clear();
			readCache.clear();
		} else {
			while (lruQueue.size() > MAX_READ_QUEUE_SIZE / 2) {
				TaskDataState state = lruQueue.poll();
				if (state != null) {
					Map<String, TaskDataState> tasksMap = readCache.get(state.getUrl());
					if (tasksMap != null) {
						tasksMap.remove(state.getId());
					}
				}
			}
		}
	}

	/**
	 * For testing...
	 */
	public Queue<TaskDataState> getReadQueue() {
		return lruQueue;
	}

	public Map<String, Map<String, TaskDataState>> getReadCache() {
		return readCache;
	}

	public Map<String, Map<String, TaskDataState>> getWriteCache() {
		return writeCache;
	}

}
