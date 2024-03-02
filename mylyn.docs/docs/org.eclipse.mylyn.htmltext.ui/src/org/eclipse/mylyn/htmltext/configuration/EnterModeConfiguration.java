/*******************************************************************************
 * Copyright (c) 2011, 2012 Tom Seidel, Remus Software and others.
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
package org.eclipse.mylyn.htmltext.configuration;


/**
 * Sets the behavior for the ENTER key. It also dictates other behaviour rules
 * in the editor, like whether the <br>
 * element is to be used as a paragraph separator when indenting text. The
 * allowed values are the following constants, and their relative behavior:
 * 
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.8
 * @noextend This class is not intended to be subclassed by clients.
 */
public class EnterModeConfiguration extends ConfigurationElement {

	public static enum EnterMode {

		BR("BR"), P("P"), DIV("DIV");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		private final String strRepresentation;

		EnterMode(String strRepresentation) {
			this.strRepresentation = strRepresentation;
		}

		public String getStrRepresentation() {
			return strRepresentation;
		}
	}

	public EnterModeConfiguration(EnterMode mode) {
		super("enterMode", mode); //$NON-NLS-1$
	}

	@Override
	protected EnterMode doGetDefaultValue() {
		return EnterMode.P;
	}

	@Override
	public String getValueForEditor() {
		return ((EnterMode) this.value).getStrRepresentation();
	}

}
