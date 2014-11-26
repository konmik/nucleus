package nucleus.presenter.broker;

public interface Broker<TargetType> {

    void onDestroy();

    void onTakeTarget(TargetType target);
    void onDropTarget(TargetType target);
}
