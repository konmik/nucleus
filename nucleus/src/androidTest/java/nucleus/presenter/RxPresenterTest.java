package nucleus.presenter;

import junit.framework.TestCase;

import rx.Observer;
import rx.Subscriber;

public class RxPresenterTest extends TestCase {

    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);
        presenter.restartable(1).subscribe(new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });

        presenter.restartable(1).subscribe(new Observer() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });

    }
}