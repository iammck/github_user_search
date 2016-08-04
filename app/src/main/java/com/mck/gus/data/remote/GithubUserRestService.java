package com.mck.gus.data.remote;

import com.mck.gus.data.remote.model.User;
import com.mck.gus.data.remote.model.UserList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Created by Michael on 8/1/2016.
 */
public interface GithubUserRestService {
    @GET("/search/users?per_page=2")
    Observable<UserList> searchGithubUsers(@Query("q") String searchTerm);

    @GET("/users/{username}")
    Observable<User> getUser(@Path("username") String username);
}
