/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.ui.editors;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.core.IdentityAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataUtil;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.IEditorActionBarContributor;
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
			if (actionContributor != null) {
				actionContributor.updateSelectableActions(viewer.getSelection());
			}
			activateHandlers(viewer, spellCheck);
		}

		public void focusLost(FocusEvent e) {
			deactivateHandlers();
			if (actionContributor != null) {
				StyledText st = (StyledText) e.widget;
				st.setSelectionRange(st.getCaretOffset(), 0);
				actionContributor.forceActionsEnabled();
			}
		}

	}

	private final TaskEditorActionContributor actionContributor;

	public IHandlerActivation contentAssistHandlerActivation;

	private final IHandlerService handlerService;

	private IHandlerActivation quickAssistHandlerActivation;

	private final List<TextViewer> textViewers;

	private final Color colorIncoming;

	public AttributeEditorToolkit(IHandlerService handlerService, IEditorActionBarContributor actionContributor) {
		this.handlerService = handlerService;
		if (actionContributor instanceof TaskEditorActionContributor) {
			this.actionContributor = (TaskEditorActionContributor) actionContributor;
		} else {
			this.actionContributor = null;
		}
		this.textViewers = new ArrayList<TextViewer>();
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		colorIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
				CommonColorsAndFonts.THEME_COLOR_TASKS_INCOMING_BACKGROUND);
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

	public void dispose(AbstractAttributeEditor editor) {
		if (actionContributor != null) {
			if (editor instanceof RichTextAttributeEditor) {
				actionContributor.removeTextViewer(((RichTextAttributeEditor) editor).getViewer());
			}
		}
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
			if (actionContributor != null) {
				viewer.addSelectionChangedListener(actionContributor);
				viewer.addTextListener(new ITextListener() {
					public void textChanged(TextEvent event) {
						actionContributor.updateSelectableActions(viewer.getSelection());
					}
				});

				if (viewer instanceof RepositoryTextViewer) {
					RepositoryTextViewer textViewer = (RepositoryTextViewer) viewer;
					MenuManager menuManager = textViewer.getMenuManager();
					configureContextMenuManager(menuManager, textViewer);
					textViewer.setMenu(menuManager.createContextMenu(viewer.getTextWidget()));
				}
			}
		}

		editor.decorate(getColorIncoming());
	}

	/**
	 * Adds content assist to the given text field.
	 * 
	 * @param text
	 * 		text field to decorate.
	 * @param proposalProvider
	 * 		instance providing content proposals
	 * @return the ContentAssistCommandAdapter for the field.
	 */
	private ContentAssistCommandAdapter applyContentAssist(Text text, IContentProposalProvider proposalProvider) {
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

	private void configureContextMenuManager(MenuManager manager, TextViewer textViewer) {
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				actionContributor.contextMenuAboutToShow(manager);
			}
		};
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(listener);

		textViewers.add(textViewer);
		actionContributor.addTextViewer(textViewer);
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
	 * 		attribute for which to provide content assist.
	 * @return the IContentProposalProvider.
	 */
	private IContentProposalProvider createContentProposalProvider(TaskAttribute attribute) {
		return new PersonProposalProvider(null, TaskDataUtil.toLegacyData(attribute.getTaskData(),
				IdentityAttributeFactory.getInstance()));
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

	public void dispose() {
		deactivateHandlers();
		if (actionContributor != null) {
			for (TextViewer textViewer : textViewers) {
				actionContributor.removeTextViewer(textViewer);
			}
		}
	}

	public String formatDate(Date date) {
		return DateFormat.getDateInstance().format(date);
	}

	/**
	 * Subclasses that support HTML preview of ticket description and comments override this method to return an
	 * instance of AbstractRenderingEngine
	 * 
	 * @return <code>null</code> if HTML preview is not supported for the repository (default)
	 * @since 2.1
	 */
	public AbstractRenderingEngine getRenderingEngine(TaskAttribute attribute) {
		return null;
	}

	/**
	 * Called to check if there's content assist available for the given attribute.
	 * 
	 * @param attribute
	 * 		the attribute
	 * @return true if content assist is available for the specified attribute.
	 */
	// TODO EDITOR make private
	boolean hasContentAssist(TaskAttribute taskAttribute) {
		if (TaskAttribute.TYPE_PERSON.equals(taskAttribute.getTaskData().getAttributeMapper().getType(taskAttribute))) {
			return true;
		} else if (TaskAttribute.TYPE_TASK_DEPENDENCY.equals(taskAttribute.getTaskData().getAttributeMapper().getType(
				taskAttribute))) {
			return true;
		}
		return false;
	}

	private boolean hasSpellChecking(TaskAttribute taskAttribute) {
		// TODO EDITOR
		return false;
	}

	public Color getColorIncoming() {
		return colorIncoming;
	}

}
