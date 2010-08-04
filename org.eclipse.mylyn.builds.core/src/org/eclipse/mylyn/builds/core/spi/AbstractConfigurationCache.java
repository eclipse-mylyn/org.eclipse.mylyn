/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.builds.core.spi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.builds.core.BuildsCorePlugin;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractConfigurationCache<C extends Serializable> {

	private final File cacheFile;

	private Map<String, C> configurationByUrl;

	public AbstractConfigurationCache(File cacheFile) {
		Assert.isNotNull(cacheFile);
		this.cacheFile = cacheFile;
	}

	protected abstract C createConfiguration();

	public void flush() {
		if (cacheFile == null || configurationByUrl == null) {
			return;
		}
	}

	public C getConfiguration(String url) {
		initialize();
		C configuaration = configurationByUrl.get(url);
		if (configuaration == null) {
			configuaration = createConfiguration();
			configurationByUrl.put(url, configuaration);
		}
		return configuaration;
	}

	protected void initialize() {
		if (configurationByUrl == null) {
			configurationByUrl = new HashMap<String, C>();
			readCache();
		}
	}

	protected void readCache() {
		if (cacheFile == null || !cacheFile.exists()) {
			return;
		}

		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(cacheFile));
			int size = in.readInt();
			for (int i = 0; i < size; i++) {
				String url = (String) in.readObject();
				C data = readConfiguration(in);
				if (url != null && data != null) {
					configurationByUrl.put(url, data);
				}
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.WARNING, BuildsCorePlugin.ID_PLUGIN,
					"The configuration cache could not be read", e)); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

	}

	protected abstract C readConfiguration(ObjectInputStream in) throws IOException, ClassNotFoundException;

	public void setConfiguration(String url, C configuration) {
		initialize();
		configurationByUrl.put(url, configuration);
		writeCache();
	}

	protected void writeCache() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(cacheFile));
			out.writeInt(configurationByUrl.size());
			for (String url : configurationByUrl.keySet()) {
				out.writeObject(url);
				out.writeObject(configurationByUrl.get(url));
			}
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.WARNING, BuildsCorePlugin.ID_PLUGIN,
					"The respository configuration cache could not be written", e)); //$NON-NLS-1$
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

}
