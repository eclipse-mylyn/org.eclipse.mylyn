package org.eclipse.mylyn.wikitext.toolkit;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class TestResources {

	public static String load(Class<?> relativeToClass,String path) {
		try {
			URL url = relativeToClass.getResource(path);
			checkNotNull(url,"Resource %s not found relative to %s",path,relativeToClass.getClass().getName());
			return Resources.toString(url, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}
