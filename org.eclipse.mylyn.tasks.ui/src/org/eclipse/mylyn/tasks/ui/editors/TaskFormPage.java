/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionContributor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.themes.IThemeManager;

/**
 * Used by the task editor. Not recommended to extend.
 * 
 * NOTE: likely to change for 3.0.
 * 
 * @author Rob Elves
 * @author Eugene Kuleshov (spelling correction)
 * 
 * @ref: PDEFormPage.class ref:
 * @ref: http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg19676.html
 */
public class TaskFormPage extends FormPage {

	protected boolean isDirty;

	protected TaskEditorActionContributor actionContributor;

	protected List<TextViewer> textViewers = new ArrayList<TextViewer>();

	private void addTextViewer(TextViewer viewer) {
		textViewers.add(viewer);
	}

	public TaskFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	public boolean canPerformAction(String actionId) {
		Control focusControl = getFocusControl();
		if (focusControl instanceof StyledText) {
			StyledText text = (StyledText) focusControl;
			for (TextViewer viewer : textViewers) {
				if (viewer.getTextWidget() == text) {
					return canDoGlobalAction(actionId, viewer);
				}
			}
		} else {
			if (actionId.equals(ActionFactory.UNDO.getId()) || actionId.equals(ActionFactory.REDO.getId())) {
				return false;
			} else {
				return true;
			}
		}
		// else if (focusControl instanceof Text) {
		//
		// Text textControl = (Text) focusControl;
		// if (actionId.equals(ActionFactory.CUT.getId())) {
		// return textControl.getSelectionText().length() > 0;
		// }
		// if (actionId.equals(ActionFactory.COPY.getId())) {
		// return textControl.getSelectionText().length() > 0;
		// }
		// if (actionId.equals(ActionFactory.PASTE.getId())) {
		// return true;
		// }
		// if (actionId.equals(ActionFactory.SELECT_ALL.getId())) {
		// return textControl.getText().length() > 0;
		// }
		// if (actionId.equals(ActionFactory.DELETE.getId())) {
		// return textControl.getSelectionText().length() > 0;
		// }
		// }
		return false;
	}

	public void doAction(String actionId) {
		Control focusControl = getFocusControl();
		if (focusControl == null)
			return;
		if (canPerformDirectly(actionId, focusControl)) {
			return;
		}
		if (focusControl instanceof StyledText) {
			StyledText text = (StyledText) focusControl;
			for (TextViewer viewer : textViewers) {
				if (viewer.getTextWidget() == text) {
					doGlobalAction(actionId, viewer);
					return;
				}
			}
		}
	}

