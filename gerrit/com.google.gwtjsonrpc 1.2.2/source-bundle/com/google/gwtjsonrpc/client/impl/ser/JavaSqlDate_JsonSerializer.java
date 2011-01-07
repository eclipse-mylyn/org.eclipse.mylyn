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

package com.google.gwtjsonrpc.client.impl.ser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtjsonrpc.client.impl.JsonSerializer;
import com.google.gwtjsonrpc.client.impl.ResultDeserializer;

import java.sql.Date;

/** Default serialization for a {@link java.sql.Date}. */
public final class JavaSqlDate_JsonSerializer extends
    JsonSerializer<java.sql.Date> implements ResultDeserializer<java.sql.Date> {
  public static final JavaSqlDate_JsonSerializer INSTANCE =
      new JavaSqlDate_JsonSerializer();

  @Override
  public java.sql.Date fromJson(final Object o) {
    if (o != null) {
      return parseDate((String) o);
    }
    return null;
  }

  @Override
  public void printJson(final StringBuilder sb, final java.sql.Date o) {
    sb.append('"');
    sb.append(toString(o.getTime()));
    sb.append('"');
  }

  private static native String toString(double utcMilli)
  /*-{
    var d = new Date(utcMilli);
    var p2 = @com.google.gwtjsonrpc.client.impl.ser.JavaSqlTimestamp_JsonSerializer::padTwo(I);
    return d.getUTCFullYear() + "-" +
    p2(1 + d.getUTCMonth()) + "-" +
    p2(d.getUTCDate());
  }-*/;

  @SuppressWarnings("deprecation")
  protected static java.sql.Date parseDate(final String s) {
    final String[] split = s.split("-");
    if (split.length != 3) {
      throw new IllegalArgumentException("Invalid escape format: " + s);
    }

    if (split[1].startsWith("0")) {
      split[1] = split[1].substring(1);
    }
    if (split[2].startsWith("0")) {
      split[2] = split[2].substring(1);
    }
    try {
      // Years are relative to 1900
      final int y = Integer.valueOf(split[0]) - 1900;

      // Months are internally 0-based
      final int m = Integer.decode(split[1]) - 1;
      final int d = Integer.decode(split[2]);

      return new java.sql.Date(y, m, d);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid escape format: " + s);
    }
  }

  @Override
  public Date fromResult(JavaScriptObject responseObject) {
    return fromJson(PrimitiveResultDeserializers.stringResult(responseObject));
  }
}
