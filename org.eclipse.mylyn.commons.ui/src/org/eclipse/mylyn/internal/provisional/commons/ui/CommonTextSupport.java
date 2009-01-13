/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.Iterator;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

/**
 * Utility class that enables edit actions, content assist and quick fixing for {@link TextViewer} and
 * {@link SourceViewer} controls.
 * 
 * @author Steffen Pingel
 */
public class CommonTextSupport {

	private class TextViewerFocusListener implements FocusListener {

		private final boolean spellCheck;

		private final TextViewer viewer;

		public TextViewerFocusListener(TextViewer viewer, boolean spellCheck) {
			this.viewer = viewer;
			this.spellCheck = spellCheck;
		}

		public void focusGained(FocusEvent e) {
			if (selectionChangedListener != null) {
				selectionChangedListener.selectionChanged(new SelectionChangedEvent(viewer, viewer.getSelection()));
			}
			activateHandlers(viewer, spellCheck);
		}

		public void focusLost(FocusEvent e) {
			deactivateHandlers();
			if (selectionChangedListener != null) {
				// make sure selection no text is selected when control looses focus
				StyledText st = (StyledText) e.widget;
				st.setSelectionRange(st.getCaretOffset(), 0);
				// update action enablement
				selectionChangedListener.selectionChanged(new SelectionChangedEvent(viewer, StructuredSelection.EMPTY));
			}
		}
	}

	private static final String KEY_TEXT_VIEWER = "textViewer"; //$NON-NLS-1$

