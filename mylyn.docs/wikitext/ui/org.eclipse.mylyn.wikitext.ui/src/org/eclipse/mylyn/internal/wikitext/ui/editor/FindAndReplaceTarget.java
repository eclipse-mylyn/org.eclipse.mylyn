/*******************************************************************************
 * Copyright (c) 2011, 2021 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor;

import java.util.Stack;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * Implementation based on {@link org.eclipse.ui.texteditor.IncrementalFindTarget}
 *
 * @author David Green
 */
public class FindAndReplaceTarget implements IFindReplaceTarget, IFindReplaceTargetExtension, VerifyKeyListener,
		MouseListener, FocusListener, ISelectionChangedListener, ITextListener, IExecutionListener {

	private final ITextViewer textViewer;

	private final IFindReplaceTarget findReplaceTarget;

	private final IFindReplaceTargetExtension findReplaceTargetExtension;

	private boolean installed;

	private boolean searching;

	private final Stack<Result> state = new Stack<>();

	private String findString = ""; //$NON-NLS-1$

	private int index = 0;

	private boolean findHit = true;

	private String previousFindString = ""; //$NON-NLS-1$

	private static final class Result {

		private final Point selection;

		private final String findString;

		private final int index;

		private final boolean findHit;

		public Result(Point selection, String findString, int index, boolean findHit) {
			this.findString = findString;
			this.index = index;
			this.selection = new Point(selection.x, selection.y);
			this.findHit = findHit;
		}

		@Override
		public String toString() {
			return "Result [selection=" + selection + ", findString=" + findString + ", index=" + index + ", findHit=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					+ findHit + "]"; //$NON-NLS-1$
		}

	}

	public FindAndReplaceTarget(ITextViewer textViewer) {
		this.textViewer = textViewer;
		findReplaceTarget = textViewer.getFindReplaceTarget();
		if (findReplaceTarget instanceof IFindReplaceTargetExtension frte) {
			findReplaceTargetExtension = frte;
		} else {
			findReplaceTargetExtension = null;
		}
	}

	@Override
	public boolean canPerformFind() {
		return findReplaceTarget.canPerformFind();
	}

	@Override
	public int findAndSelect(int widgetOffset, String findString, boolean searchForward, boolean caseSensitive,
			boolean wholeWord) {
		return findReplaceTarget.findAndSelect(widgetOffset, findString, searchForward, caseSensitive, wholeWord);
	}

	@Override
	public Point getSelection() {
		return findReplaceTarget.getSelection();
	}

	@Override
	public String getSelectionText() {
		return findReplaceTarget.getSelectionText();
	}

	@Override
	public boolean isEditable() {
		return findReplaceTarget.isEditable();
	}

	@Override
	public void replaceSelection(String text) {
		findReplaceTarget.replaceSelection(text);
	}

	@Override
	public void beginSession() {
		if (installed) {
			next();
			return;
		}
		clearState();

		index = 0;
		findHit = true;
		findString = ""; //$NON-NLS-1$

		StyledText textWidget = textViewer.getTextWidget();
		if (textWidget != null && !textWidget.isDisposed()) {
			index = textWidget.getCaretOffset();
			textWidget.setSelection(index);
		}

		install();

		if (findReplaceTargetExtension != null) {
			findReplaceTargetExtension.beginSession();
		}
	}

	private void next() {
		if (findHit) {
			saveState();
		}
		repeatSearch();
	}

	private void saveState() {
		Result newState = new Result(new Point(index, index + findString.length()), findString, index, findHit);

		state.push(newState);
	}

	private void clearState() {
		state.clear();
	}

	private void restoreState() {
		StyledText textWidget = textViewer.getTextWidget();
		if (textWidget == null || textWidget.isDisposed()) {
			return;
		}

		Result result = null;
		if (!state.isEmpty()) {
			result = state.pop();
		}

		if (result == null) {
			textWidget.getDisplay().beep();
			return;
		}

		findString = result.findString;
		index = result.index;
		findHit = result.findHit;

		textWidget.setSelection(result.selection);
		textWidget.showSelection();
	}

	private void addSearchCharacter(char character) {
		findString += character;

		doFind();
	}

	private void repeatSearch() {
		if (findString.length() == 0) {
			findString = previousFindString;
		}
		if (findString.length() == 0) {
			findHit = true;
			return;
		}
		// check for wrap search
		if (!findHit) {
			index = 0;
		} else {
			StyledText textWidget = textViewer.getTextWidget();
			index = textWidget.getCaretOffset();
		}
		doFind();
	}

	private void doFind() {

		boolean hasUpperCase = false;
		for (int x = 0; x < findString.length(); ++x) {
			char c = findString.charAt(x);
			if (Character.isUpperCase(c) && Character.toLowerCase(c) != c) {
				hasUpperCase = true;
				break;
			}
		}

		searching = true;

		StyledText textWidget = textViewer.getTextWidget();

		textWidget.setRedraw(false);

		int foundIndex = findReplaceTarget.findAndSelect(index, findString, true, hasUpperCase, false);

		textWidget.setRedraw(true);

		boolean findHit = foundIndex >= 0;

		if (findHit) {
			index = foundIndex;
		} else if (this.findHit) {
			textWidget.getDisplay().beep();
		}
		this.findHit = findHit;

		searching = false;
	}

	@Override
	public void endSession() {
		if (findReplaceTargetExtension != null) {
			findReplaceTargetExtension.endSession();
		}
	}

	@Override
	public IRegion getScope() {
		if (findReplaceTargetExtension != null) {
			return findReplaceTargetExtension.getScope();
		}
		return null;
	}

	@Override
	public void setScope(IRegion scope) {
		if (findReplaceTargetExtension != null) {
			findReplaceTargetExtension.setScope(scope);
		}
	}

	@Override
	public Point getLineSelection() {
		if (findReplaceTargetExtension != null) {
			return findReplaceTargetExtension.getLineSelection();
		}
		return null;
	}

	@Override
	public void setSelection(int offset, int length) {

		if (findReplaceTargetExtension != null) {
			findReplaceTargetExtension.setSelection(offset, length);
		}
	}

	@Override
	public void setScopeHighlightColor(Color color) {
		if (findReplaceTargetExtension != null) {
			findReplaceTargetExtension.setScopeHighlightColor(color);
		}
	}

	@Override
	public void setReplaceAllMode(boolean replaceAll) {
		if (findReplaceTargetExtension != null) {
			findReplaceTargetExtension.setReplaceAllMode(replaceAll);
		}
	}

	private void install() {
		if (installed) {
			return;
		}

		StyledText textWidget = textViewer.getTextWidget();
		if (textWidget == null) {
			return;
		}
		textViewer.addTextListener(this);
		textWidget.addMouseListener(this);
		textWidget.addFocusListener(this);

		ISelectionProvider selectionProvider = textViewer.getSelectionProvider();
		if (selectionProvider != null) {
			selectionProvider.addSelectionChangedListener(this);
		}

		if (textViewer instanceof ITextViewerExtension tve) {
			tve.prependVerifyKeyListener(this);
		} else {
			textWidget.addVerifyKeyListener(this);
		}

		ICommandService commandService = PlatformUI.getWorkbench().getAdapter(ICommandService.class);
		if (commandService != null) {
			commandService.addExecutionListener(this);
		}

		installed = true;
	}

	private void stop() {
		if (findString.length() > 0) {
			previousFindString = findString;
			findString = ""; //$NON-NLS-1$
		}
		index = 0;
		findHit = true;
		clearState();

		uninstall();
	}

	private void uninstall() {
		if (!installed) {
			return;
		}

		StyledText textWidget = textViewer.getTextWidget();
		if (textWidget == null) {
			return;
		}

		textViewer.removeTextListener(this);
		textWidget.removeMouseListener(this);
		textWidget.removeFocusListener(this);

		ISelectionProvider selectionProvider = textViewer.getSelectionProvider();
		if (selectionProvider != null) {
			selectionProvider.removeSelectionChangedListener(this);
		}

		if (textViewer instanceof ITextViewerExtension tve) {
			tve.removeVerifyKeyListener(this);
		} else {
			textWidget.removeVerifyKeyListener(this);
		}

		ICommandService commandService = PlatformUI.getWorkbench().getAdapter(ICommandService.class);
		if (commandService != null) {
			commandService.removeExecutionListener(this);
		}
		installed = false;
	}

	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// ignore
	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
		// ignore
	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
		// ignore
	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		if (!IWorkbenchActionDefinitionIds.FIND_INCREMENTAL.equals(commandId)) {
			stop();
		}
	}

	@Override
	public void textChanged(TextEvent event) {
		if (event.getDocumentEvent() != null) {
			stop();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		stop();
	}

	@Override
	public void focusLost(FocusEvent e) {
		stop();
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		stop();
	}

	@Override
	public void mouseDown(MouseEvent e) {
		stop();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		stop();
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		if (!event.doit) {
			return;
		}
		if (event.character == 0) {
			switch (event.keyCode) {
				case SWT.ARROW_LEFT:
				case SWT.ARROW_RIGHT:
				case SWT.ARROW_UP:
				case SWT.HOME:
				case SWT.END:
				case SWT.PAGE_DOWN:
				case SWT.PAGE_UP:
					stop();
					break;
				case SWT.ARROW_DOWN:
					next();
					event.doit = false;
					break;
			}
		} else {
			switch (event.character) {
				case SWT.ESC:
				case SWT.CR:
					stop();
					event.doit = false;
					break;
				case SWT.BS:
				case SWT.DEL:
					restoreState();
					event.doit = false;
					break;

				default:
					if (event.stateMask == 0 || event.stateMask == SWT.SHIFT) {
						saveState();
						addSearchCharacter(event.character);
						event.doit = false;
					}
			}
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (!searching) {
			stop();
		}
	}
}
