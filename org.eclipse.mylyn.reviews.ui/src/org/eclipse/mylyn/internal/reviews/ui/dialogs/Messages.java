/*******************************************************************************
 * Copyright (c) 2014 Ericsson AB and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @author Guy Perron
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.reviews.ui.dialogs.messages"; //$NON-NLS-1$

	public static String ReviewsCommentToolTip;

	public static String CommentInputDialog_Draft;

	public static String CommentInputDialog_Done;

	public static String CommentInputDialog_Reply;

	public static String CommentInputDialog_ReplyDone;

	public static String CommentInputDialog_Discard;

	public static String CommentInputDialog_Edit;

	public static String CommentInputDialog_Save;

	public static String CommandServerOperation;

	public static String CommentInputDialog_ServerError;

	public static String CommentInputDialog_No_author;

	public static String CommentInputDialog_ConfirmExit;

	public static String CommentInputDialog_ConfirmExitCaption;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
