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

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class SqlSessionDecorator implements SqlSession {
    private final SqlSession delegate;

    public SqlSessionDecorator(SqlSession delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected SqlSession getDelegate() {
        return delegate;
    }

    @Override
    public <T> T selectOne(String statement) {
        return delegate.selectOne(statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return delegate.selectOne(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement) {
        return delegate.selectList(statement);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return delegate.selectList(statement, parameter);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return delegate.selectList(statement, parameter, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        return delegate.selectMap(statement, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        return delegate.selectMap(statement, parameter, mapKey);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        return delegate.selectMap(statement, parameter, mapKey, rowBounds);
    }

    @Override
    public void select(String statement, Object parameter, ResultHandler handler) {
        delegate.select(statement, parameter, handler);
    }

    @Override
    public void select(String statement, ResultHandler handler) {
        delegate.select(statement, handler);
    }

    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        delegate.select(statement, parameter, rowBounds, handler);
    }

    @Override
    public int insert(String statement) {
        return delegate.insert(statement);
    }

    @Override
    public int insert(String statement, Object parameter) {
        return delegate.insert(statement, parameter);
    }

    @Override
    public int update(String statement) {
        return delegate.update(statement);
    }

    @Override
    public int update(String statement, Object parameter) {
        return delegate.update(statement, parameter);
    }

    @Override
    public int delete(String statement) {
        return delegate.delete(statement);
    }

    @Override
    public int delete(String statement, Object parameter) {
        return delegate.delete(statement, parameter);
    }

    @Override
    public void commit() {
        delegate.commit();
    }

    @Override
    public void commit(boolean force) {
        delegate.commit(force);
    }

    @Override
    public void rollback() {
        delegate.rollback();
    }

    @Override
    public void rollback(boolean force) {
        delegate.rollback(force);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return delegate.flushStatements();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void clearCache() {
        delegate.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return delegate.getMapper(type);
    }

    @Override
    public Connection getConnection() {
        return delegate.getConnection();
    }
}
