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
package org.codehaus.griffon.runtime.mybatis.monitor;

import griffon.annotations.core.Nonnull;
import griffon.core.env.Metadata;
import org.codehaus.griffon.runtime.monitor.AbstractMBeanRegistration;
import org.codehaus.griffon.runtime.mybatis.RecordingSqlSessionFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class SqlSessionFactoryMonitor extends AbstractMBeanRegistration implements SqlSessionFactoryMonitorMXBean {
    private RecordingSqlSessionFactory delegate;
    private final String name;

    public SqlSessionFactoryMonitor(@Nonnull Metadata metadata, @Nonnull RecordingSqlSessionFactory delegate, @Nonnull String name) {
        super(metadata);
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        this.name = name;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        return new ObjectName("griffon.plugins.mybatis:type=SqlSessionFactory,application=" + metadata.getApplicationName() + ",name=" + this.name);
    }

    @Override
    public void postDeregister() {
        delegate = null;
        super.postDeregister();
    }


    @Override
    public int getSessionCount() {
        return delegate.getSessionCount();
    }
}
