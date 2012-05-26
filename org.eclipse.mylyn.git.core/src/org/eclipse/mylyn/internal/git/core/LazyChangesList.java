/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.git.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.mylyn.versions.core.Change;

class LazyChangesList implements List<Change> {
	private final GitConnector connector;

	private List<Change> delegate;

	private final Repository repository;

	private final GitRepository scmRepository;

	private final RevCommit commit;

	public LazyChangesList(GitConnector gitConnector, GitRepository scmRepository, Repository gitRepo, RevCommit r) {
		this.connector = gitConnector;
		this.scmRepository = scmRepository;
		this.repository = gitRepo;
		this.commit = r;
	}

	private synchronized List<Change> getOrInitDelegate() {
		if (delegate == null) {
			delegate = new ArrayList<Change>();
			fetchChanges();
		}
		return delegate;

	}

	private void fetchChanges() {
		try {
			RevWalk walk = new RevWalk(repository);

			delegate.addAll(connector.diffCommit(scmRepository, repository, walk, commit));
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	public boolean add(Change arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public void add(int arg0, Change arg1) {
		throw new java.lang.UnsupportedOperationException();

	}

	public boolean addAll(Collection<? extends Change> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean addAll(int arg0, Collection<? extends Change> arg1) {
		throw new java.lang.UnsupportedOperationException();
	}

	public void clear() {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean contains(Object arg0) {
		return getOrInitDelegate().contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return getOrInitDelegate().containsAll(arg0);
	}

	public Change get(int arg0) {
		return getOrInitDelegate().get(arg0);
	}

	public int indexOf(Object arg0) {
		return getOrInitDelegate().indexOf(arg0);
	}

	public boolean isEmpty() {
		return getOrInitDelegate().isEmpty();
	}

	public Iterator<Change> iterator() {
		return getOrInitDelegate().iterator();
	}

	public int lastIndexOf(Object arg0) {
		return getOrInitDelegate().lastIndexOf(arg0);
	}

	public ListIterator<Change> listIterator() {
		return getOrInitDelegate().listIterator();
	}

	public ListIterator<Change> listIterator(int arg0) {
		return getOrInitDelegate().listIterator(arg0);
	}

	public boolean remove(Object arg0) {
		return getOrInitDelegate().remove(arg0);
	}

	public Change remove(int arg0) {
		return getOrInitDelegate().remove(arg0);
	}

	public boolean removeAll(Collection<?> arg0) {
		return getOrInitDelegate().removeAll(arg0);
	}

	public boolean retainAll(Collection<?> arg0) {
		return getOrInitDelegate().retainAll(arg0);
	}

	public Change set(int arg0, Change arg1) {
		return getOrInitDelegate().set(arg0, arg1);
	}

	public int size() {
		return getOrInitDelegate().size();
	}

	public List<Change> subList(int arg0, int arg1) {
		return getOrInitDelegate().subList(arg0, arg1);
	}

	public Object[] toArray() {
		return getOrInitDelegate().toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		return getOrInitDelegate().toArray(arg0);
	}
}