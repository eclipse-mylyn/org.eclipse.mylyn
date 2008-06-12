/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class EditorUtil {

//	public static final String DATE_FORMAT = "yyyy-MM-dd";
//
//	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	@Deprecated
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	@Deprecated
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	static final String KEY_MARKER = "marker";

	static final String KEY_TEXT_VIEWER = "textViewer";

	public static final int MAXIMUM_HEIGHT = 140;

	public static final int MAXIMUM_WIDTH = 500;

	// XXX why is this required?
	public static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	static boolean canDoGlobalAction(String actionId, TextViewer textViewer) {
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

	private static Control findControl(Composite composite, String key) {
		if (!composite.isDisposed()) {
			for (Control child : composite.getChildren()) {
				if (key.equals(getMarker(child))) {
					return child;
				}
				if (child instanceof Composite) {
					Control found = findControl((Composite) child, key);
					if (found != null) {
						return found;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Scroll to a specified piece of text
	 * 
	 * @param control
	 *            The StyledText to scroll to
	 */
	private static void focusOn(ScrolledForm form, Control control) {
		int pos = 0;
		control.setEnabled(true);
		control.setFocus();
		control.forceFocus();
		while (control != null && control != form.getBody()) {
			pos += control.getLocation().y;
			control = control.getParent();
		}

		pos = pos - 60; // form.getOrigin().y;
		if (!form.getBody().isDisposed()) {
			form.setOrigin(0, pos);
		}
	}

	static String formatDate(Date date) {
		return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
	}

	static String formatDateTime(Date date) {
		return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date);
	}

	public static Control getFocusControl(IFormPage page) {
		if (page == null) {
			return null;
		}
		IManagedForm form = page.getManagedForm();
		if (form == null) {
			return null;
		}
		Control control = form.getForm();
		if (control == null || control.isDisposed()) {
			return null;
		}
		Display display = control.getDisplay();
		Control focusControl = display.getFocusControl();
		if (focusControl == null || focusControl.isDisposed()) {
			return null;
		}
		return focusControl;
	}

	public static String getMarker(Widget widget) {
		return (String) widget.getData(KEY_MARKER);
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

	public static NewAttachmentWizardDialog openNewAttachmentWizard(final AbstractTaskEditorPage page, Mode mode,
			AbstractTaskAttachmentSource source) {
		TaskAttributeMapper mapper = page.getModel().getTaskData().getAttributeMapper();
		TaskAttribute attribute = mapper.createTaskAttachment(page.getModel().getTaskData());
		final NewAttachmentWizardDialog dialog = TasksUiInternal.openNewAttachmentWizard(page.getSite().getShell(),
				page.getTaskRepository(), page.getTask(), attribute, mode, source);
		dialog.getShell().addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				if (dialog.getReturnCode() == Window.OK) {
					page.getTaskEditor().refreshPages();
				}
			}

		});
		return dialog;
	}

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param o
	 *            The object to be selected.
	 * @param highlight
	 *            Whether or not the object should be highlighted.
	 */
	public static boolean reveal(ScrolledForm form, String key) {
		Control control = findControl(form.getBody(), key);
		if (control != null) {
			// expand all children
			if (control instanceof ExpandableComposite) {
				ExpandableComposite ex = (ExpandableComposite) control;
				if (!ex.isExpanded()) {
					toggleExpandableComposite(true, ex);
				}
			}

			// expand all parents of control
			Composite comp = control.getParent();
			while (comp != null) {
				if (comp instanceof Section) {
					if (!((Section) comp).isExpanded()) {
						((Section) comp).setExpanded(true);
					}
				} else if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					if (!ex.isExpanded()) {
						toggleExpandableComposite(true, ex);
					}

					// HACK: This is necessary
					// due to a bug in SWT's ExpandableComposite.
					// 165803: Expandable bars should expand when clicking anywhere
					// https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=165803
					if (ex.getData() != null && ex.getData() instanceof Composite) {
						((Composite) ex.getData()).setVisible(true);
					}
				}
				comp = comp.getParent();
			}
			focusOn(form, control);
		}
		return true;
	}

	public static void setEnabledState(Composite composite, boolean enabled) {
		if (!composite.isDisposed()) {
			composite.setEnabled(enabled);
			for (Control control : composite.getChildren()) {
				control.setEnabled(enabled);
				if (control instanceof Composite) {
					setEnabledState(((Composite) control), enabled);
				}
			}
		}
	}

	public static void setMarker(Widget widget, String text) {
		widget.setData(KEY_MARKER, text);
	}

	public static void setTextViewer(Widget widget, TextViewer textViewer) {
		widget.setData(KEY_TEXT_VIEWER, textViewer);
	}

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 */
	public static void toggleExpandableComposite(boolean expanded, ExpandableComposite comp) {
		if (comp.isExpanded() != expanded) {
			Method method = null;
			try {
				method = ExpandableComposite.class.getDeclaredMethod("programmaticToggleState");
				method.setAccessible(true);
				method.invoke(comp);
			} catch (Exception e) {
				// ignore
			}
		}
	}

}
