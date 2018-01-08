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

import com.sky.xposed.load.data.db.convert.ListLongConverter;

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
public class HookEntity {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String packageName;

    @Convert(columnType = String.class, converter = ListLongConverter.class)
    private List<Long> pluginIds;

    @Generated(hash = 26948440)
    public HookEntity(Long id, String packageName, List<Long> pluginIds) {
        this.id = id;
        this.packageName = packageName;
        this.pluginIds = pluginIds;
    }

    @Generated(hash = 428386220)
    public HookEntity() {
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

    public List<Long> getPluginIds() {
        return this.pluginIds;
    }

    public void setPluginIds(List<Long> pluginIds) {
        this.pluginIds = pluginIds;
    }
}
