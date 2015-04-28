package nucleus.factory;

import nucleus.presenter.Presenter;

/**
 * Created by rharter on 4/26/15.
 */
public class ReflectionPresenterFactory<T extends Presenter> implements PresenterFactory<T> {

    Class<T> presenterClass;

    public ReflectionPresenterFactory(Class<T> presenterClass) {
        this.presenterClass = presenterClass;
    }

    @Override public T createPresenter() {
        T presenter;
        try {
            presenter = presenterClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return presenter;
    }
}
