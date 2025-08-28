/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.reviewdb.Account;

//import org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo;

public class SubmitRecord {

	public class Label {
		private String label;

		private String status;

		private Account.Id appliedBy;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Account.Id getAppliedBy() {
			return appliedBy;
		}

		public void setAppliedBy(Account.Id appliedBy) {
			this.appliedBy = appliedBy;
		}
	}

	String status;

	private HashMap<String, AccountInfo> ok;

	private HashMap<String, AccountInfo> reject;

	private HashMap<String, AccountInfo> need;

	private HashMap<String, AccountInfo> may;

	private List<Label> labels = Collections.emptyList();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public HashMap<String, AccountInfo> getOkMap() {
		return ok;
	}

	public HashMap<String, AccountInfo> getNeedMap() {
		return need;
	}

	public HashMap<String, AccountInfo> getRejectMap() {
		return reject;
	}

	public HashMap<String, AccountInfo> getMayMap() {
		return may;
	}

	public List<Label> createLabel(SubmitRecord record, HashMap<String, AccountInfo> value, String status) {
		List<Label> list = new ArrayList<>();
		if (value != null) {
			for (Map.Entry<String, AccountInfo> info : value.entrySet()) {
				Label label = new Label();
				label.setLabel(info.getKey());
				label.setStatus(status);
				if (info.getValue().getId() != null) {
					label.setAppliedBy(info.getValue().getId());
				}
				list.add(label);
			}
		}
		return list;
	}

}
