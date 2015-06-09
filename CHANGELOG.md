# Changelog

### 09.06.2015, Version: 1.3.3

* Jar library release of core library to provide sources in IntelliJ IDEA.
 Support libraries are still in aar format because of scary compilation warnings. :D

### 09.06.2015, Version: 1.3.1

* Lazy presenter creation before the actual `onTakeView` call. In some cases this allows to initialize
  presenter dependencies before initializing presenter itself.
* `RxPresenter.add(Subscription)` method to automatically unsubscribe subscriptions on presenter destruction.

### 07.05.2015, Version: 1.3.0

* An ability to instantiate presenters with custom `PresenterFactory`, this allows
  to put arguments into a presenter's constructor or to make an instance-specific
  dependency injection.
* `NucleusAppCompatActivity`
* `@RequiresPresenter` has been moved to `nucleus.factory` package.

### 03.04.2015, Version: 1.1.2

* Separate `PresenterHelper` class for easier View class creation.

### 03.04.2015, Version: 1.1.0

* Base view classes for support libraries has been extracted to separate artifacts.
* There is a possibility to NOT put `@RequiresPresenter` annotation now.
* `@RequiresPresenter` annotation has been moved to `view` package.
* `PresenterManager` does not analyse View to find Presenter's class now. It requires presenter's class as an argument.