	protected boolean canPerformDirectly(String id, Control control) {
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

	protected Control getFocusControl() {
		IManagedForm form = getManagedForm();
		if (form == null)
			return null;
		Control control = form.getForm();
		if (control == null || control.isDisposed())
			return null;
		Display display = control.getDisplay();
		Control focusControl = display.getFocusControl();
		if (focusControl == null || focusControl.isDisposed())
			return null;
		return focusControl;
	}

	private boolean doGlobalAction(String actionId, TextViewer textViewer) {
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

	private boolean canDoGlobalAction(String actionId, TextViewer textViewer) {
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

	/**
	 * Text viewer generally used for displaying non-editable text. No annotation model or spell checking support.
	 * Supports cut/copy/paste/etc..
	 */
	protected TextViewer addTextViewer(TaskRepository repository, Composite composite, String text, int style) {

		if (actionContributor == null) {
			actionContributor = ((TaskEditor) getEditor()).getContributor();
		}

		final RepositoryTextViewer commentViewer = new RepositoryTextViewer(repository, composite, style);

		// NOTE: Configuration must be applied before the document is set in
		// order for
		// Hyperlink colouring to work. (Presenter needs document object up
		// front)
		TaskTextViewerConfiguration repositoryViewerConfig = new TaskTextViewerConfiguration(false);
		commentViewer.configure(repositoryViewerConfig);

		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();

		commentViewer.getTextWidget().setFont(
				themeManager.getCurrentTheme().getFontRegistry().get(TaskListColorsAndFonts.TASK_EDITOR_FONT));

		commentViewer.addSelectionChangedListener(actionContributor);

		commentViewer.getTextWidget().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				actionContributor.updateSelectableActions(commentViewer.getSelection());

			}

			public void focusLost(FocusEvent e) {
				StyledText st = (StyledText) e.widget;
				st.setSelectionRange(st.getCaretOffset(), 0);
				actionContributor.forceActionsEnabled();
			}
		});

		commentViewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				actionContributor.updateSelectableActions(commentViewer.getSelection());
			}
		});

		commentViewer.setEditable(false);
		commentViewer.getTextWidget().setMenu(getManagedForm().getForm().getMenu());
		Document document = new Document(text);
		commentViewer.setDocument(document);

		addTextViewer(commentViewer);
		return commentViewer;
	}

	/**
	 * For viewing and editing text. Spell checking w/ annotations supported One or two max per editor, any more and the
	 * spell checker will bring the editor to a grinding halt.
	 */
	protected TextViewer addTextEditor(TaskRepository repository, Composite composite, String text, boolean spellCheck,
			int style) {

		if (actionContributor == null) {
			actionContributor = ((TaskEditor) getEditor()).getContributor();
		}

		AnnotationModel annotationModel = new AnnotationModel();
		final RepositoryTextViewer commentViewer = new RepositoryTextViewer(null, null, repository, composite, style);
		commentViewer.showAnnotations(false);
		commentViewer.showAnnotationsOverview(false);

		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();

		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(commentViewer, null,
				annotationAccess, EditorsUI.getSharedTextColors());

		@SuppressWarnings("unchecked")
		Iterator e = new MarkerAnnotationPreferences().getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference) e.next());
		}

		support.install(EditorsUI.getPreferenceStore());

		commentViewer.getTextWidget().setIndent(2);
		commentViewer.getTextWidget().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.uninstall();
			}
		});

		final IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
				IHandlerService.class);

		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();

		commentViewer.getTextWidget().setFont(
				themeManager.getCurrentTheme().getFontRegistry().get(TaskListColorsAndFonts.TASK_EDITOR_FONT));

		commentViewer.addSelectionChangedListener(actionContributor);

		commentViewer.getTextWidget().addFocusListener(new FocusListener() {
			private IHandlerActivation handlerActivation;
			
			public void focusGained(FocusEvent e) {
				actionContributor.updateSelectableActions(commentViewer.getSelection());

				handlerActivation = handlerService.activateHandler(
						ITextEditorActionDefinitionIds.QUICK_ASSIST, createQuickFixActionHandler(commentViewer),
						new ActiveShellExpression(commentViewer.getTextWidget().getShell()));
			}

			public void focusLost(FocusEvent e) {
				StyledText st = (StyledText) e.widget;
				st.setSelectionRange(st.getCaretOffset(), 0);
				actionContributor.forceActionsEnabled();
				
				if(handlerActivation!=null) {
					handlerService.deactivateHandler(handlerActivation);
				}
			}
		});

		commentViewer.addTextListener(new ITextListener() {
			public void textChanged(TextEvent event) {
				actionContributor.updateSelectableActions(commentViewer.getSelection());
			}
		});

		commentViewer.setEditable(false);
		commentViewer.getTextWidget().setMenu(getManagedForm().getForm().getMenu());
		Document document = new Document(text);

		// NOTE: Configuration must be applied before the document is set in order for
		// Hyperlink coloring to work. (Presenter needs document object up front)
		TextSourceViewerConfiguration viewerConfig = new TaskTextViewerConfiguration(spellCheck);
		commentViewer.configure(viewerConfig);

		commentViewer.setDocument(document, annotationModel);

		// !DND! hover manager that shows text when we hover
		// AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(fCompositeRuler,
		//     commentViewer, new AnnotationHover(fAnnotationModel), new AnnotationConfiguration());
		// fAnnotationHoverManager.install(annotationRuler.getControl());

		// !DND! Sample debugging code
		// document.set("Here's some texst so that we have somewhere to show an error");
		//
		// // // add an annotation
		// ErrorAnnotation errorAnnotation = new ErrorAnnotation(1, "");
		// // lets underline the word "texst"
		// fAnnotationModel.addAnnotation(errorAnnotation, new Position(12, 5));

		// CoreSpellingProblem iProblem = new CoreSpellingProblem(12, 5, 1, 
		//    "problem message", "theword", false, false, document, "task editor");
		// editorInput.getName()
		//
		// fAnnotationModel.addAnnotation(new ProblemAnnotation(iProblem, null), new Position(12, 5));

		addTextViewer(commentViewer);
		return commentViewer;
	}

	private IHandler createQuickFixActionHandler(final SourceViewer viewer) {
		Action quickFixAction = new Action() {
			public void run() {
				if (viewer.canDoOperation(ISourceViewer.QUICK_ASSIST)) {
					viewer.doOperation(ISourceViewer.QUICK_ASSIST);
				}
			}
		};
		quickFixAction.setActionDefinitionId(ITextEditorActionDefinitionIds.QUICK_ASSIST);
		return new ActionHandler(quickFixAction);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void markDirty(boolean dirty) {
		isDirty = dirty;
		getManagedForm().dirtyStateChanged();
		return;
	}

	static class AnnotationMarkerAccess implements IAnnotationAccess, IAnnotationAccessExtension {
		public Object getType(Annotation annotation) {
			return annotation.getType();
		}

		public boolean isMultiLine(Annotation annotation) {
			return true;
		}

		public boolean isTemporary(Annotation annotation) {
			return !annotation.isPersistent();
		}

		public String getTypeLabel(Annotation annotation) {
			if (annotation instanceof ErrorAnnotation)
				return "Errors";

			return null;
		}

		public int getLayer(Annotation annotation) {
			if (annotation instanceof ErrorAnnotation)
				return ((ErrorAnnotation) annotation).getLayer();

			return 0;
		}

		public void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds) {
			ImageUtilities.drawImage(((ErrorAnnotation) annotation).getImage(), gc, canvas, bounds, SWT.CENTER, SWT.TOP);
		}

		public boolean isPaintable(Annotation annotation) {
			if (annotation instanceof ErrorAnnotation)
				return ((ErrorAnnotation) annotation).getImage() != null;

			return false;
		}

		public boolean isSubtype(Object annotationType, Object potentialSupertype) {
			if (annotationType.equals(potentialSupertype))
				return true;

			return false;

		}

		public Object[] getSupertypes(Object annotationType) {
			return new Object[0];
		}
	}

	static class AnnotationHover implements IAnnotationHover, ITextHover {

		AnnotationModel fAnnotationModel = null;

		public AnnotationHover(AnnotationModel model) {
			this.fAnnotationModel = model;
		}

		@SuppressWarnings("unchecked")
		public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
			Iterator ite = fAnnotationModel.getAnnotationIterator();

			ArrayList<String> all = new ArrayList<String>();

			while (ite.hasNext()) {
				Annotation a = (Annotation) ite.next();
				if (a instanceof ErrorAnnotation) {
					all.add(((ErrorAnnotation) a).getText());
				}
			}

			StringBuffer total = new StringBuffer();
			for (int x = 0; x < all.size(); x++) {
				String str = all.get(x);
				total.append(" " + str + (x == (all.size() - 1) ? "" : "\n"));
			}

			return total.toString();
		}

		public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
			return null;
		}

		public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
			return null;
		}
	}

	public static class ErrorAnnotation extends Annotation {

		public static String ERROR_TYPE = "spelling.error";

		// ProblemAnnotation.SPELLING_ANNOTATION_TYPE;

		private IMarker marker;

		private String text;

		private int line;

		private Position position;

		public ErrorAnnotation(IMarker marker) {
			this.marker = marker;
		}

		public ErrorAnnotation(int line, String text) {
			super(ERROR_TYPE, true, null);
			this.marker = null;
			this.line = line;
			this.text = text;
		}

		public IMarker getMarker() {
			return marker;
		}

		public int getLine() {
			return line;
		}

		@Override
		public String getText() {
			return text;
		}

		public Image getImage() {
			return null;// ERROR_IMAGE;
		}

		public int getLayer() {
			return 3;
		}

		@Override
		public String getType() {
			return ERROR_TYPE;
		}

		public Position getPosition() {
			return position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}
	}

	// NOTE: See commented code below for example implementation
	static class SharedTextColors implements ISharedTextColors {

		/** Creates an returns a shared color manager. */
		public SharedTextColors() {
			super();
		}

		public Color getColor(RGB rgb) {
			return TaskListColorsAndFonts.COLOR_SPELLING_ERROR;
		}

		public void dispose() {
			return;
		}
	}

	// DND relves
	//// From org.eclipse.ui.internal.editors.text.SharedTextColors
	// static class SharedTextColors implements ISharedTextColors {
	// /** The display table. */
	// @SuppressWarnings("unchecked")
	// private Map fDisplayTable;
	//
	// /** Creates an returns a shared color manager. */
	// public SharedTextColors() {
	// super();
	// }
	//
	// /*
	// * @see ISharedTextColors#getColor(RGB)
	// */
	// @SuppressWarnings("unchecked")
	// public Color getColor(RGB rgb) {
	// if (rgb == null)
	// return null;
	//
	// if (fDisplayTable == null)
	// fDisplayTable = new HashMap(2);
	//
	// Display display = Display.getCurrent();
	//
	// Map colorTable = (Map) fDisplayTable.get(display);
	// if (colorTable == null) {
	// colorTable = new HashMap(10);
	// fDisplayTable.put(display, colorTable);
	// }
	//
	// Color color = (Color) colorTable.get(rgb);
	// if (color == null) {
	// color = new Color(display, rgb);
	// colorTable.put(rgb, color);
	// }
	//
	// return color;
	// }
	//
	// /*
	// * @see ISharedTextColors#dispose()
	// */
	// @SuppressWarnings("unchecked")
	// public void dispose() {
	// if (fDisplayTable != null) {
	// Iterator j = fDisplayTable.values().iterator();
	// while (j.hasNext()) {
	// Iterator i = ((Map) j.next()).values().iterator();
	// while (i.hasNext())
	// ((Color) i.next()).dispose();
	// }
	// }
	// }
	// }

	static class AnnotationConfiguration implements IInformationControlCreator {
		public IInformationControl createInformationControl(Shell shell) {
			return new DefaultInformationControl(shell);
		}
	}

}
