/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.List;
import java.util.Map;

/**
 * Data model object for <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#label-info">LabelInfo</a>.
 */
public class LabelInfo {

	private List<ApprovalInfo> all;

	private Map<String, String> values;

	public List<ApprovalInfo> getAll() {
		return all;
	}

	public Map<String/*vote*/, String/*description*/> getValues() {
		return values;
	}
}
