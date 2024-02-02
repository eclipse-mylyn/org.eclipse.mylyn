/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 ******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.List;

/**
 * Data model object for
 * <a href="https://gerrit-review.googlesource.com/Documentation/rest-api-changes.html#related-changes-info" >RelatedChangesInfo</a>.
 */
public class RelatedChangesInfo {

	private List<RelatedChangeAndCommitInfo> changes;

	public List<RelatedChangeAndCommitInfo> getCommitInfo() {
		return changes;
	}

}
