/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * Encapsulates attributes for task data.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class TaskAttribute {

	/**
	 * Boolean attribute. If true, repository user needs to be added to the cc list.
	 */
	public static final String ADD_SELF_CC = "task.common.addselfcc"; //$NON-NLS-1$

	public static final String ATTACHMENT_AUTHOR = "task.common.attachment.author"; //$NON-NLS-1$

	public static final String ATTACHMENT_CONTENT_TYPE = "task.common.attachment.ctype"; //$NON-NLS-1$

	public static final String ATTACHMENT_DATE = "task.common.attachment.date"; //$NON-NLS-1$

	public static final String ATTACHMENT_DESCRIPTION = "task.common.attachment.description"; //$NON-NLS-1$

	public static final String ATTACHMENT_FILENAME = "filename"; //$NON-NLS-1$

	public static final String ATTACHMENT_ID = "task.common.attachment.id"; //$NON-NLS-1$

	public static final String ATTACHMENT_IS_DEPRECATED = "task.common.attachment.deprecated"; //$NON-NLS-1$

	public static final String ATTACHMENT_IS_PATCH = "task.common.attachment.patch"; //$NON-NLS-1$

	public static final String ATTACHMENT_SIZE = "task.common.attachment.size"; //$NON-NLS-1$

	public static final String ATTACHMENT_URL = "task.common.attachment.url"; //$NON-NLS-1$

	public static final String COMMENT_ATTACHMENT_ID = "task.common.comment.attachment.id"; //$NON-NLS-1$

	public static final String COMMENT_AUTHOR = "task.common.comment.author"; //$NON-NLS-1$

	@Deprecated
	public static final String COMMENT_AUTHOR_NAME = "task.common.comment.author.name"; //$NON-NLS-1$

	public static final String COMMENT_DATE = "task.common.comment.date"; //$NON-NLS-1$

	public static final String COMMENT_HAS_ATTACHMENT = "task.common.comment.attachment"; //$NON-NLS-1$

	public static final String COMMENT_NEW = "task.common.comment.new"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String COMMENT_NUMBER = "task.common.comment.number"; //$NON-NLS-1$

	public static final String COMMENT_TEXT = "task.common.comment.text"; //$NON-NLS-1$

	public static final String COMMENT_URL = "task.common.comment.url"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String COMPONENT = "task.common.component"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String DATE_COMPLETION = "task.common.date.completed"; //$NON-NLS-1$

	public static final String DATE_CREATION = "task.common.date.created"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String DATE_DUE = "task.common.date.due"; //$NON-NLS-1$

	public static final String DATE_MODIFICATION = "task.common.date.modified"; //$NON-NLS-1$

	public static final String DESCRIPTION = "task.common.description"; //$NON-NLS-1$

	public static final String KEYWORDS = "task.common.keywords"; //$NON-NLS-1$

	public static final String KIND_DEFAULT = "task.common.kind.default"; //$NON-NLS-1$

	public static final String KIND_OPERATION = "task.common.kind.operation"; //$NON-NLS-1$

	public static final String KIND_PEOPLE = "task.common.kind.people"; //$NON-NLS-1$

	//public static final String META_SHOW_IN_ATTRIBUTES_SECTION = "task.meta.showInTaskEditorAttributesSection";

	public static final String META_ASSOCIATED_ATTRIBUTE_ID = "task.meta.associated.attribute"; //$NON-NLS-1$

	public static final String META_ATTRIBUTE_KIND = "task.meta.attributeKind"; //$NON-NLS-1$

	public static final String META_ATTRIBUTE_TYPE = "task.meta.type"; //$NON-NLS-1$

	public static final String META_DEFAULT_OPTION = "task.meta.defaultOption"; //$NON-NLS-1$

//	public static final String META_DETAIL_LEVEL = "task.meta.detailLevel";

	public static final String META_LABEL = "task.meta.label"; //$NON-NLS-1$

	public static final String META_READ_ONLY = "task.meta.readOnly"; //$NON-NLS-1$

	public static final String NEW_ATTACHMENT = "task.common.new.attachment"; //$NON-NLS-1$

	// XXX merge with USER_CC
	//public static final String NEW_CC = "task.common.newcc";

	public static final String OPERATION = "task.common.operation"; //$NON-NLS-1$

	public static final String PERSON_NAME = "task.common.person.name"; //$NON-NLS-1$

	public static final String PREFIX_ATTACHMENT = "task.common.attachment-"; //$NON-NLS-1$

	public static final String PREFIX_COMMENT = "task.common.comment-"; //$NON-NLS-1$

	// XXX merge with USER_CC
	//public static final String REMOVE_CC = "task.common.removecc";

	public static final String PREFIX_OPERATION = "task.common.operation-"; //$NON-NLS-1$

	public static final String PRIORITY = "task.common.priority"; //$NON-NLS-1$

	public static final String PRODUCT = "task.common.product"; //$NON-NLS-1$

	public static final String RESOLUTION = "task.common.resolution"; //$NON-NLS-1$

	public static final String STATUS = "task.common.status"; //$NON-NLS-1$

	public static final String SUMMARY = "task.common.summary"; //$NON-NLS-1$

	public static final String TASK_KEY = "task.common.key"; //$NON-NLS-1$

	public static final String TASK_KIND = "task.common.kind"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TASK_URL = "task.common.url"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_ATTACHMENT = "attachment"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_BOOLEAN = "boolean"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_COMMENT = "comment"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_CONTAINER = "container"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_DATE = "date"; //$NON-NLS-1$

	/**
	 * @since 3.1
	 */
	public static final String TYPE_DATETIME = "dateTime"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_INTEGER = "integer"; //$NON-NLS-1$

	/**
	 * @since 3.1
	 */
	public static final String TYPE_LONG = "long"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_LONG_RICH_TEXT = "longRichText"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_LONG_TEXT = "longText"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_MULTI_SELECT = "multiSelect"; //$NON-NLS-1$

	public static final String TYPE_OPERATION = "operation"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_PERSON = "person"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_SHORT_RICH_TEXT = "shortRichText"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_SHORT_TEXT = "shortText"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_SINGLE_SELECT = "singleSelect"; //$NON-NLS-1$

	/**
	 * @since 3.0
	 */
	public static final String TYPE_TASK_DEPENDENCY = "taskDepenedency"; //$NON-NLS-1$

	public static final String TYPE_URL = "url"; //$NON-NLS-1$

	public static final String USER_ASSIGNED = "task.common.user.assigned"; //$NON-NLS-1$

	@Deprecated
	public static final String USER_ASSIGNED_NAME = "task.common.user.assigned.name"; //$NON-NLS-1$

	public static final String USER_CC = "task.common.user.cc"; //$NON-NLS-1$

	public static final String USER_REPORTER = "task.common.user.reporter"; //$NON-NLS-1$

	@Deprecated
	public static final String USER_REPORTER_NAME = "task.common.user.reporter.name"; //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final String SEVERITY = "task.common.severity"; //$NON-NLS-1$

	/**
	 * @since 3.2
	 */
	public static final String VERSION = "task.common.version"; //$NON-NLS-1$

	private Map<String, TaskAttribute> attributeById;

	private final String attributeId;

	private Map<String, String> metaData;

	private Map<String, String> optionByKey;

	private final TaskAttribute parentAttribute;

	private final TaskData taskData;

	/**
	 * Attribute's values (selected or added)
	 */
	private final List<String> values;

	public TaskAttribute(TaskAttribute parentAttribute, String attributeId) {
		Assert.isNotNull(parentAttribute);
		Assert.isNotNull(attributeId);
		this.parentAttribute = parentAttribute;
		this.attributeId = attributeId;
		this.taskData = parentAttribute.getTaskData();
		this.values = new ArrayList<String>(1);
		parentAttribute.add(this);
	}

	/**
	 * Constructor for the root node.
	 */
	TaskAttribute(TaskData taskData) {
		Assert.isNotNull(taskData);
		this.parentAttribute = null;
		this.taskData = taskData;
		this.attributeId = "root"; //$NON-NLS-1$
		this.values = new ArrayList<String>(1);
	}

	private void add(TaskAttribute attribute) {
		if (attributeById == null) {
			attributeById = new LinkedHashMap<String, TaskAttribute>();
		}
		attributeById.put(attribute.getId(), attribute);
	}

	public void addValue(String value) {
		Assert.isNotNull(value);
		values.add(value);
	}

	public void clearAttributes() {
		attributeById = null;
	}

	void clearMetaDataMap() {
		metaData = null;
	}

	public void clearOptions() {
		optionByKey = null;
	}

	public void clearValues() {
		values.clear();
	}

	public TaskAttribute createAttribute(String attributeId) {
		return new TaskAttribute(this, attributeId);
	}

	public void deepAddCopy(TaskAttribute source) {
		TaskAttribute target = createAttribute(source.getId());
		target.values.addAll(source.values);
		if (source.metaData != null) {
			target.metaData = new LinkedHashMap<String, String>(source.metaData);
		}
		if (source.optionByKey != null) {
			target.optionByKey = new LinkedHashMap<String, String>(source.optionByKey);
		}
		if (source.attributeById != null) {
			for (TaskAttribute child : source.attributeById.values()) {
				target.deepAddCopy(child);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TaskAttribute other = (TaskAttribute) obj;
		if (attributeId == null) {
			if (other.attributeId != null) {
				return false;
			}
		} else if (!attributeId.equals(other.attributeId)) {
			return false;
		}
		return true;
	}

	public TaskAttribute getAttribute(String attributeId) {
		Assert.isNotNull(attributeId);
		return (attributeById != null) ? attributeById.get(attributeId) : null;
	}

	public Map<String, TaskAttribute> getAttributes() {
		if (attributeById != null) {
			return Collections.unmodifiableMap(attributeById);
		} else {
			return Collections.emptyMap();
		}
	}

	public String getId() {
		return attributeId;
	}

	public TaskAttribute getMappedAttribute(String attributeId) {
		Assert.isNotNull(attributeId);
		return (attributeById != null) ? attributeById.get(getTaskData().getAttributeMapper().mapToRepositoryKey(this,
				attributeId)) : null;
	}

	public TaskAttribute getMappedAttribute(String[] path) {
		TaskAttribute attribute = this;
		for (String id : path) {
			attribute = attribute.getMappedAttribute(id);
			if (attribute == null) {
				break;
			}
		}
		return attribute;
	}

	String getMetaDatum(String key) {
		return (metaData != null) ? metaData.get(key) : null;
	}

	Map<String, String> getMetaDataMap() {
		if (metaData != null) {
			return Collections.unmodifiableMap(metaData);
		} else {
			return Collections.emptyMap();
		}
	}

	public String getOption(String key) {
		return (optionByKey != null) ? optionByKey.get(key) : null;
	}

	public Map<String, String> getOptions() {
		if (optionByKey != null) {
			return Collections.unmodifiableMap(optionByKey);
		} else {
			return Collections.emptyMap();
		}
	}

	public TaskAttribute getParentAttribute() {
		return parentAttribute;
	}

	public String[] getPath() {
		List<String> path = new ArrayList<String>();
		TaskAttribute attribute = this;
		while (attribute.getParentAttribute() != null) {
			path.add(attribute.getId());
			attribute = attribute.getParentAttribute();
		}
		Collections.reverse(path);
		return path.toArray(new String[0]);
	}

	public TaskAttributeMetaData getMetaData() {
		return new TaskAttributeMetaData(this);
	}

	public TaskData getTaskData() {
		return taskData;
	}

	public String getValue() {
		if (values.size() > 0) {
			return values.get(0);
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public List<String> getValues() {
		return Collections.unmodifiableList(values);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeId == null) ? 0 : attributeId.hashCode());
		return result;
	}

	void putMetaDatum(String key, String value) {
		Assert.isNotNull(key);
		Assert.isNotNull(value);
		if (metaData == null) {
			metaData = new LinkedHashMap<String, String>();
		}
		metaData.put(key, value);
	}

	/**
	 * Adds an attribute option value
	 * 
	 * @param readableValue
	 *            The value displayed on the screen
	 * @param parameterValue
	 *            The option value used when sending the form to the server
	 */
	public void putOption(String key, String value) {
		Assert.isNotNull(key);
		Assert.isNotNull(value);
		if (optionByKey == null) {
			optionByKey = new LinkedHashMap<String, String>();
		}
		optionByKey.put(key, value);
	}

	public void removeAttribute(String attributeId) {
		if (attributeById != null) {
			attributeById.remove(attributeId);
		}
	}

	void removeMetaDatum(String metaDataId) {
		if (metaData != null) {
			metaData.remove(metaDataId);
		}
	}

	public void removeValue(String value) {
		values.remove(value);
	}

	public void setValue(String value) {
		Assert.isNotNull(value);
		if (values.size() > 0) {
			values.clear();
		}
		values.add(value);
	}

	public void setValues(List<String> values) {
		this.values.clear();
		this.values.addAll(values);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, ""); //$NON-NLS-1$
		return sb.toString();
	}

	private void toString(StringBuilder sb, String prefix) {
		sb.append(prefix);
		sb.append("TaskAttribute[id="); //$NON-NLS-1$
		sb.append(attributeId);
		sb.append(",values="); //$NON-NLS-1$
		sb.append(values);
		sb.append(",options="); //$NON-NLS-1$
		sb.append(optionByKey);
		sb.append(",metaData="); //$NON-NLS-1$
		sb.append(metaData);
		sb.append("]"); //$NON-NLS-1$
		if (attributeById != null) {
			for (TaskAttribute child : attributeById.values()) {
				sb.append("\n"); //$NON-NLS-1$
				child.toString(sb, prefix + " "); //$NON-NLS-1$
			}
		}
	}

	public TaskAttribute createMappedAttribute(String attributeId) {
		Assert.isNotNull(attributeId);
		String mappedAttributeId = getTaskData().getAttributeMapper().mapToRepositoryKey(this, attributeId);
		Assert.isNotNull(mappedAttributeId);
		return new TaskAttribute(this, mappedAttributeId);
	}
}
