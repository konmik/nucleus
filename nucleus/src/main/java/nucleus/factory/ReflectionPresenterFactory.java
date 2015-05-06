package nucleus.factory;

import nucleus.presenter.Presenter;

/**
 * Created by rharter on 4/26/15.
 */
public class ReflectionPresenterFactory<PresenterType extends Presenter> implements PresenterFactory<PresenterType> {

    private Class<PresenterType> presenterClass;

    public static <PresenterType extends Presenter> PresenterFactory<PresenterType> fromViewClass(Class<?> viewClass) {
        RequiresPresenter annotation = viewClass.getAnnotation(RequiresPresenter.class);
        //noinspection unchecked
        Class<PresenterType> presenterClass = annotation == null ? null : (Class<PresenterType>)annotation.value();
        return presenterClass == null ? null : new ReflectionPresenterFactory<>(presenterClass);
    }

    public ReflectionPresenterFactory(Class<PresenterType> presenterClass) {
        this.presenterClass = presenterClass;
    }

    @Override
    public PresenterType createPresenter() {
        PresenterType presenter;
        try {
            presenter = presenterClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return presenter;
    }
}
