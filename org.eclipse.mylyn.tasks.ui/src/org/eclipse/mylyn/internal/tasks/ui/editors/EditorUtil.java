/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFormUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonTextSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard.Mode;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskFormPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.eclipse.ui.internal.EditorAreaHelper;
import org.eclipse.ui.internal.WorkbenchPage;

public class EditorUtil {

	private static final int PAGE_H_SCROLL_INCREMENT = 64;

	public static final int HEADER_COLUMN_MARGIN = 6;

//	public static final String DATE_FORMAT = "yyyy-MM-dd";
//
//	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	static final String KEY_MARKER = "marker"; //$NON-NLS-1$

	static final String KEY_TEXT_VIEWER = "textViewer"; //$NON-NLS-1$

	public static final int MAXIMUM_HEIGHT = 140;

	public static final int MAXIMUM_WIDTH = 300;

	// XXX why is this required?
	public static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	public static final String KEY_TOGGLE_TO_MAXIMIZE_ACTION = "maximizeAction"; //$NON-NLS-1$

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

	/**
	 * @deprecated use {@link CommonTextSupport#canPerformAction(String, Control)} instead
	 */
	@Deprecated
	public static boolean canPerformAction(String actionId, Control focusControl) {
		return CommonTextSupport.canPerformAction(actionId, focusControl);
	}

