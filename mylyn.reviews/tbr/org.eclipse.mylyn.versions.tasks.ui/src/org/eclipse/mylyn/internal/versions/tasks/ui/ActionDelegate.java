/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;

/**
 * Delegates all calls to a delegate IAction.
 * This allows to override methods.
 *
 * @author Kilian Matt
 *
 */
public abstract class ActionDelegate implements IAction {
	private final IAction delegate;

	public ActionDelegate(IAction delegate) {
		this.delegate = delegate;
		Assert.isNotNull(delegate);
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		delegate.addPropertyChangeListener(listener);
	}

	public int getAccelerator() {
		return delegate.getAccelerator();
	}

	public String getActionDefinitionId() {
		return delegate.getActionDefinitionId();
	}

	public String getDescription() {
		return delegate.getDescription();
	}

	public ImageDescriptor getDisabledImageDescriptor() {
		return delegate.getDisabledImageDescriptor();
	}

	public HelpListener getHelpListener() {
		return delegate.getHelpListener();
	}

	public ImageDescriptor getHoverImageDescriptor() {
		return delegate.getHoverImageDescriptor();
	}

	public String getId() {
		return delegate.getId();
	}

	public ImageDescriptor getImageDescriptor() {
		return delegate.getImageDescriptor();
	}

	public IMenuCreator getMenuCreator() {
		return delegate.getMenuCreator();
	}

	public int getStyle() {
		return delegate.getStyle();
	}

	public String getText() {
		return delegate.getText();
	}

	public String getToolTipText() {
		return delegate.getToolTipText();
	}

	public boolean isChecked() {
		return delegate.isChecked();
	}

	public boolean isEnabled() {
		return delegate.isEnabled();
	}

	public boolean isHandled() {
		return delegate.isHandled();
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		delegate.removePropertyChangeListener(listener);
	}

	public void run() {
		delegate.run();
	}

	public void runWithEvent(Event event) {
		delegate.runWithEvent(event);
	}

	public void setActionDefinitionId(String id) {
		delegate.setActionDefinitionId(id);
	}

	public void setChecked(boolean checked) {
		delegate.setChecked(checked);
	}

	public void setDescription(String text) {
		delegate.setDescription(text);
	}

	public void setDisabledImageDescriptor(ImageDescriptor newImage) {
		delegate.setDisabledImageDescriptor(newImage);
	}

	public void setEnabled(boolean enabled) {
		delegate.setEnabled(enabled);
	}

	public void setHelpListener(HelpListener listener) {
		delegate.setHelpListener(listener);

	}

	public void setHoverImageDescriptor(ImageDescriptor newImage) {
		delegate.setHoverImageDescriptor(newImage);
	}

	public void setId(String id) {
		delegate.setId(id);
	}

	public void setImageDescriptor(ImageDescriptor newImage) {
		delegate.setImageDescriptor(newImage);
	}

	public void setMenuCreator(IMenuCreator creator) {
		delegate.setMenuCreator(creator);
	}

	public void setText(String text) {
		delegate.setText(text);
	}

	public void setToolTipText(String text) {
		delegate.setToolTipText(text);
	}

	public void setAccelerator(int keycode) {
		delegate.setAccelerator(keycode);
	}
}
