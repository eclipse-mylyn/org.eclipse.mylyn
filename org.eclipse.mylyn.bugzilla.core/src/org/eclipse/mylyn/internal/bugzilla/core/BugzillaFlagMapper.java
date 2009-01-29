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

package org.eclipse.mylyn.internal.bugzilla.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Frank Becker
 * @since 3.1
 */
public class BugzillaFlagMapper {

	private String requestee;

	private String setter;

	private String state;

	private String flagId;

	private int number;

	public String getRequestee() {
		return requestee;
	}

	public void setRequestee(String requestee) {
		this.requestee = requestee;
	}

	public String getSetter() {
		return setter;
	}

	public void setSetter(String setter) {
		this.setter = setter;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFlagId() {
		return flagId;
	}

	public void setFlagId(String flagId) {
		this.flagId = flagId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void applyTo(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);
		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		TaskAttributeMetaData meta = taskAttribute.getMetaData().defaults();
		meta.setType(IBugzillaConstants.EDITOR_TYPE_FLAG);
		meta.setLabel(description);
		BugzillaVersion bugzillaVersion = null;
		RepositoryConfiguration repositoryConfiguration;
		try {
			repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(mapper.getTaskRepository(), false,
					new NullProgressMonitor());
			bugzillaVersion = repositoryConfiguration.getInstallVersion();
		} catch (CoreException e) {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}

		if (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_2) >= 0) {
			meta.setKind(TaskAttribute.KIND_DEFAULT);
		}
		meta.setReadOnly(false);

		if (getNumber() != 0) {
			TaskAttribute child = taskAttribute.createMappedAttribute("number"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_INTEGER);
			mapper.setIntegerValue(child, getNumber());
		}
		if (getRequestee() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("requestee"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getRequestee());
		}
		if (getSetter() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("setter"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SHORT_TEXT);
			mapper.setValue(child, getSetter());
		}
		if (getState() != null) {
			TaskAttribute child = taskAttribute.createMappedAttribute("state"); //$NON-NLS-1$
			child.getMetaData().defaults().setType(TaskAttribute.TYPE_SINGLE_SELECT);
			child.getMetaData().setLabel(flagId);
			child.getMetaData().setReadOnly(false);
			mapper.setValue(child, getState());
			taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, "state"); //$NON-NLS-1$

		}
	}

}
