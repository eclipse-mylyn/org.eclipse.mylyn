/*******************************************************************************
 * Copyright (c) 2010, 2024 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.commands;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.mylyn.htmltext.HtmlComposer;
import org.eclipse.mylyn.htmltext.model.TriState;

/**
 * A command is a wrapper for a JavaScript command which is executed in the context of a {@link HtmlComposer}. It can
 * has a state (see {@link TriState} )which is calculated by the {@link HtmlComposer} if possible.
 *
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public abstract class Command {

	protected HtmlComposer composer;

	private TriState state;

	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public void execute() {
		composer.execute(this);
	}

	public Object executeWithReturn() {
		return composer.executeWithReturn(this);
	}

	public abstract String getCommandIdentifier();

	protected String getCommandDefinitionStub() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Returns the "native" JavaScript command which executed against a {@link HtmlComposer}
	 *
	 * @return the command to execute
	 */
	public String getCommand() {
		return "integration.executeCommand('" + getCommandIdentifier() + "');"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Returns the current state of the command. Can change if the selected dom node within the editor changes
	 *
	 * @return
	 */
	public TriState getState() {
		return state;
	}

	/**
	 * Sets the state of the command. It's not intended that clients are setting the state.
	 *
	 * @param state
	 *            the state to set
	 */
	public void setState(TriState state) {
		TriState oldValue = this.state;
		this.state = state;
		firePropertyChange("state", oldValue, state); //$NON-NLS-1$
	}

	public void addPropertyChangeListener(final PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(final String prop, final Object old, final Object newValue) {
		if (listeners.hasListeners(prop)) {
			listeners.firePropertyChange(prop, old, newValue);
		}
	}

	public void setComposer(HtmlComposer composer) {
		this.composer = composer;
		if (trackCommand()) {
			composer.trackCommand(this);
		}
	}

	/**
	 * Indicates whether the commands state should be set by the {@link HtmlComposer}. If the editor should track this
	 * event the underlying ckeditor must be a command defined which has the same id like in
	 * {@link #getCommandIdentifier()}.
	 *
	 * @return <code>true</code> if the composer should set changes to the commands state, else <code>false</code>.
	 */
	protected boolean trackCommand() {
		return true;
	}

	public void dispose() {
		// does nothing by default
	}

}
