/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import java.io.File;
import java.util.Set;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

abstract class ImageFetchingStrategy {

	private Task task;

	protected File dest;

	/**
	 * Fetch images
	 * 
	 * @return the set of images filenames that were retrieved
	 */
	public abstract Set<String> fetchImages();

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Location getLocation() {
		return task.getLocation();
	}

	public Project getProject() {
		return task.getProject();
	}

	public void log(String msg, int msgLevel) {
		task.log(msg, msgLevel);
	}

	public void log(String msg, Throwable t, int msgLevel) {
		task.log(msg, t, msgLevel);
	}

	public void log(String msg) {
		task.log(msg);
	}

	public void log(Throwable t, int msgLevel) {
		task.log(t, msgLevel);
	}

	public File getDest() {
		return dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

}
