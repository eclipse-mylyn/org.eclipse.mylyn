/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.core;

import static org.eclipse.mylyn.tasks.core.data.TaskAttribute.META_INDEXED_AS_CONTENT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataManagerListener;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManagerEvent;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * An index on a task list, provides a way to {@link #find(String, TaskCollector, int) search for tasks}, and a way to
 * {@link #matches(ITask, String) match tasks}. Tasks are matched against a search query.
 * <p>
 * The task list has a configurable delay before it updates, meaning that there is a period of time where the index will
 * be out of date with respect to task changes. The idea is that updates to the index can be "batched" for greater
 * efficiency. Additionally, it's possible for a task to be updated either before or after the index is added to the
 * task list as a listener, thus opening the possibility of changes without updates to the index. In either of these
 * cases, the index can be out of date with respect to the current state of tasks. If the index is used in such a state,
 * the result could be either false matches, no match where there should be a match, or incorrect prioritization of
 * index "hits".
 * </p>
 * <p>
 * The index has the option of reindexing all tasks via API. This will bring the index up to date and is useful for
 * cases where it's known that the index may not be up to date. In its current form this reindex operation can be
 * triggered by the user by including "index:reset" in the search string. Reindexing is potentially an expensive, IO
 * intensive long-running operation. With about 20,000 tasks in my task list and an SSD, reindexing takes about 90
 * seconds.
 * </p>
 * 
 * @author David Green
 */
public class TaskListIndex implements ITaskDataManagerListener, ITaskListChangeListener, IRepositoryListener {

	private class MaintainIndexJob extends Job {

		public MaintainIndexJob() {
			super(Messages.TaskListIndex_indexerJob);
			setUser(false);
			setSystem(false); // true?
			setPriority(Job.LONG);
		}

		@Override
		public IStatus run(IProgressMonitor m) {
			if (m.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			try {
				maintainIndex(m);
			} catch (CoreException e) {
				MultiStatus logStatus = new MultiStatus(TasksIndexCore.ID_PLUGIN, 0,
						"Failed to update task list index", e); //$NON-NLS-1$
				logStatus.add(e.getStatus());
				StatusHandler.log(logStatus);
			}
			return Status.OK_STATUS;
		}

	}

	public abstract static class TaskCollector {

		public abstract void collect(ITask task);

	}

	private static final Object COMMAND_RESET_INDEX = "index:reset"; //$NON-NLS-1$

	public static enum IndexField {
		IDENTIFIER(false, null, TaskAttribute.TYPE_SHORT_TEXT), //
		TASK_KEY(false, DefaultTaskSchema.getInstance().TASK_KEY), //
		REPOSITORY_URL(false, null, TaskAttribute.TYPE_URL), //
		SUMMARY(true, DefaultTaskSchema.getInstance().SUMMARY), //
		CONTENT(true, null, TaskAttribute.TYPE_LONG_TEXT), //
		ASSIGNEE(true, DefaultTaskSchema.getInstance().USER_ASSIGNED), //
		REPORTER(true, DefaultTaskSchema.getInstance().USER_REPORTER), //
		PERSON(true, null, TaskAttribute.TYPE_PERSON), //
		COMPONENT(true, DefaultTaskSchema.getInstance().COMPONENT), //
		COMPLETION_DATE(true, DefaultTaskSchema.getInstance().DATE_COMPLETION), //
		CREATION_DATE(true, DefaultTaskSchema.getInstance().DATE_CREATION), //
		DUE_DATE(true, DefaultTaskSchema.getInstance().DATE_DUE), //
		MODIFICATION_DATE(true, DefaultTaskSchema.getInstance().DATE_MODIFICATION), //
		DESCRIPTION(true, DefaultTaskSchema.getInstance().DESCRIPTION), //
		KEYWORDS(true, DefaultTaskSchema.getInstance().KEYWORDS), //
		PRODUCT(true, DefaultTaskSchema.getInstance().PRODUCT), //
		RESOLUTION(true, DefaultTaskSchema.getInstance().RESOLUTION), //
		SEVERITY(true, DefaultTaskSchema.getInstance().SEVERITY), //
		STATUS(true, DefaultTaskSchema.getInstance().STATUS);

		private final String attributeId;

		private final String type;

		private final boolean userVisible;

		private IndexField(boolean userVisible, AbstractTaskSchema.Field field) {
			this(userVisible, field.getKey(), field.getType());
		}

		private IndexField(boolean userVisible, String attributeId, String type) {
			this.userVisible = userVisible;
			this.attributeId = attributeId;
			this.type = type;
		}

		public String fieldName() {
			return name().toLowerCase();
		}

		/**
		 * get the task attribute id, or null if this field has special handling
		 */
		public String getAttributeId() {
			return attributeId;
		}

		/**
		 * indicate if the field should be exposed in the UI
		 */
		public boolean isUserVisible() {
			return userVisible;
		}

		/**
		 * the type of field, as it relates to <code>TaskAttribute.TYPE_*</code>
		 */
		public String getType() {
			return type;
		}

		/**
		 * indicate if the field is a date or date/time field
		 */
		public boolean isTypeDate() {
			return type != null && (TaskAttribute.TYPE_DATE.equals(type) || TaskAttribute.TYPE_DATETIME.equals(type));
		}

		/**
		 * indicate if this is a person field
		 */
		public boolean isPersonField() {
			return type != null && (TaskAttribute.TYPE_PERSON.equals(type) || TaskAttribute.TYPE_PERSON.equals(type));
		}

		public static IndexField fromFieldName(String fieldName) {
			try {
				return IndexField.valueOf(fieldName.toUpperCase());
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

	}

	private static enum MaintainIndexType {
		STARTUP, REINDEX
	}

	// FIXME: document concurrency model

	private Directory directory;

	private MaintainIndexJob maintainIndexJob;

	/**
	 * must be synchronized before accessing or modifying
	 */
	private final Map<ITask, TaskData> reindexQueue = new HashMap<ITask, TaskData>();

	/**
	 * do not access directly, instead use {@link #getIndexReader()}. 'this' must be synchronized before accessing or
	 * modifying
	 */
	private IndexReader indexReader;

	/**
	 * indicate the need to rebuild the whole index
	 */
	private volatile boolean rebuildIndex = false;

	/**
	 * 'this' must be synchronized before accessing or modifying
	 */
	private String lastPatternString;

	/**
	 * 'this' must be synchronized before accessing or modifying
	 */
	private Set<String> lastResults;

	private IndexField defaultField = IndexField.SUMMARY;

	private final TaskList taskList;

	private final TaskDataManager dataManager;

	private final IRepositoryManager repositoryManager;

	private long startupDelay = 6000L;

	private long reindexDelay = 3000L;

	private int maxMatchSearchHits = 1500;

	/**
	 * must hold this lock as a read lock when accessing the index, and must hold this lock as a write lock when closing
	 * or reassigning {@link #indexReader}.
	 */
	private final ReadWriteLock indexReaderLock = new ReentrantReadWriteLock(true);

	private TaskListIndex(TaskList taskList, TaskDataManager dataManager, IRepositoryManager repositoryManager) {
		Assert.isNotNull(taskList);
		Assert.isNotNull(dataManager);
		Assert.isNotNull(repositoryManager);

		this.taskList = taskList;
		this.dataManager = dataManager;
		this.repositoryManager = repositoryManager;
	}

	/**
	 * the task list associated with this index
	 */
	public ITaskList getTaskList() {
		return taskList;
	}

	/**
	 * the data manager associated with this index
	 */
	public ITaskDataManager getDataManager() {
		return dataManager;
	}

	/**
	 * the repository manager associated with this index
	 */
	public IRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	/**
	 * Create an index on the given task list. Must be matched by a corresponding call to {@link #close()}.
	 * 
	 * @param taskList
	 *            the task list that is to be indexed
	 * @param dataManager
	 *            the data manager that corresponds to the task list
	 * @param repositoryManager
	 *            the repository manager that corresponds to the task list
	 * @param indexLocation
	 *            the location of the index on the filesystem
	 * @see #TaskListIndex(TaskList, TaskDataManager, Directory)
	 */
	public TaskListIndex(TaskList taskList, TaskDataManager dataManager, IRepositoryManager repositoryManager,
			File indexLocation) {
		this(taskList, dataManager, repositoryManager, indexLocation, 6000L);
	}

	/**
	 * Create an index on the given task list. Must be matched by a corresponding call to {@link #close()}.
	 * 
	 * @param taskList
	 *            the task list that is to be indexed
	 * @param dataManager
	 *            the data manager that corresponds to the task list
	 * @param repositoryManager
	 *            the repository manager that corresponds to the task list
	 * @param startupDelay
	 *            the delay in miliseconds before the index initialization maintenance process should begin
	 * @see #TaskListIndex(TaskList, TaskDataManager, File)
	 */
	public TaskListIndex(TaskList taskList, TaskDataManager dataManager, IRepositoryManager repositoryManager,
			File indexLocation, long startupDelay) {
		this(taskList, dataManager, repositoryManager);
		Assert.isTrue(startupDelay >= 0L && startupDelay <= (1000L * 60));
		Assert.isNotNull(indexLocation);

		this.startupDelay = startupDelay;
		if (!indexLocation.exists()) {
			rebuildIndex = true;
			if (!indexLocation.mkdirs()) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksIndexCore.ID_PLUGIN,
						"Cannot create task list index folder: " + indexLocation)); //$NON-NLS-1$
			}
		}
		if (indexLocation.exists() && indexLocation.isDirectory()) {
			try {
				directory = new NIOFSDirectory(indexLocation);
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksIndexCore.ID_PLUGIN,
						"Cannot create task list index", e)); //$NON-NLS-1$
			}
		}
		initialize();
	}

	/**
	 * Create an index on the given task list. Must be matched by a corresponding call to {@link #close()}.
	 * 
	 * @param taskList
	 *            the task list that is to be indexed
	 * @param dataManager
	 *            the data manager that corresponds to the task list
	 * @param repositoryManager
	 *            the repository manager that corresponds to the task list
	 * @param directory
	 *            the directory in which the index should be stored
	 * @see #TaskListIndex(TaskList, TaskDataManager, File)
	 */
	public TaskListIndex(TaskList taskList, TaskDataManager dataManager, IRepositoryManager repositoryManager,
			Directory directory) {
		this(taskList, dataManager, repositoryManager);
		this.directory = directory;
		initialize();
	}

	/**
	 * the delay before reindexing occurs after a task has changed or after {@link #reindex()} is called
	 */
	public long getReindexDelay() {
		return reindexDelay;
	}

	/**
	 * the delay before reindexing occurs after a task has changed or after {@link #reindex()} is called.
	 * 
	 * @param reindexDelay
	 *            The delay in miliseconds. Specify 0 to indicate no delay.
	 */
	public void setReindexDelay(long reindexDelay) {
		Assert.isTrue(reindexDelay >= 0);
		this.reindexDelay = reindexDelay;
	}

	/**
	 * the default field used to match tasks when unspecified in the query
	 */
	public IndexField getDefaultField() {
		return defaultField;
	}

	/**
	 * the default field used to match tasks when unspecified in the query
	 */
	public void setDefaultField(IndexField defaultField) {
		Assert.isNotNull(defaultField);
		this.defaultField = defaultField;
		synchronized (this) {
			lastResults = null;
		}
	}

	/**
	 * the maximum number of search hits that should be provided when using {@link #matches(ITask, String)}
	 */
	public int getMaxMatchSearchHits() {
		return maxMatchSearchHits;
	}

	/**
	 * the maximum number of search hits that should be provided when using {@link #matches(ITask, String)}
	 */
	public void setMaxMatchSearchHits(int maxMatchSearchHits) {
		this.maxMatchSearchHits = maxMatchSearchHits;
	}

	private void initialize() {
		if (!rebuildIndex) {
			IndexReader indexReader = null;
			try {
				indexReader = getIndexReader();
			} catch (Exception e) {
				// ignore, this can happen if the index is corrupt
			}
			if (indexReader == null) {
				rebuildIndex = true;
			}
		}
		maintainIndexJob = new MaintainIndexJob();
		dataManager.addListener(this);
		taskList.addChangeListener(this);
		repositoryManager.addListener(this);

		scheduleIndexMaintenance(MaintainIndexType.STARTUP);
	}

	private void scheduleIndexMaintenance(MaintainIndexType type) {
		long delay = 0L;
		switch (type) {
		case STARTUP:
			delay = startupDelay;
			break;
		case REINDEX:
			delay = reindexDelay;
		}

		if (delay == 0L) {
			// primarily for testing purposes

			maintainIndexJob.cancel();
			try {
				maintainIndexJob.join();
			} catch (InterruptedException e) {
				// ignore
			}
			try {
				maintainIndex(new NullProgressMonitor());
			} catch (CoreException e) {
				MultiStatus logStatus = new MultiStatus(TasksIndexCore.ID_PLUGIN, 0,
						"Failed to update task list index", e); //$NON-NLS-1$
				logStatus.add(e.getStatus());
				StatusHandler.log(logStatus);
			}
		} else {
			maintainIndexJob.schedule(delay);
		}
	}

	/**
	 * Indicates if the given task matches the given pattern string. Uses the backing index to detect a match by looking
	 * for tasks that match the given pattern string. The results of the search are cached such that future calls to
	 * this method using the same pattern string do not require use of the backing index, making this method very
	 * efficient for multiple calls with the same pattern string. Cached results for a given pattern string are
	 * discarded if this method is called with a different pattern string.
	 * 
	 * @param task
	 *            the task to match
	 * @param patternString
	 *            the pattern used to detect a match
	 */
	public boolean matches(ITask task, String patternString) {
		if (patternString.equals(COMMAND_RESET_INDEX)) {
			reindex();
		}
		Lock readLock = indexReaderLock.readLock();
		readLock.lock();
		try {

			IndexReader indexReader = getIndexReader();
			if (indexReader != null) {
				Set<String> hits;

				final boolean needIndexHit;
				synchronized (this) {
					needIndexHit = lastResults == null
							|| (lastPatternString == null || !lastPatternString.equals(patternString));
				}
				if (needIndexHit) {
					this.lastPatternString = patternString;

					hits = new HashSet<String>();

					IndexSearcher indexSearcher = new IndexSearcher(indexReader);
					try {
						Query query = computeQuery(patternString);
						TopDocs results = indexSearcher.search(query, maxMatchSearchHits);
						for (ScoreDoc scoreDoc : results.scoreDocs) {
							Document document = indexReader.document(scoreDoc.doc);
							hits.add(document.get(IndexField.IDENTIFIER.fieldName()));
						}
					} catch (IOException e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksIndexCore.ID_PLUGIN,
								"Unexpected failure within task list index", e)); //$NON-NLS-1$
					} finally {
						try {
							indexSearcher.close();
						} catch (IOException e) {
							// ignore
						}
					}

				} else {
					hits = lastResults;
				}
				synchronized (this) {
					if (this.indexReader == indexReader) {
						this.lastPatternString = patternString;
						this.lastResults = hits;
					}
				}
				String taskIdentifier = task.getHandleIdentifier();
				return hits != null && hits.contains(taskIdentifier);
			}

		} finally {
			readLock.unlock();
		}
		return false;
	}

	public void reindex() {
		rebuildIndex = true;
		scheduleIndexMaintenance(MaintainIndexType.REINDEX);
	}

	/**
	 * call to wait until index maintenance has completed
	 * 
	 * @throws InterruptedException
	 */
	public void waitUntilIdle() throws InterruptedException {
		if (!Platform.isRunning() && reindexDelay != 0L) {
			// job join() behaviour is not the same when platform is not running
			Logger.getLogger(TaskListIndex.class.getName()).warning(
					"Index job joining may not work properly when Eclipse platform is not running"); //$NON-NLS-1$
		}
		maintainIndexJob.join();
	}

	/**
	 * finds tasks that match the given pattern string
	 * 
	 * @param patternString
	 *            the pattern string, used to match tasks
	 * @param collector
	 *            the collector that receives tasks
	 * @param resultsLimit
	 *            the maximum number of tasks to find. Specifying a limit enables the index to be more efficient since
	 *            it can skip over matching tasks that do not score highly enough. Specify {@link Integer#MAX_VALUE} if
	 *            there should be no limit.
	 */
	public void find(String patternString, TaskCollector collector, int resultsLimit) {
		Assert.isNotNull(patternString);
		Assert.isNotNull(collector);
		Assert.isTrue(resultsLimit > 0);

		Lock readLock = indexReaderLock.readLock();
		readLock.lock();
		try {
			IndexReader indexReader = getIndexReader();
			if (indexReader != null) {
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				try {
					Query query = computeQuery(patternString);
					TopDocs results = indexSearcher.search(query, resultsLimit);
					for (ScoreDoc scoreDoc : results.scoreDocs) {
						Document document = indexReader.document(scoreDoc.doc);
						String taskIdentifier = document.get(IndexField.IDENTIFIER.fieldName());
						AbstractTask task = taskList.getTask(taskIdentifier);
						if (task != null) {
							collector.collect(task);
						}
					}
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksIndexCore.ID_PLUGIN,
							"Unexpected failure within task list index", e)); //$NON-NLS-1$
				} finally {
					try {
						indexSearcher.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		} finally {
			readLock.unlock();
		}
	}

	private Query computeQuery(String patternString) {
		String upperPatternString = patternString.toUpperCase();

		boolean hasBooleanSpecifiers = upperPatternString.contains(" OR ") || upperPatternString.contains(" AND ") //$NON-NLS-1$ //$NON-NLS-2$
				|| upperPatternString.contains(" NOT "); //$NON-NLS-1$

		if (patternString.indexOf(':') == -1 && !hasBooleanSpecifiers && defaultField == IndexField.SUMMARY
				&& patternString.indexOf('"') == -1) {
			return new PrefixQuery(new Term(defaultField.fieldName(), patternString));
		}
		QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, defaultField.fieldName(), new TaskAnalyzer());
		Query q;
		try {
			q = qp.parse(patternString);
		} catch (ParseException e) {
			return new PrefixQuery(new Term(defaultField.fieldName(), patternString));
		}

		// relax term clauses to be prefix clauses so that we get results close
		// to what we're expecting
		// from previous task list search
		if (q instanceof BooleanQuery) {
			BooleanQuery query = (BooleanQuery) q;
			for (BooleanClause clause : query.getClauses()) {
				if (clause.getQuery() instanceof TermQuery) {
					TermQuery termQuery = (TermQuery) clause.getQuery();
					clause.setQuery(new PrefixQuery(termQuery.getTerm()));
				}
				if (!hasBooleanSpecifiers) {
					clause.setOccur(Occur.MUST);
				}
			}
		} else if (q instanceof TermQuery) {
			return new PrefixQuery(((TermQuery) q).getTerm());
		}
		return q;
	}

	public void close() {
		dataManager.removeListener(this);
		taskList.removeChangeListener(this);
		repositoryManager.removeListener(this);

		maintainIndexJob.cancel();
		try {
			maintainIndexJob.join();
		} catch (InterruptedException e) {
			// ignore
		}

		Lock writeLock = indexReaderLock.writeLock();
		writeLock.lock();
		try {
			synchronized (this) {
				if (indexReader != null) {
					try {
						indexReader.close();
					} catch (IOException e) {
						// ignore
					}
					indexReader = null;
				}
			}
			try {
				directory.close();
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksIndexCore.ID_PLUGIN,
						"Cannot close index: " + e.getMessage(), e)); //$NON-NLS-1$
			}
		} finally {
			writeLock.unlock();
		}
	}

	private IndexReader getIndexReader() {
		try {
			synchronized (this) {
				if (indexReader == null) {
					indexReader = IndexReader.open(directory, true);
					lastResults = null;
				}
				return indexReader;
			}
		} catch (CorruptIndexException e) {
			rebuildIndex = true;
			if (maintainIndexJob != null) {
				scheduleIndexMaintenance(MaintainIndexType.REINDEX);
			}
		} catch (FileNotFoundException e) {
			rebuildIndex = true;
			// expected if the index doesn't exist
		} catch (IOException e) {
			// ignore
		}
		return null;
	}

	public void taskDataUpdated(TaskDataManagerEvent event) {
		reindex(event.getTask(), event.getTaskData());
	}

	public void editsDiscarded(TaskDataManagerEvent event) {
		reindex(event.getTask(), event.getTaskData());
	}

	public void containersChanged(Set<TaskContainerDelta> containers) {
		for (TaskContainerDelta delta : containers) {
			switch (delta.getKind()) {
			case ADDED:
			case REMOVED:
			case CONTENT:
				IRepositoryElement element = delta.getElement();
				if (element instanceof ITask) {
					ITask task = (ITask) element;
					if ("local".equals(((AbstractTask) task).getConnectorKind())) { //$NON-NLS-1$
						reindex(task, null);
					}
				}
			}
		}
	}

	/**
	 * advanced usage: cause the given task to be reindexed using {@link MaintainIndexType#REINDEX reindex scheduling
	 * rule}.
	 * 
	 * @param task
	 *            the task
	 * @param taskData
	 *            the task data, or nul if it's not available
	 */
	protected void reindex(ITask task, TaskData taskData) {
		if (task == null) {
			// this can happen when edits are discarded
			return;
		}
		if (!taskIsIndexable(task, taskData)) {
			return;
		}
		synchronized (reindexQueue) {
			reindexQueue.put(task, taskData);
		}
		scheduleIndexMaintenance(MaintainIndexType.REINDEX);
	}

	private void addIndexedAttributes(Document document, ITask task, TaskAttribute root) {
		addIndexedAttribute(document, IndexField.TASK_KEY, task.getTaskKey());
		addIndexedAttribute(document, IndexField.REPOSITORY_URL, task.getRepositoryUrl());
		addIndexedAttribute(document, IndexField.SUMMARY, root.getMappedAttribute(TaskAttribute.SUMMARY));

		for (TaskAttribute contentAttribute : computeContentAttributes(root)) {
			addIndexedAttribute(document, IndexField.CONTENT, contentAttribute);
		}

		addIndexedDateAttributes(document, task);

		List<TaskAttribute> commentAttributes = root.getTaskData()
				.getAttributeMapper()
				.getAttributesByType(root.getTaskData(), TaskAttribute.TYPE_COMMENT);
		for (TaskAttribute commentAttribute : commentAttributes) {

			TaskComment taskComment = new TaskComment(root.getTaskData().getAttributeMapper().getTaskRepository(),
					task, commentAttribute);
			root.getTaskData().getAttributeMapper().updateTaskComment(taskComment, commentAttribute);

			String text = taskComment.getText();
			if (text.length() != 0) {
				addIndexedAttribute(document, IndexField.CONTENT, text);
			}
			IRepositoryPerson author = taskComment.getAuthor();
			if (author != null) {
				addIndexedAttribute(document, IndexField.PERSON, author.getPersonId());
			}
		}

		List<TaskAttribute> personAttributes = root.getTaskData()
				.getAttributeMapper()
				.getAttributesByType(root.getTaskData(), TaskAttribute.TYPE_PERSON);
		for (TaskAttribute personAttribute : personAttributes) {
			addIndexedAttribute(document, IndexField.PERSON, personAttribute);
		}

		for (IndexField field : IndexField.values()) {
			if (field.getAttributeId() != null) {
				addIndexedAttribute(document, field, root.getMappedAttribute(field.getAttributeId()));
			}
		}
	}

	/**
	 * compute attributes that should be indexed as {@link IndexField#CONTENT}
	 */
	private Collection<TaskAttribute> computeContentAttributes(TaskAttribute root) {
		Set<TaskAttribute> attributes = new LinkedHashSet<TaskAttribute>();

		// add default content attributes
		{
			TaskAttribute attribute = root.getMappedAttribute(TaskAttribute.SUMMARY);
			if (attribute != null) {
				attributes.add(attribute);
			}
			attribute = root.getMappedAttribute(TaskAttribute.DESCRIPTION);
			if (attribute != null) {
				attributes.add(attribute);
			}
		}

		for (TaskAttribute attribute : root.getAttributes().values()) {
			if (Boolean.parseBoolean(attribute.getMetaData().getValue(META_INDEXED_AS_CONTENT))) {
				attributes.add(attribute);
			}
		}

		return attributes;
	}

	private void addIndexedAttributes(Document document, ITask task) {
		addIndexedAttribute(document, IndexField.TASK_KEY, task.getTaskKey());
		addIndexedAttribute(document, IndexField.REPOSITORY_URL, task.getRepositoryUrl());
		addIndexedAttribute(document, IndexField.SUMMARY, task.getSummary());
		addIndexedAttribute(document, IndexField.CONTENT, task.getSummary());
		addIndexedAttribute(document, IndexField.CONTENT, ((AbstractTask) task).getNotes());
		addIndexedDateAttributes(document, task);
	}

	private void addIndexedDateAttributes(Document document, ITask task) {
		addIndexedAttribute(document, IndexField.COMPLETION_DATE, task.getCompletionDate());
		addIndexedAttribute(document, IndexField.CREATION_DATE, task.getCreationDate());
		addIndexedAttribute(document, IndexField.DUE_DATE, task.getDueDate());
		addIndexedAttribute(document, IndexField.MODIFICATION_DATE, task.getModificationDate());
	}

	private void addIndexedAttribute(Document document, IndexField indexField, TaskAttribute attribute) {
		if (attribute == null) {
			return;
		}
		List<String> values = attribute.getTaskData().getAttributeMapper().getValueLabels(attribute);
		if (values.isEmpty()) {
			return;
		}

		if (indexField.isPersonField()) {
			IRepositoryPerson repositoryPerson = attribute.getTaskData()
					.getAttributeMapper()
					.getRepositoryPerson(attribute);
			addIndexedAttribute(document, indexField, repositoryPerson);

			if (values.size() <= 1) {
				return;
			}
		}

		for (String value : values) {
			if (value.length() != 0) {
				addIndexedAttribute(document, indexField, value);
			}
		}
	}

	private void addIndexedAttribute(Document document, IndexField indexField, IRepositoryPerson person) {
		if (person != null) {
			addIndexedAttribute(document, indexField, person.getPersonId());
			addIndexedAttribute(document, indexField, person.getName());
		}
	}

	private void addIndexedAttribute(Document document, IndexField indexField, String value) {
		if (value == null) {
			return;
		}
		Field field = document.getField(indexField.fieldName());
		if (field == null) {
			field = new Field(indexField.fieldName(), value, Store.YES, org.apache.lucene.document.Field.Index.ANALYZED);
			document.add(field);
		} else {
			String existingValue = field.stringValue();
			if (indexField != IndexField.PERSON || !existingValue.contains(value)) {
				field.setValue(existingValue + " " + value); //$NON-NLS-1$
			}
		}
	}

	private void addIndexedAttribute(Document document, IndexField indexField, Date date) {
		if (date == null) {
			return;
		}
		// FIXME: date tools converts dates to GMT, and we don't really want that.  So
		// move the date by the GMT offset if there is any

		String value = DateTools.dateToString(date, Resolution.HOUR);
		Field field = document.getField(indexField.fieldName());
		if (field == null) {
			field = new Field(indexField.fieldName(), value, Store.YES, org.apache.lucene.document.Field.Index.ANALYZED);
			document.add(field);
		} else {
			field.setValue(value);
		}
	}

	/**
	 * Computes a query element for a field that must lie in a specified date range.
	 * 
	 * @param field
	 *            the field
	 * @param lowerBoundInclusive
	 *            the date lower bound that the field value must match, inclusive
	 * @param upperBoundInclusive
	 *            the date upper bound that the field value must match, inclusive
	 * @return
	 */
	public String computeQueryFieldDateRange(IndexField field, Date lowerBoundInclusive, Date upperBoundInclusive) {
		return field.fieldName()
				+ ":[" + DateTools.dateToString(lowerBoundInclusive, Resolution.DAY) + " TO " + DateTools.dateToString(upperBoundInclusive, Resolution.DAY) + "]"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	/**
	 * Indicates if the given task is indexable. The default implementation returns true, subclasses may override to
	 * filter some tasks from the task list. This method may be called more than once per task, with some calls omitting
	 * the task data. In this way implementations can avoid loading task data if the decision to filter tasks can be
	 * based on the ITask alone. Implementations that must read the task data in order to determine eligibility for
	 * indexing should return true for tasks where the provided task data is null.
	 * 
	 * @param task
	 *            the task
	 * @param taskData
	 *            the task data, or null if there is no task data
	 * @return true if the given task should be indexed, otherwise false.
	 */
	protected boolean taskIsIndexable(ITask task, TaskData taskData) {
		return true;
	}

	/**
	 * Escapes special characters in the given literal value so that they are not interpreted as special characters in a
	 * query.
	 * 
	 * @param value
	 *            the value to escape
	 * @return a representation of the value with characters escaped
	 */
	public String escapeFieldValue(String value) {
		// see http://lucene.apache.org/java/2_9_1/queryparsersyntax.html#Escaping%20Special%20Characters
		String escaped = value.replaceAll("([\\+\\-\\!\\(\\)\\{\\}\\[\\]^\"~\\*\\?:\\\\]|&&|\\|\\|)", "\\\\$1"); //$NON-NLS-1$ //$NON-NLS-2$
		return escaped;
	}

	private void maintainIndex(IProgressMonitor m) throws CoreException {
		final int WORK_PER_SEGMENT = 1000;
		SubMonitor monitor = SubMonitor.convert(m, 2 * WORK_PER_SEGMENT);
		try {
			try {
				if (!rebuildIndex) {
					try {
						IndexReader reader = IndexReader.open(directory, false);
						reader.close();
					} catch (CorruptIndexException e) {
						rebuildIndex = true;
					}
				}

				if (rebuildIndex) {
					synchronized (reindexQueue) {
						reindexQueue.clear();
					}

					IStatus status = rebuildIndexCompletely(monitor.newChild(WORK_PER_SEGMENT));
					if (!status.isOK()) {
						StatusHandler.log(status);
					}
				} else {
					monitor.worked(WORK_PER_SEGMENT);
				}

				// index any tasks that have been changed
				indexQueuedTasks(monitor.newChild(WORK_PER_SEGMENT));

				// prevent new searches from reading the now-stale index
				closeIndexReader();
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, TasksIndexCore.ID_PLUGIN,
						"Unexpected exception: " + e.getMessage(), e)); //$NON-NLS-1$
			}
		} finally {
			monitor.done();
		}
	}

	private void closeIndexReader() throws IOException {
		Lock writeLock = indexReaderLock.writeLock();
		writeLock.lock();
		try {
			synchronized (this) {
				if (indexReader != null) {
					indexReader.close();
					indexReader = null;
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	private void indexQueuedTasks(SubMonitor monitor) throws CorruptIndexException, LockObtainFailedException,
			IOException {

		synchronized (reindexQueue) {
			if (reindexQueue.isEmpty()) {
				return;
			}

			monitor.beginTask(Messages.TaskListIndex_task_rebuilding_index, reindexQueue.size());
		}

		try {
			IndexWriter writer = null;
			try {
				Map<ITask, TaskData> workingQueue = new HashMap<ITask, TaskData>();

				// reindex tasks that are in the reindexQueue, making multiple passes so that we catch anything
				// added/changed while we were reindexing
				for (;;) {
					workingQueue.clear();

					synchronized (reindexQueue) {
						if (reindexQueue.isEmpty()) {
							break;
						}
						// move items from the reindexQueue to the temporary working queue
						workingQueue.putAll(reindexQueue);
						reindexQueue.keySet().removeAll(workingQueue.keySet());
					}

					if (writer == null) {
						writer = new IndexWriter(directory, new TaskAnalyzer(), false,
								IndexWriter.MaxFieldLength.UNLIMITED);
					}

					monitor.setWorkRemaining(workingQueue.size());

					for (Entry<ITask, TaskData> entry : workingQueue.entrySet()) {
						ITask task = entry.getKey();
						TaskData taskData = entry.getValue();

						writer.deleteDocuments(new Term(IndexField.IDENTIFIER.fieldName(), task.getHandleIdentifier()));

						add(writer, task, taskData);

						monitor.worked(1);
					}
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} finally {
			monitor.done();
		}
	}

	private class TaskListState implements ITaskListRunnable {
		List<ITask> indexableTasks;

		public void execute(IProgressMonitor monitor) throws CoreException {
			Collection<AbstractTask> tasks = taskList.getAllTasks();
			indexableTasks = new ArrayList<ITask>(tasks.size());

			for (ITask task : tasks) {
				if (taskIsIndexable(task, null)) {
					indexableTasks.add(task);
				}
			}
		}

	}

	private IStatus rebuildIndexCompletely(SubMonitor monitor) throws CorruptIndexException, LockObtainFailedException,
			IOException, CoreException {

		MultiStatus multiStatus = new MultiStatus(TasksIndexCore.ID_PLUGIN, 0, null, null);

		// get indexable tasks from the task list
		final TaskListState taskListState = new TaskListState();
		taskList.run(taskListState, monitor.newChild(0));

		monitor.beginTask(Messages.TaskListIndex_task_rebuilding_index, taskListState.indexableTasks.size());
		try {
			final IndexWriter writer = new IndexWriter(directory, new TaskAnalyzer(), true,
					IndexWriter.MaxFieldLength.UNLIMITED);
			try {

				for (ITask task : taskListState.indexableTasks) {
					if (taskIsIndexable(task, null)) {
						try {
							TaskData taskData = dataManager.getTaskData(task);
							add(writer, task, taskData);
						} catch (CoreException e) {
							// an individual task data error should not prevent the index from updating
							multiStatus.add(e.getStatus());
						}
					}
					monitor.worked(1);
				}
				synchronized (this) {
					rebuildIndex = false;
				}
			} finally {
				writer.close();
			}
		} finally {
			monitor.done();
		}
		return multiStatus;
	}

	/**
	 * @param writer
	 * @param task
	 *            the task
	 * @param taskData
	 *            may be null for local tasks
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void add(IndexWriter writer, ITask task, TaskData taskData) throws CorruptIndexException, IOException {
		if (!taskIsIndexable(task, taskData)) {
			return;
		}

		Document document = new Document();

		document.add(new Field(IndexField.IDENTIFIER.fieldName(), task.getHandleIdentifier(), Store.YES,
				org.apache.lucene.document.Field.Index.ANALYZED));
		if (taskData == null) {
			if ("local".equals(((AbstractTask) task).getConnectorKind())) { //$NON-NLS-1$
				addIndexedAttributes(document, task);
			} else {
				return;
			}
		} else {
			addIndexedAttributes(document, task, taskData.getRoot());
		}
		writer.addDocument(document);
	}

	public void repositoryAdded(TaskRepository repository) {
		// ignore
	}

	public void repositoryRemoved(TaskRepository repository) {
		// ignore
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		// ignore
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		reindex();
	}
}
