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
import org.pf4j.ExtensionPoint;

import java.util.stream.Collectors;

public interface Provider<T extends Resource> extends ExtensionPoint {

    Resources resources();

    default Schemas schemas() {
        return new Schemas(
                resources().list().stream()
                        .map(SchemaDefinition::toSchemaDefinition)
                        .toList()
        );
    }

    default String schemasString() {
        return resources().list().stream()
                .map(SchemaDefinition::toSchema)
                .collect(Collectors.joining());
    }

    T read(T declaration);

}
