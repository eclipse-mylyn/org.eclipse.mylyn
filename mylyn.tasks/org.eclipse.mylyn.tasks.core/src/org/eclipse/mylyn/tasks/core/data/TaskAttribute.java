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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.core.RepositoryPerson;

/**
 * Encapsulates attributes for task data.
 *
 * @author Rob Elves
 * @author Steffen Pingel
 * @author Miles Parker
 * @author David Green
 * @since 3.0
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

	/**
	 * @since 3.4
	 */
	public static final String ATTACHMENT_REPLACE_EXISTING = "task.common.attachment.replaceExisting"; //$NON-NLS-1$

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

	/**
	 * A {@link TaskAttributeMetaData#getKind() kind} used to indicate that the field is related to a description.
	 *
	 * @since 3.11
	 */
	public static final String KIND_DESCRIPTION = "task.common.kind.description"; //$NON-NLS-1$

	public static final String KIND_PEOPLE = "task.common.kind.people"; //$NON-NLS-1$

	//public static final String META_SHOW_IN_ATTRIBUTES_SECTION = "task.meta.showInTaskEditorAttributesSection";

	public static final String META_ASSOCIATED_ATTRIBUTE_ID = "task.meta.associated.attribute"; //$NON-NLS-1$

	public static final String META_ATTRIBUTE_KIND = "task.meta.attributeKind"; //$NON-NLS-1$

	public static final String META_ATTRIBUTE_TYPE = "task.meta.type"; //$NON-NLS-1$

	/**
	 * A key for {@link TaskAttributeMetaData} that is used to specify the precision of a date or time attribute, which
	 * must be parseable by {@link TimeUnit#valueOf(String)}. This specifies the precision with which values are
	 * represented <i>on the server</i>. This is separate from the attribute {@link #META_ATTRIBUTE_TYPE type}, which
	 * may specify that an attribute should be <i>displayed</i> as a {@link #TYPE_DATE date} or a {@link #TYPE_DATETIME
	 * date with time}.
	 * <p>
	 * Connectors should ensure that {@link TaskAttributeMapper#getDateValue(TaskAttribute)} and
	 * {@link TaskAttributeMapper#setDateValue(TaskAttribute, java.util.Date) setDateValue(TaskAttribute, Date)}
	 * respectively return and accept dates at midnight in the local time zone when the precision is
	 * {@link TimeUnit#DAYS} or coarser.
	 *
	 * @since 3.18
	 * @see TaskAttributeMetaData#getPrecision()
	 * @see TaskAttributeMetaData#setPrecision()
	 */
	public static final String META_ATTRIBUTE_PRECISION = "task.meta.precision"; //$NON-NLS-1$

	/**
	 * A key for {@link TaskAttributeMetaData} that is used for specifying the ID of the parent {@link TaskAttribute}
	 * for attributes that have a dependency. When the parent is changed we look for all attributes with have a
	 * {@link TaskAttributeMetaData} of this key and an value of the ID from the changed {@link TaskAttribute} and also
	 * trigger a change. With this we can refresh the options of each {@link TaskAttribute}.<br>
	 * <br>
	 * Example: In Bugzilla we have COMPONENT, VERSION, TARGET_MILESTONE as depends on the PRODUCT. We can so update the
	 * options of the attributes to match the definition of the PRODUCT.
	 *
	 * @see #BugzillaRestCreateTaskSchema
	 * @since 3.17
	 */
	public static final String META_DEPENDS_ON_ATTRIBUTE_ID = "task.meta.dependson.attribute"; //$NON-NLS-1$

	/**
	 * A key for {@link TaskAttributeMetaData} that is used for specifying the media type of a
	 * {@link #TYPE_LONG_RICH_TEXT} or {@link #TYPE_SHORT_RICH_TEXT}. The media type if specified must be a valid
	 * <a href="http://en.wikipedia.org/wiki/Internet_media_type">Internet Media Type</a> (also known as Content-Type,
	 * mime-type) according to <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a> and
	 * <a href="http://www.ietf.org/rfc/rfc2046.txt">RFC 2046</a>.
	 *
	 * @see #TYPE_LONG_RICH_TEXT
	 * @see #TYPE_SHORT_RICH_TEXT
	 * @see #META_ATTRIBUTE_TYPE
	 * @since 3.10
	 */
	public static final String META_ATTRIBUTE_MEDIA_TYPE = "task.meta.mediaType"; //$NON-NLS-1$

	public static final String META_DEFAULT_OPTION = "task.meta.defaultOption"; //$NON-NLS-1$

