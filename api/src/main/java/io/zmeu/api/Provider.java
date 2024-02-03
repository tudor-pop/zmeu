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

import io.zmeu.api.schema.ResourceToSchemaConverter;
import org.pf4j.ExtensionPoint;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;

/**
 * @author Decebal Suiu
 */
public interface Provider<T extends Resource> extends ExtensionPoint {

    Resources items();

    default Schemas schemas() {
        return new Schemas(
                items().getResources().stream()
                        .map(ResourceToSchemaConverter::toSchema)
                        .toList()
        );
    }
    default String schemasString() {
        return items().getResources().stream()
                .map(ResourceToSchemaConverter::toSchema)
                .collect(Collectors.joining());
    }

    T read(T declaration) throws FileNotFoundException;

}
