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
package org.codehaus.griffon.runtime.mybatis;

import griffon.plugins.mybatis.MybatisCallback;
import griffon.plugins.mybatis.MybatisFactory;
import griffon.plugins.mybatis.MybatisHandler;
import griffon.plugins.mybatis.MybatisStorage;
import griffon.plugins.mybatis.exceptions.RuntimeMybatisException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultMybatisHandler implements MybatisHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMybatisHandler.class);
    private static final String ERROR_SQLSESSION_BLANK = "Argument 'sessionFactoryName' must not be blank";
    private static final String ERROR_SQLSESSION_NULL = "Argument 'session' must not be null";
    private static final String ERROR_CALLBACK_NULL = "Argument 'callback' must not be null";

    private final MybatisFactory mybatisFactory;
    private final MybatisStorage mybatisStorage;

    @Inject
    public DefaultMybatisHandler(@Nonnull MybatisFactory mybatisFactory, @Nonnull MybatisStorage mybatisStorage) {
        this.mybatisFactory = requireNonNull(mybatisFactory, "Argument 'mybatisFactory' must not be null");
        this.mybatisStorage = requireNonNull(mybatisStorage, "Argument 'mybatisStorage' must not be null");
    }

    @Nullable
    @Override
    public <R> R withSqlSession(@Nonnull MybatisCallback<R> callback) throws RuntimeMybatisException {
        return withSqlSession(DefaultMybatisFactory.KEY_DEFAULT, callback);
    }

    @Nullable
    @Override
    public <R> R withSqlSession(@Nonnull String sessionFactoryName, @Nonnull MybatisCallback<R> callback) throws RuntimeMybatisException {
        requireNonBlank(sessionFactoryName, ERROR_SQLSESSION_BLANK);
        requireNonNull(callback, ERROR_CALLBACK_NULL);
        SqlSession session = getSqlSession(sessionFactoryName);
        try {
            LOG.debug("Executing statements on mybatis '{}'", sessionFactoryName);
            return callback.handle(sessionFactoryName, session);
        } catch (Exception e) {
            throw new RuntimeMybatisException(sessionFactoryName, e);
        } finally {
            session.commit();
            session.close();
        }
    }

    @Override
    public void closeSqlSession() {
        closeSqlSession(DefaultMybatisFactory.KEY_DEFAULT);
    }

    @Override
    public void closeSqlSession(@Nonnull String sessionFactoryName) {
        SqlSessionFactory mybatis = mybatisStorage.get(sessionFactoryName);
        if (mybatis != null) {
            mybatisFactory.destroy(sessionFactoryName, mybatis);
            mybatisStorage.remove(sessionFactoryName);
        }
    }

    @Nonnull
    private SqlSession getSqlSession(@Nonnull String sessionFactoryName) {
        SqlSessionFactory sqlSessionFactory = mybatisStorage.get(sessionFactoryName);
        if (sqlSessionFactory == null) {
            sqlSessionFactory = mybatisFactory.create(sessionFactoryName);
            mybatisStorage.set(sessionFactoryName, sqlSessionFactory);
        }
        return openSession(sessionFactoryName, sqlSessionFactory);
    }

    @Nonnull
    protected SqlSession openSession(@Nonnull String sessionFactoryName, @Nonnull SqlSessionFactory sqlSessionFactory) {
        return sqlSessionFactory.openSession(true);
    }
}
