/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;
import org.eclipse.core.runtime.Assert;

/**
 * Enumeration of fields, which are indexed 
 * @author Kilian Matt
 *
 */
public enum IndexedFields {
	REVISION("revision", ChangesetPropertyAccess.REVISION), 
	REPOSITORY("repositoryUrl", ChangesetPropertyAccess.REPOSITORY),
	COMMIT_MESSAGE("message", ChangesetPropertyAccess.COMMIT_MESSAGE),
	;

	private final String indexKey;
	private final ChangesetPropertyAccess accessor;

	IndexedFields(String indexKey, ChangesetPropertyAccess accessor) {
		this.indexKey = indexKey;
		this.accessor = accessor;
		Assert.isNotNull(indexKey);
		Assert.isNotNull(accessor);
	}
	
	public String getIndexKey() {
		return indexKey;
	}
	
	public ChangesetPropertyAccess getAccessor() {
		return accessor;
	}

}