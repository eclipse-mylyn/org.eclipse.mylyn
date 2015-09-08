/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	@SuppressWarnings("rawtypes")
	public Map getParameterValues() {
		Map<String, String> result = new HashMap<String, String>();
		for (Mode mode : CopyDetailsHandler.Mode.values()) {
			result.put(mode.toString(), mode.name());
		}
		return result;
	}

}
