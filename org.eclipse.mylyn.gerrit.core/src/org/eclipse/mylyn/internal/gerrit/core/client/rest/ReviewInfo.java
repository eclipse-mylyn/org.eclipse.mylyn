/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.Map;

/**
 * Data model object for https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#review-info
 */
public class ReviewInfo {

	private Map<String, Short> labels;

	public Map<String, Short> getLabels() {
		return labels;
	}

}
