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

import java.util.Date;

import org.eclipse.mylar.provisional.tasklist.IRemoteContextDelegate;

/**
 * @author Rob Elves
 * TODO: Use of this delegate probably isn't necessary anymore
 */
public class RemoteContextDelegate implements IRemoteContextDelegate {

	private RepositoryAttachment attachment;
	
	public RemoteContextDelegate(RepositoryAttachment attachment) {
		this.attachment = attachment;
	}
	public Date getDate() {
		return attachment.getDateCreated();
	}

	public String getAuthor() {
		return attachment.getCreator();
	}

	public String getComment() {
		return attachment.getDescription();
	}

	public int getId() {
		return attachment.getId();
	}
		
}
