/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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

	public static final String NEW_CC = "task.common.newcc"; //$NON-NLS-1$

	public static final String REMOVE_CC = "task.common.removecc"; //$NON-NLS-1$

	public static final EnumSet<Flag> NO_FLAGS = EnumSet.noneOf(Flag.class);

	public static boolean isInternalAttribute(TaskAttribute attribute) {
		String type = attribute.getMetaData().getType();
		if (TaskAttribute.TYPE_ATTACHMENT.equals(type) || TaskAttribute.TYPE_OPERATION.equals(type)
				|| TaskAttribute.TYPE_COMMENT.equals(type)) {
			return true;
		}
		String id = attribute.getId();
		return TaskAttribute.COMMENT_NEW.equals(id) || TaskAttribute.ADD_SELF_CC.equals(id) || REMOVE_CC.equals(id)
				|| NEW_CC.equals(id);
	}

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
			attribute.setValue(TracUtil.toTracTime(date) + ""); //$NON-NLS-1$
		}
	}

}
