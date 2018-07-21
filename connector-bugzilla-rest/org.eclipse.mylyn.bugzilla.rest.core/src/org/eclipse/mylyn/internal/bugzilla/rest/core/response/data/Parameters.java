/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution; private String and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core.response.data;

import java.io.Serializable;

public class Parameters implements Serializable {

	private static final long serialVersionUID = 2401238027314515202L;

	private String allowemailchange;

	private String attachment_base;

	private String commentonchange_resolution;

	private String commentonduplicate;

	private String cookiepath;

	private String defaultopsys;

	private String defaultplatform;

	private String defaultpriority;

	private String defaultseverity;

	private String duplicate_or_move_bug_status;

	private String emailregexpdesc;

	private String emailsuffix;

	private String letsubmitterchoosemilestone;

	private String letsubmitterchoosepriority;

	private String mailfrom;

	private String maintainer;

	private String maxattachmentsize;

	private String maxlocalattachment;

	private String musthavemilestoneonaccept;

	private String noresolveonopenblockers;

	private String password_complexity;

	private String rememberlogin;

	private String requirelogin;

	private String search_allow_no_criteria;

	private String urlbase;

	private String use_see_also;

	private String useclassification;

	private String usemenuforusers;

	private String useqacontact;

	private String usestatuswhiteboard;

	private String usetargetmilestone;

	public String getAllowemailchange() {
		return allowemailchange;
	}

	public String getAttachmentBase() {
		return attachment_base;
	}

	public String getCommentonchangeResolution() {
		return commentonchange_resolution;
	}

	public String getCommentonduplicate() {
		return commentonduplicate;
	}

	public String getCookiepath() {
		return cookiepath;
	}

	public String getDefaultopsys() {
		return defaultopsys;
	}

	public String getDefaultplatform() {
		return defaultplatform;
	}

	public String getDefaultpriority() {
		return defaultpriority;
	}

	public String getDefaultseverity() {
		return defaultseverity;
	}

	public String getDuplicateOrMoveBugStatus() {
		return duplicate_or_move_bug_status;
	}

	public String getEmailregexpdesc() {
		return emailregexpdesc;
	}

	public String getEmailsuffix() {
		return emailsuffix;
	}

	public String getLetsubmitterchoosemilestone() {
		return letsubmitterchoosemilestone;
	}

	public String getLetsubmitterchoosepriority() {
		return letsubmitterchoosepriority;
	}

	public String getMailfrom() {
		return mailfrom;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public String getMaxattachmentsize() {
		return maxattachmentsize;
	}

	public String getMaxlocalattachment() {
		return maxlocalattachment;
	}

	public String getMusthavemilestoneonaccept() {
		return musthavemilestoneonaccept;
	}

	public String getNoresolveonopenblockers() {
		return noresolveonopenblockers;
	}

	public String getPasswordComplexity() {
		return password_complexity;
	}

	public String getRememberlogin() {
		return rememberlogin;
	}

	public String getRequirelogin() {
		return requirelogin;
	}

	public String getSearchAllowNoCriteria() {
		return search_allow_no_criteria;
	}

	public String getUrlbase() {
		return urlbase;
	}

	public String getUseSeeAlso() {
		return use_see_also;
	}

	public String getUseclassification() {
		return useclassification;
	}

	public String getUsemenuforusers() {
		return usemenuforusers;
	}

	public String getUseqacontact() {
		return useqacontact;
	}

	public String getUsestatuswhiteboard() {
		return usestatuswhiteboard;
	}

	public String getUsetargetmilestone() {
		return usetargetmilestone;
	}

}
