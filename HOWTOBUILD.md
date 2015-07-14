
How to build
---

* Go to `gradle.properties`.

* Change `RELEASE_REPOSITORY_URL` to your local maven repository path.

* Run `gradle clean build uploadArchives`.

How to build support libraries
---

* Go to `settings.gradle`.

* Comment out all modules except `:nucleus-test-kit` and `:nucleus`.

* Run `gradle clean build uploadArchives`.

* Restore `settings.gradle`.

* Now you can build support libraries and run tests.

How to test
---

Run tests on 4.1.1 version - it does not have instrumentation bugs preventing from correct activity recreation test.

The device should be in the portrait mode.
