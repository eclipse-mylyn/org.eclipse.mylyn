/*******************************************************************************
 * Copyright (c) 2011, 2012 SAP and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sascha Scholz (SAP) - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Sascha Scholz
 * @author Steffen Pingel
 */
public interface ChangeDetailService extends com.google.gerrit.common.data.ChangeDetailService {

	void patchSetDetail(PatchSet.Id keyA, PatchSet.Id keyB, AccountDiffPreference diffPrefs,
			AsyncCallback<PatchSetDetail> callback);

	void patchSetDetail2(PatchSet.Id keyA, PatchSet.Id keyB, AccountDiffPreference diffPrefs,
			AsyncCallback<PatchSetDetail> callback);

	void patchSetPublishDetailX(PatchSet.Id key, AsyncCallback<PatchSetPublishDetailX> callback);

	void changeDetailX(Change.Id id, AsyncCallback<ChangeDetailX> callback);

}
