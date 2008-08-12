/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GCData;

/**
 * 
 * 
 * @author David Green
 */
public class MockDevice extends Device {

	@Override
	public void internal_dispose_GC(int arg0, GCData arg1) {
	}

	@Override
	public int internal_new_GC(GCData arg0) {
		return 0;
	}

}
