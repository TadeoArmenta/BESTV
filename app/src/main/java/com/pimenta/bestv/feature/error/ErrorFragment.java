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
package com.pimenta.bestv.feature.error;

import android.app.Activity;
import android.os.Bundle;

import com.pimenta.bestv.R;
import com.pimenta.bestv.feature.base.BaseErrorFragment;

/**
 * Created by marcus on 11-02-2018.
 */
public class ErrorFragment extends BaseErrorFragment {

    public static final String TAG = "ErrorFragment";

    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImageDrawable(getResources().getDrawable(R.drawable.lb_ic_sad_cloud, getActivity().getTheme()));
        setMessage(getResources().getString(R.string.error_fragment_message));
        setDefaultBackground(true);

        setButtonText(getResources().getString(R.string.error_fragment_button));
        setButtonClickListener(arg -> {
            if (getTarget() != null) {
                getTarget().onActivityResult(getTargetCode(), Activity.RESULT_OK, null);
            }
        });
    }
}