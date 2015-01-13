package nucleus.presenter.broker;

import nucleus.presenter.Presenter;

public abstract class Broker<TargetType> implements Presenter.TargetListener<TargetType>, Presenter.OnDestroyListener {

    private TargetType target;

    @Override
    public void onTakeTarget(TargetType target) {
        this.target = target;
        present();
    }

    @Override
    public void onDropTarget(TargetType target) {
        if (this.target == target)
            this.target = null;
    }

    @Override
    public void onDestroy() {
    }

    public TargetType getTarget() {
        return target;
    }

    public void present() {
        TargetType target = getTarget();
        if (target != null)
            onPresent(target);
    }

    protected abstract void onPresent(TargetType target);
}
