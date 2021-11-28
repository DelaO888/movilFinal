package com.example.blessflag.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.blessflag.R;
import com.example.blessflag.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    TextView inicio;
    MediaController mediaController;
    int pos=0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        inicio = root.findViewById(R.id.text_home);
        inicio.setText("A la Burger App");
        VideoView videoView = root.findViewById(R.id.video_view);
        String videopath = "android.resource://"+ getContext().getPackageName() +"/"+R.raw.tocho;
        videoView.setVideoURI(Uri.parse(videopath));
        if(this.mediaController==null){
            this.mediaController = new MediaController(getContext());
            this.mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.seekTo(pos);
                if(pos==1){
                    videoView.start();
                }
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}