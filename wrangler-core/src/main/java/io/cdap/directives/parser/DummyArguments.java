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
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.TokenType;

import java.util.HashMap;
import java.util.Map;

public class DummyArguments implements Arguments {
  private final Map<String, Token> tokens = new HashMap<>();

  public DummyArguments(Map<String, Token> tokens) {
    this.tokens.putAll(tokens);
  }

  @Override
  public <T extends Token> T value(String name) {
    return (T) tokens.get(name);
  }

  @Override
  public int size() {
    return tokens.size();
  }

  @Override
  public boolean contains(String name) {
    return tokens.containsKey(name);
  }

  @Override
  public TokenType type(String name) {
    Token token = tokens.get(name);
    return token != null ? token.type() : null;
  }

  @Override
  public int line() {
    return 1;
  }

  @Override
  public int column() {
    return 1;
  }

  @Override
  public String source() {
    return "mock-source";
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    for (Map.Entry<String, Token> entry : tokens.entrySet()) {
      json.addProperty(entry.getKey(), entry.getValue().value().toString());
    }
    return json;
  }
}
