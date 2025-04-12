/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.directives.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.cdap.etl.api.StageMetrics;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.TransientStore;
import io.cdap.wrangler.api.TransientVariableScope;
import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.cdap.etl.api.Lookup;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DummyExecutorContext implements ExecutorContext {
  @Override
  public Environment getEnvironment() {
    return Environment.TESTING;
  }

  @Override
  public String getNamespace() {
    return "test-namespace";
  }

  @Override
  public StageMetrics getMetrics() {
    return null;
  }

  @Override
  public String getContextName() {
    return "test-context";
  }

  @Override
  public Map<String, String> getProperties() {
    return Collections.emptyMap();
  }

  @Override
  public URL getService(String applicationId, String serviceId) {
    return null;
  }


  @Override
   

  public TransientStore getTransientStore() {
      return new TransientStore() {
          private final Map<TransientVariableScope, Map<String, Object>> scopedStore = new HashMap<>();
  
          @Override
          public Object get(String key) {
              Map<String, Object> localScope = scopedStore.get(TransientVariableScope.LOCAL);
              return localScope != null ? localScope.get(key) : null;
          }
  
          @Override
          public void set(TransientVariableScope scope, String key, Object value) {
              scopedStore.computeIfAbsent(scope, k -> new HashMap<>()).put(key, value);
          }
  
          @Override
          public void increment(TransientVariableScope scope, String key, long value) {
              Map<String, Object> scopeStore = scopedStore.computeIfAbsent(scope, k -> new HashMap<>());
              Object current = scopeStore.getOrDefault(key, 0L);
              if (current instanceof Long) {
                  scopeStore.put(key, (Long) current + value);
              } else {
                  scopeStore.put(key, value);
              }
          }
  
          @Override
          public void reset(TransientVariableScope scope) {
              scopedStore.remove(scope);
          }
  
          @Override
          public Set<String> getVariables() {
              Map<String, Object> localScope = scopedStore.get(TransientVariableScope.LOCAL);
              return localScope != null ? localScope.keySet() : Collections.emptySet();
          }
      };
  }
  
  @Override
  public <T> Lookup<T> provide(String lookupName, Map<String, String> properties) {
    return null;
  }

  @Override
  public boolean isSchemaManagementEnabled() {
    return false;
  }
}
