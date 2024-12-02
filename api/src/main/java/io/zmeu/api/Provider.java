/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zmeu.api;

import io.zmeu.api.schema.SchemaDefinition;
import org.pf4j.Extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Extension
public abstract class Provider<T> implements IProvider<T> {
    private final Map<String, Class<?>> schemaMap = new HashMap<>();

    public Provider() {
        schemasMap();
    }

    abstract public Resources<T> resources();

    public Schemas schemas() {
        var resources = resources().list();
        var list = new ArrayList<SchemaDefinition>(resources.size());
        for (var res : resources) {
            var schemaDefinition = SchemaDefinition.toSchemaDefinition(res);
            list.add(schemaDefinition);
        }
        return new Schemas(list);
    }

    private Map<String, Class<?>> schemasMap() {
        for (var res : resources().list()) {
            var definition = SchemaDefinition.toSchemaDefinition(res);
            schemaMap.put(definition.getName(), definition.getResourceClass());
        }
        return schemaMap;
    }

    public Class<?> getSchema(String name) {
        return schemaMap.get(name);
    }

    public String schemasString() {
        return resources().list().stream()
                .map(SchemaDefinition::toSchema)
                .collect(Collectors.joining());
    }

}
