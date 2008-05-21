/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class TaskEditorPartDescriptor {

	private final String id;

	private String path;

//	public AbstractTaskEditorPart createPart() {
//		final AbstractTaskEditorPart[] result = new AbstractTaskEditorPart[1];
//		SafeRunnable.run(new ISafeRunnable() {
//
//			public void handleException(Throwable exception) {
//				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
//						"Unable to create instance of class \"" + getClassName() + "\" for task editor part \""
//								+ getId() + "\""));
//			}
//
//			public void run() throws Exception {
//				Class<?> clazz = Class.forName(getClassName());
//				result[0] = (AbstractTaskEditorPart) clazz.newInstance();
//			}
//
//		});
//		return result[0];
//	}

	public TaskEditorPartDescriptor(String id) {
		Assert.isNotNull(id);
		this.id = id;
	}

	public abstract AbstractTaskEditorPart createPart();

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskEditorPartDescriptor other = (TaskEditorPartDescriptor) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public TaskEditorPartDescriptor setPath(String path) {
		this.path = path;
		return this;
	}

}
