# Changelog

### XX.05.2015, Version: 1.3.0

* An ability to instantiate presenters with custom PresenterFactory, this allows
  to put arguments into a presenter's constructor or to make an instance-specific
  dependency injection.
* NucleusAppCompatActivity


### 03.04.2015, Version: 1.1.2

* Separate `PresenterHelper` class for easier View class creation.

### 03.04.2015, Version: 1.1.0

* Base view classes for support libraries has been extracted to separate artifacts.
* There is a possibility to NOT put `@RequiresPresenter` annotation now.
* `@RequiresPresenter` annotation has been moved to `view` package.
* `PresenterManager` does not analyse View to find Presenter's class now. It requires presenter's class as an argument.
