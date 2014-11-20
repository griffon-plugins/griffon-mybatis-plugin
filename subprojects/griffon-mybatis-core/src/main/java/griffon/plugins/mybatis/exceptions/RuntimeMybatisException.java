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
package griffon.plugins.mybatis.exceptions;

import griffon.exceptions.GriffonException;

import javax.annotation.Nonnull;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class RuntimeMybatisException extends GriffonException {
    private final String sessionFactoryName;

    public RuntimeMybatisException(@Nonnull String sessionFactoryName, @Nonnull Exception sqle) {
        super(format(sessionFactoryName), requireNonNull(sqle, "sqle"));
        this.sessionFactoryName = sessionFactoryName;
    }

    @Nonnull
    private static String format(@Nonnull String sessionFactoryName) {
        requireNonBlank(sessionFactoryName, "sessionFactoryName");
        return "An error occurred when executing a statement on mybatis '" + sessionFactoryName + "'";
    }

    @Nonnull
    public String getMybatisName() {
        return sessionFactoryName;
    }
}
