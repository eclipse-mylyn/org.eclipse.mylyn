/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.lang.reflect.Constructor;
import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;

/**
 * A hyperlink presenter that displays a tooltip when hovering over a {@link TaskHyperlink}.
 * 
 * @author Steffen Pingel
 * @author Frank Becker
 * @since 3.1
 */
public final class TaskHyperlinkPresenter extends DefaultHyperlinkPresenter {

	private IRegion activeRegion;

	/**
	 * Stores which task a tooltip is being displayed for. It is used to avoid having the same tooltip being set
	 * multiple times while you move the mouse over a task hyperlink (bug#209409)
	 */
	private ITask currentTaskHyperlink;

	private ITextViewer textViewer;

	private IHyperlinkPresenter multiplePresenter;

	/**
	 * @see DefaultHyperlinkPresenter#DefaultHyperlinkPresenter(IPreferenceStore)
	 */
	public TaskHyperlinkPresenter(IPreferenceStore store) {
		super(store);
		initMultipleHyperlinkSupport(IPreferenceStore.class, store);
	}

	/**
	 * @see DefaultHyperlinkPresenter#DefaultHyperlinkPresenter(RGB)
	 */
	public TaskHyperlinkPresenter(RGB color) {
		super(color);
		initMultipleHyperlinkSupport(RGB.class, color);
	}

	@Override
	public boolean canShowMultipleHyperlinks() {
		return multiplePresenter != null;
	}

	// TODO e3.4 remove reflection
	@SuppressWarnings("unchecked")
	private <T> void initMultipleHyperlinkSupport(Class<T> argClass, T arg) {
		try {
			Class<IHyperlinkPresenter> clazz = (Class<IHyperlinkPresenter>) Class.forName("org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter"); //$NON-NLS-1$
			Constructor<IHyperlinkPresenter> constructor = clazz.getDeclaredConstructor(argClass);
			multiplePresenter = constructor.newInstance(arg);
		} catch (Throwable t) {
			// ignore
		}
	}

	@Override
	public void install(ITextViewer textViewer) {
		this.textViewer = textViewer;
		super.install(textViewer);
		if (multiplePresenter != null) {
			multiplePresenter.install(textViewer);
		}
	}

	@Override
	public void uninstall() {
		hideHyperlinks();
		this.textViewer = null;
		super.uninstall();
		if (multiplePresenter != null) {
			multiplePresenter.uninstall();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void applyTextPresentation(TextPresentation textPresentation) {
		super.applyTextPresentation(textPresentation);
		// decorate hyperlink as strike-through if task is completed, this is now also handled by TaskHyperlinkTextPresentationManager
		if (activeRegion != null && currentTaskHyperlink != null && currentTaskHyperlink.isCompleted()) {
			Iterator<StyleRange> styleRangeIterator = textPresentation.getAllStyleRangeIterator();
			while (styleRangeIterator.hasNext()) {
				StyleRange styleRange = styleRangeIterator.next();
				if (activeRegion.getOffset() == styleRange.start && activeRegion.getLength() == styleRange.length) {
					styleRange.strikeout = true;
					break;
				}
			}
		}
	}

	@Override
	public void showHyperlinks(IHyperlink[] hyperlinks) {
		if (hyperlinks.length > 1 && multiplePresenter != null) {
			multiplePresenter.showHyperlinks(hyperlinks);
		} else {
			activeRegion = null;
			if (hyperlinks.length > 0 && hyperlinks[0] instanceof TaskHyperlink) {
				TaskHyperlink hyperlink = (TaskHyperlink) hyperlinks[0];

				TaskList taskList = TasksUiPlugin.getTaskList();
				String repositoryUrl = hyperlink.getRepository().getRepositoryUrl();

				ITask task = taskList.getTask(repositoryUrl, hyperlink.getTaskId());
				if (task == null) {
					task = taskList.getTaskByKey(repositoryUrl, hyperlink.getTaskId());
				}

				if (task != null && task != currentTaskHyperlink) {
					currentTaskHyperlink = task;
					activeRegion = hyperlink.getHyperlinkRegion();
					if (textViewer != null && textViewer.getTextWidget() != null
							&& !textViewer.getTextWidget().isDisposed()) {
						if (task.getTaskKey() == null) {
							textViewer.getTextWidget().setToolTipText(task.getSummary());
						} else {
							textViewer.getTextWidget().setToolTipText(task.getTaskKey() + ": " + task.getSummary()); //$NON-NLS-1$
						}
					}
				}
			}
			super.showHyperlinks(hyperlinks);
		}
	}

	@Override
	public void hideHyperlinks() {
		if (currentTaskHyperlink != null) {
			if (textViewer != null && textViewer.getTextWidget() != null && !textViewer.getTextWidget().isDisposed()) {
				textViewer.getTextWidget().setToolTipText(null);
			}
			currentTaskHyperlink = null;
		}
		if (multiplePresenter != null) {
			multiplePresenter.hideHyperlinks();
		}
		super.hideHyperlinks();
	}

}
