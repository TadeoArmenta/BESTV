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

package com.pimenta.bestv.feature.castdetail.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pimenta.bestv.BesTV;
import com.pimenta.bestv.R;
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsFragment;
import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter;
import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter.CastDetailsContract;
import com.pimenta.bestv.repository.entity.Cast;
import com.pimenta.bestv.repository.entity.Work;
import com.pimenta.bestv.feature.workdetail.ui.WorkDetailsActivity;
import com.pimenta.bestv.feature.base.BaseDetailsFragment;
import com.pimenta.bestv.feature.widget.CastDetailsDescriptionPresenter;
import com.pimenta.bestv.feature.widget.WorkCardPresenter;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by marcus on 04-04-2018.
 */
public class CastDetailsFragment extends BaseDetailsFragment implements CastDetailsContract {

    public static final String TAG = "CastDetailsFragment";
    public static final String SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME";
    public static final String CAST = "CAST";

    private static final int ACTION_MOVIES = 1;
    private static final int ACTION_TV_SHOWS = 2;
    private static final int MOVIES_HEADER_ID = 1;
    private static final int TV_SHOWS_HEADER_ID = 2;

    private ArrayObjectAdapter mAdapter;
    private ArrayObjectAdapter mActionAdapter;
    private ArrayObjectAdapter mMoviesRowAdapter;
    private ArrayObjectAdapter mTvShowsRowAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private DetailsOverviewRow mDetailsOverviewRow;

    private Cast mCast;

    @Inject
    CastDetailsPresenter mPresenter;

    public static WorkDetailsFragment newInstance() {
        return new WorkDetailsFragment();
    }

    @Override
    public void onAttach(@Nullable Context context) {
        super.onAttach(context);
        BesTV.getApplicationComponent().inject(this);
        mPresenter.register(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCast == null) {
            mCast = (Cast) getActivity().getIntent().getSerializableExtra(CAST);
        }

        setupDetailsOverviewRow();
        setupDetailsOverviewRowPresenter();
        setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProgressBarManager().show();
        mPresenter.loadCastDetails(mCast);
    }

    @Override public void onDetach() {
        mPresenter.unRegister();
        super.onDetach();
    }

    @Override
    public void onCastLoaded(Cast cast, List<? extends Work> movies, List<? extends Work> tvShow) {
        getProgressBarManager().hide();
        if (cast != null) {
            mCast = cast;
            mDetailsOverviewRow.setItem(cast);
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }

        if (movies != null && movies.size() > 0) {
            mActionAdapter.add(new Action(ACTION_MOVIES, getResources().getString(R.string.movies)));
            WorkCardPresenter workCardPresenter = new WorkCardPresenter();
            workCardPresenter
                  .setLoadWorkPosterListener((movie, imageView) -> mPresenter.loadWorkPosterImage(movie, imageView));
            mMoviesRowAdapter = new ArrayObjectAdapter(workCardPresenter);
            mMoviesRowAdapter.addAll(0, movies);
            HeaderItem moviesHeader = new HeaderItem(MOVIES_HEADER_ID, getString(R.string.movies));
            mAdapter.add(new ListRow(moviesHeader, mMoviesRowAdapter));
        }

        if (tvShow != null && tvShow.size() > 0) {
            mActionAdapter.add(new Action(ACTION_TV_SHOWS, getResources().getString(R.string.tv_shows)));
            WorkCardPresenter workCardPresenter = new WorkCardPresenter();
            workCardPresenter
                  .setLoadWorkPosterListener((movie, imageView) -> mPresenter.loadWorkPosterImage(movie, imageView));
            mTvShowsRowAdapter = new ArrayObjectAdapter(workCardPresenter);
            mTvShowsRowAdapter.addAll(0, tvShow);
            HeaderItem tvShowsHeader = new HeaderItem(TV_SHOWS_HEADER_ID, getString(R.string.tv_shows));
            mAdapter.add(new ListRow(tvShowsHeader, mTvShowsRowAdapter));
        }
    }

    @Override
    public void onCardImageLoaded(Drawable resource) {
        mDetailsOverviewRow.setImageDrawable(resource);
        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }

    private void setupDetailsOverviewRow() {
        mPresenterSelector = new ClassPresenterSelector();
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);

        mDetailsOverviewRow = new DetailsOverviewRow(mCast);
        mPresenter.loadCastImage(mCast);

        mActionAdapter = new ArrayObjectAdapter();
        mDetailsOverviewRow.setActionsAdapter(mActionAdapter);
        mAdapter.add(mDetailsOverviewRow);

        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter = new FullWidthDetailsOverviewRowPresenter(
              new CastDetailsDescriptionPresenter()) {

            private ImageView mDetailsImageView;

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(final ViewGroup parent) {
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);
                mDetailsImageView = viewHolder.view.findViewById(R.id.details_overview_image);
                ViewGroup.LayoutParams lp = mDetailsImageView.getLayoutParams();
                lp.width = getResources().getDimensionPixelSize(R.dimen.movie_card_width);
                lp.height = getResources().getDimensionPixelSize(R.dimen.movie_card_height);
                mDetailsImageView.setLayoutParams(lp);
                return viewHolder;
            }
        };
        detailsPresenter.setActionsBackgroundColor(getResources().getColor(R.color.detail_view_actionbar_background, getActivity().getTheme()));
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.detail_view_background, getActivity().getTheme()));

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper = new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(getActivity(), SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);
        detailsPresenter.setOnActionClickedListener(action -> {
            int position = 0;
            switch ((int) action.getId()) {
                case ACTION_TV_SHOWS:
                    if (mTvShowsRowAdapter != null && mTvShowsRowAdapter.size() > 0) {
                        position++;
                    }
                case ACTION_MOVIES:
                    if (mMoviesRowAdapter != null && mMoviesRowAdapter.size() > 0) {
                        position++;
                    }
                    setSelectedPosition(position);
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder,
              Row row) {
            if (row != null && row.getHeaderItem() != null) {
                switch ((int) row.getHeaderItem().getId()) {
                    case MOVIES_HEADER_ID:
                    case TV_SHOWS_HEADER_ID:
                        Work work = (Work) item;
                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                              ((ImageCardView) itemViewHolder.view).getMainImageView(),
                              WorkDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                        startActivity(WorkDetailsActivity.newInstance(getContext(), work), bundle);
                        break;
                }
            }
        }
    }
}