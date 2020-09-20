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
package org.codehaus.griffon.runtime.mybatis;

import griffon.annotations.core.Nonnull;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andres Almiray
 */
public class RecordingSqlSessionFactory extends SqlSessionFactoryDecorator {
    private AtomicInteger sessionCount = new AtomicInteger(0);

    public RecordingSqlSessionFactory(@Nonnull SqlSessionFactory delegate) {
        super(delegate);
    }

    public int increaseSessionCount() {
        return sessionCount.incrementAndGet();
    }

    public int decreaseSessionCount() {
        return sessionCount.decrementAndGet();
    }

    public int getSessionCount() {
        return sessionCount.get();
    }

    @Override
    public SqlSession openSession() {
        SqlSession session = super.openSession();
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        SqlSession session = super.openSession(autoCommit);
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        SqlSession session = super.openSession(connection);
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        SqlSession session = super.openSession(level);
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        SqlSession session = super.openSession(execType);
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        SqlSession session = super.openSession(execType, autoCommit);
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        SqlSession session = super.openSession(execType, level);
        increaseSessionCount();
        return wrap(session);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        SqlSession session = super.openSession(execType, connection);
        increaseSessionCount();
        return wrap(session);
    }

    @Nonnull
    private SqlSession wrap(@Nonnull SqlSession session) {
        return session instanceof LinkedSqlSession ? session : new LinkedSqlSession(session, this);
    }
}
