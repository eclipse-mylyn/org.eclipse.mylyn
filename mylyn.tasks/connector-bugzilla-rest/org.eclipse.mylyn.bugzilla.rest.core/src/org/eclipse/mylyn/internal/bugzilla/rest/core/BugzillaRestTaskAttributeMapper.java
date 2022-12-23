/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
				// TODO: change this when we have offline cache for the repository configuration so we build the options in an temp var
				if (repositoryConfiguration != null) {
					if (!productAttribute.getValue().equals("")) { //$NON-NLS-1$
						boolean found = false;
						attribute.clearOptions();
						for (String productName : productAttribute.getValues()) {
							Product actualProduct = repositoryConfiguration.getProductWithName(productName);
							if (attribute.getId()
									.equals(BugzillaRestCreateTaskSchema.getDefault().COMPONENT.getKey())) {
								internalSetAttributeOptions4Product(attribute, actualProduct.getComponents());

							} else if (attribute.getId()
									.equals(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())) {
								internalSetAttributeOptions4Product(attribute, actualProduct.getMilestones());
							} else if (attribute.getId()
									.equals(BugzillaRestCreateTaskSchema.getDefault().VERSION.getKey())) {
								internalSetAttributeOptions4Product(attribute, actualProduct.getVersions());
							}
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

	private void internalSetAttributeOptions4Product(TaskAttribute taskAttribute,
			SortableActiveEntry[] actualProductEntry) {
		boolean found = false;
		String actualValue = taskAttribute.getValue();
		for (SortableActiveEntry SortableActiveEntry : actualProductEntry) {
			if (SortableActiveEntry.isActive()) {
				// TODO: remove when we have offline cache for the repository configuration
				taskAttribute.putOption(SortableActiveEntry.getName(), SortableActiveEntry.getName());
				found |= actualValue.equals(SortableActiveEntry.getName());
			}
		}
		if (!found) {
			taskAttribute.setValue(""); //$NON-NLS-1$
		}
	}

	@Override
	public String mapToRepositoryKey(@NonNull TaskAttribute parent, @NonNull String key) {
		if (key.equals(TaskAttribute.TASK_KEY)) {
			return BugzillaRestTaskSchema.getDefault().BUG_ID.getKey();
		} else {
			return super.mapToRepositoryKey(parent, key);
		}
	}

	public void updateNewAttachmentAttribute(TaskAttribute attachmentAttribute) {
		BugzillaRestConfiguration repositoryConfiguration;
		try {
			repositoryConfiguration = connector.getRepositoryConfiguration(this.getTaskRepository());
			repositoryConfiguration.updateAttachmentFlags(attachmentAttribute);
		} catch (CoreException e) {
			StatusHandler.log(
					new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "Eerror in updateNewAttachmentAttribute", e)); //$NON-NLS-1$
		}

	}

}