	private static boolean canDoGlobalAction(String actionId, TextViewer textViewer) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.CUT);
		} else if (actionId.equals(ActionFactory.COPY.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.COPY);
		} else if (actionId.equals(ActionFactory.PASTE.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.PASTE);
		} else if (actionId.equals(ActionFactory.DELETE.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.DELETE);
		} else if (actionId.equals(ActionFactory.UNDO.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.UNDO);
		} else if (actionId.equals(ActionFactory.REDO.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.REDO);
		} else if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			return textViewer.canDoOperation(ITextOperationTarget.SELECT_ALL);
		}
		return false;
	}

	public static boolean canPerformAction(String actionId, Control focusControl) {
		TextViewer viewer = getTextViewer(focusControl);
		if (viewer != null) {
			return canDoGlobalAction(actionId, viewer);
		}
		if (actionId.equals(ActionFactory.UNDO.getId()) || actionId.equals(ActionFactory.REDO.getId())) {
			return false;
		}
		return true;
	}

	private static boolean canPerformDirectly(String id, Control control) {
		if (control instanceof Text) {
			Text text = (Text) control;
			if (id.equals(ActionFactory.CUT.getId())) {
				text.cut();
				return true;
			}
			if (id.equals(ActionFactory.COPY.getId())) {
				text.copy();
				return true;
			}
			if (id.equals(ActionFactory.PASTE.getId())) {
				text.paste();
				return true;
			}
			if (id.equals(ActionFactory.SELECT_ALL.getId())) {
				text.selectAll();
				return true;
			}
			if (id.equals(ActionFactory.DELETE.getId())) {
				int count = text.getSelectionCount();
				if (count == 0) {
					int caretPos = text.getCaretPosition();
					text.setSelection(caretPos, caretPos + 1);
				}
				text.insert(""); //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	public static void doAction(String actionId, Control focusControl) {
		if (canPerformDirectly(actionId, focusControl)) {
			return;
		}
		TextViewer viewer = getTextViewer(focusControl);
		if (viewer != null) {
			doGlobalAction(actionId, viewer);
		}
	}

	private static boolean doGlobalAction(String actionId, TextViewer textViewer) {
		if (actionId.equals(ActionFactory.CUT.getId())) {
			textViewer.doOperation(ITextOperationTarget.CUT);
			return true;
		} else if (actionId.equals(ActionFactory.COPY.getId())) {
			textViewer.doOperation(ITextOperationTarget.COPY);
			return true;
		} else if (actionId.equals(ActionFactory.PASTE.getId())) {
			textViewer.doOperation(ITextOperationTarget.PASTE);
			return true;
		} else if (actionId.equals(ActionFactory.DELETE.getId())) {
			textViewer.doOperation(ITextOperationTarget.DELETE);
			return true;
		} else if (actionId.equals(ActionFactory.UNDO.getId())) {
			textViewer.doOperation(ITextOperationTarget.UNDO);
			return true;
		} else if (actionId.equals(ActionFactory.REDO.getId())) {
			textViewer.doOperation(ITextOperationTarget.REDO);
			return true;
		} else if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
			textViewer.doOperation(ITextOperationTarget.SELECT_ALL);
			return true;
		}
		return false;
	}

	public static TextViewer getTextViewer(Widget widget) {
		if (widget instanceof StyledText) {
			Object data = widget.getData(KEY_TEXT_VIEWER);
			if (data instanceof TextViewer) {
				return (TextViewer) data;
			}
		}
		return null;
	}

	public static void setTextViewer(Widget widget, TextViewer textViewer) {
		widget.setData(KEY_TEXT_VIEWER, textViewer);
	}

	public IHandlerActivation contentAssistHandlerActivation;

	private final IHandlerService handlerService;

	private IHandlerActivation quickAssistHandlerActivation;

	private ISelectionChangedListener selectionChangedListener;

	public CommonTextSupport(IHandlerService handlerService) {
		this.handlerService = handlerService;
	}

	private IHandlerActivation activateHandler(TextViewer viewer, int operation, String actionDefinitionId) {
		IHandler handler = createActionHandler(viewer, operation, actionDefinitionId);
		return handlerService.activateHandler(actionDefinitionId, handler, //
				new ActiveShellExpression(viewer.getTextWidget().getShell()));
	}

	private void activateHandlers(TextViewer viewer, boolean spellCheck) {
		deactivateHandlers();
		if (spellCheck) {
			quickAssistHandlerActivation = activateHandler(viewer, ISourceViewer.QUICK_ASSIST,
					ITextEditorActionDefinitionIds.QUICK_ASSIST);
		}
		contentAssistHandlerActivation = activateHandler(viewer, ISourceViewer.CONTENTASSIST_PROPOSALS,
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
	}

	public void configure(final TextViewer viewer, Document document, boolean spellCheck) {
		if (spellCheck && viewer instanceof ISourceViewer) {
			configureAsEditor((ISourceViewer) viewer, document);
		} else {
			viewer.setDocument(document);
		}
		install(viewer, spellCheck);
	}

	/** Configures annotation model for spell checking. */
	private void configureAsEditor(ISourceViewer viewer, Document document) {
		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(viewer, null, annotationAccess,
				EditorsUI.getSharedTextColors());
		Iterator<?> e = new MarkerAnnotationPreferences().getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference) e.next());
		}
		support.install(EditorsUI.getPreferenceStore());
		viewer.getTextWidget().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.uninstall();
			}
		});
		AnnotationModel annotationModel = new AnnotationModel();
		viewer.setDocument(document, annotationModel);
	}

	private IHandler createActionHandler(final ITextOperationTarget viewer, final int operation,
			String actionDefinitionId) {
		Action action = new Action() {
			@Override
			public void run() {
				if (viewer.canDoOperation(operation)) {
					viewer.doOperation(operation);
				}
			}
		};
		action.setActionDefinitionId(actionDefinitionId);
		return new ActionHandler(action);
	}

	private void deactivateHandlers() {
		if (quickAssistHandlerActivation != null) {
			handlerService.deactivateHandler(quickAssistHandlerActivation);
			quickAssistHandlerActivation = null;
		}
		if (contentAssistHandlerActivation != null) {
			handlerService.deactivateHandler(contentAssistHandlerActivation);
			contentAssistHandlerActivation = null;
		}
	}

	public void dispose() {
		deactivateHandlers();
	}

	public ISelectionChangedListener getSelectionChangedListener() {
		return selectionChangedListener;
	}

	public void install(final TextViewer viewer, boolean spellCheck) {
		viewer.getControl().addFocusListener(new TextViewerFocusListener(viewer, spellCheck));
		if (selectionChangedListener != null) {
			viewer.addSelectionChangedListener(selectionChangedListener);
			viewer.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					if (selectionChangedListener != null) {
						selectionChangedListener.selectionChanged(new SelectionChangedEvent(viewer,
								viewer.getSelection()));
					}
				}
			});
		}
		setTextViewer(viewer.getControl(), viewer);
	}

	public void setSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		this.selectionChangedListener = selectionChangedListener;
	}

}
