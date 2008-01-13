/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * @author Steffen Pingel
 */
// TODO EDITOR rename to AttributeUiToolkit?
public class AttributeEditorToolkit {

	private class StyledTextFocusListener implements org.eclipse.swt.events.FocusListener {

		private final SourceViewer viewer;

		private final boolean spellCheck;

		public StyledTextFocusListener(SourceViewer viewer, boolean spellCheck) {
			this.viewer = viewer;
			this.spellCheck = spellCheck;
		}

		private void activate() {
			if (spellCheck) {
				deactivate();
				if (spellFixHandlerActivation == null) {
					spellFixHandlerActivation = handlerService.activateHandler(
							ITextEditorActionDefinitionIds.QUICK_ASSIST, createQuickFixActionHandler(viewer),
							new ActiveShellExpression(viewer.getTextWidget().getShell()));
				}
			}
		}

		private IHandler createQuickFixActionHandler(final SourceViewer viewer) {
			Action quickFixAction = new Action() {
				@Override
				public void run() {
					if (viewer.canDoOperation(ISourceViewer.QUICK_ASSIST)) {
						viewer.doOperation(ISourceViewer.QUICK_ASSIST);
					}
				}
			};
			quickFixAction.setActionDefinitionId(ITextEditorActionDefinitionIds.QUICK_ASSIST);
			return new ActionHandler(quickFixAction);
		}

		private void deactivate() {
			if (spellCheck) {
				if (spellFixHandlerActivation != null) {
					handlerService.deactivateHandler(spellFixHandlerActivation);
					spellFixHandlerActivation = null;
				}
			}
		}

		public void focusGained(FocusEvent e) {
			actionContributor.updateSelectableActions(viewer.getSelection());
			activate();
		}

		public void focusLost(FocusEvent e) {
			StyledText st = (StyledText) e.widget;
			st.setSelectionRange(st.getCaretOffset(), 0);
			actionContributor.forceActionsEnabled();

			deactivate();
		}

	}

	// TODO EDITOR
	private TaskEditorActionContributor actionContributor;

	// TODO EDITOR  
	private IHandlerActivation spellFixHandlerActivation;

	private final IHandlerService handlerService;

	public AttributeEditorToolkit() {
		handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
	}

	public void adapt(AbstractAttributeEditor editor) {
		if (editor.getControl() instanceof Text && hasContentAssist(editor.getTaskAttribute())) {
			Text text = (Text) editor.getControl();

			IContentProposalProvider contentProposalProvider = createContentProposalProvider(editor.getTaskAttribute());
			ILabelProvider labelPropsalProvider = createLabelProposalProvider(editor.getTaskAttribute());

			if (contentProposalProvider != null && labelPropsalProvider != null) {
				ContentAssistCommandAdapter adapter = applyContentAssist(text, contentProposalProvider);
				adapter.setLabelProvider(labelPropsalProvider);
				adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			}
		} else if (editor instanceof RichTextAttributeEditor) {
			RichTextAttributeEditor richTextEditor = (RichTextAttributeEditor) editor;
			boolean spellCheck = hasSpellChecking(editor.getTaskAttribute());
			final SourceViewer viewer = richTextEditor.getViewer();
			viewer.getControl().addFocusListener(new StyledTextFocusListener(viewer, spellCheck));
			viewer.addSelectionChangedListener(actionContributor);
			viewer.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					actionContributor.updateSelectableActions(viewer.getSelection());
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
	// TODO EDITOR make private
	protected ContentAssistCommandAdapter applyContentAssist(Text text, IContentProposalProvider proposalProvider) {
		ControlDecoration controlDecoration = new ControlDecoration(text, (SWT.TOP | SWT.LEFT));
		controlDecoration.setMarginWidth(0);
		controlDecoration.setShowHover(true);
		controlDecoration.setShowOnlyOnFocus(true);

		FieldDecoration contentProposalImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
		controlDecoration.setImage(contentProposalImage.getImage());

		TextContentAdapter textContentAdapter = new TextContentAdapter();

		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(text, textContentAdapter,
				proposalProvider, "org.eclipse.ui.edit.text.contentAssist.proposals", new char[0]);

		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		controlDecoration.setDescriptionText(NLS.bind("Content Assist Available ({0})",
				bindingService.getBestActiveBindingFormattedFor(adapter.getCommandId())));

		return adapter;
	}

	/**
	 * Creates an IContentProposalProvider to provide content assist proposals for the given attribute.
	 * 
	 * @param attribute
	 *            attribute for which to provide content assist.
	 * @return the IContentProposalProvider.
	 */
	protected IContentProposalProvider createContentProposalProvider(RepositoryTaskAttribute attribute) {
		return new PersonProposalProvider(null, attribute.getTaskData());
	}

	protected ILabelProvider createLabelProposalProvider(RepositoryTaskAttribute attribute) {
		return new PersonProposalLabelProvider();
	}

	/**
	 * Subclasses that support HTML preview of ticket description and comments override this method to return an
	 * instance of AbstractRenderingEngine
	 * 
	 * @return <code>null</code> if HTML preview is not supported for the repository (default)
	 * @since 2.1
	 */
	public AbstractRenderingEngine getRenderingEngine(RepositoryTaskAttribute attribute) {
		return null;
	}

	/**
	 * Called to check if there's content assist available for the given attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return true if content assist is available for the specified attribute.
	 */
	// TODO EDITOR make private
	boolean hasContentAssist(RepositoryTaskAttribute taskAttribute) {
		// TODO EDITOR implement
		return false;
	}

	private boolean hasSpellChecking(RepositoryTaskAttribute taskAttribute) {
		// TODO EDITOR
		return false;
	};

//	/**
//	 * Creates an IContentProposalProvider to provide content assist proposals for the given operation.
//	 * 
//	 * @param operation
//	 *            operation for which to provide content assist.
//	 * @return the IContentProposalProvider.
//	 */
//	protected IContentProposalProvider createContentProposalProvider(RepositoryOperation operation) {
//
//		return new PersonProposalProvider(repositoryTask, taskData);
//	}

//	protected ILabelProvider createProposalLabelProvider(RepositoryOperation operation) {
//
//		return new PersonProposalLabelProvider();
//	}

//	/**
//	 * Called to check if there's content assist available for the given operation.
//	 * 
//	 * @param operation
//	 *            the operation
//	 * @return true if content assist is available for the specified operation.
//	 */
//	protected boolean hasContentAssist(RepositoryOperation operation) {
//		return false;
//	}
//

}
