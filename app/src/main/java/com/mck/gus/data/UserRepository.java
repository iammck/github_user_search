package com.mck.gus.data;

import com.mck.gus.data.remote.model.User;

import java.util.List;

import rx.Observable;

/**
 * Created by Michael on 8/1/2016.
 */
public interface UserRepository {
    public Observable<List<User>> searchUsers(final String searchTerm);
}
