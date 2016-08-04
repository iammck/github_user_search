package com.mck.gus.data;

import com.mck.gus.data.remote.GithubUserRestService;
import com.mck.gus.data.remote.model.User;

import java.io.IOException;
import java.util.List;

import rx.Observable;

/**
 * Created by Michael on 8/1/2016.
 */
public class UserRepositoryImpl implements UserRepository {
    private GithubUserRestService githubUserRestService;

    public UserRepositoryImpl(GithubUserRestService githubUserRestService){
        this.githubUserRestService = githubUserRestService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<List<User>> searchUsers(final String searchTerm){
        return Observable.defer(() -> githubUserRestService.searchGithubUsers(searchTerm).concatMap(
                usersList -> Observable.from(usersList.getItems())
                        .concatMap(user -> githubUserRestService.getUser(user.getLogin())).toList()))
                .retryWhen(observable -> observable.flatMap(o -> {
                    if (o instanceof IOException) {
                        return Observable.just(null);
                    }
                    return Observable.error(o);
                }));
    }
}
