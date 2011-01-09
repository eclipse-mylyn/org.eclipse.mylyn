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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeDetailService;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Change.Id;
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

		public abstract void execute(SubMonitor monitor) throws GerritException;

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
	public ChangeDetail getReview(String reviewId, IProgressMonitor monitor) throws GerritException {
		final Id id = new Id(Integer.parseInt(reviewId));
		return execute(monitor, new GerritOperation<ChangeDetail>() {
			@Override
			public void execute(SubMonitor monitor) throws GerritException {
				getChangeDetailService().changeDetail(id, this);
			}
		});
//				Change change = changeDetail.getChange();
//				changeDetail.getCurrentPatchSetDetail().getInfo().getMessage();
//				changeResult = new GerritTask("" + change.getChangeId(), change.getSubject());
//				changeResult.setOwner(change.getOwner().toString());
//				changeResult.setBranch(change.getDest().get());
//				changeResult.setProject(change.getProject().get());
//				changeResult.setStatus(change.getStatus().toString());
//				changeResult.setUploaded(change.getCreatedOn());
//				changeResult.setUpdated(change.getLastUpdatedOn());
//				changeResult.setChangeId(change.getKey().get());
//				changeResult.setDescription(changeDetail.getDescription());
//				getChangeDetailDone = true;
	}

	/**
	 * Returns the latest 25 reviews.
	 */
	public List<ChangeInfo> queryAllReviews(IProgressMonitor monitor) throws GerritException {
		SingleListChangeInfo sl = execute(monitor, new GerritOperation<SingleListChangeInfo>() {
			@Override
			public void execute(SubMonitor monitor) throws GerritException {
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
			public void execute(SubMonitor monitor) throws GerritException {
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
		Account acount = execute(monitor, new GerritOperation<Account>() {
			@Override
			public void execute(SubMonitor monitor) throws GerritException {
				getAccountService().myAccount(this);
			}
		});

		synchronized (this) {
			myAcount = acount;
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

	protected <T> T execute(IProgressMonitor monitor, GerritOperation<T> operation) throws GerritException {
		try {
			GerritRequest.setCurrentRequest(new GerritRequest(monitor));
			operation.execute(SubMonitor.convert(monitor));
			if (operation.getException() != null) {
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

}
