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

package com.pimenta.bestv.feature.main.presentation.ui.fragment

import android.content.Context
import androidx.core.os.bundleOf
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.common.presentation.model.GenreViewModel
import com.pimenta.bestv.feature.main.di.GenreWorkGridFragmentComponent

private const val GENRE = "GENRE"

/**
 * Created by marcus on 11-02-2018.
 */
class GenreWorkGridFragment : AbstractWorkGridFragment() {

    private val genreViewModel: GenreViewModel by lazy { arguments?.getSerializable(GENRE) as GenreViewModel }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        GenreWorkGridFragmentComponent.build(this, requireActivity().application)
                .inject(this)
    }

    override fun loadData() {
        presenter.loadWorkByGenre(genreViewModel)
    }

    override fun refreshDada() {
        // DO ANYTHING
    }

    companion object {

        fun newInstance(genreViewModel: GenreViewModel) =
                GenreWorkGridFragment().apply {
                    arguments = bundleOf(
                            GENRE to genreViewModel
                    )
                }
    }
}