/*******************************************************************************
 * Copyright (c) 2011, 2024 Tom Seidel, Remus Software and others.
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
 * <p>
 * Whether automatically create wrapping blocks around inline contents inside
 * document body, this helps to ensure the integrality of the block enter mode.
 * </p>
 * <p>
 * Note: Changing the default value might introduce unpredictable usability
 * issues.
 * </p>
 * 
 * @author Tom Seidel <tom.seidel@remus-software.org>
 * @since 0.8
 * @noextend This class is not intended to be subclassed by clients.
 */
public class AutoparagrahConfiguration extends ConfigurationElement {
	
	public AutoparagrahConfiguration(boolean value) {
		super("autoParagraph", Boolean.valueOf(value)); //$NON-NLS-1$
	}

	@Override
	protected Object doGetDefaultValue() {
		return Boolean.FALSE;
	}

}
