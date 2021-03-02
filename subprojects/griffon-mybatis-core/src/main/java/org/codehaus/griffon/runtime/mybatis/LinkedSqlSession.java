/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2021 The author and/or original authors.
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
package org.codehaus.griffon.runtime.mybatis;

import griffon.annotations.core.Nonnull;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author Andres Almiray
 */
public class LinkedSqlSession extends SqlSessionDecorator {
    private RecordingSqlSessionFactory sqlSessionFactory;

    public LinkedSqlSession(@Nonnull SqlSession delegate, @Nonnull RecordingSqlSessionFactory sqlSessionFactory) {
        super(delegate);
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Nonnull
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    @Override
    public void close() {
        super.close();
        sqlSessionFactory.decreaseSessionCount();
    }
}
