/*******************************************************************************
 * Copyright (c) 2011 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
