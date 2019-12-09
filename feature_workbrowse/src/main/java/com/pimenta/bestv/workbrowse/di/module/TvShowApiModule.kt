/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.workbrowse.di.module

import com.pimenta.bestv.data.di.module.MediaRemoteModule
import com.pimenta.bestv.workbrowse.data.remote.api.TvShowTmdbApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by marcus on 20-10-2019.
 */
@Module(
        includes = [
            MediaRemoteModule::class
        ]
)
class TvShowApiModule {

    @Provides
    @Singleton
    fun provideTvShowApi(retrofit: Retrofit) =
            retrofit.create(TvShowTmdbApi::class.java)
}