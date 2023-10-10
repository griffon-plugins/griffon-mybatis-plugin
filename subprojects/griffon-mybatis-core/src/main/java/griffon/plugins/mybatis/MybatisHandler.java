/*
 * Copyright 2014-2017 the original author or authors.
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
package griffon.plugins.mybatis;

import griffon.plugins.mybatis.exceptions.RuntimeMybatisException;
import org.apache.ibatis.session.SqlSession;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 */
public interface MybatisHandler {
    // tag::methods[]
    @Nullable
    <R> R withSqlSession(@Nonnull MybatisCallback<R> callback)
        throws RuntimeMybatisException;

    @Nullable
    <R> R withSqlSession(@Nonnull String sessionFactoryName, @Nonnull MybatisCallback<R> callback)
        throws RuntimeMybatisException;

    SqlSession getSqlSession(@Nonnull String sessionFactoryName);

    SqlSession getSqlSession(@Nonnull String sessionFactoryName, boolean autoCommit);

    void closeSqlSession();

    void closeSqlSession(@Nonnull String sessionFactoryName);
    // end::methods[]
}