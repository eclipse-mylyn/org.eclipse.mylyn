/*******************************************************************************
 * Copyright (c) 2019 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.ArrayList;
import java.util.List;

import com.google.gerrit.reviewdb.Patch.ChangeType;

public class DiffInfo {

	private DiffFileMetaInfo meta_a;

	private DiffFileMetaInfo meta_b;

	private ArrayList<DiffContent> content;

	private ChangeType change_type;

	private boolean intraline_status;

	private List<String> diff_header;

	public DiffFileMetaInfo getMeta_a() {
		return meta_a;
	}

	public void setMeta_a(DiffFileMetaInfo meta_a) {
		this.meta_a = meta_a;
	}

	public DiffFileMetaInfo getMeta_b() {
		return meta_b;
	}

	public void setMeta_b(DiffFileMetaInfo meta_b) {
		this.meta_b = meta_b;
	}

	public boolean isIntraline_status() {
		return intraline_status;
	}

	public void setIntraline_status(boolean intraline_status) {
		this.intraline_status = intraline_status;
	}

	public List<String> getDiff_header() {
		return diff_header;
	}

	public void setDiff_header(List<String> diff_header) {
		this.diff_header = diff_header;
	}

	public ChangeType getChange_type() {
		return change_type;
	}

	public void setChange_type(ChangeType change_type) {
		this.change_type = change_type;
	}

	public ArrayList<DiffContent> getContent() {
		return content;
	}

	public void setContent(ArrayList<DiffContent> content) {
		this.content = content;
	}

}
