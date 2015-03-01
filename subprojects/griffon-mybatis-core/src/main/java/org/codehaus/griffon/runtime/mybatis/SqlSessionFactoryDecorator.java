/*
 * Copyright 2014-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.mybatis;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

import javax.annotation.Nonnull;
import java.sql.Connection;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class SqlSessionFactoryDecorator implements SqlSessionFactory {
    private final SqlSessionFactory delegate;

    public SqlSessionFactoryDecorator(@Nonnull SqlSessionFactory delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected SqlSessionFactory getDelegate() {
        return delegate;
    }

    @Override
    public SqlSession openSession() {
        return delegate.openSession();
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return delegate.openSession(autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return delegate.openSession(connection);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return delegate.openSession(level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return delegate.openSession(execType);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return delegate.openSession(execType, autoCommit);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return delegate.openSession(execType, level);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return delegate.openSession(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }
}
