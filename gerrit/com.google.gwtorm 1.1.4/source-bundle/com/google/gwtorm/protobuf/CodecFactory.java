// Copyright 2009 Google Inc.
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

package com.google.gwtorm.protobuf;

import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.jdbc.gen.GeneratedClassLoader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/** Creates {@link ProtobufCodec} implementations on demand. */
public final class CodecFactory {
  private static final Map<Class<?>, String> encoders =
      Collections.synchronizedMap(new WeakHashMap<Class<?>, String>());

  /**
   * Create an implementation to encode/decode an arbitrary object.
   * <p>
   * The object must use the {@link Column} annotations to denote the fields
   * that should be encoded or decoded.
   *
   * @param <T> type of the object to be supported.
   * @param type the object type.
   * @return an encoder for this object type.
   * @throws IllegalArgumentException the object's fields aren't declared
   *         properly. This is a programming error that cannot be recovered.
   */
  public static <T> ProtobufCodec<T> encoder(Class<T> type)
      throws IllegalStateException {
    final GeneratedClassLoader loader = newLoader(type);
    ProtobufCodec<T> encoder = null;
    String cacheName = encoders.get(type);
    if (cacheName != null) {
      encoder = get(loader, cacheName);
    }
    if (encoder == null) {
      final CodecGen<T> gen = new CodecGen<T>(loader, type);
      try {
        encoder = gen.create();
      } catch (OrmException e) {
        throw new IllegalArgumentException("Class " + type.getName()
            + " cannot be supported on protobuf", e);
      }
      encoders.put(type, encoder.getClass().getName());
    }
    return encoder;
  }

  private static <T> GeneratedClassLoader newLoader(final Class<T> type) {
    return new GeneratedClassLoader(type.getClassLoader());
  }

  @SuppressWarnings("unchecked")
  private static <T> ProtobufCodec<T> get(ClassLoader cl, String name) {
    try {
      return (ProtobufCodec<T>) Class.forName(name, true, cl).newInstance();
    } catch (InstantiationException e) {
      return null;
    } catch (IllegalAccessException e) {
      return null;
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private CodecFactory() {
  }
}
