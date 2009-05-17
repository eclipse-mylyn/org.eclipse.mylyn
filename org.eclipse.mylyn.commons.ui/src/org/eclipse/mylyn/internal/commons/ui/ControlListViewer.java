/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Based on {@link org.eclipse.ui.internal.progress.DetailedProgressViewer}.
 * 
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public abstract class ControlListViewer extends StructuredViewer {

	Composite control;

	private final ScrolledComposite scrolled;

	private final Composite noEntryArea;

	/**
	 * Create a new instance of the receiver with a control that is a child of parent with style style.
	 * 
	 * @param parent
	 * @param style
	 */
	public ControlListViewer(Composite parent, int style) {
		scrolled = new ScrolledComposite(parent, style);
		int height = JFaceResources.getDefaultFont().getFontData()[0].getHeight();
		scrolled.getVerticalBar().setIncrement(height * 2);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		control = new Composite(scrolled, SWT.NONE) {
			@Override
			public boolean setFocus() {
				return true;
			}
		};
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 1;
		control.setLayout(layout);
		control.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		control.addFocusListener(new FocusAdapter() {
			private boolean settingFocus = false;

			@Override
			public void focusGained(FocusEvent e) {
				if (!settingFocus) {
					// Prevent new focus events as a result this update
					// occurring
					settingFocus = true;
					//setFocus();
					settingFocus = false;
				}
			}
		});

		control.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
				updateVisibleItems();

			}

			public void controlResized(ControlEvent e) {
				updateVisibleItems();
			}
		});

		scrolled.setContent(control);
		hookControl(control);

		noEntryArea = new Composite(scrolled, SWT.NONE);
		doCreateNoEntryArea(noEntryArea);

		control.addTraverseListener(new TraverseListener() {
			private boolean handleEvent = true;

			public void keyTraversed(TraverseEvent e) {
				if (!handleEvent) {
					return;
				}
				switch (e.detail) {
				case SWT.TRAVERSE_ARROW_PREVIOUS: {
					Control[] children = control.getChildren();
					if (children.length > 0) {
						boolean selected = false;
						for (int i = 0; i < children.length; i++) {
							ControlListItem item = (ControlListItem) children[i];
							if (item.isSelected()) {
								selected = true;
								if (i > 0) {
									setSelection(new StructuredSelection(children[i - 1].getData()), true);
								}
								break;
							}
						}
						if (!selected) {
							setSelection(new StructuredSelection(children[children.length - 1].getData()), true);
						}
					}
					break;
				}
				case SWT.TRAVERSE_ARROW_NEXT: {
					Control[] children = control.getChildren();
					if (children.length > 0) {
						boolean selected = false;
						for (int i = 0; i < children.length; i++) {
							ControlListItem item = (ControlListItem) children[i];
							if (item.isSelected()) {
								selected = true;
								if (i < children.length - 1) {
									setSelection(new StructuredSelection(children[i + 1].getData()), true);
								}
								break;
							}
						}
						if (!selected) {
							setSelection(new StructuredSelection(children[0].getData()), true);
						}
					}
					break;
				}
				default:
					handleEvent = false;
					control.traverse(e.detail);
					handleEvent = true;
					break;
				}
			}
		});
	}

	protected void doCreateNoEntryArea(Composite parent) {
	}

	public void add(Object[] elements) {
		ViewerComparator sorter = getComparator();

		// Use a Set in case we are getting something added that exists
		Set<Object> newItems = new HashSet<Object>(elements.length);

		Control[] existingChildren = control.getChildren();
		for (Control element : existingChildren) {
			if (element.getData() != null) {
				newItems.add(element.getData());
			}
		}

		for (Object element : elements) {
			if (element != null) {
				newItems.add(element);
			}
		}

		Object[] infos = new Object[newItems.size()];
		newItems.toArray(infos);

		if (sorter != null) {
			sorter.sort(this, infos);
		}

		// Update with the new elements to prevent flash
		for (Control element : existingChildren) {
			((ControlListItem) element).dispose();
		}

		for (int i = 0; i < infos.length; i++) {
			ControlListItem item = createNewItem(infos[i]);
			item.updateColors(i);
		}

		control.layout(true);
		doUpdateContent();
	}

	protected void doUpdateContent() {
		if (control.getChildren().length > 0) {
			updateMinSize();
			scrolled.setContent(control);
		} else {
			scrolled.setContent(noEntryArea);
		}
	}

	/**
	 * Create a new item for info.
	 * 
	 * @param element
	 * @return ControlListItem
	 */
	private ControlListItem createNewItem(Object element) {
		final ControlListItem item = doCreateItem(control, element);
		item.setIndexListener(new ControlListItem.IndexListener() {
			public void selectNext() {
				Control[] children = control.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (item == children[i]) {
						if (i < children.length - 1) {
							setSelection(new StructuredSelection(children[i + 1].getData()));
						}
						break;
					}
				}
			}

			public void selectPrevious() {
				Control[] children = control.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (item == children[i]) {
						if (i > 0) {
							setSelection(new StructuredSelection(children[i - 1].getData()));
						}
						break;
					}
				}
			}

			public void select() {
				setSelection(new StructuredSelection(item.getData()));
			}
		});

		// Refresh to populate with the current tasks
		item.refresh();
		return item;
	}

	protected abstract ControlListItem doCreateItem(Composite parent, Object element);

	@Override
	protected ControlListItem doFindInputItem(Object element) {
		return null;
	}

	@Override
	protected ControlListItem doFindItem(Object element) {
		Control[] children = control.getChildren();
		for (Control child : children) {
			if (child.isDisposed() || child.getData() == null) {
				continue;
			}
			if (child.getData().equals(element)) {
				return (ControlListItem) child;
			}
		}
		return null;
	}

	@Override
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		if (usingElementMap()) {
			unmapElement(item);
		}
		item.dispose();
		add(new Object[] { element });
	}

	@Override
	public ScrolledComposite getControl() {
		return scrolled;
	}

	@Override
	protected List<?> getSelectionFromWidget() {
		Control[] children = control.getChildren();
		ArrayList<Object> selection = new ArrayList<Object>(children.length);
		for (Control child : children) {
			ControlListItem item = (ControlListItem) child;
			if (item.isSelected() && item.getData() != null) {
				selection.add(item.getData());
			}
		}
		return selection;
	}

	@Override
	protected void inputChanged(Object input, Object oldInput) {
		super.inputChanged(input, oldInput);
		refreshAll();
		doUpdateContent();
	}

	@Override
	protected void internalRefresh(Object element) {
		if (element == null) {
			return;
		}

		if (element.equals(getRoot())) {
			refreshAll();
			return;
		}
		Widget widget = findItem(element);
		if (widget == null) {
			add(new Object[] { element });
			return;
		}
		((ControlListItem) widget).refresh();

		updateMinSize();
	}

	private void updateMinSize() {
		// Update the minimum size
		Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolled.setMinSize(size);
	}

	public void remove(Object[] elements) {
		for (Object element : elements) {
			Widget item = doFindItem(element);
			if (item != null) {
				unmapElement(element);
				item.dispose();
			}
		}

		Control[] existingChildren = control.getChildren();
		for (int i = 0; i < existingChildren.length; i++) {
			ControlListItem item = (ControlListItem) existingChildren[i];
			item.updateColors(i);
		}
		control.layout(true);
		doUpdateContent();
	}

	@Override
	public void reveal(Object element) {
		Control control = doFindItem(element);
		if (control != null) {
			revealControl(control);
		}
	}

	private void revealControl(Control control) {
		Rectangle clientArea = scrolled.getClientArea();
		Point origin = scrolled.getOrigin();
		Point location = control.getLocation();
		Point size = control.getSize();
		if (location.y + size.y > origin.y + clientArea.height) {
			scrolled.setOrigin(origin.x, location.y + size.y - clientArea.height);
		}
		if (location.y < origin.y) {
			scrolled.setOrigin(origin.x, location.y);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setSelectionToWidget(List list, boolean reveal) {
		HashSet<Object> elements = new HashSet<Object>(list);
		Control[] children = control.getChildren();
		for (Control control : children) {
			ControlListItem child = (ControlListItem) control;
			boolean selected = elements.contains(child.getData());
			if (selected != child.isSelected()) {
				child.setSelected(selected);
			}
			if (reveal && selected) {
				revealControl(child);
				reveal = false;
			}
		}
	}

	/**
	 * Set focus on the current selection.
	 */
	public void setFocus() {
		Control[] children = control.getChildren();
		if (children.length > 0) {
			for (Control element : children) {
				ControlListItem item = (ControlListItem) element;
				if (item.isSelected()) {
					item.setFocus();
					return;
				}
			}
			children[0].setFocus();
		} else {
			noEntryArea.setFocus();
		}
	}

	/**
	 * Refresh everything as the root is being refreshed.
	 */
	private void refreshAll() {
		Object[] infos = getSortedChildren(getRoot());
		Control[] existingChildren = control.getChildren();

		for (Control element : existingChildren) {
			element.dispose();
		}

		for (int i = 0; i < infos.length; i++) {
			ControlListItem item = createNewItem(infos[i]);
			item.updateColors(i);
		}

		control.layout(true);
		doUpdateContent();
	}

	/**
	 * Set the virtual items to be visible or not depending on the displayed area.
	 */
	private void updateVisibleItems() {
		Control[] children = control.getChildren();
		int top = scrolled.getOrigin().y;
		int bottom = top + scrolled.getParent().getBounds().height;
		for (Control element : children) {
			ControlListItem item = (ControlListItem) element;
			item.setDisplayed(top, bottom);
		}
	}

}
