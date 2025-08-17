/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Francois Chouinard - Initial implementation
 *   Marc-Andre Laperle - Add Topic to dashboard
 *   Marc-Andre Laperle - Add Status to dashboard
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.gerrit.dashboard.Messages;
import org.eclipse.mylyn.internal.gerrit.core.GerritQueryResultSchema;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * A Dashboard Gerrit review task
 *
 * @author Francois Chouinard
 * @version 0.1
 */
@SuppressWarnings("restriction")
public class GerritTask extends AbstractTask {

	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/**
	 * Mylyn Task ID
	 */
	public static final String TASK_ID = "dashboard.mylyn.task.id"; //$NON-NLS-1$

	/**
	 * Gerrit Review shortened Change-Id
	 */
	public static final String SHORT_CHANGE_ID = TaskAttribute.TASK_KEY;

	/**
	 * Gerrit Review Change-Id
	 */
	public static final String CHANGE_ID = GerritQueryResultSchema.getDefault().CHANGE_ID.getKey();

	/**
	 * Gerrit Review subject
	 */
	public static final String SUBJECT = TaskAttribute.SUMMARY;

	/**
	 * Gerrit Review status
	 */
	public static final String STATUS = GerritTaskSchema.getDefault().STATUS.getKey();

	/**
	 * Gerrit Review owner
	 */
	public static final String OWNER = GerritTaskSchema.getDefault().OWNER.getKey();

	/**
	 * Gerrit Review project
	 */
	public static final String PROJECT = TaskAttribute.PRODUCT;

	/**
	 * Gerrit Review branch
	 */
	public static final String BRANCH = GerritTaskSchema.getDefault().BRANCH.getKey();

	/**
	 * Gerrit Review topic
	 */
	public static final String TOPIC = GerritTaskSchema.getDefault().TOPIC.getKey();

	/**
	 * Gerrit Review creation date
	 */
	public static final String DATE_CREATION = TaskAttribute.DATE_CREATION;

	/**
	 * Gerrit Review last modification date
	 */
	public static final String DATE_MODIFICATION = TaskAttribute.DATE_MODIFICATION;

	/**
	 * Gerrit Review completion date
	 */
	public static final String DATE_COMPLETION = TaskAttribute.DATE_COMPLETION;

	/**
	 * Gerrit Review flags
	 */
	public static final String IS_STARRED = GerritTaskSchema.getDefault().IS_STARRED.getKey();

	public static final String REVIEW_STATE = GerritTaskSchema.getDefault().REVIEW_STATE.getKey();

	public static final String VERIFY_STATE = GerritTaskSchema.getDefault().VERIFY_STATE.getKey();

	/**
	 * Date format
	 */
	private static SimpleDateFormat FORMAT_HOUR = new SimpleDateFormat("h:mm a"); //$NON-NLS-1$

	private static SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat("MMM d"); //$NON-NLS-1$

	private static SimpleDateFormat FORMAT_FULL = new SimpleDateFormat("MMM d, yyyy"); //$NON-NLS-1$

	// -------------------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------------------

	// The connector kind
	private final String fConnectorKind;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * Construct an GerritTask from a Gerrit query result. Some fields may be missing from the task data.
	 *
	 * @param taskData
	 *            the Gerrit task data
	 */
	public GerritTask(final TaskData taskData) {
		super(taskData.getRepositoryUrl(), taskData.getTaskId(),
				taskData.getRoot().getAttribute(TaskAttribute.SUMMARY).getValue() + " [" //$NON-NLS-1$
						+ taskData.getRoot().getAttribute(TaskAttribute.TASK_KEY).getValue() + "]"); //$NON-NLS-1$

		fConnectorKind = taskData.getConnectorKind();

		TaskAttribute root = taskData.getRoot();
		Map<String, TaskAttribute> attributes = root.getAttributes();

		setAttribute(TASK_ID, taskData.getTaskId());
		setAttribute(SHORT_CHANGE_ID, getValue(attributes.get(SHORT_CHANGE_ID)));
		setAttribute(CHANGE_ID, getValue(attributes.get(CHANGE_ID)));
		setAttribute(SUBJECT, getValue(attributes.get(SUBJECT)));
		setAttribute(STATUS, getValue(attributes.get(STATUS)));

		setAttribute(OWNER, taskData.getAttributeMapper().getValueLabel(attributes.get(OWNER)));
		setAttribute(PROJECT, getValue(attributes.get(PROJECT)));
		setAttribute(BRANCH, getValue(attributes.get(BRANCH)));
		setAttribute(TOPIC, getValue(attributes.get(TOPIC)));

		setAttribute(DATE_CREATION, getValue(attributes.get(DATE_CREATION)));
		setAttribute(DATE_MODIFICATION, getValue(attributes.get(DATE_MODIFICATION)));
		setAttribute(DATE_COMPLETION, getValue(attributes.get(DATE_COMPLETION)));

		setAttribute(IS_STARRED, getValue(attributes.get(IS_STARRED)));
		setAttribute(REVIEW_STATE, getValue(attributes.get(REVIEW_STATE)));
		setAttribute(VERIFY_STATE, getValue(attributes.get(VERIFY_STATE)));
	}

