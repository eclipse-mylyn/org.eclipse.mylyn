/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class ControlSelectionService {

	private final Map<Object, Control> controlBySelectableObject = new HashMap<Object, Control>();

	private final AbstractTaskEditorPage page;

	public ControlSelectionService(AbstractTaskEditorPage page) {
		this.page = page;
	}

	/**
	 * @see #select(Object, boolean)
	 */
	public void addSelectableControl(Object item, Control control) {
		controlBySelectableObject.put(item, control);
	}

	/**
	 * Scroll to a specified piece of text
	 * 
	 * @param selectionComposite
	 *            The StyledText to scroll to
	 */
	private void focusOn(Control selectionComposite, boolean highlight) {
		int pos = 0;
		// if (previousText != null && !previousText.isDisposed()) {
		// previousText.setsetSelection(0);
		// }

		// if (selectionComposite instanceof FormText)
		// previousText = (FormText) selectionComposite;

		if (selectionComposite != null) {

			// if (highlight && selectionComposite instanceof FormText &&
			// !selectionComposite.isDisposed())
			// ((FormText) selectionComposite).set.setSelection(0, ((FormText)
			// selectionComposite).getText().length());

			// get the position of the text in the composite
			pos = 0;
			Control s = selectionComposite;
			if (s.isDisposed()) {
				return;
			}
			s.setEnabled(true);
			s.setFocus();
			s.forceFocus();
			while (s != null && s != page.getEditorComposite()) {
				if (!s.isDisposed()) {
					pos += s.getLocation().y;
					s = s.getParent();
				}
			}

			pos = pos - 60; // form.getOrigin().y;

		}
		if (!page.getManagedForm().getForm().getBody().isDisposed()) {
			page.getManagedForm().getForm().setOrigin(0, pos);
		}
	}

	/**
	 * @see #addSelectableControl(Object, Control)
	 */
	public void removeSelectableControl(Object item) {
		controlBySelectableObject.remove(item);
	}

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param o
	 *            The object to be selected.
	 * @param highlight
	 *            Whether or not the object should be highlighted.
	 */
	public boolean select(Object o, boolean highlight) {
		Control control = controlBySelectableObject.get(o);
		if (control != null && !control.isDisposed()) {

			// expand all children
			if (control instanceof ExpandableComposite) {
				ExpandableComposite ex = (ExpandableComposite) control;
				if (!ex.isExpanded()) {
					EditorUtil.toggleExpandableComposite(true, ex);
				}
			}

			// expand all parents of control
			Composite comp = control.getParent();
			while (comp != null) {
				if (comp instanceof Section) {
					((Section) comp).setExpanded(true);
				} else if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					if (!ex.isExpanded()) {
						EditorUtil.toggleExpandableComposite(true, ex);
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
			focusOn(control, highlight);
		} else if (o instanceof TaskData) {
			focusOn(null, highlight);
		} else {
			return false;
		}
		return true;
	}

}
