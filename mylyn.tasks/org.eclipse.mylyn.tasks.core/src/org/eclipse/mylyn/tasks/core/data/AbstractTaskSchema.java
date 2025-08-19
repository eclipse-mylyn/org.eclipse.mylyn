/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies and others.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.core.runtime.Assert;

/**
 * Base class for task schemas. Clients should subclass to define a specific schema.
 *
 * @author Steffen Pingel
 * @author David Green
 * @author Miles Parker
 * @since 3.5
 */
public abstract class AbstractTaskSchema {

	public static class Field {

		private EnumSet<Flag> flags;

		private final String key;

		private final String label;

		private final String type;

		private final String indexKey;

		private final String dependsOn;

		protected Field(String key, String label, String type) {
			this(key, label, type, null, null, (Flag[]) null);
		}

		protected Field(String key, String label, String type, Flag... flags) {
			this(key, label, type, null, null, flags);
		}

		/**
		 * @param key
		 *            the task attribute key, which may be a common task attribute key defined in defined in {@link TaskAttribute}
		 * @param label
		 *            the user-visible label that is used by the user to identify this field
		 * @param type
		 *            the type of the field, should be one of the constants defined in TaskAttribute ( <code>TaskAttribute.TYPE_*</code>)
		 * @param indexKey
		 *            the index key, or null if this should not be indexed
		 * @param flags
		 *            the flags, or null
		 * @since 3.7
		 */
		public Field(String key, String label, String type, String indexKey, Flag... flags) {
			this(key, label, type, indexKey, null, flags);
		}

		/**
		 * @param key
		 *            the task attribute key, which may be a common task attribute key defined in defined in {@link TaskAttribute}
		 * @param label
		 *            the user-visible label that is used by the user to identify this field
		 * @param type
		 *            the type of the field, should be one of the constants defined in TaskAttribute ( <code>TaskAttribute.TYPE_*</code>)
		 * @param indexKey
		 *            the index key, or null if this should not be indexed
		 * @param dependsOn
		 *            the key of the TaskAttribute which is the parent of the dependency
		 * @param flags
		 *            the flags, or null
		 * @since 3.17
		 */
		public Field(String key, String label, String type, String indexKey, String dependsOn, Flag... flags) {
			Assert.isNotNull(key);
			Assert.isNotNull(label);
			Assert.isNotNull(type);
			this.key = key;
			this.label = label;
			this.type = type;
			this.indexKey = indexKey;
			this.dependsOn = dependsOn;
			if (flags == null || flags.length == 0) {
				this.flags = EnumSet.noneOf(Flag.class);
			} else {
				this.flags = EnumSet.copyOf(Arrays.asList(flags));
			}
		}

		public TaskAttribute createAttribute(TaskAttribute parent) {
			TaskAttribute attribute = parent.createMappedAttribute(getKey());
			// meta data
			TaskAttributeMetaData metaData = attribute.getMetaData();
			metaData.setLabel(getLabel());
			metaData.setType(getType());
			metaData.setReadOnly(isReadOnly());
			metaData.setKind(getKind());
			metaData.setRequired(isRequired());
			if (getDependsOn() != null) {
				metaData.setDependsOn(getDependsOn());
			}
			// options
			Map<String, String> options = getDefaultOptions();
			if (options != null) {
				for (Entry<String, String> option : options.entrySet()) {
					attribute.putOption(option.getKey(), option.getValue());
				}
			}
			return attribute;
		}

		public Map<String, String> getDefaultOptions() {
			return Collections.emptyMap();
		}

		public String getKey() {
			return key;
		}

		/**
		 * the key to use when indexing this field
		 *
		 * @return the index key, or null if this should not be indexed
		 * @since 3.7
		 */
		public String getIndexKey() {
			return indexKey;
		}

		public String getKind() {
			if (flags.contains(Flag.ATTRIBUTE)) {
				return TaskAttribute.KIND_DEFAULT;
			} else if (flags.contains(Flag.PEOPLE)) {
				return TaskAttribute.KIND_PEOPLE;
			} else if (flags.contains(Flag.OPERATION)) {
				return TaskAttribute.KIND_OPERATION;
			} else if (flags.contains(Flag.DESCRIPTION)) {
				return TaskAttribute.KIND_DESCRIPTION;
			}
			return null;
		}

		public String getLabel() {
			return label;
		}

