/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * GerritConnector.
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 * 
 */
public class GerritConnector extends AbstractRepositoryConnector {

    /**
     * Connector kind
     */
    public static final String CONNECTOR_KIND = "org.eclipse.mylyn.gerrit";

    /**
     * Label for the connector.
     */
    public static final String CONNECTOR_LABEL = "Gerrit Code Review";

    /**
     * prefix for taskid in a task-url:
     * http://[gerrit-repository]/#change,[task.id]
     */
    public static final String CHANGE_PREFIX = "/#change,";

    private final GerritTaskDataHandler taskDataHandler = new GerritTaskDataHandler();

    private TaskDataCollector resultCollector = null;
    private GerritClient gerritClient = null;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#canCreateNewTask
     * (org.eclipse.mylyn.tasks.core.TaskRepository)
     */
    @Override
    public boolean canCreateNewTask(TaskRepository arg0) {
        // can't create new tasks, as these are triggered by git
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#canCreateTaskFromKey
     * (org.eclipse.mylyn.tasks.core.TaskRepository)
     */
    @Override
    public boolean canCreateTaskFromKey(TaskRepository arg0) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getConnectorKind
     * ()
     */
    @Override
    public String getConnectorKind() {
        return CONNECTOR_KIND;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getLabel()
     */
    @Override
    public String getLabel() {
        return CONNECTOR_LABEL;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#
     * getRepositoryUrlFromTaskUrl(java.lang.String)
     */
    @Override
    public String getRepositoryUrlFromTaskUrl(String url) {
        //
        // TODO: when uncommenting this we can't open the link in eclipse:
        // 

        // example: https://review.sonyericsson.net/#change,14175
        // if ((url != null) && (url.length() > 0)) {
        // int index = url.indexOf(CHANGE_PREFIX);
        // if (index > 0) {
        // return url.substring(0, index);
        // }
        // }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskData(
     * org.eclipse.mylyn.tasks.core.TaskRepository, java.lang.String,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
            throws CoreException {
        if(gerritClient==null){
            gerritClient = GerritClient.getGerritClient(repository);
        }
        gerritClient.addUpdateListener(this);
        return taskDataHandler.getTaskData(repository, taskId, monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskIdFromTaskUrl
     * (java.lang.String)
     */
    @Override
    public String getTaskIdFromTaskUrl(String url) {
        // example: https://review.sonyericsson.net/#change,14175
        if ((url != null) && (url.length() > 0)) {
            int index = url.indexOf(CHANGE_PREFIX);
            if (index > 0) {
                return url.substring(index + CHANGE_PREFIX.length());
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskUrl(java
     * .lang.String, java.lang.String)
     */
    @Override
    public String getTaskUrl(String repositoryUrl, String taskId) {
        // return null;
        String url = repositoryUrl + CHANGE_PREFIX + taskId;
        return ((repositoryUrl != null) && (taskId != null)) ? url : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#hasTaskChanged
     * (org.eclipse.mylyn.tasks.core.TaskRepository,
     * org.eclipse.mylyn.tasks.core.ITask,
     * org.eclipse.mylyn.tasks.core.data.TaskData)
     */
    @Override
    public boolean hasTaskChanged(TaskRepository repository, ITask task, TaskData taskData) {
        ITaskMapping taskMapping = getTaskMapping(taskData);
        Date repositoryDate = taskMapping.getModificationDate();
        Date localDate = task.getModificationDate();
        if (repositoryDate != null && repositoryDate.equals(localDate)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#performQuery
     * (org.eclipse.mylyn.tasks.core.TaskRepository,
     * org.eclipse.mylyn.tasks.core.IRepositoryQuery,
     * org.eclipse.mylyn.tasks.core.data.TaskDataCollector,
     * org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus performQuery(TaskRepository repository, IRepositoryQuery query,
            TaskDataCollector resultCollector, ISynchronizationSession session,
            IProgressMonitor monitor) {
        try {
            this.resultCollector = resultCollector;
            monitor.beginTask("executing query...", IProgressMonitor.UNKNOWN);
            try {
                gerritClient = GerritClient.getGerritClient(repository);
                gerritClient.addUpdateListener(this);
                if (GerritQuery.ALL_OPEN_CHANGES.equals(query.getAttribute("gerrit query type"))) {
                    gerritClient.allQuery(repository, monitor);
                } else if (GerritQuery.MY_OPEN_CHANGES.equals(query
                        .getAttribute("gerrit query type"))) {
                    gerritClient.myQuery(repository, monitor);
                }
            } catch (Throwable e) {
                // TODO:
                e.printStackTrace();
            }

            return Status.OK_STATUS;
        } finally {
            monitor.done();
        }

    }

    @Override
    public void updateRepositoryConfiguration(TaskRepository arg0, IProgressMonitor arg1)
            throws CoreException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#
     * updateTaskFromTaskData(org.eclipse.mylyn.tasks.core.TaskRepository,
     * org.eclipse.mylyn.tasks.core.ITask,
     * org.eclipse.mylyn.tasks.core.data.TaskData)
     */
    @Override
    public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
        TaskMapper mapper = (TaskMapper)getTaskMapping(taskData);
        mapper.applyTo(task);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskDataHandler
     * ()
     */
    @Override
    public AbstractTaskDataHandler getTaskDataHandler() {
        return taskDataHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector#getTaskMapping
     * (org.eclipse.mylyn.tasks.core.data.TaskData)
     */
    @Override
    public ITaskMapping getTaskMapping(TaskData taskData) {
        return new TaskMapper(taskData);
    }
    /**
     * Updates the task repository from an Async callback from Gerrit
     * @param repository The task repository in which to create tasks
     * @param result The list of tasks to be put in the repository
     * @param monitor The progress monitor
     */
    public void updateTaskRepositoryAsync(TaskRepository repository, List<GerritTask> result,
            IProgressMonitor monitor) {
        try {
            for (GerritTask gerritTask : result) {

                TaskData taskData = taskDataHandler.createTaskData(repository, gerritTask.getId(),
                        monitor);
                taskData.setPartial(true);
                taskDataHandler.updateTaskData(taskData, gerritTask);

                resultCollector.accept(taskData);

            }
        } catch (CoreException c) {
            c.printStackTrace();
        }
    }

}