	/*
	 * Extract the first value from the specified task attributes list.
	 *
	 * @param taskAttribute
	 *
	 * @return the first value in the list (if any)
	 */
	private String getValue(TaskAttribute taskAttribute) {
		if (taskAttribute != null) {
			List<String> values = taskAttribute.getValues();
			if (values != null && values.size() > 0) {
				return values.get(0);
			}
		}
		return null;
	}

	// -------------------------------------------------------------------------
	// Getters and Setters
	// -------------------------------------------------------------------------

	/**
	 * Format the requested Gerrit Review attribute as a date string. As in the Gerrit web UI, the output format depends on the date
	 * relation with 'today': Same day: 'hh:mm am/pm' Same year, different day: 'Mon DD' Different year: 'Mon DD, YYYY' (not implemented)
	 *
	 * @param key
	 *            one of { DATE_CREATION, DATE_MODIFICATION, DATE_COMPLETION }
	 * @return
	 */
	public String getAttributeAsDate(String key) {
		// Validate the supplied key
		if (!key.equals(DATE_CREATION) && !key.equals(DATE_MODIFICATION) && !key.equals(DATE_COMPLETION)) {
			return ""; //$NON-NLS-1$
		}

		// Retrieve the date
		String rawDate = getAttribute(key);
		if (rawDate == null) {
			return ""; //$NON-NLS-1$
		}

		// Format the date
		Date date = new Date(Long.parseLong(rawDate));
		if (isToday(date)) {
			return FORMAT_HOUR.format(date);
		}
		if (isThisYear(date)) {
			return FORMAT_MONTH.format(date);
		}
		return FORMAT_FULL.format(date);
	}

	/**
	 * Indicates if a date is 'today'
	 *
	 * @param date
	 *            the date to check against 'today'
	 * @return true if 'today'
	 */
	private boolean isToday(Date date) {
		Calendar cal1 = Calendar.getInstance(); // today
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date);

		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

		return sameDay;
	}

	/**
	 * Indicates if a date is 'this year'
	 *
	 * @param date
	 *            the date to check
	 * @return true if same year as today
	 */
	private boolean isThisYear(Date date) {
		Calendar cal1 = Calendar.getInstance(); // today
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date);

		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	// -------------------------------------------------------------------------
	// AbstractTask
	// -------------------------------------------------------------------------

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public String getConnectorKind() {
		return fConnectorKind;
	}

	// -------------------------------------------------------------------------
	// Object
	// -------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(Messages.GerritTask_taskID).append(getAttribute(GerritTask.TASK_ID)).append('\n');
		buffer.append(Messages.GerritTask_shortID).append(getAttribute(GerritTask.SHORT_CHANGE_ID)).append('\n');
		buffer.append(Messages.GerritTask_changeID).append(getAttribute(GerritTask.CHANGE_ID)).append('\n');
		buffer.append(Messages.GerritTask_subject).append(getAttribute(GerritTask.SUBJECT)).append('\n');
		buffer.append(Messages.GerritTask_status).append(getAttribute(GerritTask.STATUS)).append('\n');
		buffer.append(Messages.GerritTask_owner).append(getAttribute(GerritTask.OWNER)).append('\n');
		buffer.append(Messages.GerritTask_project).append(getAttribute(GerritTask.PROJECT)).append('\n');
		buffer.append(Messages.GerritTask_branch).append(getAttribute(GerritTask.BRANCH)).append('\n');
		buffer.append(Messages.GerritTask_topic).append(getAttribute(GerritTask.TOPIC)).append('\n');
		buffer.append(Messages.GerritTask_updated)
				.append(getAttributeAsDate(GerritTask.DATE_MODIFICATION))
				.append('\n');
		buffer.append(Messages.GerritTask_star)
				.append(getAttribute(GerritTask.IS_STARRED))
				.append(", CRVW = ") //$NON-NLS-1$
				.append(getAttribute(GerritTask.REVIEW_STATE))
				.append(", VRIF = ") //$NON-NLS-1$
				.append(getAttribute(GerritTask.VERIFY_STATE))
				.append("\n"); //$NON-NLS-1$
		return buffer.toString();
	}

}
