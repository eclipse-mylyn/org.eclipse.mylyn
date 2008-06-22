/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.util.Date;
import java.util.EnumSet;

import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

/**
 * Provides a mapping from Mylyn task keys to Trac ticket keys.
 * 
 * @author Steffen Pingel
 */
public class TracAttributeMapper extends TaskAttributeMapper {

	public enum Flag {
		READ_ONLY, ATTRIBUTE, PEOPLE
	};

	public static final String NEW_CC = "task.common.newcc";

	public static final String REMOVE_CC = "task.common.removecc";

	public static final EnumSet<Flag> NO_FLAGS = EnumSet.noneOf(Flag.class);

	public static boolean isInternalAttribute(TaskAttribute attribute) {
		if (TaskAttribute.TYPE_ATTACHMENT.equals(attribute.getMetaData().getType())
				|| TaskAttribute.TYPE_OPERATION.equals(attribute.getMetaData().getType())
				|| TaskAttribute.TYPE_COMMENT.equals(attribute.getMetaData().getType())) {
			return true;
		}
		String id = attribute.getId();
		return TaskAttribute.COMMENT_NEW.equals(id) || TaskAttribute.ADD_SELF_CC.equals(id) || REMOVE_CC.equals(id)
				|| NEW_CC.equals(id);
	}

//
//	@Override
//	public boolean isHidden(String key) {
//		if (isInternalAttribute(key)) {
//			return true;
//		}
//
//		TracAttribute tracAttribute = attributeByTracKey.get(key);
//		return (tracAttribute != null) ? tracAttribute.isHidden() : false;
//	}
//
//	@Override
//	public String getName(String key) {
//		TracAttribute tracAttribute = attributeByTracKey.get(key);
//		// TODO if attribute == null it is probably a custom field: need 
//		// to query custom field information from repoository
//		return (tracAttribute != null) ? tracAttribute.toString() : key;
//	}
//
//	@Override
//	public boolean isReadOnly(String key) {
//		TracAttribute tracAttribute = attributeByTracKey.get(key);
//		return (tracAttribute != null) ? tracAttribute.isReadOnly() : false;
//	}

	public TracAttributeMapper(TaskRepository taskRepository) {
		super(taskRepository);
	}

	@Override
	public Date getDateValue(TaskAttribute attribute) {
		return TracUtil.parseDate(attribute.getValue());
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		TracAttribute attribute = TracAttribute.getByTaskKey(key);
		return (attribute != null) ? attribute.getTracKey() : key;
	}

	@Override
	public void setDateValue(TaskAttribute attribute, Date date) {
		if (date == null) {
			attribute.clearValues();
		} else {
			attribute.setValue(TracUtil.toTracTime(date) + "");
		}
	}

}