//	public static final String META_DETAIL_LEVEL = "task.meta.detailLevel";

	public static final String META_LABEL = "task.meta.label"; //$NON-NLS-1$

	public static final String META_READ_ONLY = "task.meta.readOnly"; //$NON-NLS-1$

	/**
	 * Key for {@link TaskAttributeMetaData} used to specify that a field requires a value before it can be submitted to
	 * the server. This meta-data is used by the framework as a UI hint and does not guarantee that a connector will
	 * enforce compliance before attempting to post task data.
	 *
	 * @since 3.11
	 */
	public static final String META_REQUIRED = "task.meta.required"; //$NON-NLS-1$

	/**
	 * @since 3.6
	 */
	public static final String COMMENT_ISPRIVATE = "task.common.comment.isprivate"; //$NON-NLS-1$

	/**
	 * Key for the meta datum that determines if an attribute is disabled. This is used to indicate that an attribute
	 * should not be modified, e.g. due to work-flow state but it may still be generally writeable.
	 *
	 * @since 3.5
	 * @see TaskAttributeMetaData#isDisabled()
	 */
	public static final String META_DISABLED = "task.meta.disabled"; //$NON-NLS-1$

	/**
	 * Key for the meta datum that provides a description of an attribute, e.g. for display in a tooltip.
	 *
	 * @since 3.5
	 * @see TaskAttributeMetaData
	 */
	public static final String META_DESCRIPTION = "task.meta.description"; //$NON-NLS-1$

	/**
	 * Task attribute meta-data key that should be set to "true" to have attribute value indexed as part of the task
	 * content. Provides a way for connectors to specify non-standard attributes as plain-text indexable. By default,
	 * {@link #SUMMARY summary} and {@link #DESCRIPTION description} are indexed. Note that setting this meta-data is
	 * advisory only and will not guarantee that content is indexed.
	 *
	 * @since 3.7
	 */
	public static final String META_INDEXED_AS_CONTENT = "task.meta.index.content"; //$NON-NLS-1$

	public static final String NEW_ATTACHMENT = "task.common.new.attachment"; //$NON-NLS-1$

	// XXX merge with USER_CC
	//public static final String NEW_CC = "task.common.newcc";

	public static final String OPERATION = "task.common.operation"; //$NON-NLS-1$

	public static final String PERSON_NAME = "task.common.person.name"; //$NON-NLS-1$

	/**
	 * Key for {@link RepositoryPerson} used to store the human-readable username used to log into the repository, if it
	 * is different than the ID used to identify the person in {@link TaskData}.
	 *
	 * @since 3.18
	 */
	public static final String PERSON_USERNAME = "task.common.person.username"; //$NON-NLS-1$

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
	 * @since 3.3
	 */
	public static final String RANK = "task.common.rank"; //$NON-NLS-1$

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
	 * @see #META_ATTRIBUTE_MEDIA_TYPE
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
	 * @see #META_ATTRIBUTE_MEDIA_TYPE
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

	/**
	 * @since 3.5
	 */
	public static final String TYPE_DOUBLE = "double"; //$NON-NLS-1$

	/**
	 * @since 3.21
	 */
	public static final String TYPE_LABEL = "label"; //$NON-NLS-1$

	/**
	 * @since 3.21
	 */
	public static final String TYPE_MULTI_LABEL = "multiLabel"; //$NON-NLS-1$

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
		this.attributeId = attributeId.intern();
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
		return (attributeById != null)
				? attributeById.get(getTaskData().getAttributeMapper().mapToRepositoryKey(this, attributeId))
				: null;
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

	/**
	 * Returns the current value for a single value attribute. For a multi-value attribute, returns the first value.
	 * Note: returns an empty string if the value has not been set <em>or</em> if the value is actually an empty string.
	 * To determine whether a value has been explicitly set, use {@link #hasValue()}.
	 */
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

	/**
	 * Indicates whether any value(s) are currently set for this attribute. Note that this is a different case from
	 * testing whether or not {@link #getValue()} returns an empty string, as it is possible that an empty string value
	 * has been explicitly set for the attribute. Call {@link #clearValues()} to return the attribute to the unset
	 * state.
	 *
	 * @return true if any value is set (may be an empty string), false if no value is set.
	 * @since 3.9
	 */
	public boolean hasValue() {
		return values.size() > 0;
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
		metaData.put(key.intern(), value);
	}

	/**
	 * Adds an attribute option value
	 *
	 * @param key
	 *            The option value used when sending the form to the server
	 * @param value
	 *            The value displayed on the screen
	 */
	public void putOption(String key, String value) {
		Assert.isNotNull(key);
		Assert.isNotNull(value);
		if (optionByKey == null) {
			optionByKey = new LinkedHashMap<String, String>();
		}
		optionByKey.put(key.intern(), value);
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
		Assert.isNotNull(values);
		Assert.isTrue(!values.contains(null));
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
