package ir.markazandroid.masteradvertiser.network;


import ir.markazandroid.masteradvertiser.object.ErrorObject;

/**
 * Coded by Ali on 03/11/2017.
 */

public interface OnResultLoaded<T> {
    void loaded(T result);

    void failed(Exception e);

    interface ActionListener<T> {
        void onSuccess(T successResult);

        void onError(ErrorObject error);

        void failed(Exception e);
    }
}
