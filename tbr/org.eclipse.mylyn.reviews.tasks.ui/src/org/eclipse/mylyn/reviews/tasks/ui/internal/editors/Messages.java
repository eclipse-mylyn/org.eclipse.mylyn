/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

import org.eclipse.osgi.util.NLS;

/*
 * @author Kilian Matt
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.reviews.tasks.ui.internal.editors.messages"; //$NON-NLS-1$
	public static String EditorSupport_Original;
	public static String EditorSupport_Patched;
	public static String ReviewSummaryTaskEditorPart_Header_Result;
	public static String ReviewSummaryTaskEditorPart_Partname;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
