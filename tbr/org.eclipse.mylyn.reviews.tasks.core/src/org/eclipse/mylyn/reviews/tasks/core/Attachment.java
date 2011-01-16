/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core;

/**
 * 
 * @author mattk
 * 
 */
public class Attachment {

	private String fileName;
	private String author;
	private String date;
	private String url;
	private boolean isPatch;
	private ITaskProperties task;

	public Attachment(ITaskProperties task, String fileName, String author,
			String date, boolean isPatch, String url) {
		this.task = task;
		this.fileName = fileName;
		this.author = author;
		this.date = date;
		this.isPatch = isPatch;
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public String getUrl() {
		return url;
	}

	public ITaskProperties getTask() {
		return task;
	}

	public boolean isPatch() {
		return isPatch;
	}

}
