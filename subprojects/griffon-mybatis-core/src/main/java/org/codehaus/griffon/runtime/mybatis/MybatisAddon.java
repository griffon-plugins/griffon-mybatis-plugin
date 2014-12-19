/*
 * Copyright 2014 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.inject.DependsOn;
import griffon.plugins.mybatis.MybatisCallback;
import griffon.plugins.mybatis.MybatisFactory;
import griffon.plugins.mybatis.MybatisHandler;
import org.apache.ibatis.session.SqlSession;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
@DependsOn("datasource")
@Named("mybatis")
public class MybatisAddon extends AbstractGriffonAddon {
    @Inject
    private MybatisHandler mybatisHandler;

    @Inject
    private MybatisFactory mybatisFactory;

    public void onBootstrapEnd(@Nonnull GriffonApplication application) {
        for (String sessionFactoryName : mybatisFactory.getSessionFactoryNames()) {
            Map<String, Object> config = mybatisFactory.getConfigurationFor(sessionFactoryName);
            if (getConfigValueAsBoolean(config, "connect_on_startup", false)) {
                mybatisHandler.withSqlSession(sessionFactoryName, new MybatisCallback<Void>() {
                    @Override
                    public Void handle(@Nonnull String sessionFactoryName, @Nonnull SqlSession session) {
                        return null;
                    }
                });
            }
        }
    }

    public void onShutdownStart(@Nonnull GriffonApplication application) {
        for (String sessionFactoryName : mybatisFactory.getSessionFactoryNames()) {
            mybatisHandler.closeSqlSession(sessionFactoryName);
        }
    }
}
