/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2021 The author and/or original authors.
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

import griffon.annotations.inject.DependsOn;
import griffon.core.Configuration;
import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.plugins.mybatis.MybatisFactory;
import griffon.plugins.mybatis.MybatisHandler;
import griffon.plugins.mybatis.MybatisStorage;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;
import java.util.ResourceBundle;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 */
@DependsOn("datasource")
@Named("mybatis")
@ServiceProviderFor(Module.class)
public class MybatisModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(ResourceBundle.class)
            .withClassifier(named("mybatis"))
            .toProvider(new ResourceBundleProvider("Mybatis"))
            .asSingleton();

        bind(Configuration.class)
            .withClassifier(named("mybatis"))
            .to(DefaultMybatisConfiguration.class)
            .asSingleton();

        bind(MybatisStorage.class)
            .to(DefaultMybatisStorage.class)
            .asSingleton();

        bind(MybatisFactory.class)
            .to(DefaultMybatisFactory.class)
            .asSingleton();

        bind(MybatisHandler.class)
            .to(DefaultMybatisHandler.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(MybatisAddon.class)
            .asSingleton();
        // end::bindings[]
    }
}
