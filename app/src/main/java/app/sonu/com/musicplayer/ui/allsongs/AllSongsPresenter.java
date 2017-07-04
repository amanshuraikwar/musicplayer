package app.sonu.com.musicplayer.ui.allsongs;

import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.ui.allsongs.list.SongVisitable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsPresenter extends BasePresenter<AllSongsMvpView> implements
        AllSongsMvpPresenter{

    private static final String TAG = AllSongsPresenter.class.getSimpleName();
    private PublishSubject<Song> selectedSongSubject;

    private Observable<List<Song>> songListObservable =
            Observable.just(mDataManager.getSongListFromLocalStorage());

    public AllSongsPresenter(DataManager dataManager,
                             PublishSubject<Song> selectedSongSubject) {
        super(dataManager);
        this.selectedSongSubject = selectedSongSubject;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {

        songListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mMvpView.startLoading();
                    }

                    @Override
                    public void onNext(List<Song> value) {
                        mMvpView.displayList(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMvpView.stopLoading();
                    }

                    @Override
                    public void onComplete() {
                        mMvpView.stopLoading();
                    }
                });

//        mMvpView.startLoading();
//        if (mDataManager.isFirstRun()) {
////            mDataManager.eraseDb();
//            List<Song> songs = mDataManager.getSongListFromLocalStorage();
//            for (Song song : songs) {
//                Log.d(TAG, "onStart: song = "+song);
//            }
//            mMvpView.displayList(getVisitableList(songs));
//
////            mDataManager.addSongsToDb(songs);
//
////            List<Song> songss = DataSupport.where("title like ?", "%sa%").find(Song.class);
//
////            Log.d(TAG, "onStart: song---- = "+songss);
////
////            mMvpView.displayList(getVisitableList(mDataManager.getAllSongsFromDb()));
//        } else {
//            mMvpView.displayList(getVisitableList(mDataManager.getAllSongsFromDb()));
//        }
//

    }

    @Override
    public void onRefresh() {
        songListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Song>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mMvpView.startLoading();
                    }

                    @Override
                    public void onNext(List<Song> value) {
                        mMvpView.displayList(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMvpView.stopLoading();
                    }

                    @Override
                    public void onComplete() {
                        mMvpView.stopLoading();
                    }
                });
    }

    @Override
    public void onSongClick(Song song) {
        Log.d(TAG, "onSongClick:called");
        selectedSongSubject.onNext(song);
    }
}
