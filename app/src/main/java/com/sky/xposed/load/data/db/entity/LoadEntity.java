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

/**
 * Created by sky on 18-1-4.
 */
@Entity
public class LoadEntity {

    @Id(autoincrement = true)
    private Long id;

    @Convert(columnType = Long.class, converter = ListStringConverter.class)
    private List<String> packageNames;

    private String main;
    private int status;
    @Generated(hash = 1809032816)
    public LoadEntity(Long id, List<String> packageNames, String main, int status) {
        this.id = id;
        this.packageNames = packageNames;
        this.main = main;
        this.status = status;
    }
    @Generated(hash = 1204654049)
    public LoadEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<String> getPackageNames() {
        return this.packageNames;
    }
    public void setPackageNames(List<String> packageNames) {
        this.packageNames = packageNames;
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
}
