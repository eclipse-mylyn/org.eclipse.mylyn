/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.commons.repositories.core.auth.CertificateCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class CommonTestUtil {

	public enum PrivilegeLevel {
		ADMIN, ANONYMOUS, GUEST, READ_ONLY, USER
	}

	public static final String KEY_CREDENTIALS_FILE = "mylyn.credentials";

	private final static int MAX_RETRY = 5;

	/**
	 * Returns the given file path with its separator character changed from the given old separator to the given new
	 * separator.
	 * 
	 * @param path
	 *            a file path
	 * @param oldSeparator
	 *            a path separator character
	 * @param newSeparator
	 *            a path separator character
	 * @return the file path with its separator character changed from the given old separator to the given new
	 *         separator
	 */
	public static String changeSeparator(String path, char oldSeparator, char newSeparator) {
		return path.replace(oldSeparator, newSeparator);
	}

	/**
	 * Copies the given source file to the given destination file.
	 */
	public static void copy(File source, File dest) throws IOException {
		InputStream in = new FileInputStream(source);
		try {
			OutputStream out = new FileOutputStream(dest);
			try {
				transferData(in, out);
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Copies all files in the current data directory to the specified folder. Will overwrite.
	 */
	public static void copyFolder(File sourceFolder, File targetFolder) throws IOException {
		for (File sourceFile : sourceFolder.listFiles()) {
			if (sourceFile.isFile()) {
				File destFile = new File(targetFolder, sourceFile.getName());
				copy(sourceFile, destFile);
			}
		}
	}

	/**
	 * Copies all files in the current data directory to the specified folder. Will overwrite.
	 */
	public static void copyFolderRecursively(File sourceFolder, File targetFolder) throws IOException {
		for (File sourceFile : sourceFolder.listFiles()) {
			if (sourceFile.isFile()) {
				File destFile = new File(targetFolder, sourceFile.getName());
				copy(sourceFile, destFile);
			} else if (sourceFile.isDirectory()) {
				File destDir = new File(targetFolder, sourceFile.getName());
				if (!destDir.exists()) {
					if (!destDir.mkdir()) {
						throw new IOException("Unable to create destination folder: " + destDir.getAbsolutePath());
					}
				}
				copyFolderRecursively(sourceFile, destDir);
			}
		}
	}

	public static File createTempFileInPlugin(Plugin plugin, IPath path) {
		IPath stateLocation = plugin.getStateLocation();
		stateLocation = stateLocation.append(path);
		return stateLocation.toFile();
	}

	public static File createTempFolder(String prefix) throws IOException {
		File location = File.createTempFile(prefix, null);
		location.delete();
		location.mkdirs();
		return location;
	}

	public static void delete(File file) {
		if (file.exists()) {
			for (int i = 0; i < MAX_RETRY; i++) {
				if (file.delete()) {
					i = MAX_RETRY;
				} else {
					try {
						Thread.sleep(1000); // sleep a second
					} catch (InterruptedException e) {
						// don't need to catch this
					}
				}
			}
		}
	}

	public static void deleteFolder(File path) {
		if (path.isDirectory()) {
			for (File file : path.listFiles()) {
				file.delete();
			}
			path.delete();
		}
	}

	public static void deleteFolderRecursively(File path) {
		File[] files = path.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolderRecursively(file);
				} else {
					file.delete();
				}
			}
		}
		path.delete();
	}

	public static CertificateCredentials getCertificateCredentials() {
		File keyStoreFile;
		try {
			keyStoreFile = CommonTestUtil.getFile(CommonTestUtil.class, "testdata/keystore");
			String password = CommonTestUtil.getUserCredentials().getPassword();
			return new CertificateCredentials(keyStoreFile.getAbsolutePath(), password, null);
		} catch (IOException cause) {
			AssertionFailedError e = new AssertionFailedError("Failed to load keystore file");
			e.initCause(cause);
			throw e;
		}
	}

	public static boolean hasCredentials(PrivilegeLevel level) {
		try {
			CommonTestUtil.getCredentials(level);
			return true;
		} catch (AssertionFailedError error) {
			return false;
		}
	}

	public static UserCredentials getCredentials(PrivilegeLevel level) {
		return getCredentials(level, null);
	}

	public static UserCredentials getCredentials(PrivilegeLevel level, String realm) {
		Properties properties = new Properties();
		try {
			File file;
			String filename = System.getProperty(KEY_CREDENTIALS_FILE);
			if (filename != null) {
				// 1. use user specified file
				file = new File(filename);
			} else {
				// 2. check in home directory
				file = new File(new File(System.getProperty("user.home"), ".mylyn"), "credentials.properties");
				if (!file.exists()) {
					// 3. fall back to included credentials file
					file = getFile(CommonTestUtil.class, "testdata/credentials.properties");
				}
			}
			properties.load(new FileInputStream(file));
		} catch (Exception e) {
			AssertionFailedError error = new AssertionFailedError(
					"must define credentials in $HOME/.mylyn/credentials.properties");
			error.initCause(e);
			throw error;
		}

		String defaultPassword = properties.getProperty("pass");

		realm = (realm != null) ? realm + "." : "";
		switch (level) {
		case ANONYMOUS:
			return createCredentials(properties, realm + "anon.", "", "");
		case GUEST:
			return createCredentials(properties, realm + "guest.", "guest@mylyn.eclipse.org", defaultPassword);
		case USER:
			return createCredentials(properties, realm, "tests@mylyn.eclipse.org", defaultPassword);
		case READ_ONLY:
			return createCredentials(properties, realm, "read-only@mylyn.eclipse.org", defaultPassword);
		case ADMIN:
			return createCredentials(properties, realm + "admin.", "admin@mylyn.eclipse.org", null);
		}

		throw new AssertionFailedError("invalid privilege level");
	}

	public static File getFile(Object source, String filename) throws IOException {
		Class<?> clazz = (source instanceof Class<?>) ? (Class<?>) source : source.getClass();
		if (Platform.isRunning()) {
			ClassLoader classLoader = clazz.getClassLoader();
			if (classLoader instanceof DefaultClassLoader) {
				// TODO e3.5 replace with: URL url = ((BundleClassLoader) classLoader).getBundle().getEntry(filename);
				URL url = ((DefaultClassLoader) classLoader).getClasspathManager()
						.getBaseData()
						.getBundle()
						.getEntry(filename);
				if (url != null) {
					URL localURL = FileLocator.toFileURL(url);
					return new File(localURL.getFile());
				}
			}
		} else {
			URL localURL = clazz.getResource("");
			String path = URLDecoder.decode(localURL.getFile(), Charset.defaultCharset().name());
			int i = path.indexOf("!");
			if (i != -1) {
				int j = path.lastIndexOf(File.separatorChar, i);
				if (j != -1) {
					path = path.substring(0, j) + File.separator;
				} else {
					throw new AssertionFailedError("Unable to determine location for '" + filename + "' at '" + path
							+ "'");
				}
				// class file is nested in jar, use jar path as base
				if (path.startsWith("file:")) {
					path = path.substring(5);
				}
				return new File(path + filename);
			} else {
				// remove all package segments from name
				String directory = clazz.getName().replaceAll("[^.]", "");
				directory = directory.replaceAll(".", "../");
				if (path.contains("/bin/")) {
					// account for bin/ when running from Eclipse workspace
					directory += "../";
				} else if (path.contains("/target/classes/")) {
					// account for bin/ when running from Eclipse workspace
					directory += "../../";
				}
				filename = path + (directory + filename).replaceAll("/", Matcher.quoteReplacement(File.separator));
				return new File(filename).getCanonicalFile();
			}
		}
		throw new AssertionFailedError("Could not locate " + filename);
	}

	public static InputStream getResource(Object source, String filename) throws IOException {
		Class<?> clazz = (source instanceof Class<?>) ? (Class<?>) source : source.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream in = classLoader.getResourceAsStream(filename);
		if (in == null) {
			File file = getFile(source, filename);
			if (file != null) {
				return new FileInputStream(file);
			}
		}
		if (in == null) {
			throw new IOException(NLS.bind("Failed to locate ''{0}'' for ''{1}''", filename, clazz.getName()));
		}
		return in;
	}

	public static UserCredentials getUserCredentials() {
		return getCredentials(PrivilegeLevel.USER, null);
	}

	public static String read(File source) throws IOException {
		InputStream in = new FileInputStream(source);
		try {
			StringBuilder sb = new StringBuilder();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				sb.append(new String(buf, 0, len));
			}
			return sb.toString();
		} finally {
			in.close();
		}
	}

	/**
	 * Returns whether to run a limited suite of tests. Returns true, unless a system property has been set to force
	 * running of all tests.
	 */
	public static boolean runHeartbeatTestsOnly() {
		return !Boolean.parseBoolean(System.getProperty("org.eclipse.mylyn.tests.all"));
	}

	/**
	 * Unzips the given zip file to the given destination directory extracting only those entries the pass through the
	 * given filter.
	 * 
	 * @param zipFile
	 *            the zip file to unzip
	 * @param dstDir
	 *            the destination directory
	 * @throws IOException
	 *             in case of problem
	 */
	public static void unzip(ZipFile zipFile, File dstDir) throws IOException {
		unzip(zipFile, dstDir, dstDir, 0);
	}

	public static void write(String fileName, StringBuffer content) throws IOException {
		Writer writer = new FileWriter(fileName);
		try {
			writer.write(content.toString());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// don't need to catch this
			}
		}
	}

	private static UserCredentials createCredentials(Properties properties, String prefix, String defaultUsername,
			String defaultPassword) {
		String username = properties.getProperty(prefix + "user");
		String password = properties.getProperty(prefix + "pass");

		if (username == null) {
			username = defaultUsername;
		}

		if (password == null) {
			password = defaultPassword;
		}

		if (username == null || password == null) {
			throw new AssertionFailedError("username or password not found for " + prefix
					+ " in <plug-in dir>/credentials.properties, make sure file is valid");
		}

		return new UserCredentials(username, password);
	}

	/**
	 * Copies all bytes in the given source stream to the given destination stream. Neither streams are closed.
	 * 
	 * @param source
	 *            the given source stream
	 * @param destination
	 *            the given destination stream
	 * @throws IOException
	 *             in case of error
	 */
	private static void transferData(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
	}

	private static void unzip(ZipFile zipFile, File rootDstDir, File dstDir, int depth) throws IOException {

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		try {
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				String entryName = entry.getName();
				File file = new File(dstDir, changeSeparator(entryName, '/', File.separatorChar));
				file.getParentFile().mkdirs();
				InputStream src = null;
				OutputStream dst = null;
				try {
					src = zipFile.getInputStream(entry);
					dst = new FileOutputStream(file);
					transferData(src, dst);
				} finally {
					if (dst != null) {
						try {
							dst.close();
						} catch (IOException e) {
							// don't need to catch this
						}
					}
					if (src != null) {
						try {
							src.close();
						} catch (IOException e) {
							// don't need to catch this
						}
					}
				}
			}
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				// don't need to catch this
			}
		}
	}

	public static boolean isCertificateAuthBroken() {
		// not entirely correct since 1.6.0_3 would also satisfy this check but it should be sufficient in reality
		return new VersionRange("[0.0.0,1.6.0.25]").isIncluded(CoreUtil.getRuntimeVersion());
	}

	public static boolean hasCertificateCredentials() {
		try {
			CommonTestUtil.getCertificateCredentials();
			return true;
		} catch (AssertionFailedError error) {
			return false;
		}
	}

	public static String getShortUserName(UserCredentials credentials) {
		String username = credentials.getUserName();
		if (username.contains("@")) {
			return username.substring(0, username.indexOf("@"));
		}
		return username;
	}

	public static boolean fixProxyConfiguration() {
		if (Platform.isRunning() && CommonsNetPlugin.getProxyService() != null
				&& CommonsNetPlugin.getProxyService().isSystemProxiesEnabled()
				&& !CommonsNetPlugin.getProxyService().hasSystemProxies()) {
			// XXX e3.5/gtk.x86_64 activate manual proxy configuration which
			// defaults to Java system properties if system proxy support is
			// not available
			System.err.println("Forcing manual proxy configuration");
			CommonsNetPlugin.getProxyService().setSystemProxiesEnabled(false);
			CommonsNetPlugin.getProxyService().setProxiesEnabled(true);
			return true;
		}
		return false;
	}

	public static void dumpSystemInfo(PrintStream out) {
		Properties p = System.getProperties();
		if (Platform.isRunning()) {
			p.put("build.system", Platform.getOS() + "-" + Platform.getOSArch() + "-" + Platform.getWS());
		} else {
			p.put("build.system", "standalone");
		}
		String info = "System: ${os.name} ${os.version} (${os.arch}) / ${build.system} / ${java.vendor} ${java.vm.name} ${java.version}";
		for (Entry<Object, Object> entry : p.entrySet()) {
			info = info.replaceFirst(Pattern.quote("${" + entry.getKey() + "}"), entry.getValue().toString());
		}
		out.println(info);
		out.print("Proxy : " + WebUtil.getProxyForUrl("http://mylyn.eclipse.org") + " (Platform)");
		try {
			out.print(" / " + ProxySelector.getDefault().select(new URI("http://mylyn.eclipse.org")) + " (Java)");
		} catch (URISyntaxException e) {
			// ignore
		}
		out.println();
		out.println();
	}

}
