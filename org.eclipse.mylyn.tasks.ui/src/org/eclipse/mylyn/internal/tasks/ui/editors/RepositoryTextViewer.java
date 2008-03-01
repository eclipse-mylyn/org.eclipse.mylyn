/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Rob Elves
 */
public class RepositoryTextViewer extends SourceViewer {

	private final MenuManager menuManager;

	private TaskRepository repository;

	public RepositoryTextViewer(IVerticalRuler vertRuler, IOverviewRuler overRuler, TaskRepository repository,
			Composite composite, int style) {
		super(composite, vertRuler, overRuler, true, style);
		this.menuManager = new MenuManager();
		this.repository = repository;

	}

	public RepositoryTextViewer(TaskRepository repository, Composite composite, int style) {// FormEditor
		super(composite, null, style);
		this.menuManager = new MenuManager();
		this.repository = repository;
	}

	@Override
	public void setDocument(IDocument doc) {
		if (doc != null && this.getAnnotationModel() != null) {
			this.getAnnotationModel().connect(doc);
			super.setDocument(doc, this.getAnnotationModel());
		} else {
			super.setDocument(doc);
		}
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	@Override
	protected void handleDispose() {
		menuManager.dispose();
		super.handleDispose();
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenu(Menu menu) {
		if (getTextWidget() != null && !getTextWidget().isDisposed()) {
			getTextWidget().setMenu(menu);
		}
	}
}
