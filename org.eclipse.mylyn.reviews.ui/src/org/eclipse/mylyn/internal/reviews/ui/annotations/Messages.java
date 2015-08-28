/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.annotations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.reviews.ui.annotations.messages"; //$NON-NLS-1$

	public static String CommentAnnotation_X_dash_Y;

	public static String CommentAnnotationHover_Multiple_comments;

	public static String CommandServerOperation;

	public static String CommentPopupDialog_ServerError;

	public static String CommentPopupDialog_ReplyDone;

	public static String CommentPopupDialog_Discard;

	public static String CommentPopupDialog_Cancel;

	public static String CommentPopupDialog_Save;

	public static String CommentPopupDialog_Done;

	public static String CommentPopupDialog_HelpText;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
