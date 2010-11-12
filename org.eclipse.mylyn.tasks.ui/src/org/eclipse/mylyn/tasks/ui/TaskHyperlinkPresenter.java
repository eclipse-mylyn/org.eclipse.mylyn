/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;

/**
 * A hyperlink presenter that displays a tooltip when hovering over a {@link TaskHyperlink}.
 * 
 * @author Steffen Pingel
 * @author Frank Becker
 * @since 3.1
 */
public final class TaskHyperlinkPresenter extends MultipleHyperlinkPresenter {

	private IRegion activeRegion;

	/**
	 * Stores which task a tooltip is being displayed for. It is used to avoid having the same tooltip being set
	 * multiple times while you move the mouse over a task hyperlink (bug#209409)
	 */
	private ITask currentTask;

	private TaskHyperlink currentTaskHyperlink;

	private ITextViewer textViewer;

	/**
	 * @see DefaultHyperlinkPresenter#DefaultHyperlinkPresenter(IPreferenceStore)
	 */
	public TaskHyperlinkPresenter(IPreferenceStore store) {
		super(store);
	}

	/**
	 * @see DefaultHyperlinkPresenter#DefaultHyperlinkPresenter(RGB)
	 */
	public TaskHyperlinkPresenter(RGB color) {
		super(color);
	}

	@Override
	public void install(ITextViewer textViewer) {
		this.textViewer = textViewer;
		super.install(textViewer);
	}

	@Override
	public void uninstall() {
		hideHyperlinks();
		this.textViewer = null;
		super.uninstall();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void applyTextPresentation(TextPresentation textPresentation) {
		super.applyTextPresentation(textPresentation);
		// decorate hyperlink as strike-through if task is completed, this is now also handled by TaskHyperlinkTextPresentationManager
		if (activeRegion != null && currentTask != null && currentTask.isCompleted()) {
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
		if (hyperlinks.length > 1) {
			super.showHyperlinks(hyperlinks);
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

				if (!hyperlinks[0].equals(currentTaskHyperlink)) {
					currentTaskHyperlink = (TaskHyperlink) hyperlinks[0];
					currentTask = task;
					activeRegion = hyperlink.getHyperlinkRegion();
					if (textViewer != null && textViewer.getTextWidget() != null
							&& !textViewer.getTextWidget().isDisposed()) {
						if (task == null) {
							String taskLabel = TasksUiInternal.getTaskPrefix(hyperlink.getRepository()
									.getConnectorKind());
							taskLabel += currentTaskHyperlink.getTaskId();
							textViewer.getTextWidget().setToolTipText(
									NLS.bind(Messages.TaskHyperlinkPresenter_Not_In_Task_List, taskLabel));
						} else if (task.getTaskKey() == null) {
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
			currentTask = null;
		}
		super.hideHyperlinks();
	}

}
