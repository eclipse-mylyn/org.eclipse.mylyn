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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

/**
 * @author mattk
 *
 */
public class ReviewResult {
	private String reviewer;
	private Rating rating;
	private String comment;
	private Date date;
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public Date getDate() {
		return date!=null?new Date(date.getTime()):null;
	}

	public void setDate(Date date) {
		Date old = this.date;
		this.date = date!=null?new Date( date.getTime()):null;
		changeSupport.firePropertyChange("date", old, date);
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		String old = this.reviewer;
		this.reviewer = reviewer;
		changeSupport.firePropertyChange("reviewer", old, reviewer);
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		Rating old = this.rating;
		this.rating = rating;
		changeSupport.firePropertyChange("rating", old, rating);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		String old = this.comment;
		this.comment = comment;
		changeSupport.firePropertyChange("comment", old, comment);
	}

}
