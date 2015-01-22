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

import griffon.core.ApplicationClassLoader;
import griffon.core.GriffonApplication;
import griffon.core.injection.Injector;
import griffon.plugins.datasource.DataSourceFactory;
import griffon.plugins.datasource.DataSourceStorage;
import griffon.plugins.mybatis.MybatisBootstrap;
import griffon.plugins.mybatis.MybatisFactory;
import griffon.plugins.mybatis.MybatisMapper;
import griffon.util.GriffonClassUtils;
import griffon.util.ServiceLoaderUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.codehaus.griffon.runtime.core.storage.AbstractObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultMybatisFactory extends AbstractObjectFactory<SqlSessionFactory> implements MybatisFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMybatisFactory.class);
    private static final String ERROR_SESSION_FACTORY_NAME_BLANK = "Argument 'sessionFactoryName' must not be blank";
    private final Set<String> sessionFactoryNames = new LinkedHashSet<>();

    @Inject
    private DataSourceFactory dataSourceFactory;

    @Inject
    private DataSourceStorage dataSourceStorage;

    @Inject
    private ApplicationClassLoader applicationClassLoader;

    @Inject
    private Injector injector;

    private final Set<Class<?>> mappers = new LinkedHashSet<>();

    @Inject
    public DefaultMybatisFactory(@Nonnull @Named("mybatis") griffon.core.Configuration configuration, @Nonnull GriffonApplication application) {
        super(configuration, application);
        sessionFactoryNames.add(KEY_DEFAULT);

        if (configuration.containsKey(getPluralKey())) {
            Map<String, Object> sessionFactories = (Map<String, Object>) configuration.get(getPluralKey());
            sessionFactoryNames.addAll(sessionFactories.keySet());
        }
    }

    @Nonnull
    @Override
    public Set<String> getSessionFactoryNames() {
        return sessionFactoryNames;
    }

    @Nonnull
    @Override
    public Map<String, Object> getConfigurationFor(@Nonnull String sessionFactoryName) {
        requireNonBlank(sessionFactoryName, ERROR_SESSION_FACTORY_NAME_BLANK);
        return narrowConfig(sessionFactoryName);
    }

    @Nonnull
    @Override
    protected String getSingleKey() {
        return "sessionFactory";
    }

    @Nonnull
    @Override
    protected String getPluralKey() {
        return "sessionFactories";
    }

    @Nonnull
    @Override
    public SqlSessionFactory create(@Nonnull String name) {
        Map<String, Object> config = narrowConfig(name);
        event("MybatisConnectStart", asList(name, config));
        SqlSessionFactory sqlSessionFactory = createSqlSessionFactory(config, name);

        try (SqlSession session = openSession(name, sqlSessionFactory)) {
            for (Object o : injector.getInstances(MybatisBootstrap.class)) {
                ((MybatisBootstrap) o).init(name, session);
            }
        }

        event("MybatisConnectEnd", asList(name, config, sqlSessionFactory));
        return sqlSessionFactory;
    }

    @Override
    public void destroy(@Nonnull String name, @Nonnull SqlSessionFactory instance) {
        requireNonNull(instance, "Argument 'instance' must not be null");
        Map<String, Object> config = narrowConfig(name);
        event("MybatisDisconnectStart", asList(name, config, instance));

        try (SqlSession session = openSession(name, instance)) {
            for (Object o : injector.getInstances(MybatisBootstrap.class)) {
                ((MybatisBootstrap) o).destroy(name, session);
            }
        }

        closeDataSource(name);

        event("MybatisDisconnectEnd", asList(name, config));
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private SqlSessionFactory createSqlSessionFactory(@Nonnull Map<String, Object> config, @Nonnull String dataSourceName) {
        DataSource dataSource = getDataSource(dataSourceName);
        Environment environment = new Environment(dataSourceName, new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);

        Map<String, Object> copyOfConfig = new LinkedHashMap<>(config);
        copyOfConfig.remove("connect_on_startup");
        GriffonClassUtils.setProperties(configuration, copyOfConfig);

        if (mappers.isEmpty()) {
            readMappers();
        }

        for (Class<?> mapper : mappers) {
            configuration.addMapper(mapper);
        }

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private void closeDataSource(@Nonnull String dataSourceName) {
        DataSource dataSource = dataSourceStorage.get(dataSourceName);
        if (dataSource != null) {
            dataSourceFactory.destroy(dataSourceName, dataSource);
            dataSourceStorage.remove(dataSourceName);
        }
    }

    @Nonnull
    private DataSource getDataSource(@Nonnull String dataSourceName) {
        DataSource dataSource = dataSourceStorage.get(dataSourceName);
        if (dataSource == null) {
            dataSource = dataSourceFactory.create(dataSourceName);
            dataSourceStorage.set(dataSourceName, dataSource);
        }
        return dataSource;
    }

    @Nonnull
    protected SqlSession openSession(@Nonnull String sessionFactoryName, @Nonnull SqlSessionFactory sqlSessionFactory) {
        return sqlSessionFactory.openSession(true);
    }

    private void readMappers() {
        ServiceLoaderUtils.load(applicationClassLoader.get(), "META-INF/types/", MybatisMapper.class, new ServiceLoaderUtils.LineProcessor() {
            @Override
            public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                try {
                    Class<?> mapperClass = loadClass(line.trim(), classLoader);
                    LOG.debug("Registering {} as mybatis mapper class", mapperClass.getName());
                    mappers.add(mapperClass);
                } catch (Exception e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Could not load " + type.getName() + " with " + line, sanitize(e));
                    }
                }
            }
        });
    }

    protected Class<?> loadClass(@Nonnull String className, @Nonnull ClassLoader classLoader) throws ClassNotFoundException {
        ClassNotFoundException cnfe;

        ClassLoader cl = DefaultMybatisFactory.class.getClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        cl = classLoader;
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        throw cnfe;
    }
}
