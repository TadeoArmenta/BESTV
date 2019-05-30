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

package com.pimenta.bestv.feature.castdetail.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.R
import com.pimenta.bestv.common.kotlin.isNotNullOrEmpty
import com.pimenta.bestv.common.presentation.model.CastViewModel
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.common.presentation.model.loadThumbnail
import com.pimenta.bestv.common.presentation.ui.render.CastDetailsDescriptionRender
import com.pimenta.bestv.common.presentation.ui.render.WorkCardRenderer
import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment
import javax.inject.Inject

/**
 * Created by marcus on 04-04-2018.
 */
class CastDetailsFragment : DetailsSupportFragment(), CastDetailsPresenter.View {

    private val mainAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(presenterSelector) }
    private val actionAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter() }
    private val moviesRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val tvShowsRowAdapter: ArrayObjectAdapter by lazy { ArrayObjectAdapter(WorkCardRenderer()) }
    private val presenterSelector: ClassPresenterSelector by lazy { ClassPresenterSelector() }
    private val detailsOverviewRow: DetailsOverviewRow by lazy { DetailsOverviewRow(castViewModel) }
    private lateinit var castViewModel: CastViewModel

    @Inject
    lateinit var presenter: CastDetailsPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getCastDetailsFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)

        castViewModel = arguments?.getSerializable(CAST) as CastViewModel

        setupDetailsOverviewRow()
        setupDetailsOverviewRowPresenter()
        adapter = mainAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        progressBarManager.apply {
            setRootView(container)
            enableProgressBar()
            initialDelay = 0
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.show()
        presenter.loadCastDetails(castViewModel)
    }

    override fun onCastLoaded(castViewModel: CastViewModel?, movies: List<WorkViewModel>?, tvShow: List<WorkViewModel>?) {
        progressBarManager.hide()

        if (castViewModel != null) {
            this.castViewModel = castViewModel
            detailsOverviewRow.item = castViewModel
            mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
        }

        if (movies.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_MOVIES.toLong(), resources.getString(R.string.movies)))
            moviesRowAdapter.addAll(0, movies)
            val moviesHeader = HeaderItem(MOVIES_HEADER_ID.toLong(), getString(R.string.movies))
            mainAdapter.add(ListRow(moviesHeader, moviesRowAdapter))
        }

        if (tvShow.isNotNullOrEmpty()) {
            actionAdapter.add(Action(ACTION_TV_SHOWS.toLong(), resources.getString(R.string.tv_shows)))
            tvShowsRowAdapter.addAll(0, tvShow)
            val tvShowsHeader = HeaderItem(TV_SHOWS_HEADER_ID.toLong(), getString(R.string.tv_shows))
            mainAdapter.add(ListRow(tvShowsHeader, tvShowsRowAdapter))
        }
    }

    private fun setupDetailsOverviewRow() {
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        castViewModel.loadThumbnail(requireNotNull(context), object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                detailsOverviewRow.imageDrawable = resource
                mainAdapter.notifyArrayItemRangeChanged(0, mainAdapter.size())
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                //DO ANYTHING
            }
        })

        detailsOverviewRow.actionsAdapter = actionAdapter
        mainAdapter.add(detailsOverviewRow)

        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is WorkViewModel) {
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireNotNull(activity),
                        (itemViewHolder.view as ImageCardView).mainImageView,
                        WorkDetailsFragment.SHARED_ELEMENT_NAME
                ).toBundle()
                startActivity(WorkDetailsActivity.newInstance(context, item), bundle)
            }
        }
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = object : FullWidthDetailsOverviewRowPresenter(CastDetailsDescriptionRender()) {

            private lateinit var mDetailsImageView: ImageView

            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                val viewHolder = super.createRowViewHolder(parent)
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image)
                val lp = mDetailsImageView.layoutParams
                lp.width = resources.getDimensionPixelSize(R.dimen.movie_card_width)
                lp.height = resources.getDimensionPixelSize(R.dimen.movie_card_height)
                mDetailsImageView.layoutParams = lp
                return viewHolder
            }
        }
        detailsPresenter.actionsBackgroundColor = resources.getColor(R.color.detail_view_actionbar_background, requireNotNull(activity).theme)
        detailsPresenter.backgroundColor = resources.getColor(R.color.detail_view_background, requireNotNull(activity).theme)

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(activity, SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true
        detailsPresenter.setOnActionClickedListener { action ->
            var position = 0
            when (action.id.toInt()) {
                ACTION_TV_SHOWS -> {
                    if (tvShowsRowAdapter.size() > 0) {
                        position++
                    }
                    if (moviesRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
                ACTION_MOVIES -> {
                    if (moviesRowAdapter.size() > 0) {
                        position++
                    }
                    setSelectedPosition(position)
                }
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    companion object {

        const val CAST = "CAST"
        const val SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME"

        private const val ACTION_MOVIES = 1
        private const val ACTION_TV_SHOWS = 2
        private const val MOVIES_HEADER_ID = 1
        private const val TV_SHOWS_HEADER_ID = 2

        fun newInstance(castViewModel: CastViewModel) =
                CastDetailsFragment().apply {
                    arguments = bundleOf(
                            CAST to castViewModel
                    )
                }
    }
}