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
package org.codehaus.griffon.runtime.jmx;

import griffon.core.env.Metadata;
import griffon.plugins.mybatis.MybatisStorage;
import org.apache.ibatis.session.SqlSessionFactory;
import org.codehaus.griffon.runtime.monitor.AbstractObjectStorageMonitor;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class MybatisStorageMonitor extends AbstractObjectStorageMonitor<SqlSessionFactory> implements MybatisStorageMonitorMXBean {
    public MybatisStorageMonitor(@Nonnull Metadata metadata, @Nonnull MybatisStorage delegate) {
        super(metadata, delegate);
    }

    @Override
    protected String getStorageName() {
        return "mybatis";
    }
}
