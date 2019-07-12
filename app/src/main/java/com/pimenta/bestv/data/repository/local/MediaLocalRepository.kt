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

package com.pimenta.bestv.data.repository.local

import com.pimenta.bestv.data.entity.Movie
import com.pimenta.bestv.data.entity.TvShow
import com.pimenta.bestv.data.entity.Work
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by marcus on 20-05-2018.
 */
interface MediaLocalRepository {

    fun isFavorite(work: Work): Single<Boolean>

    fun hasFavorite(): Single<Boolean>

    fun saveFavorite(work: Work): Completable

    fun deleteFavorite(work: Work): Completable

    fun getMovies(): Single<List<Movie>>

    fun getTvShows(): Single<List<TvShow>>

}