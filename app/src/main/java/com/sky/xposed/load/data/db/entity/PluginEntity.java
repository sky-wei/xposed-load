/*
 * Copyright (c) 2018 The sky Authors.
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

package com.sky.xposed.load.data.db.entity;

import com.sky.xposed.load.data.db.convert.ListStringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by sky on 18-1-4.
 */
@Entity
public class PluginEntity {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String packageName;
    private String main;
    private int status;

    @Convert(columnType = String.class, converter = ListStringConverter.class)
    private List<String> hookPackageNames;

    @Generated(hash = 1084132996)
    public PluginEntity(Long id, String packageName, String main, int status,
            List<String> hookPackageNames) {
        this.id = id;
        this.packageName = packageName;
        this.main = main;
        this.status = status;
        this.hookPackageNames = hookPackageNames;
    }

    @Generated(hash = 18870672)
    public PluginEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMain() {
        return this.main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getHookPackageNames() {
        return this.hookPackageNames;
    }

    public void setHookPackageNames(List<String> hookPackageNames) {
        this.hookPackageNames = hookPackageNames;
    }
}
