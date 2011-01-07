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

package com.google.gwtorm.schema;


public class Util {
  private static int nameCounter;

  public static synchronized String createRandomName() {
    return "GwtOrm$$" + nameCounter++;
  }

  public static String makeSqlFriendly(final String name) {
    final StringBuilder r = new StringBuilder(name.length() + 8);
    boolean lastWasCap = true;
    for (int i = 0; i < name.length(); i++) {
      final char c = name.charAt(i);
      if (Character.isUpperCase(c)) {
        if (!lastWasCap) {
          r.append('_');
          lastWasCap = true;
        }
        r.append(Character.toLowerCase(c));
      } else if (c == '_') {
        lastWasCap = true;
        r.append(c);
      } else {
        lastWasCap = false;
        r.append(c);
      }
    }
    return r.toString();
  }

  public static String any(final String a, final String b) {
    if (a != null && a.length() > 0) {
      return a;
    }
    return b;
  }

  public static boolean samePackage(final String aName, final String bName) {
    return packageOf(aName).equals(packageOf(bName));
  }

  public static String packageOf(final String className) {
    final int end = className.lastIndexOf('.');
    if (end < 0) {
      return "";
    }
    return className.substring(0, end);
  }

  public static boolean isSqlPrimitive(final Class<?> type) {
    if (type == null || type == Void.TYPE) {
      return false;
    }
    if (type.isPrimitive()) {
      return true;
    }
    if (type == String.class) {
      return true;
    }
    if (type == java.sql.Date.class) {
      return true;
    }
    if (type == java.sql.Timestamp.class) {
      return true;
    }
    if (type == byte[].class) {
      return true;
    }
    return false;
  }

  private Util() {
  }
}
