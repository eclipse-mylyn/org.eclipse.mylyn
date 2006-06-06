/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * TODO: Make generic. This currently represents Bugzilla attachments only
 * @author Rob Elves
 */
public class RepositoryAttachment extends AttributeContainer implements Serializable {

	public RepositoryAttachment(AbstractAttributeFactory attributeFactory) {
		super(attributeFactory);		
	}

	private static final long serialVersionUID = -9123545810321250785L;
	
	/** Parser for dates in the report */
	private static SimpleDateFormat creation_ts_date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private boolean isObsolete = false;

	private Date created;
	
	private String creator = "";
	
	public boolean isObsolete() {
		return isObsolete;
	}

	public void setObsolete(boolean isObsolete) {
		this.isObsolete = isObsolete;
	}
	

	/** 
	 * may return null if date is unknown/unparseable
	 */
	public Date getDateCreated() {
		if(created == null) {
			//created = Calendar.getInstance().getTime();
			try {
				created = creation_ts_date_format.parse(getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_DATE));
			} catch (Exception e) {
			}			
		}
		return created;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;		
	}
	public String getDescription() {
		//System.err.println(getAttributeValue(BugzillaReportElement.DESC));
		return getAttributeValue(RepositoryTaskAttribute.DESCRIPTION);
	}

	public int getId() {
		try {
			return Integer.parseInt(getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_ID));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public String getContentType() {
		// Eclipse.org uses "ctype", others use "type"
		if(getAttribute(RepositoryTaskAttribute.ATTACHMENT_TYPE) != null) {
			return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_TYPE);
		} else {
			return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_CTYPE);
		}
	}
	
}
