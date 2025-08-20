/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskMapping;

/**
 * @author Steffen Pingel
 */
public class KeyValueMapping extends TaskMapping {

	private final Map<String, String> attributes;

	public KeyValueMapping(Map<String, String> attributes) {
		Assert.isNotNull(attributes);
		this.attributes = attributes;
	}

	@Override
	public String getComponent() {
		return attributes.get(IRepositoryConstants.COMPONENT);
	}

	@Override
	public String getDescription() {
		return attributes.get(IRepositoryConstants.DESCRIPTION);
	}

	@Override
	public List<String> getKeywords() {
		// TODO implement
		return null;
	}

	@Override
	public String getOwner() {
		// TODO implement
		return null;
	}

	@Override
	public String getPriority() {
		return attributes.get(IRepositoryConstants.PRIORITY);
	}

	@Override
	public String getProduct() {
		return attributes.get(IRepositoryConstants.PRODUCT);
	}

	@Override
	public String getSeverity() {
		return attributes.get(IRepositoryConstants.SEVERITY);
	}

	@Override
	public String getSummary() {
		return attributes.get(IRepositoryConstants.SUMMARY);
	}

	@Override
	public String getTaskKind() {
		// TODO implement
		return null;
	}

	@Override
	public String getVersion() {
		return attributes.get(IRepositoryConstants.VERSION);
	}

	@Override
	public void merge(ITaskMapping source) {
	}

}