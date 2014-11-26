package nucleus.example.main;

import nucleus.presenter.broker.Broker;

public class LogBroker<TargetType> implements Broker<TargetType> {

    private final String CLS = getClass().getSimpleName();

    public LogBroker() {
        System.out.println(CLS + ".constructor");
    }

    @Override
    public void onDestroy() {
        System.out.println(CLS + ".destroy");
    }

    @Override
    public void onTakeTarget(TargetType target) {
        System.out.println(CLS + ".onTakeTarget " + target);
    }

    @Override
    public void onDropTarget(TargetType target) {
        System.out.println(CLS + ".onDropTarget " + target);
    }
}
