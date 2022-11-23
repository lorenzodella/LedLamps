// Della Matera Lorenzo 5E

package com.example.ledlamps.main;

import androidx.fragment.app.Fragment;

public abstract class SynchronizableFragment extends Fragment {
    protected boolean showToast;

    public void setShowToast(boolean showToast){
        this.showToast = showToast;
    }

    public boolean isShowToast() {
        return showToast;
    }

    public abstract void sync();
}
