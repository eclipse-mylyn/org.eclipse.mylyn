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

public class ConfigInfo {

	private String description;

	private InheritedBooleanInfo use_contributor_agreements;

	private InheritedBooleanInfo use_content_merge;

	private InheritedBooleanInfo use_signed_off_by;

	private SubmitTypeInfo default_submit_type;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the use_contributor_agreements
	 */
	public InheritedBooleanInfo getUse_contributor_agreements() {
		return use_contributor_agreements;
	}

	/**
	 * @param use_contributor_agreements
	 *            the use_contributor_agreements to set
	 */
	public void setUse_contributor_agreements(InheritedBooleanInfo use_contributor_agreements) {
		this.use_contributor_agreements = use_contributor_agreements;
	}

	/**
	 * @return the use_content_merge
	 */
	public InheritedBooleanInfo getUse_content_merge() {
		return use_content_merge;
	}

	/**
	 * @param use_content_merge
	 *            the use_content_merge to set
	 */
	public void setUse_content_merge(InheritedBooleanInfo use_content_merge) {
		this.use_content_merge = use_content_merge;
	}

	/**
	 * @return the use_signed_off_by
	 */
	public InheritedBooleanInfo getUse_signed_off_by() {
		return use_signed_off_by;
	}

	/**
	 * @param use_signed_off_by
	 *            the use_signed_off_by to set
	 */
	public void setUse_signed_off_by(InheritedBooleanInfo use_signed_off_by) {
		this.use_signed_off_by = use_signed_off_by;
	}

	/**
	 * @return the default_submit_type
	 */
	public SubmitTypeInfo getDefault_submit_type() {
		return default_submit_type;
	}

	/**
	 * @param default_submit_type
	 *            the default_submit_type to set
	 */
	public void setDefault_submit_type(SubmitTypeInfo default_submit_type) {
		this.default_submit_type = default_submit_type;
	}
}
