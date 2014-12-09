package nucleus.presenter.broker;

public class Broker<TargetType> {

    private TargetType target;

    public void onDestroy() {
    }

    public TargetType getTarget() {
        return target;
    }

    public void onTakeTarget(TargetType target) {
        this.target = target;
    }

    public void onDropTarget(TargetType target) {
        if (this.target == target)
            this.target = null;
    }
}
