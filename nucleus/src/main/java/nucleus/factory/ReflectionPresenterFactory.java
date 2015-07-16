package nucleus.factory;

import android.support.annotation.Nullable;

import nucleus.presenter.Presenter;

public class ReflectionPresenterFactory<P extends Presenter> implements PresenterFactory<P> {

    private Class<P> presenterClass;

    @Nullable
    public static <PresenterType extends Presenter> PresenterFactory<PresenterType> fromViewClass(Class<?> viewClass) {
        RequiresPresenter annotation = viewClass.getAnnotation(RequiresPresenter.class);
        //noinspection unchecked
        Class<PresenterType> presenterClass = annotation == null ? null : (Class<PresenterType>)annotation.value();
        return presenterClass == null ? null : new ReflectionPresenterFactory<>(presenterClass);
    }

    public ReflectionPresenterFactory(Class<P> presenterClass) {
        this.presenterClass = presenterClass;
    }

    @Override
    public P createPresenter() {
        try {
            return presenterClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
