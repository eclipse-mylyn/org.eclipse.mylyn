/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
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

import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Steffen Pingel
 */
public interface PatchDetailService extends com.google.gerrit.common.data.PatchDetailService {

	void addReviewers(Change.Id id, List<String> reviewers, boolean confirmed, AsyncCallback<ReviewerResult> callback);

	void patchScriptX(Patch.Key key, PatchSet.Id a, PatchSet.Id b, AccountDiffPreference diffPrefs,
			AsyncCallback<PatchScriptX> callback);

}
