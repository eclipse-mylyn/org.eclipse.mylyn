/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.osgi.util.NLS;

import com.google.common.base.Strings;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class TaskAttributeMetaData {

//	public enum DetailLevel {
//		/** A little bit of detail, e.g. a task showing in the Task List. */
//		LOW,
//		/** More detail, e.g. a task showing in a tool tip. */
//		MEDIUM,
//		/** A lot of detail, e.g. a task showing in an editor. */
//		//HIGH
//	};

	private final TaskAttribute taskAttribute;

	TaskAttributeMetaData(TaskAttribute taskAttribute) {
		this.taskAttribute = taskAttribute;
	}

	public TaskAttributeMetaData defaults() {
		setLabel(null);
		setKind(null);
		setReadOnly(true);
		setType(TaskAttribute.TYPE_SHORT_TEXT);
		setRequired(false);
		// only for test
		// putValue(TaskAttribute.META_DESCRIPTION, "Tooltip Defaul Text"); //$NON-NLS-1$
		return this;
	}

	public TaskAttributeMetaData clear() {
		taskAttribute.clearMetaDataMap();
		return this;
	}

	/**
	 * @deprecated not use, see {@link #setDefaultOption(String)}
	 */
	@Deprecated
	public String getDefaultOption() {
		return taskAttribute.getMetaDatum(TaskAttribute.META_DEFAULT_OPTION);
	}

//	public DetailLevel getDetailLevel() {
//		try {
//			return DetailLevel.valueOf(taskAttribute.getMetaDatum(TaskAttribute.META_DEFAULT_OPTION));
//		} catch (IllegalArgumentException e) {
//			return null;
//		}
//	}

	public String getKind() {
		return taskAttribute.getMetaDatum(TaskAttribute.META_ATTRIBUTE_KIND);
	}

	public String getLabel() {
		return taskAttribute.getMetaDatum(TaskAttribute.META_LABEL);
	}

	public String getType() {
		return taskAttribute.getMetaDatum(TaskAttribute.META_ATTRIBUTE_TYPE);
	}

	public String getValue(String key) {
		return taskAttribute.getMetaDatum(key);
	}

	public Map<String, String> getValues() {
		return taskAttribute.getMetaDataMap();
	}

	/**
	 * @since 3.5
	 * @see TaskAttribute#META_DISABLED
	 */
	public boolean isDisabled() {
		return Boolean.parseBoolean(taskAttribute.getMetaDatum(TaskAttribute.META_DISABLED));
	}

	public boolean isReadOnly() {
		return Boolean.parseBoolean(taskAttribute.getMetaDatum(TaskAttribute.META_READ_ONLY));
	}

	/**
	 * @since 3.11
	 * @see TaskAttribute#META_REQUIRED
	 */
	public boolean isRequired() {
		return Boolean.parseBoolean(taskAttribute.getMetaDatum(TaskAttribute.META_REQUIRED));
	}

	/**
	 * @since 3.11
	 * @see TaskAttribute#META_ATTRIBUTE_MEDIA_TYPE
	 */
	public String getMediaType() {
		return taskAttribute.getMetaDatum(TaskAttribute.META_ATTRIBUTE_MEDIA_TYPE);
	}

	/**
	 * @since 3.17
	 * @see TaskAttribute#META_DEPENDS_ON_ATTRIBUTE_ID
	 */
	public String getDependsOn() {
		return taskAttribute.getMetaDatum(TaskAttribute.META_DEPENDS_ON_ATTRIBUTE_ID);
	}

	public TaskAttributeMetaData putValue(String key, String value) {
		taskAttribute.putMetaDatum(key, value);
		return this;
	}

	/**
	 * The default option property is not used. Connectors are expected to set default values in
	 * {@link AbstractTaskDataHandler#initializeTaskData(org.eclipse.mylyn.tasks.core.TaskRepository, TaskData, org.eclipse.mylyn.tasks.core.ITaskMapping, org.eclipse.core.runtime.IProgressMonitor)}
	 * .
	 * 
	 * @deprecated Not used, set default value in
	 *             {@link AbstractTaskDataHandler#initializeTaskData(org.eclipse.mylyn.tasks.core.TaskRepository, TaskData, org.eclipse.mylyn.tasks.core.ITaskMapping, org.eclipse.core.runtime.IProgressMonitor)}
	 *             instead.
	 */
	@Deprecated
	public TaskAttributeMetaData setDefaultOption(String defaultOption) {
		if (defaultOption != null) {
			taskAttribute.putMetaDatum(TaskAttribute.META_DEFAULT_OPTION, defaultOption);
		} else {
			taskAttribute.removeMetaDatum(TaskAttribute.META_DEFAULT_OPTION);
		}
		return this;
	}

//	public TaskAttributeMetaData setDetailLevel(DetailLevel detailLevel) {
//		if (detailLevel != null) {
//			taskAttribute.putMetaDatum(TaskAttribute.META_DETAIL_LEVEL, detailLevel.name());
//		} else {
//			taskAttribute.removeMetaDatum(TaskAttribute.META_DETAIL_LEVEL);
//		}
//		return this;
//	}

	public TaskAttributeMetaData setKind(String value) {
		if (value != null) {
			taskAttribute.putMetaDatum(TaskAttribute.META_ATTRIBUTE_KIND, value);
		} else {
			taskAttribute.removeMetaDatum(TaskAttribute.META_ATTRIBUTE_KIND);
		}
		return this;
	}

	public TaskAttributeMetaData setLabel(String value) {
		if (value != null) {
			taskAttribute.putMetaDatum(TaskAttribute.META_LABEL, value);
		} else {
			taskAttribute.removeMetaDatum(TaskAttribute.META_LABEL);
		}
		return this;
	}

	public TaskAttributeMetaData setReadOnly(boolean value) {
		taskAttribute.putMetaDatum(TaskAttribute.META_READ_ONLY, Boolean.toString(value));
		return this;
	}

	public TaskAttributeMetaData setType(String value) {
		if (value != null) {
			taskAttribute.putMetaDatum(TaskAttribute.META_ATTRIBUTE_TYPE, value);
		} else {
			taskAttribute.removeMetaDatum(TaskAttribute.META_ATTRIBUTE_TYPE);
		}
		return this;
	}

	/**
	 * @since 3.5
	 * @see TaskAttribute#META_DISABLED
	 * @return this
	 */
	public TaskAttributeMetaData setDisabled(boolean value) {
		taskAttribute.putMetaDatum(TaskAttribute.META_DISABLED, Boolean.toString(value));
		return this;
	}

	/**
	 * @since 3.11
	 * @see TaskAttribute#META_REQUIRED
	 * @return this
	 */
	public TaskAttributeMetaData setRequired(boolean value) {
		taskAttribute.putMetaDatum(TaskAttribute.META_REQUIRED, Boolean.toString(value));
		return this;
	}

	/**
	 * @since 3.11
	 * @see TaskAttribute#META_ATTRIBUTE_MEDIA_TYPE
	 * @return this
	 */
	public TaskAttributeMetaData setMediaType(String value) {
		if (value != null) {
			taskAttribute.putMetaDatum(TaskAttribute.META_ATTRIBUTE_MEDIA_TYPE, value);
		} else {
			taskAttribute.removeMetaDatum(TaskAttribute.META_ATTRIBUTE_MEDIA_TYPE);
		}
		return this;
	}

	/**
	 * @since 3.17
	 * @see TaskAttribute#META_DEPENDS_ON_ATTRIBUTE_ID
	 * @return this
	 */
	public TaskAttributeMetaData setDependsOn(String value) {
		taskAttribute.putMetaDatum(TaskAttribute.META_DEPENDS_ON_ATTRIBUTE_ID, value);
		return this;
	}

	/**
	 * Get the precision of a date or time attribute. Returns <code>null</code> if there is no precision specified.
	 * 
	 * @since 3.18
	 * @see TaskAttribute#META_ATTRIBUTE_PRECISION
	 */
	@Nullable
	public TimeUnit getPrecision() {
		String precision = taskAttribute.getMetaDatum(TaskAttribute.META_ATTRIBUTE_PRECISION);
		if (!Strings.isNullOrEmpty(precision)) {
			try {
				return TimeUnit.valueOf(precision);
			} catch (IllegalArgumentException e) {
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, NLS.bind(
						"Could not parse precision '{0}'", precision), e)); //$NON-NLS-1$
			}
		}
		return null;
	}

	/**
	 * Set the precision of a date or time attribute.
	 * 
	 * @since 3.18
	 * @see TaskAttribute#META_ATTRIBUTE_PRECISION
	 */
	public void setPrecision(TimeUnit precision) {
		if (precision == null) {
			taskAttribute.removeMetaDatum(TaskAttribute.META_ATTRIBUTE_PRECISION);
		} else {
			taskAttribute.putMetaDatum(TaskAttribute.META_ATTRIBUTE_PRECISION, precision.name());
		}
	}

}
