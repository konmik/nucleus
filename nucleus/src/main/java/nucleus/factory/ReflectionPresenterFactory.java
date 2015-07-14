package nucleus.factory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import nucleus.presenter.Presenter;

public class ReflectionPresenterFactory<P extends Presenter> implements PresenterFactory<P> {

    private static final String PRESENTER_ID_KEY = "presenter_id";

    private static HashMap<String, Presenter> idToPresenter = new HashMap<>();
    private static HashMap<Presenter, String> presenterToId = new HashMap<>();

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
    public P providePresenter(Bundle savedState) {
        String id = providePresenterId(presenterClass.getSimpleName(), savedState);

        if (idToPresenter.containsKey(id))
            //noinspection unchecked
            return (P)idToPresenter.get(id);

        final P presenter = instantiatePresenter(presenterClass);

        idToPresenter.put(id, presenter);
        presenterToId.put(presenter, id);

        presenter.addOnDestroyListener(new Presenter.OnDestroyListener() {
            @Override
            public void onDestroy() {
                idToPresenter.remove(presenterToId.remove(presenter));
            }
        });

        presenter.onCreate(savedState);
        return presenter;
    }

    @Override
    public void savePresenter(Presenter presenter, @NonNull Bundle bundle) {
        bundle.putString(PRESENTER_ID_KEY, presenterToId.get(presenter));
        presenter.onSave(bundle);
    }

    private static <P extends Presenter> P instantiatePresenter(Class<P> presenterClass) {
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
