/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.List;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Steffen Pingel
 */
public class GerritConfigX extends GerritConfig {

	private List<CommentLink> commentLinks;

	public List<CommentLink> getCommentLinks2() {
		return commentLinks;
	}

}