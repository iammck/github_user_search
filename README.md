# GitHub user search (GUS)

This project follows a tutorial by Rebecca Franks posted on their[site](https://riggaroo.co.za/)titled Introduction to Android Testing. It currently covers the first three parts.

After aggravating a need for better testing, considering application architectures and project structure, the series builds and Android application in android studio. In the third part the application's REST service (GithubUserRestService) is implemented using retrofit and GSon, then UserRepositoryImpl is implemented with rxJava using the new jack compiler so that lambda expressions can be used. Notice also in the build.gradle file that compile options for java version 1 8.

Once the these two classes are implemented, Franks implements UserRepositoryImplTest. This test uses mockito for mocking a GithubUserRestService implementation. Tests are set up using when, then, given. Given section sets up the mock to return appropriate test results when the mock returns. The section When creates a TestSubscriber instance and subscribes the the method under test. in the final section after waiting for the terminal event results are checkd for success with junit and Mockito. Tests cover proper use case and edge use cases.

 [This](https://github.com/riggaroo/GithubUsersSearchApp)is a link to the GitHub[repo](https://github.com/riggaroo/GithubUsersSearchApp). Thanks Rebecca Franks.