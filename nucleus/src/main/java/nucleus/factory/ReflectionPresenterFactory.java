package nucleus.factory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import nucleus.presenter.Presenter;

public class ReflectionPresenterFactory<PresenterType extends Presenter> implements PresenterFactory<PresenterType> {

    private static final String PRESENTER_ID_KEY = "presenter_id";

    private static HashMap<String, Presenter> idToPresenter = new HashMap<>();
    private static HashMap<Presenter, String> presenterToId = new HashMap<>();

    private Class<PresenterType> presenterClass;

    @Nullable
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
    public PresenterType providePresenter(Bundle savedState) {
        String id = providePresenterId(presenterClass.getSimpleName(), savedState);

        if (idToPresenter.containsKey(id))
            //noinspection unchecked
            return (PresenterType)idToPresenter.get(id);

        final PresenterType presenter = instantiatePresenter(presenterClass);

        idToPresenter.put(id, presenter);
        presenterToId.put(presenter, id);

        presenter.addOnDestroyListener(new Presenter.OnDestroyListener() {
            @Override
            public void onDestroy() {
                idToPresenter.remove(presenterToId.remove(presenter));
            }
        });

        presenter.create(savedState);
        return presenter;
    }

    @Override
    public void savePresenter(Presenter presenter, @NonNull Bundle bundle) {
        bundle.putString(PRESENTER_ID_KEY, presenterToId.get(presenter));
        presenter.save(bundle);
    }

    private static <PresenterType extends Presenter> PresenterType instantiatePresenter(Class<PresenterType> presenterClass) {
        try {
            return presenterClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String providePresenterId(String prefix, Bundle savedState) {
        return savedState != null ? savedState.getString(PRESENTER_ID_KEY) :
            prefix + "/" + System.nanoTime() + "/" + (int)(Math.random() * Integer.MAX_VALUE);
    }
}
