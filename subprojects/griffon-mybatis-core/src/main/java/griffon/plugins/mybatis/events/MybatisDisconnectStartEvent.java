/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2020 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.mybatis.events;

import griffon.annotations.core.Nonnull;
import griffon.core.event.Event;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class MybatisDisconnectStartEvent extends Event {
    private final String name;
    private final Map<String, Object> config;
    private final SqlSessionFactory sqlSessionFactory;

    public MybatisDisconnectStartEvent(@Nonnull String name, @Nonnull Map<String, Object> config, @Nonnull SqlSessionFactory sqlSessionFactory) {
        this.name = requireNonBlank(name, "Argument 'name' must not be blank");
        this.config = requireNonNull(config, "Argument 'config' must not be null");
        this.sqlSessionFactory = requireNonNull(sqlSessionFactory, "Argument 'sqlSessionFactory' must not be null");
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Map<String, Object> getConfig() {
        return config;
    }

    @Nonnull
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @Nonnull
    public static MybatisDisconnectStartEvent of(@Nonnull String name, @Nonnull Map<String, Object> config, @Nonnull SqlSessionFactory sqlSessionFactory) {
        return new MybatisDisconnectStartEvent(name, config, sqlSessionFactory);
    }
}
