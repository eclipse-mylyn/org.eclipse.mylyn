/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core;

import java.util.Date;

import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Kilian Matt
 */
public class ReviewSubTask {

	private String comment;
	private ITask task;
	private Date creationDate;

	public ReviewSubTask(String patchFile, Date creationDate, String author,
			String reviewer, Rating result, String comment, ITask task) {
		super();
		this.patchFile = patchFile;
		this.creationDate = new Date(creationDate.getTime());
		this.author = author;
		this.reviewer = reviewer;
		this.result = result;
		this.comment = comment;
		this.task = task;
	}

	private String patchFile;
	private String author;
	private String reviewer;
	private Rating result;

	public String getPatchFile() {
		return patchFile;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getPatchDescription() {
		return String.format("%s %s", patchFile, creationDate);
	}

	public String getAuthor() {
		return author;
	}

	public String getReviewer() {
		return reviewer;
	}

	public Rating getResult() {
		return result;
	}

	public String getComment() {
		return comment;
	}

	public ITask getTask() {
		return task;
	}

	@Override
	public String toString() {
		return "Review Subtask " + patchFile + " by " + author + " revd by "
				+ reviewer + " rated as " + result;
	}
}
