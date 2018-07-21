/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
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

	private String oldToolTip;

	private boolean restoreToolTip;

	// TODO e3.7 remove all references to delegate and replace with calls to super methods
	private final MultipleHyperlinkPresenter delegate;

	private boolean errorLogged;

	/**
	 * @see DefaultHyperlinkPresenter#DefaultHyperlinkPresenter(IPreferenceStore)
	 */
	public TaskHyperlinkPresenter(IPreferenceStore store) {
		super(store);
		delegate = new MultipleHyperlinkPresenter(store);
	}

	/**
	 * @see DefaultHyperlinkPresenter#DefaultHyperlinkPresenter(RGB)
	 */
	public TaskHyperlinkPresenter(RGB color) {
		super(color);
		delegate = new MultipleHyperlinkPresenter(color);
	}

	@Override
	public void install(ITextViewer textViewer) {
		this.textViewer = textViewer;
		delegate.install(textViewer);
	}

	@Override
	public void uninstall() {
		hideHyperlinks();
		this.textViewer = null;
		delegate.uninstall();
	}

	@Override
	public void applyTextPresentation(TextPresentation textPresentation) {
		delegate.applyTextPresentation(textPresentation);
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

	// TODO e3.7 remove method
	@Override
	public void showHyperlinks(IHyperlink[] hyperlinks) {
		showHyperlinks(hyperlinks, false);
	}

	// TODO e3.7 add @Override annotation
	@Override
	public void showHyperlinks(IHyperlink[] hyperlinks, boolean takesFocusWhenVisible) {
		activeRegion = null;
		// show task name in tooltip
		if (hyperlinks.length == 1 && hyperlinks[0] instanceof TaskHyperlink) {
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
					oldToolTip = textViewer.getTextWidget().getToolTipText();
					restoreToolTip = true;
					if (task == null) {
						String taskLabel = TasksUiInternal.getTaskPrefix(hyperlink.getRepository().getConnectorKind());
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

		// invoke super implementation

		try {
			// Eclipse 3.7
			Method method = MultipleHyperlinkPresenter.class.getDeclaredMethod("showHyperlinks", //$NON-NLS-1$
					IHyperlink[].class, boolean.class);
			method.invoke(delegate, hyperlinks, takesFocusWhenVisible);
		} catch (NoSuchMethodException e) {
			// Eclipse 3.6 and earlier
			delegate.showHyperlinks(hyperlinks);
		} catch (Exception e) {
			if (!errorLogged) {
				errorLogged = true;
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Unexpected error while displaying hyperlink", e)); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void hideHyperlinks() {
		if (currentTaskHyperlink != null) {
			if (restoreToolTip && textViewer != null && textViewer.getTextWidget() != null
					&& !textViewer.getTextWidget().isDisposed()) {
				textViewer.getTextWidget().setToolTipText(oldToolTip);
				restoreToolTip = false;
			}
			currentTaskHyperlink = null;
			currentTask = null;
		}
		delegate.hideHyperlinks();
	}

	@Override
	public boolean canHideHyperlinks() {
		return delegate.canHideHyperlinks();
	}

	@Override
	public boolean canShowMultipleHyperlinks() {
		return delegate.canShowMultipleHyperlinks();
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		delegate.documentAboutToBeChanged(event);
	}

	@Override
	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		delegate.inputDocumentAboutToBeChanged(oldInput, newInput);
	}

	@Override
	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		delegate.inputDocumentChanged(oldInput, newInput);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		delegate.propertyChange(event);
	}

	@Override
	public void setColor(Color color) {
		delegate.setColor(color);
	}

}
