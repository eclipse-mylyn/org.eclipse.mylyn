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

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
// TODO EDITOR rename to AttributeUiToolkit?
public class AttributeEditorToolkit {

	private class StyledTextFocusListener implements FocusListener {

		private final boolean spellCheck;

		private final SourceViewer viewer;

		public StyledTextFocusListener(SourceViewer viewer, boolean spellCheck) {
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

	private final Color colorIncoming;

	public IHandlerActivation contentAssistHandlerActivation;

	private final IHandlerService handlerService;

	private Menu menu;

	private IHandlerActivation quickAssistHandlerActivation;

	private ISelectionChangedListener selectionChangedListener;

	private AbstractRenderingEngine renderingEngine;

	AttributeEditorToolkit(IHandlerService handlerService) {
		this.handlerService = handlerService;
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		colorIncoming = themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_INCOMING_BACKGROUND);
	}

	private IHandlerActivation activateHandler(SourceViewer viewer, int operation, String actionDefinitionId) {
		IHandler handler = createActionHandler(viewer, operation, actionDefinitionId);
		return handlerService.activateHandler(actionDefinitionId, handler, //
				new ActiveShellExpression(viewer.getTextWidget().getShell()));
	}

	private void activateHandlers(SourceViewer viewer, boolean spellCheck) {
		deactivateHandlers();
		if (spellCheck) {
			quickAssistHandlerActivation = activateHandler(viewer, ISourceViewer.QUICK_ASSIST,
					ITextEditorActionDefinitionIds.QUICK_ASSIST);
		}
		contentAssistHandlerActivation = activateHandler(viewer, ISourceViewer.CONTENTASSIST_PROPOSALS,
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);

	}

	public void adapt(AbstractAttributeEditor editor) {
		if (editor.getControl() instanceof Text || editor.getControl() instanceof CCombo) {
			Control control = editor.getControl();
			if (!editor.isReadOnly() && hasContentAssist(editor.getTaskAttribute())) {
				IContentProposalProvider contentProposalProvider = createContentProposalProvider(editor.getTaskAttribute());
				ILabelProvider labelPropsalProvider = createLabelProposalProvider(editor.getTaskAttribute());
				if (contentProposalProvider != null && labelPropsalProvider != null) {
					ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(control,
							getContentAdapter(control), contentProposalProvider,
							ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, new char[0], true);
					adapter.setLabelProvider(labelPropsalProvider);
					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
				}
			}
		} else if (editor instanceof RichTextAttributeEditor) {
			RichTextAttributeEditor richTextEditor = (RichTextAttributeEditor) editor;
			boolean spellCheck = hasSpellChecking(editor.getTaskAttribute());
			final SourceViewer viewer = richTextEditor.getViewer();
			viewer.getControl().addFocusListener(new StyledTextFocusListener(viewer, spellCheck));
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
			if (!editor.isReadOnly() && richTextEditor.getMode() == Mode.TASK_RELATION) {
				installContentAssistControlDecoration(viewer.getControl());
			}
			installMenu(viewer.getControl());
			EditorUtil.setTextViewer(editor.getControl(), viewer);
		} else {
			final TextViewer viewer = EditorUtil.getTextViewer(editor.getControl());
			if (viewer != null) {
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
				installMenu(viewer.getControl());
			}
		}

		// for outline
		EditorUtil.setMarker(editor.getControl(), editor.getTaskAttribute().getId());

		editor.decorate(getColorIncoming());
	}

	private IControlContentAdapter getContentAdapter(Control control) {
		if (control instanceof Combo) {
			return new ComboContentAdapter();
		} else if (control instanceof Text) {
			return new TextContentAdapter();
		}
		return null;
	}

	private void installMenu(final Control control) {
		if (menu != null) {
			control.setMenu(menu);
			control.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					control.setMenu(null);
				}
			});
		}
	}

	/**
	 * Adds content assist to the given text field.
	 * 
	 * @param text
	 *            text field to decorate.
	 * @param proposalProvider
	 *            instance providing content proposals
	 * @return the ContentAssistCommandAdapter for the field.
	 */
	private ControlDecoration installContentAssistControlDecoration(Control control) {
		ControlDecoration controlDecoration = new ControlDecoration(control, (SWT.TOP | SWT.LEFT));
		controlDecoration.setShowOnlyOnFocus(true);
		FieldDecoration contentProposalImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
		controlDecoration.setImage(contentProposalImage.getImage());
		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		controlDecoration.setDescriptionText(NLS.bind("Content Assist Available ({0})",
				bindingService.getBestActiveBindingFormattedFor(ContentAssistCommandAdapter.CONTENT_PROPOSAL_COMMAND)));
		return controlDecoration;
	}

	private IHandler createActionHandler(final SourceViewer viewer, final int operation, String actionDefinitionId) {
		Action quickFixAction = new Action() {
			@Override
			public void run() {
				if (viewer.canDoOperation(operation)) {
					viewer.doOperation(operation);
				}
			}
		};
		quickFixAction.setActionDefinitionId(actionDefinitionId);
		return new ActionHandler(quickFixAction);
	}

	/**
	 * Creates an IContentProposalProvider to provide content assist proposals for the given attribute.
	 * 
	 * @param attribute
	 *            attribute for which to provide content assist.
	 * @return the IContentProposalProvider.
	 */
	private IContentProposalProvider createContentProposalProvider(TaskAttribute attribute) {
		return new PersonProposalProvider(null, attribute.getTaskData());
	}

	private ILabelProvider createLabelProposalProvider(TaskAttribute attribute) {
		return new PersonProposalLabelProvider();
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

	void dispose() {
		deactivateHandlers();
	}

	public Color getColorIncoming() {
		return colorIncoming;
	}

	Menu getMenu() {
		return menu;
	}

	/**
	 * Subclasses that support HTML preview of ticket description and comments override this method to return an
	 * instance of AbstractRenderingEngine
	 * 
	 * @return <code>null</code> if HTML preview is not supported for the repository (default)
	 * @since 2.1
	 */
	public AbstractRenderingEngine getRenderingEngine(TaskAttribute attribute) {
		return renderingEngine;
	}

	ISelectionChangedListener getSelectionChangedListener() {
		return selectionChangedListener;
	}

	/**
	 * Called to check if there's content assist available for the given attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return true if content assist is available for the specified attribute.
	 */
	private boolean hasContentAssist(TaskAttribute taskAttribute) {
		String type = taskAttribute.getMetaData().getType();
		if (TaskAttribute.TYPE_PERSON.equals(type)) {
			return true;
		} else if (TaskAttribute.TYPE_TASK_DEPENDENCY.equals(type)) {
			return true;
		}
		return false;
	}

	boolean hasSpellChecking(TaskAttribute taskAttribute) {
		String type = taskAttribute.getMetaData().getType();
		if (TaskAttribute.TYPE_LONG_RICH_TEXT.equals(type) || TaskAttribute.TYPE_SHORT_RICH_TEXT.equals(type)) {
			return true;
		}
		return false;
	}

	void setMenu(Menu menu) {
		this.menu = menu;
	}

	void setSelectionChangedListener(ISelectionChangedListener selectionListener) {
		this.selectionChangedListener = selectionListener;
	}

	public void setRenderingEngine(AbstractRenderingEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}

}
