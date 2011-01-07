// Copyright 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gwtorm.jdbc.gen;

import com.google.gwtorm.client.OrmException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hacked ClassLoader to inject generated code into the parent.
 * <p>
 * This ClassLoader allows our code generators to inject their generated classes
 * into the parent ClassLoader, which should be the same ClassLoader that
 * defined the application's Schema interface extension. This is necessary to
 * ensure the generated classes can access protected and default-access fields
 * within the entities.
 */
public class GeneratedClassLoader extends ClassLoader {
  private static final boolean debugCodeGen;

  private static final Method defineClass;

  static {
    debugCodeGen = "true".equals(System.getProperty("gwtorm.debugCodeGen"));

    Method m;
    try {
      m =
          ClassLoader.class.getDeclaredMethod("defineClass", String.class,
              byte[].class, Integer.TYPE, Integer.TYPE);
      m.setAccessible(true);
    } catch (SecurityException e) {
      throw new LinkageError("No defineClass in ClassLoader");
    } catch (NoSuchMethodException e) {
      throw new LinkageError("No defineClass in ClassLoader");
    }
    defineClass = m;
  }

  public GeneratedClassLoader(final ClassLoader parent) {
    super(parent);
  }

  public void defineClass(final String name, final byte[] code)
      throws OrmException {
    if (debugCodeGen) {
      final File outClassFile =
          new File("generated_classes/" + name.replace('.', '/') + ".class");
      outClassFile.getParentFile().mkdirs();
      try {
        final FileOutputStream out = new FileOutputStream(outClassFile);
        try {
          out.write(code);
        } finally {
          out.close();
        }
      } catch (IOException e) {
        throw new OrmException("Cannot save debug class " + outClassFile, e);
      }
    }

    try {
      defineClass.invoke(getParent(), name, code, Integer.valueOf(0), Integer
          .valueOf(code.length));
    } catch (IllegalArgumentException e) {
      throw new OrmException("Unable to inject class " + name, e);
    } catch (SecurityException e) {
      throw new OrmException("Unable to inject class " + name, e);
    } catch (IllegalAccessException e) {
      throw new OrmException("Unable to inject class " + name, e);
    } catch (InvocationTargetException e) {
      throw new OrmException("Unable to inject class " + name, e);
    }
  }
}
