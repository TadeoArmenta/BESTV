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

package com.pimenta.bestv.feature.widget.presenter

import android.widget.ImageView
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.manager.ImageManager
import com.pimenta.bestv.repository.entity.Video
import com.pimenta.bestv.repository.entity.Work
import javax.inject.Inject

/**
 * Created by marcus on 28-01-2019.
 */
class VideoCardPresenter @Inject constructor(
        private val imageManager: ImageManager
) {

    /**
     * Loads the [Video] thumbnail into [ImageView]
     *
     * @param video     [Video]
     * @param imageView [ImageView]
     */
    fun loadVideoThumbnailImage(video: Video, imageView: ImageView) {
        imageManager.loadImageInto(imageView, String.format(BuildConfig.YOUTUBE_THUMBNAIL_BASE_URL, video.key))
    }

}