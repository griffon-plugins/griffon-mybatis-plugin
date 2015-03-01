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

import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.MXBeanUtils.unregisterFromPlatformMBeanServer;

/**
 * @author Andres Almiray
 */
public class JMXAwareSqlSessionFactory extends SqlSessionFactoryDecorator {
    private static final String ERROR_OBJECT_NAME_BLANK = "Argument 'objectName' must not be blank";
    private final Set<String> objectNames = new LinkedHashSet<>();

    public JMXAwareSqlSessionFactory(@Nonnull SqlSessionFactory delegate) {
        super(delegate);
    }

    public void addObjectName(@Nonnull String objectName) {
        objectNames.add(requireNonBlank(objectName, ERROR_OBJECT_NAME_BLANK));
    }

    public void removeObjectName(@Nonnull String objectName) {
        objectNames.remove(requireNonBlank(objectName, ERROR_OBJECT_NAME_BLANK));
    }

    public void disposeMBeans() {
        for (String objectName : objectNames) {
            unregisterFromPlatformMBeanServer(objectName);
        }
    }
}
