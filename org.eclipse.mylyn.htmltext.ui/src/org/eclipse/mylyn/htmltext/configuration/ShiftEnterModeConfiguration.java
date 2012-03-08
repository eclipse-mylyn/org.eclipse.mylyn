/*******************************************************************************
 * Copyright (c) 2011 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.htmltext.configuration;

import org.eclipse.mylyn.htmltext.configuration.EnterModeConfiguration.EnterMode;

/**
 * Just like the {@link EnterModeConfiguration} setting, it defines the behavior for
 * the SHIFT+ENTER key. The allowed values are the following constants, and
 * their relative behavior:
 * 
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.8
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ShiftEnterModeConfiguration extends ConfigurationElement {


	public ShiftEnterModeConfiguration(EnterMode mode) {
		super("shiftEnterMode", mode);
	}

	@Override
	protected EnterMode doGetDefaultValue() {
		return EnterMode.BR;
	}

	@Override
	public String getValueForEditor() {
		return ((EnterMode) this.value).getStrRepresentation();
	}

}
