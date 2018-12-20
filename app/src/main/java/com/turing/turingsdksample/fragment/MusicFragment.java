package com.turing.turingsdksample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.turing.music.InitListener;
import com.turing.music.LogUtil;
import com.turing.music.MusicManager;
import com.turing.music.OnPlayerStateListener;
import com.turing.music.OnSearchListener;
import com.turing.music.bean.MusicEntity;
import com.turing.turingsdksample.R;

import java.util.List;

public class MusicFragment extends BaseFragment implements View.OnClickListener {
    private MusicEntity musicEntity;
    private EditText editText;
    private String TAG = MusicFragment.class.getSimpleName();
    private TextView resultTv;
    private boolean canReleased = false;
    private EditText etChannel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, null);
        view.findViewById(R.id.btn_init).setOnClickListener(this);
        view.findViewById(R.id.btn_pause).setOnClickListener(this);
        view.findViewById(R.id.btn_play).setOnClickListener(this);
        view.findViewById(R.id.btn_next).setOnClickListener(this);
        view.findViewById(R.id.btn_resume).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);
        view.findViewById(R.id.btn_stop).setOnClickListener(this);
        view.findViewById(R.id.btn_getpos).setOnClickListener(this);
        view.findViewById(R.id.btn_getmax).setOnClickListener(this);
        view.findViewById(R.id.btn_seekto).setOnClickListener(this);
        view.findViewById(R.id.btn_release).setOnClickListener(this);
        view.findViewById(R.id.btn_page_search).setOnClickListener(this);
        resultTv = (TextView) view.findViewById(R.id.tv_result);
        editText = (EditText) view.findViewById(R.id.et_song);
        etChannel = (EditText) view.findViewById(R.id.et_channelID);

        return view;
    }


    private InitListener initListener = new InitListener() {
        @Override
        public void onSuccess() {
            canReleased = true;
            Log.e(TAG, "initListener onSuccess: " + canReleased);
        }

        @Override
        public void onFailed(int code, String msg) {
            Log.e(TAG, "onFailed: ");
        }
    };


    private int curIndex;
    private List<MusicEntity> musicList;
    private OnSearchListener onSearchListener = new OnSearchListener() {

        @Override
        public void onSuccess(List<MusicEntity> list) {
            if (list == null) {
                return;
            }
            musicList = list;
            musicEntity = list.get(curIndex);
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                MusicEntity musicEntity = list.get(i);
                String s = musicEntity.toString();
                result.append(s).append("\n");
            }
            LogUtil.d(TAG, "LIST RESULT:" + result);
            resultTv.setText(result);

        }

        @Override
        public void onFailed(int errorCode, final String message) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private OnPlayerStateListener onPlayerStateListener = new OnPlayerStateListener() {
        @Override
        public void onStart() {
            Log.e(TAG, "onStart: ");
        }

        @Override
        public void onComplete() {
            Log.e(TAG, "onComplete: ");
        }

        @Override
        public void onError(int i, String errorMsg) {
            Log.e(TAG, "onError: errorCode=" + i + "   errorMsg=" + errorMsg);

        }

        @Override
        public void onBufferingUpdate(int i) {
            Log.d(TAG, "onBufferingUpdate Percent = " + i + "%");
        }


    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init:
                String channelID = etChannel.getText().toString();
                if (TextUtils.isEmpty(channelID)) {
                    Toast.makeText(getContext(), "请输入咪咕渠道号，找图灵商务申请", Toast.LENGTH_SHORT).show();
                    return;
                }
                //新SDK才有这个
//                MusicManager.getInstance().setChannelID(channelID);
                initMusic();
                break;
            case R.id.btn_play:
                curIndex = 0;
                if (musicEntity == null) {
                    Toast.makeText(getActivity(), "音乐参数不能为空.请先搜索", Toast.LENGTH_SHORT).show();
                    return;
                }
                MusicManager.getInstance().play(musicEntity);
                MusicManager.getInstance().setOnPlayStateListener(onPlayerStateListener);
                break;
            case R.id.btn_next:
                curIndex++;
                if (curIndex < musicList.size() - 1) {
                    musicEntity = musicList.get(curIndex);
                    MusicManager.getInstance().play(musicEntity);
                    Toast.makeText(getContext(), musicEntity.getTitle(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_pause:
                MusicManager.getInstance().pause();
                break;
            case R.id.btn_stop:
                MusicManager.getInstance().stop();
                break;
            case R.id.btn_search:
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(getActivity(), "搜索参数不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                MusicManager.getInstance().search(editText.getText().toString(), onSearchListener);
                break;
            case R.id.btn_page_search:
                MusicManager.getInstance().search(editText.getText().toString(), 1, 2, onSearchListener);
                break;
            case R.id.btn_resume:
                MusicManager.getInstance().resume();
                break;
            case R.id.btn_seekto:
                Log.e(TAG, "btn_seekto: ");
                MusicManager.getInstance().seekTo(290836);
                break;
            case R.id.btn_getpos:
                Log.e(TAG, " getPos : " + MusicManager.getInstance().getCurrentPosition());
                break;
            case R.id.btn_getmax:
                Log.e(TAG, " getPos : " + MusicManager.getInstance().getDuration());
                break;
            case R.id.btn_release:
                if (!canReleased) {
                    Log.e(TAG, getString(R.string.already_release));
                    return;
                }
                canReleased = false;
                MusicManager.getInstance().release();
                break;
            default:
                break;
        }
    }

    private void initMusic() {

        MusicManager.getInstance().init(getActivity(), initListener);


    }

    @Override
    public void exitLogic() {
        Log.d(TAG,"exitLogic");
        if (MusicManager.getInstance().isPlaying()){
            MusicManager.getInstance().stop();
        }
    }
}
