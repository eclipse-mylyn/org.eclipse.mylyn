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

package com.google.gwtorm.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking a method in an {@link Access} interface as a query.
 * <p>
 * Query methods must return a parameterized {@link ResultSet}, for example:
 *
 * <pre>
 * public interface FooAccess extends Access&lt;Foo, Foo.Key&gt; {
 *   &#064;Query(&quot;WHERE a=?&quot;)
 *   ResultSet&lt;Foo&gt; find(int a) throws OrmException;
 * }
 *</pre>
 *<p>
 * Query strings must conform to the following grammar:
 *
 * <pre>
 * [WHERE &lt;condition&gt; [AND &lt;condition&gt; ...]]
 * [ORDER BY &lt;property&gt; [ASC | DESC] [, &lt;property&gt; [ASC | DESC] ...]]
 * [LIMIT { &lt;count&gt; | ? }]
 *
 * &lt;condition&gt; := &lt;property&gt; { &lt; | &lt;= | &gt; | &gt;= | = | != } &lt;value&gt;
 * &lt;value&gt; := { ? | true | false | &lt;int&gt; | &lt;string&gt; }
 * </pre>
 * <p>
 * Method parameters are bound in order to the placeholders (?) declared in the
 * query conditions. The type of the limit placeholder parameter (if used in the
 * query) must be <code>int</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {
  /**
   * @return the query clause. Defaults to "", matching all entities, no order.
   */
  String value() default "";
}
