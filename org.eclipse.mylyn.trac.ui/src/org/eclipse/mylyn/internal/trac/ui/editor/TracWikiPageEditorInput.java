/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Xiaoyang Guan
 */
public class TracWikiPageEditorInput implements IEditorInput {

	private TaskRepository repository;

	private TracWikiPage page;

	private String pageUrl;

	public TracWikiPageEditorInput(TaskRepository repository, TracWikiPage page, String pageUrl) {
		this.repository = repository;
		this.page = page;
		this.pageUrl = pageUrl;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	public TracWikiPage getPage() {
		return page;
	}

	public void setPage(TracWikiPage page) {
		this.page = page;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public boolean exists() {
		// ignore
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// ignore
		return null;
	}

	public String getName() {
		return page.getPageInfo().getPageName();
	}

	public IPersistableElement getPersistable() {
		// ignore
		return null;
	}

	public String getToolTipText() {
		return getName() + "  [" + repository.getRepositoryLabel() + "]";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IEditorInput.class) {
			return this;
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (this == obj) {
			return true;
		} else if (getClass() != obj.getClass()) {
			return false;
		} else {
			TracWikiPageEditorInput other = (TracWikiPageEditorInput) obj;
			return repository.equals(other.repository) && page.equals(other.page);
		}
	}

}
