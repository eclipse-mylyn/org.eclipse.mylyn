/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.Product;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.SortableActiveEntry;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;

public class BugzillaRestTaskAttributeMapper extends TaskAttributeMapper {

	private final BugzillaRestConnector connector;

	public BugzillaRestTaskAttributeMapper(TaskRepository taskRepository, BugzillaRestConnector connector) {
		super(taskRepository);
		this.connector = connector;
	}

	@Override
	public Map<String, String> getOptions(@NonNull TaskAttribute attribute) {
		if (attribute.getId().equals(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
				|| attribute.getId().equals(BugzillaRestCreateTaskSchema.getDefault().VERSION.getKey())
				|| attribute.getId().equals(BugzillaRestCreateTaskSchema.getDefault().COMPONENT.getKey())) {
			TaskAttribute productAttribute = attribute.getParentAttribute()
					.getAttribute(BugzillaRestCreateTaskSchema.getDefault().PRODUCT.getKey());
			BugzillaRestConfiguration repositoryConfiguration;
			try {
				repositoryConfiguration = connector.getRepositoryConfiguration(this.getTaskRepository());
				if (repositoryConfiguration != null) {
					if (!productAttribute.getValue().equals("")) { //$NON-NLS-1$
						Product actualProduct = repositoryConfiguration.getProductWithName(productAttribute.getValue());
						if (attribute.getId()
								.equals(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())) {
							internalSetAttributeOptions(attribute, actualProduct.getMilestones());
						} else
							if (attribute.getId().equals(BugzillaRestCreateTaskSchema.getDefault().VERSION.getKey())) {
							internalSetAttributeOptions(attribute, actualProduct.getVersions());
						} else if (attribute.getId()
								.equals(BugzillaRestCreateTaskSchema.getDefault().COMPONENT.getKey())) {
							internalSetAttributeOptions(attribute, actualProduct.getComponents());
						}
					}
				}
			} catch (CoreException e) {
				StatusHandler.log(new RepositoryStatus(getTaskRepository(), IStatus.ERROR, BugzillaRestCore.ID_PLUGIN,
						0, "Failed to obtain repository configuration", e)); //$NON-NLS-1$
			}
		}
		return super.getOptions(attribute);
	}

	private void internalSetAttributeOptions(TaskAttribute taskAttribute, SortableActiveEntry[] actualProductEntry) {
		boolean found = false;
		String actualValue = taskAttribute.getValue();
		taskAttribute.clearOptions();
		for (SortableActiveEntry SortableActiveEntry : actualProductEntry) {
			if (SortableActiveEntry.isActive()) {
				// TODO: remove when we have offline cache for the repository configuration
				taskAttribute.putOption(SortableActiveEntry.getName(), SortableActiveEntry.getName());
				if (!found) {
					found = actualValue.equals(SortableActiveEntry.getName());
				}
			}
		}
		if (!found) {
			taskAttribute.setValue(""); //$NON-NLS-1$
		}
	}

}
