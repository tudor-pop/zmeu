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

import io.zmeu.api.resource.Resource;
import io.zmeu.api.schema.SchemaDefinition;
import lombok.Getter;
import org.pf4j.Extension;

import java.util.HashMap;
import java.util.Map;

@Extension
public abstract class Provider<T extends Resource> implements IProvider<T> {
    private final Map<String, Class<?>> schemaMap = new HashMap<>();
    @Getter
    private final T resource;

    public Provider() {
        this.resource = initResource();
        schemasMap();
    }

    /**
     * Use this method to create an instance of your resource
     */
    protected abstract T initResource();

    public SchemaDefinition schema() {
        return SchemaDefinition.toSchema(resource);
    }

    private Map<String, Class<?>> schemasMap() {
        var definition = SchemaDefinition.toSchema(resource);
        schemaMap.put(definition.getName(), definition.getResourceClass());
        return schemaMap;
    }

    public Class<?> getSchema(String name) {
        return schemaMap.get(name);
    }

    public String schemasString() {
        return SchemaDefinition.toString(resource);
    }

}
