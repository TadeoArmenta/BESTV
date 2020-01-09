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

package com.pimenta.bestv.workbrowse.presentation.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.Row
import com.pimenta.bestv.presentation.extension.addFragment
import com.pimenta.bestv.presentation.extension.popBackStack
import com.pimenta.bestv.presentation.ui.fragment.ErrorFragment
import com.pimenta.bestv.route.Route
import com.pimenta.bestv.workbrowse.R
import com.pimenta.bestv.workbrowse.di.WorkBrowseFragmentComponent
import com.pimenta.bestv.workbrowse.presentation.presenter.ABOUT_ID
import com.pimenta.bestv.workbrowse.presentation.presenter.TOP_WORK_LIST_ID
import com.pimenta.bestv.workbrowse.presentation.presenter.WORK_GENRE_ID
import com.pimenta.bestv.workbrowse.presentation.presenter.WorkBrowsePresenter
import com.pimenta.bestv.workbrowse.presentation.ui.diffcallback.RowDiffCallback
import com.pimenta.bestv.workbrowse.presentation.ui.headeritem.GenreHeaderItem
import com.pimenta.bestv.workbrowse.presentation.ui.headeritem.WorkTypeHeaderItem
import javax.inject.Inject

/**
 * Created by marcus on 07-02-2018.
 */
private const val ERROR_FRAGMENT_REQUEST_CODE = 1

class WorkBrowseFragment : BrowseSupportFragment(), WorkBrowsePresenter.View {

    private val rowsAdapter by lazy { ArrayObjectAdapter(ListRowPresenter()) }
    private val rowDiffCallback by lazy { RowDiffCallback() }

    @Inject
    lateinit var presenter: WorkBrowsePresenter

    override fun onAttach(context: Context) {
        WorkBrowseFragmentComponent.create(this, requireActivity().application)
                .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.bindTo(this.lifecycle)

        setupUIElements()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarManager.apply {
            enableProgressBar()
            setProgressBarView(
                    LayoutInflater.from(context).inflate(R.layout.view_load, null).also {
                        (view.parent as ViewGroup).addView(it)
                    })
            initialDelay = 0
        }

        adapter = rowsAdapter
        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()
        presenter.addFavoriteRow(selectedPosition)
    }

    override fun onShowProgress() {
        progressBarManager.show()
    }

    override fun onHideProgress() {
        progressBarManager.hide()
    }

    override fun onDataLoaded(rows: List<Row>) {
        rowsAdapter.setItems(rows, rowDiffCallback)

        startEntranceTransition()
    }

    override fun onUpdateSelectedPosition(selectedPosition: Int) {
        this.selectedPosition = selectedPosition
    }

    override fun onErrorDataLoaded() {
        val fragment = ErrorFragment.newInstance().apply {
            setTargetFragment(this@WorkBrowseFragment, ERROR_FRAGMENT_REQUEST_CODE)
        }
        requireActivity().addFragment(fragment, ErrorFragment.TAG)
    }

    override fun openSearch(route: Route) {
        startActivity(route.intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ERROR_FRAGMENT_REQUEST_CODE -> {
                requireActivity().popBackStack(ErrorFragment.TAG, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                if (resultCode == Activity.RESULT_OK) {
                    presenter.loadData()
                }
            }
        }
    }

    private fun setupUIElements() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        setOnSearchClickedListener {
            presenter.searchClicked()
        }

        searchAffordanceColor = resources.getColor(R.color.background_color, requireActivity().theme)
        mainFragmentRegistry.registerFragment(PageRow::class.java, PageRowFragmentFactory())

        prepareEntranceTransition()
    }

    private inner class PageRowFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any): Fragment {
            presenter.refreshRows()

            val row = rowObj as Row
            when (row.headerItem.id) {
                TOP_WORK_LIST_ID -> {
                    val movieListTypeHeaderItem = row.headerItem as WorkTypeHeaderItem
                    title = row.headerItem.name
                    return TopWorkGridFragment.newInstance(movieListTypeHeaderItem.topWorkTypeViewModel)
                }
                WORK_GENRE_ID -> {
                    val genreHeaderItem = row.headerItem as GenreHeaderItem
                    title = genreHeaderItem.genreViewModel.name
                    return GenreWorkGridFragment.newInstance(genreHeaderItem.genreViewModel)
                }
                ABOUT_ID -> {
                    title = ""
                    return AboutFragment.newInstance()
                }
            }

            throw IllegalArgumentException(String.format("Invalid row %s", rowObj))
        }
    }

    companion object {

        fun newInstance() = WorkBrowseFragment()
    }
}
