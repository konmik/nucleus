package nucleus.presenter.broker;

public class SimpleBroker<TargetType> implements Broker<TargetType> {

    @Override
    public void onDestroy() {
    }

    @Override
    public void onTakeTarget(TargetType target) {
    }

    @Override
    public void onDropTarget(TargetType target) {
    }
}
