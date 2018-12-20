package com.turing.turingsdksample.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.turing.music.MusicManager;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.fragment.ASRFragment;
import com.turing.turingsdksample.fragment.AllFragment;
import com.turing.turingsdksample.fragment.AuthorityFragment;
import com.turing.turingsdksample.fragment.BaseFragment;
import com.turing.turingsdksample.fragment.IOTFragment;
import com.turing.turingsdksample.fragment.MusicFragment;
import com.turing.turingsdksample.fragment.SemanticFragment;
import com.turing.turingsdksample.fragment.TTSFragment;
import com.turing.turingsdksample.music.TuringMusic;


/**
 * @author：licheng@uzoo.com
 */

public class MainFragmentActivity extends BaseFragmentActivity {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();
    private AllFragment allFragment;
    private ASRFragment asrFragment;
    private TTSFragment ttsFragment;
    private MusicFragment musicFragment;
    private IOTFragment iotFragment;
    private SemanticFragment semanticFragment;
    private BaseFragment authorityFragment;
    private BaseFragment curFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragments();
        showFragments(getString(R.string.personvsrobot), false);
    }

    private void initFragments() {
        allFragment = new AllFragment();
        asrFragment = new ASRFragment();
        ttsFragment = new TTSFragment();
        musicFragment = new MusicFragment();
        iotFragment = new IOTFragment();
        semanticFragment = new SemanticFragment();
        authorityFragment = new AuthorityFragment();
    }

    private void showFragments(String tag, boolean needback) {
        FragmentTransaction trans = mFragMgr.beginTransaction();
        curFragment = (BaseFragment) getFragmentByTag(tag);
        trans.replace(R.id.base_main, curFragment, tag);
        trans.commit();
    }

    private Fragment getFragmentByTag(String tag) {
        if (getString(R.string.personvsrobot).equals(tag)) {
            return allFragment;
        }
        if (getString(R.string.text_asr).equals(tag)) {
            return asrFragment;
        }
        if (getString(R.string.text_tts).equals(tag)) {
            return ttsFragment;
        }
        if (getString(R.string.music).equals(tag)) {
            return musicFragment;
        }
        if (getString(R.string.iot).equals(tag)) {
            return iotFragment;
        }
        if (getString(R.string.pop_semantic).equals(tag)) {
            return semanticFragment;
        }

        if (getString(R.string.authority).equals(tag)) {
            return authorityFragment;
        }

        return null;
    }

    /**
     * 用于响应3个点击功能
     **/
    @Override
    public void onSelectFragment(String str) {
        showFragments(str, true);
    }

    private long clickTime;



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - clickTime < 2 * 1000) {
                finish();
            } else {
                clickTime = System.currentTimeMillis();
                toast(getString(R.string.resume_exit));
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (TTSManager.getInstance().isSpeaking()) {
            TTSManager.getInstance().stopTTS();
        }
        if (MusicManager.getInstance().isPlaying()) {
            MusicManager.getInstance().stop();
        }

        if (TuringMusic.getInstance().isPlaying()) {
            TuringMusic.getInstance().stop();
        }
        super.onDestroy();
    }
}
