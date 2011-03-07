/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.dsl;

/**
 * 
 * @author mattk
 * 
 */
public class ReviewDslChangesetScopeItem extends ReviewDslScopeItem {

	private String revision;
	private String repoUrl;

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	@Override
	public StringBuilder serialize(StringBuilder sb) {
		sb.append("Changeset \"");
		sb.append(revision);
		sb.append("\" from \"");
		sb.append(repoUrl);
		sb.append("\"");
		return sb;
	}

}
