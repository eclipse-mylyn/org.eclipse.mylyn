/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * GitHub task attribute metadata
 */
public class GitHubAttributeMetadata {

	private String id;
	private String label;
	private String type;
	private String kind;
	private boolean readOnly;
	private boolean initTask;

	/**
	 * Create attribute metadata
	 * 
	 * @param id
	 * @param label
	 * @param type
	 * @param readOnly
	 */
	public GitHubAttributeMetadata(final String id, final String label,
			final String type, final boolean readOnly) {
		this(id, label, type, readOnly, false);
	}

	/**
	 * Create attribute metadata
	 * 
	 * @param id
	 * @param label
	 * @param type
	 * @param readOnly
	 * @param initTask
	 */
	public GitHubAttributeMetadata(final String id, final String label,
			final String type, final boolean readOnly, boolean initTask) {
		this(id, label, TaskAttribute.KIND_DEFAULT, type, readOnly, initTask);
	}

	/**
	 * Create attribute metadata
	 * 
	 * @param id
	 * @param label
	 * @param kind
	 * @param type
	 * @param readOnly
	 * @param initTask
	 */
	public GitHubAttributeMetadata(final String id, final String label,
			final String kind, final String type, final boolean readOnly,
			boolean initTask) {
		this.id = id;
		this.label = label;
		this.kind = kind;
		this.type = type;
		this.readOnly = readOnly;
		this.initTask = initTask;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @return readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @return initTask
	 */
	public boolean isInitTask() {
		return initTask;
	}

	/**
	 * Create task attribute under root of task data
	 * 
	 * @param data
	 * @return created attribute
	 */
	public TaskAttribute create(final TaskData data) {
		return create(data.getRoot());
	}

	/**
	 * Create task attribute under parent
	 * 
	 * @param parent
	 * @return created attribute
	 */
	public TaskAttribute create(final TaskAttribute parent) {
		final TaskAttribute attribute = new TaskAttribute(parent, id);
		attribute.getMetaData().defaults().setLabel(label).setType(type)
				.setKind(kind).setReadOnly(readOnly);
		return attribute;
	}

	/**
	 * Get value of this attribute from the task attribute under the root of the
	 * given {@link TaskData}.
	 * 
	 * @param data
	 * @return value
	 */
	public String getValue(TaskData data) {
		TaskAttribute root = data.getRoot();
		TaskAttribute attribute = root.getAttribute(id);
		return attribute != null ? data.getAttributeMapper()
				.getValue(attribute) : ""; //$NON-NLS-1$
	}

	/**
	 * Set the value of this attribute in the task attribute under the root of
	 * the given {@link TaskData}.
	 * 
	 * @param data
	 * @param value
	 */
	public void setValue(TaskData data, String value) {
		TaskAttribute root = data.getRoot();
		if (value == null)
			value = ""; //$NON-NLS-1$
		TaskAttribute attribute = root.getAttribute(id);
		if (attribute != null)
			data.getAttributeMapper().setValue(attribute, value);
	}
}
