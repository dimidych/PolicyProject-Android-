package com.dimidych.policyservicestarter.mvp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class StateMaintainer {
    protected final String TAG = getClass().getSimpleName();

    private final String _stateMaintenerTag;
    private final WeakReference<FragmentManager> _fragmentManager;
    private StateMngFragment _stateMaintainerFrag;

    public StateMaintainer(String stateMaintainerTAG) {
        _fragmentManager = null;
        _stateMaintenerTag = stateMaintainerTAG;
    }

    public StateMaintainer(FragmentManager fragmentManager, String stateMaintainerTAG) {
        _fragmentManager = new WeakReference<>(fragmentManager);
        _stateMaintenerTag = stateMaintainerTAG;
    }

    public boolean firstTimeIn() {
        try {
            // Recovering the reference
            _stateMaintainerFrag = (StateMngFragment)
                    _fragmentManager.get().findFragmentByTag(_stateMaintenerTag);

            // Creating a new RetainedFragment
            if (_stateMaintainerFrag == null) {
                Log.d(TAG, "Creating a new RetainedFragment " + _stateMaintenerTag);
                _stateMaintainerFrag = new StateMngFragment();
                _fragmentManager.get().beginTransaction()
                        .add(_stateMaintainerFrag, _stateMaintenerTag).commit();
                return true;
            } else {
                Log.d(TAG, "Returns a existent retained fragment existente " + _stateMaintenerTag);
                return false;
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "Error firstTimeIn()");
            return false;
        }
    }

    public void put(String key, Object obj) {
        _stateMaintainerFrag.put(key, obj);
    }

    public void put(Object obj) {
        put(obj.getClass().getName(), obj);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return _stateMaintainerFrag.get(key);
    }

    public boolean hasKey(String key) {
        return _stateMaintainerFrag.get(key) != null;
    }

    public static class StateMngFragment extends Fragment {
        private HashMap<String, Object> mData = new HashMap<>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Grants that the frag will be preserved
            setRetainInstance(true);
        }

        public void put(String key, Object obj) {
            mData.put(key, obj);
        }

        public void put(Object object) {
            put(object.getClass().getName(), object);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) mData.get(key);
        }
    }
}
