/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeDetailService;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.PatchDetailService;
import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.RemoteJsonService;

/**
 * Facade to the Gerrit RPC API.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritClient {

	private abstract class GerritOperation<T> implements AsyncCallback<T> {

		private Throwable exception;

		private T result;

		public abstract void execute(IProgressMonitor monitor) throws GerritException;

		public Throwable getException() {
			return exception;
		}

		public T getResult() {
			return result;
		}

		public void onFailure(Throwable exception) {
			this.exception = exception;
		}

		public void onSuccess(T result) {
			setResult(result);
		}

		protected void setResult(T result) {
			this.result = result;
		}
	}

	private final GerritHttpClient client;

	private Account myAcount;

	private final Map<Class<? extends RemoteJsonService>, RemoteJsonService> serviceByClass;

	public GerritClient(AbstractWebLocation location) {
		this.client = new GerritHttpClient(location);
		this.serviceByClass = new HashMap<Class<? extends RemoteJsonService>, RemoteJsonService>();
	}

	/**
	 * Returns the details for a specific review.
	 */
	public ChangeDetail getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		final Change.Id id = new Change.Id(reviewId);
		return execute(monitor, new GerritOperation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeDetailService().changeDetail(id, this);
			}
		});
	}

	public GerritSystemInfo getInfo(IProgressMonitor monitor) throws GerritException {
		Account account = execute(monitor, new GerritOperation<Account>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getAccountService().myAccount(this);
			}
		});
		return new GerritSystemInfo(account);
	}

	public PatchScript getPatchScript(final Patch.Key key, final PatchSet.Id leftId, final PatchSet.Id rightId,
			IProgressMonitor monitor) throws GerritException {
		final AccountDiffPreference diffPrefs = null;
		return execute(monitor, new GerritOperation<PatchScript>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().patchScript(key, leftId, rightId, diffPrefs, this);
			}
		});
	}

	public PatchSetDetail getPatchSetDetail(Change.Id changeId, int patchSetId, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(changeId, patchSetId);
		return execute(monitor, new GerritOperation<PatchSetDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeDetailService().patchSetDetail(id, this);
			}
		});
	}

	public int id(String id) throws GerritException {
		if (id == null) {
			throw new GerritException("Invalid ID (null)");
		}
		try {
			return Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new GerritException(NLS.bind("Invalid ID ('{0}')", id));
		}
	}

	/**
	 * Returns the latest 25 reviews.
	 */
	public List<ChangeInfo> queryAllReviews(IProgressMonitor monitor) throws GerritException {
		SingleListChangeInfo sl = execute(monitor, new GerritOperation<SingleListChangeInfo>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeListService().allQueryNext("status:open", "z", 25, this);
			}
		});
		return sl.getChanges();
	}

	/**
	 * Called to get all gerrit tasks associated with the id of the user. This includes all open, closed and reviewable
	 * reviews for the user.
	 */
	public List<ChangeInfo> queryMyReviews(IProgressMonitor monitor) throws GerritException {
		final Account account = getAccount(monitor);
		AccountDashboardInfo ad = execute(monitor, new GerritOperation<AccountDashboardInfo>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeListService().forAccount(account.getId(), this);
			}
		});

		List<ChangeInfo> allMyChanges = ad.getByOwner();
		allMyChanges.addAll(ad.getForReview());
		allMyChanges.addAll(ad.getClosed());
		return allMyChanges;
	}

	private Account getAccount(IProgressMonitor monitor) throws GerritException {
		synchronized (this) {
			if (myAcount != null) {
				return myAcount;
			}
		}
		Account account = execute(monitor, new GerritOperation<Account>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getAccountService().myAccount(this);
			}
		});

		synchronized (this) {
			myAcount = account;
		}
		return myAcount;
	}

	private AccountService getAccountService() {
		return getService(AccountService.class);
	}

	private ChangeDetailService getChangeDetailService() {
		return getService(ChangeDetailService.class);
	}

	private ChangeListService getChangeListService() {
		return getService(ChangeListService.class);
	}

	private PatchDetailService getPatchDetailService() {
		return getService(PatchDetailService.class);
	}

	protected <T> T execute(IProgressMonitor monitor, GerritOperation<T> operation) throws GerritException {
		try {
			GerritRequest.setCurrentRequest(new GerritRequest(monitor));
			operation.execute(monitor);
			if (operation.getException() instanceof GerritException) {
				throw (GerritException) operation.getException();
			} else if (operation.getException() != null) {
				GerritException e = new GerritException();
				e.initCause(operation.getException());
				throw e;
			}
			return operation.getResult();
		} finally {
			GerritRequest.setCurrentRequest(null);
		}
	}

	protected synchronized <T extends RemoteJsonService> T getService(Class<T> clazz) {
		RemoteJsonService service = serviceByClass.get(clazz);
		if (service == null) {
			service = GerritService.create(clazz, client);
			serviceByClass.put(clazz, service);
		}
		return clazz.cast(service);
	}

	public IReview getReview(String reviewId, IProgressMonitor monitor) throws GerritException {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		ChangeDetail detail = getChangeDetail(Integer.parseInt(reviewId), monitor);
		List<PatchSet> patchSets = detail.getPatchSets();
		for (PatchSet patchSet : patchSets) {
			//
		}
		if (detail.getCurrentPatchSetDetail() != null) {
			for (Patch patch : detail.getCurrentPatchSetDetail().getPatches()) {
				IFileItem item = ReviewsFactory.eINSTANCE.createFileItem();
				item.setName(patch.getFileName());
				review.getReviewItems().add(item);
			}
		}
		return review;
	}

}
