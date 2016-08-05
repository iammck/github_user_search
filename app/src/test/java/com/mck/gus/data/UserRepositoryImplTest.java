package com.mck.gus.data;

import com.mck.gus.data.remote.GithubUserRestService;
import com.mck.gus.data.remote.model.User;
import com.mck.gus.data.remote.model.UserList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Michael on 8/3/2016.
 */
public class UserRepositoryImplTest {

    private static final String USER_LOGIN = "user_login";
    private static final String USER_NAME = "user_name";
    private static final String USER_AVATAR_URL = "user_avatar_url";
    private static final String USER_BIO = "user_bio";

    @Mock
    GithubUserRestService githubUserRestService;

    private UserRepository userRepository;
    private User testUser1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepository = new UserRepositoryImpl(githubUserRestService);
    }

    @Test
    public void searchUsers_2000kResponse_InvokesCorrectApiCalls() {
        // Given the github service returns certain users
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(getTestUser1()), Observable.just(getTestUser2()));

        // when the repository impl is used to search users
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(userLogin(1)).subscribe(subscriber);

        // then the results from repo imple should match the certain users.
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        List<List<User>> onNextEvents = subscriber.getOnNextEvents();
        List<User> users = onNextEvents.get(0);
        Assert.assertEquals(userLogin(1), users.get(0).getLogin());
        Assert.assertEquals(userLogin(2), users.get(1).getLogin());
        verify(githubUserRestService).searchGithubUsers(userLogin(1));
        verify(githubUserRestService).getUser(getTestUser1().getLogin());
        verify(githubUserRestService).getUser(getTestUser2().getLogin());
    }

    @Test
    public void searchUsers_IOExceptionThenSuccess_SearchUsersRetried(){
        // given
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(githubUserList()));

        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(getTestUser1()), Observable.just(getTestUser2()));

        // when search for user
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(userLogin(1)).subscribe(subscriber);

        // then assert no errors and verify results
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        verify(githubUserRestService, times(2)).searchGithubUsers(userLogin(1));
        verify(githubUserRestService).getUser(userLogin(1));
        verify(githubUserRestService).getUser(userLogin(2));

    }

    @Test
    public void searchUsers_GetUserIOExceptionThenSuccess_SearchUsersRetried() {
        // give the rest service behaves like this,
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(getIOExceptionError(),
                        Observable.just(getTestUser1()),
                        Observable.just(getTestUser2()));

        // when searching users with test subscriber
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(userLogin(1)).subscribe(subscriber);

        // then there should be no error and are expecting results.
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();
        verify(githubUserRestService,times(2)).searchGithubUsers(userLogin(1));
        verify(githubUserRestService, times(2)).getUser(userLogin(1));
        verify(githubUserRestService).getUser(userLogin(2));
    }

    @Test
    public void searchUsers_OtherHttpError_SearchTerminatedWithError(){
        // given
        when(githubUserRestService.searchGithubUsers(any()))
                .thenReturn(get403ForbiddenError());
        
        // when
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(userLogin(1)).subscribe(subscriber);
        
        // then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);
        
        verify(githubUserRestService).searchGithubUsers(userLogin(1));
        verify(githubUserRestService, never()).getUser(userLogin(1));
        verify(githubUserRestService, never()).getUser(userLogin(2));
    }
    
    private UserList githubUserList(){
        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(getTestUser(1));
        githubUsers.add(getTestUser(2));
        UserList result = new UserList();
        result.setItems(githubUsers);
        return result;
    }

    public User getTestUser1() {
        return getTestUser(1);
    }

    public User getTestUser2() {
        return getTestUser(2);
    }

    private User getTestUser(int i) {
        User user  = new User();
        user.setLogin(userLogin(i));
        user.setName(userName(i));
        user.setAvatarUrl(userAvatar(i));
        user.setBio(userBio(i));
        return user;
    }

    private String userBio(int i) {
        return USER_BIO + "_" + i;
    }

    private String userAvatar(int i) {
        return USER_AVATAR_URL + "_" + i;
    }

    private String userName(int i) {
        return USER_NAME + "_" + i;
    }

    private String userLogin(int i) {
        return USER_LOGIN + "_" + i;
    }


    public Observable getIOExceptionError() {
        return Observable.error(new IOException());
    }

    public Observable<UserList> get403ForbiddenError() {
        return Observable.error(new HttpException(
                Response.error(403, ResponseBody.create(
                        MediaType.parse("application/json"), "Forbidden"))));
    }
}