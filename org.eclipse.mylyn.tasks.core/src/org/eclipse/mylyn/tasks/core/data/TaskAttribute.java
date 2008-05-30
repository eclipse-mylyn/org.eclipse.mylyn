/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 */
public final class TaskAttribute {

	/**
	 * Boolean attribute. If true, repository user needs to be added to the cc list.
	 */
	public static final String ADD_SELF_CC = "task.common.addselfcc";

	public static final String ATTACHMENT_AUTHOR = "task.common.attachment.author";

	public static final String ATTACHMENT_CONTENT_TYPE = "task.common.attachment.ctype";

	public static final String ATTACHMENT_DATE = "task.common.attachment.date";

	public static final String ATTACHMENT_DESCRIPTION = "task.common.attachment.description";

	public static final String ATTACHMENT_FILENAME = "filename";

	public static final String ATTACHMENT_ID = "task.common.attachment.id";

	public static final String ATTACHMENT_IS_DEPRECATED = "task.common.attachment.deprecated";

	public static final String ATTACHMENT_IS_PATCH = "task.common.attachment.patch";

	public static final String ATTACHMENT_SIZE = "task.common.attachment.size";

	public static final String ATTACHMENT_URL = "task.common.attachment.url";

	public static final String COMMENT_ATTACHMENT_ID = "task.common.comment.attachment.id";

	public static final String COMMENT_AUTHOR = "task.common.comment.author";

	@Deprecated
	public static final String COMMENT_AUTHOR_NAME = "task.common.comment.author.name";

	public static final String COMMENT_DATE = "task.common.comment.date";

	public static final String COMMENT_HAS_ATTACHMENT = "task.common.comment.attachment";

	public static final String COMMENT_NEW = "task.common.comment.new";

	/**
	 * @since 3.0
	 */
	public static final String COMMENT_NUMBER = "task.common.comment.number";

	public static final String COMMENT_TEXT = "task.common.comment.text";

	public static final String COMMENT_URL = "task.common.comment.url";

	/**
	 * @since 3.0
	 */
	public static final String COMPONENT = "task.common.component";

	/**
	 * @since 3.0
	 */
	public static final String DATE_COMPLETION = "task.common.date.completed";

	public static final String DATE_CREATION = "task.common.date.created";

	/**
	 * @since 3.0
	 */
	public static final String DATE_DUE = "task.common.date.due";

	public static final String DATE_MODIFICATION = "task.common.date.modified";

	public static final String DESCRIPTION = "task.common.description";

	public static final String KEYWORDS = "task.common.keywords";

	public static final String KIND_DEFAULT = "task.common.kind.default";

	public static final String KIND_OPERATION = "task.common.kind.operation";

	public static final String KIND_PEOPLE = "task.common.kind.default";

	//public static final String META_SHOW_IN_ATTRIBUTES_SECTION = "task.meta.showInTaskEditorAttributesSection";

	public static final String META_ASSOCIATED_ATTRIBUTE_ID = "task.meta.associated.attribute";

	public static final String META_ATTRIBUTE_KIND = "task.meta.attributeKind";

	public static final String META_ATTRIBUTE_TYPE = "task.meta.type";

	public static final String META_DEFAULT_OPTION = "task.meta.defaultOption";

	public static final String META_LABEL = "task.meta.label";

	public static final String META_READ_ONLY = "task.meta.readOnly";

	public static final String NEW_ATTACHMENT = "task.common.new.attachment";

	// XXX merge with USER_CC
	//public static final String NEW_CC = "task.common.newcc";

	public static final String OPERATION = "task.common.operation";

	public static final String PERSON_NAME = "task.common.person.name";

	public static final String PREFIX_ATTACHMENT = "task.common.attachment-";

	public static final String PREFIX_COMMENT = "task.common.comment-";

	// XXX merge with USER_CC
	//public static final String REMOVE_CC = "task.common.removecc";

	public static final String PREFIX_OPERATION = "task.common.operation-";

	public static final String PRIORITY = "task.common.priority";

	public static final String PRODUCT = "task.common.product";

	public static final String RESOLUTION = "task.common.resolution";

	public static final String STATUS = "task.common.status";

	public static final String SUMMARY = "task.common.summary";

	public static final String TASK_KEY = "task.common.key";

	public static final String TASK_KIND = "task.common.kind";

	/**
	 * @since 3.0
	 */
	public static final String TASK_URL = "task.common.url";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_ATTACHMENT = "attachment";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_BOOLEAN = "boolean";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_COMMENT = "comment";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_CONTAINER = "container";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_DATE = "date";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_INTEGER = "integer";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_LONG_RICH_TEXT = "longRichText";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_LONG_TEXT = "longText";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_MULTI_SELECT = "multiSelect";

	public static final String TYPE_OPERATION = "operation";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_PERSON = "person";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_SHORT_RICH_TEXT = "shortRichText";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_SHORT_TEXT = "shortText";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_SINGLE_SELECT = "singleSelect";

	/**
	 * @since 3.0
	 */
	public static final String TYPE_TASK_DEPENDENCY = "taskDepenedency";

	public static final String TYPE_URL = "url";

	public static final String USER_ASSIGNED = "task.common.user.assigned";

	@Deprecated
	public static final String USER_ASSIGNED_NAME = "task.common.user.assigned.name";

	public static final String USER_CC = "task.common.user.cc";

	public static final String USER_REPORTER = "task.common.user.reporter";

	@Deprecated
	public static final String USER_REPORTER_NAME = "task.common.user.reporter.name";

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
		this.attributeId = "root";
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
			return "";
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
		toString(sb, "");
		return sb.toString();
	}

	private void toString(StringBuilder sb, String prefix) {
		sb.append(prefix);
		sb.append("TaskAttribute[id=" + attributeId + ",values=" + values + ",options=" + optionByKey + ",metaData="
				+ metaData + "]\n");
		if (attributeById != null) {
			for (TaskAttribute child : attributeById.values()) {
				child.toString(sb, prefix + " ");
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
