/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;
import org.eclipse.mylyn.internal.builds.ui.commands.CopyDetailsHandler.Mode;

/**
 * @author Steffen Pingel
 */
public class CopyDetailsParameterValues implements IParameterValues {

	@Override
	@SuppressWarnings("rawtypes")
	public Map getParameterValues() {
		Map<String, String> result = new HashMap<>();
		for (Mode mode : CopyDetailsHandler.Mode.values()) {
			result.put(mode.toString(), mode.name());
		}
		return result;
	}

}
