/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;

/**
 * Base class for task schemas. Clients should subclass to define a specific schema.
 * 
 * @author Steffen Pingel
 * @since 3.5
 */
public abstract class AbstractTaskSchema {

	public static class Field {

		private EnumSet<Flag> flags;

		private final String key;

		private final String label;

		private final String type;

		protected Field(String key, String label, String type) {
			this(key, label, type, (Flag[]) null);
		}

		protected Field(String key, String label, String type, Flag... flags) {
			Assert.isNotNull(key);
			Assert.isNotNull(label);
			Assert.isNotNull(type);
			this.key = key;
			this.label = label;
			this.type = type;
			if (flags == null) {
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

		public String getKind() {
			if (flags.contains(Flag.ATTRIBUTE)) {
				return TaskAttribute.KIND_DEFAULT;
			} else if (flags.contains(Flag.PEOPLE)) {
				return TaskAttribute.KIND_PEOPLE;
			} else if (flags.contains(Flag.OPERATION)) {
				return TaskAttribute.KIND_OPERATION;
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

	}

	public enum Flag {
		ATTRIBUTE, OPERATION, PEOPLE, READ_ONLY
	};

	protected class FieldFactory {

		private EnumSet<Flag> flags;

		private String key;

		private String label;

		private String type;

		public FieldFactory(Field source) {
			this.flags = EnumSet.copyOf(source.flags);
			this.key = source.key;
			this.label = source.label;
			this.type = source.type;
		}

		public FieldFactory addFlags(Flag... flags) {
			this.flags.addAll(Arrays.asList(flags));
			return this;
		}

		public Field create() {
			return createField(key, label, type, (!flags.isEmpty()) ? flags.toArray(new Flag[0]) : null);
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

	}

	private final Map<String, Field> fieldByKey = new LinkedHashMap<String, Field>();

	public Field getFieldByKey(String taskKey) {
		return fieldByKey.get(taskKey);
	}

	public void initialize(TaskData taskData) {
		for (Field field : fieldByKey.values()) {
			field.createAttribute(taskData.getRoot());
		}
	}

	protected Field createField(String key, String label, String type) {
		return createField(key, label, type, (Flag[]) null);
	}

	protected Field createField(String key, String label, String type, Flag... flags) {
		Field field = new Field(key, label, type, flags);
		fieldByKey.put(key, field);
		return field;
	}

	protected FieldFactory inheritFrom(Field source) {
		return new FieldFactory(source);
	}

}
