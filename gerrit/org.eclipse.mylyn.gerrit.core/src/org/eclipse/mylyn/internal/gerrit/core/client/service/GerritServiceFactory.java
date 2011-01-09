/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;

import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.ChangeDetailService;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.ChangeManageService;

/**
 * Factory class that produces the implemented services.
 * 
 * @author Daniel Olsson, ST Ericsson
 * @author Ingemar Olsén, Sony Ericsson
 */
public class GerritServiceFactory {

	GerritHttpClient gerritHttpClient;

	/**
	 * Constructor.
	 * 
	 * @param gerritHttpClient
	 *            An instance of AbstractGerritHttpClient that handles the communication to Gerrit
	 */
	public GerritServiceFactory(GerritHttpClient gerritHttpClient) {
		this.gerritHttpClient = gerritHttpClient;
	}

	/**
	 * Gets the ChangeListService for the AbstractGerritHttpClient.
	 * 
	 * @return The ChangeListService for the AbstractGerritHttpClient.
	 */
	public ChangeListService getChangeListService() {
		InvocationHandler handler = new GerritService(gerritHttpClient, "/gerrit/rpc/ChangeListService");
		return (ChangeListService) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { ChangeListService.class }, handler);
		//return new ChangeListServiceImpl(gerritHttpClient);
	}

	/**
	 * Gets the ChangeDetailService for the AbstractGerritHttpClient.
	 * 
	 * @return The ChangeDetailService for the AbstractGerritHttpClient.
	 */
	public ChangeDetailService getChangeDetailService() {
		InvocationHandler handler = new GerritService(gerritHttpClient, "/gerrit/rpc/ChangeDetailService");
		return (ChangeDetailService) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { ChangeDetailService.class }, handler);
	}

	/**
	 * Gets the AccountService for the AbstractGerritHttpClient.
	 * 
	 * @return The AccountService for the AbstractGerritHttpClient.
	 */
	public AccountService getAccountService() {
		InvocationHandler handler = new GerritService(gerritHttpClient, "/gerrit/rpc/AccountService");
		return (AccountService) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { AccountService.class }, handler);
	}

	public ChangeManageService getChangeManageService() {
		InvocationHandler handler = new GerritService(gerritHttpClient, "/gerrit/rpc/ChangeManageService");
		return (ChangeManageService) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { ChangeManageService.class }, handler);
	}

}