	/**
	 * @deprecated use {@link CommonTextSupport#doAction(String, Control)} instead
	 */
	@Deprecated
	public static void doAction(String actionId, Control focusControl) {
		CommonTextSupport.doAction(actionId, focusControl);
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

	static DateFormat getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.MEDIUM);
	}

	static String formatDate(Date date) {
		return getDateFormat().format(date);
	}

	static String formatDateTime(Date date) {
		return getDateTimeFormat().format(date);
	}

	static DateFormat getDateTimeFormat() {
		return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
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

	/**
	 * @deprecated use {@link CommonTextSupport#getTextViewer(Widget)} instead
	 */
	@Deprecated
	public static TextViewer getTextViewer(Widget widget) {
		return CommonTextSupport.getTextViewer(widget);
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
					CommonFormUtil.setExpanded(ex, true);
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
						CommonFormUtil.setExpanded(ex, true);
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

	/**
	 * @deprecated Use {@link CommonUiUtil#setEnabled(Composite,boolean)} instead
	 */
	@Deprecated
	public static void setEnabledState(Composite composite, boolean enabled) {
		CommonUiUtil.setEnabled(composite, enabled);
	}

	public static void setMarker(Widget widget, String text) {
		widget.setData(KEY_MARKER, text);
	}

	/**
	 * @deprecated use {@link CommonTextSupport#setTextViewer(Widget, TextViewer)} instead
	 */
	@Deprecated
	public static void setTextViewer(Widget widget, TextViewer textViewer) {
		CommonTextSupport.setTextViewer(widget, textViewer);
	}

	/**
	 * Programmatically expand the provided ExpandableComposite, using reflection to fire the expansion listeners (see
	 * bug#70358)
	 * 
	 * @param comp
	 * @deprecated Use {@link CommonFormUtil#setExpanded(ExpandableComposite,boolean)} instead
	 */
	@Deprecated
	public static void toggleExpandableComposite(boolean expanded, ExpandableComposite comp) {
		CommonFormUtil.setExpanded(comp, expanded);
	}

	/**
	 * @deprecated Use {@link CommonFormUtil#disableScrollingOnFocus(ScrolledForm)} instead
	 */
	@Deprecated
	public static void disableScrollingOnFocus(ScrolledForm form) {
		CommonFormUtil.disableScrollingOnFocus(form);
	}

	/**
	 * @deprecated Use {@link CommonFormUtil#ensureVisible(Control)} instead
	 */
	@Deprecated
	public static void ensureVisible(Control control) {
		CommonFormUtil.ensureVisible(control);
	}

	// copied from Section.reflow()
	public static void reflow(Control control) {
		Composite c = control.getParent();
		while (c != null) {
			c.setRedraw(false);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite) {
				break;
			}
		}
		c = control.getParent();
		while (c != null) {
			c.layout(true);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite) {
				((SharedScrolledComposite) c).reflow(true);
				break;
			}
		}
		c = control.getParent();
		while (c != null) {
			c.setRedraw(true);
			c = c.getParent();
			if (c instanceof SharedScrolledComposite) {
				break;
			}
		}
	}

	public static Composite getLayoutAdvisor(AbstractTaskEditorPage page) {
		Composite layoutAdvisor = page.getEditorComposite();
		do {
			layoutAdvisor = layoutAdvisor.getParent();
		} while (!(layoutAdvisor instanceof CTabFolder));
		return layoutAdvisor.getParent();
	}

	/**
	 * Recursively sets the menu of all children of <code>composite</code>.
	 * 
	 * @deprecated Use {@link CommonUiUtil#setMenu(Composite,Menu)} instead
	 */
	@Deprecated
	public static void setMenu(Composite composite, Menu menu) {
		CommonUiUtil.setMenu(composite, menu);
	}

	// TODO e3.4 replace reflection by assignment to RowLayout.center
	public static void center(RowLayout rowLayout) {
		try {
			Field field = RowLayout.class.getDeclaredField("center"); //$NON-NLS-1$
			field.set(rowLayout, Boolean.TRUE);
		} catch (Throwable e) {
			// ignore
		}
	}

	public static Composite createBorder(Composite composite, final FormToolkit toolkit, boolean paintBorder) {
		// create composite to hold rounded border
		final Composite roundedBorder = toolkit.createComposite(composite);
		if (paintBorder) {
			roundedBorder.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.setForeground(toolkit.getColors().getBorderColor());
					Point size = roundedBorder.getSize();
					e.gc.drawRoundRectangle(0, 2, size.x - 1, size.y - 5, 5, 5);
				}
			});
			roundedBorder.setLayout(GridLayoutFactory.fillDefaults().margins(4, 6).create());
		} else {
			roundedBorder.setLayout(GridLayoutFactory.fillDefaults().margins(0, 6).create());
		}
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT).grab(
				true, false).applyTo(roundedBorder);
		return roundedBorder;
	}

	public static Composite createBorder(Composite composite, final FormToolkit toolkit) {
		return createBorder(composite, toolkit, true);
	}

	public static Font setHeaderFontSizeAndStyle(Control text) {
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(text);
		// .hint(EditorUtil.MAXIMUM_WIDTH, SWT.DEFAULT)

		float sizeFactor = 1.1f;
		Font initialFont = text.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData element : fontData) {
			element.setHeight((int) (element.getHeight() * sizeFactor));
			element.setStyle(element.getStyle() | SWT.BOLD);
		}
		final Font textFont = new Font(text.getDisplay(), fontData);
		text.setFont(textFont);
		text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				textFont.dispose();
			}
		});
		Color color = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_COMPLETED);
		text.setForeground(color);
		return textFont;
	}

	public static GridData getTextControlLayoutData(TaskFormPage page, Control control, boolean expandVertically) {
		final GridData gd = new GridData();
		// wrap text at this margin, see comment below
		int width = getEditorWidth(page);
		// the goal is to make the text viewer as big as the text so it does not require scrolling when first drawn 
		// on screen
		Point size = control.computeSize(width, SWT.DEFAULT, true);
		gd.widthHint = EditorUtil.MAXIMUM_WIDTH;
		gd.minimumWidth = EditorUtil.MAXIMUM_WIDTH;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		// limit height to be avoid dynamic resizing of the text widget: 
		// MAXIMUM_HEIGHT < height < MAXIMUM_HEIGHT * 3
		//gd.minimumHeight = AbstractAttributeEditor.MAXIMUM_HEIGHT;
		gd.heightHint = Math.min(Math.max(EditorUtil.MAXIMUM_HEIGHT, size.y), EditorUtil.MAXIMUM_HEIGHT * 3);
		if (expandVertically) {
			gd.verticalAlignment = SWT.FILL;
			gd.grabExcessVerticalSpace = true;
		}
		return gd;
		// shrink the text control if the editor width is reduced, otherwise the text field will always keep it's original 
		// width and will cause the editor to have a horizonal scroll bar 
//			composite.addControlListener(new ControlAdapter() {
//				@Override
//				public void controlResized(ControlEvent e) {
//					int width = sectionComposite.getSize().x;
//					Point size = descriptionTextViewer.getTextWidget().computeSize(width, SWT.DEFAULT, true);
//					// limit width to parent widget
//					gd.widthHint = width;
//					// limit height to avoid dynamic resizing of the text widget
//					gd.heightHint = Math.min(Math.max(DESCRIPTION_HEIGHT, size.y), DESCRIPTION_HEIGHT * 4);
//					sectionComposite.layout();
//				}
//			});

	}

	private static int getEditorWidth(TaskFormPage page) {
		int widthHint = 0;
		if (page.getManagedForm() != null && page.getManagedForm().getForm() != null) {
			widthHint = page.getManagedForm().getForm().getClientArea().width - 90;
		}
		if (widthHint <= 0 && page.getEditor().getEditorSite() != null
				&& page.getEditor().getEditorSite().getPage() != null) {
			EditorAreaHelper editorManager = ((WorkbenchPage) page.getEditor().getEditorSite().getPage()).getEditorPresentation();
			if (editorManager != null && editorManager.getLayoutPart() != null) {
				widthHint = editorManager.getLayoutPart().getControl().getBounds().width - 90;
			}
		}
		if (widthHint <= 0) {
			widthHint = EditorUtil.MAXIMUM_WIDTH;
		}
		return widthHint;
	}

	public static GridLayout createSectionClientLayout() {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		// leave 1px for borders
		layout.marginTop = 2;
		// spacing if a section is expanded
		layout.marginBottom = 8;
		return layout;
	}

	public static void initializeScrollbars(ScrolledForm form) {
		// initialize scroll bars
		ScrollBar hbar = form.getHorizontalBar();
		if (hbar != null) {
			hbar.setIncrement(PAGE_H_SCROLL_INCREMENT);
		}
	}

}
