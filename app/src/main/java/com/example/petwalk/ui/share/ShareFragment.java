package com.example.petwalk.ui.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.petwalk.R;
import com.example.petwalk.utilerias.SessionManager;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;
    private SessionManager sm;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_user, container, false);

        sm = new SessionManager(getContext());
        sm.LogOutUser();

        return root;
    }
}