		public String getType() {
			return type;
		}

		public boolean isReadOnly() {
			return flags.contains(Flag.READ_ONLY);
		}

		@Override
		public String toString() {
			return getLabel();
		}

		/**
		 * @since 3.7
		 */
		@Override
		public int hashCode() {
			return Objects.hash(indexKey, key);
		}

		/**
		 * @since 3.7
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (getClass() != obj.getClass())) {
				return false;
			}
			Field other = (Field) obj;
			if (!Objects.equals(indexKey, other.indexKey)) {
				return false;
			}
			if (!Objects.equals(key, other.key)) {
				return false;
			}
			return true;
		}

		/**
		 * @since 3.11
		 */
		public boolean isRequired() {
			return flags.contains(Flag.REQUIRED);
		}

		/**
		 * @since 3.17
		 */
		public String getDependsOn() {
			return dependsOn;
		}

	}

	public enum Flag {
		ATTRIBUTE, OPERATION, PEOPLE, READ_ONLY,
		/**
		 * A flag used to indicate that the field is related to a description.
		 *
		 * @since 3.11
		 * @see TaskAttribute#KIND_DESCRIPTION
		 */
		DESCRIPTION,
		/**
		 * A flag used to indicate that the field is required.
		 *
		 * @since 3.11
		 * @see TaskAttribute#META_REQUIRED
		 */
		REQUIRED

	}

	protected class FieldFactory {

		private EnumSet<Flag> flags;

		private String key;

		private String label;

		private String type;

		private String dependsOn;

		public FieldFactory(Field source) {
			flags = EnumSet.copyOf(source.flags);
			key = source.key;
			label = source.label;
			type = source.type;
			dependsOn = source.dependsOn;
		}

		public FieldFactory addFlags(Flag... flags) {
			this.flags.addAll(Arrays.asList(flags));
			return this;
		}

		public Field create() {
			return createField(key, label, type, null, dependsOn, !flags.isEmpty() ? flags.toArray(new Flag[0]) : null);
		}

		public FieldFactory flags(Flag... flags) {
			this.flags = EnumSet.copyOf(Arrays.asList(flags));
			return this;
		}

		public FieldFactory key(String key) {
			this.key = key;
			return this;
		}

		public FieldFactory label(String label) {
			this.label = label;
			return this;
		}

		public FieldFactory removeFlags(Flag... flags) {
			this.flags.removeAll(Arrays.asList(flags));
			return this;
		}

		public FieldFactory type(String type) {
			this.type = type;
			return this;
		}

		/**
		 * @since 3.17
		 */
		public FieldFactory dependsOn(String dependsOn) {
			this.dependsOn = dependsOn;
			return this;
		}

	}

	private final Map<String, Field> fieldByKey = new LinkedHashMap<>();

	/**
	 * Returns the specified field for the given key.
	 */
	public Field getFieldByKey(String taskKey) {
		return fieldByKey.get(taskKey);
	}

	/**
	 * Creates no-value attributes with default options for the supplied task for each schema field.
	 */
	public void initialize(TaskData taskData) {
		for (Field field : fieldByKey.values()) {
			field.createAttribute(taskData.getRoot());
		}
	}

	/**
	 * Provides an iterator for all fields within the schema. Subsequent modifications to the returned collection are not reflected to
	 * schema.
	 *
	 * @since 3.9
	 * @return all fields within the schema
	 */
	public Collection<Field> getFields() {
		return new ArrayList<>(fieldByKey.values());
	}

	protected Field createField(String key, String label, String type) {
		return createField(key, label, type, null, (Flag[]) null);
	}

	protected Field createField(String key, String label, String type, Flag... flags) {
		return createField(key, label, type, null, flags);
	}

	/**
	 * @since 3.7
	 * @see Field#Field(String, String, String, String, Flag...)
	 */
	protected Field createField(String key, String label, String type, String indexKey, Flag... flags) {
		return createField(key, label, type, indexKey, null, flags);
	}

	/**
	 * @since 3.17
	 */
	protected Field createField(String key, String label, String type, String indexKey, String dependsOn,
			Flag... flags) {
		Field field = new Field(key, label, type, indexKey, dependsOn, flags);
		fieldByKey.put(key, field);
		return field;
	}

	protected FieldFactory inheritFrom(Field source) {
		return new FieldFactory(source);
	}

}
